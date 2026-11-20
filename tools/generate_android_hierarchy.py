"""Index a committed HabiTree tree and its Kotlin and Android XML links."""

import argparse
from collections import Counter, defaultdict
from dataclasses import asdict, dataclass, field
import json
from pathlib import Path, PurePosixPath
import re
import subprocess
from urllib.parse import quote, unquote
import xml.etree.ElementTree as ET


PACKAGE = re.compile(r"(?m)^\s*package\s+([\w.]+)")
IMPORT = re.compile(r"(?m)^\s*import\s+([\w.*]+)(?:\s+as\s+(\w+))?")
CODE_LITERAL = re.compile(r'//[^\n]*|/\*.*?\*/|""".*?"""|"(?:\\.|[^"\\\n])*"|\'(?:\\.|[^\'\\\n])*\'', re.DOTALL)
TYPE = re.compile(r"\b(enum\s+class|class|interface|typealias|object)\s+(\w+)")
FUNCTION = re.compile(r"\bfun\s+(?:<[^>\n]+>\s+)?(?:[\w?<>]+\.)*(\w+)\s*(?=\()")
PROPERTY = re.compile(r"(?m)^(?:public\s+|internal\s+|private\s+|const\s+|lateinit\s+)*(val|var)\s+(\w+)")
RESOURCE_USE = re.compile(r"(?:\bR\.(\w+)\.(\w+)|(?<![\w.])[@?](?:\+)?(\w+)/(\w+))")
RESOURCE_LOOKUP = re.compile(r'\bgetIdentifier\(\s*"([\w]+)(\$[\w]+)?"\s*,\s*"(\w+)"')
IDENTIFIER = re.compile(r"\b[A-Za-z_][A-Za-z0-9_]*\b")
GENERATED_COPY = {
    "shared/src/commonMain/kotlin/com/mamxanh/shared/localization/GeneratedCopy.kt",
    "shared/src/commonMain/kotlin/com/mamxanh/shared/localization/TextKey.kt",
    *(f"core-ui/src/main/res/{qualifier}/app_copy.xml" for qualifier in ("values", "values-en", "values-vi")),
}


@dataclass
class TrackedFile:
    path: str
    owner: str
    source_set: str
    bytes: int
    git_blob: str
    kind: str
    role: str = ""
    kotlin_package: str = ""
    declarations: list = field(default_factory=list)
    imports: list = field(default_factory=list)
    resources: list = field(default_factory=list)
    resource_qualifier: str = ""
    xml_root: str = ""
    depends_on: list = field(default_factory=list)
    referenced_by: list = field(default_factory=list)
    gradle_projects: list = field(default_factory=list)
    resource_references: list = field(default_factory=list)
    resource_lookup_patterns: list = field(default_factory=list)
    sample_of: list = field(default_factory=list)
    generated_from: list = field(default_factory=list)
    verified_by: list = field(default_factory=list)
    symbol_references: list = field(default_factory=list)


def path_role(path, source_set):
    parts = PurePosixPath(path).parts
    filename = parts[-1]
    suffix = PurePosixPath(filename).suffix
    if filename == "build.gradle.kts":
        return "Gradle project configuration"
    if filename == "settings.gradle.kts":
        return "Gradle settings and project inclusion"
    if filename == "AndroidManifest.xml":
        return "Android manifest and platform entry points"
    if filename.endswith("Preview.kt") or "previews" in parts:
        return "Compose preview scenario"
    if source_set == "androidTest":
        return "Android instrumentation test source or fixture"
    if source_set.endswith("Test") or source_set == "test":
        return "Host or shared test source or fixture"
    if parts[0] == ".agent":
        return "Repository instructions"
    if parts[:2] == (".github", "workflows"):
        return "GitHub Actions workflow"
    if parts[0] == "localization":
        return "Source localization catalog or Android export mapping"
    if parts[0] == "docs":
        return "Verification evidence" if suffix in {".png", ".gif"} else "Project documentation or verification tooling"
    if parts[0] == "tools":
        return "Repository maintenance tooling"
    if "res" in parts:
        return "Android resource"
    if "assets" in parts:
        return "Packaged app asset"
    if suffix == ".kt":
        return "Kotlin source"
    return "Repository support file"


def git(project, *args):
    return subprocess.check_output(["git", "-C", str(project), *args])


def owner_and_source_set(path):
    parts = PurePosixPath(path).parts
    if len(parts) == 1:
        return "repository root", ""
    module = {"data": "core-storage"}.get(parts[0], parts[0])
    if parts[1] in {"domain", "data"}:
        module += f":{parts[1]}"
    source_set = parts[parts.index("src") + 1] if "src" in parts[:-1] else ""
    return module, source_set


def tracked_files(project, revision):
    for entry in git(project, "ls-tree", "-rlz", revision).split(b"\x00"):
        if not entry:
            continue
        header, path = entry.decode("utf-8").split("\t", 1)
        mode, kind, object_id, size = header.split()
        owner, source_set = owner_and_source_set(path)
        yield TrackedFile(path, owner, source_set, int(size) if size != "-" else 0, object_id, kind,
                          role=path_role(path, source_set))


def source_blobs(project, inputs):
    process = subprocess.Popen(
        ["git", "-C", str(project), "cat-file", "--batch"],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
    )
    try:
        for file in inputs:
            process.stdin.write(f"{file.git_blob}\n".encode())
            process.stdin.flush()
            object_id, kind, size = process.stdout.readline().decode().split()
            if (object_id, kind, int(size)) != (file.git_blob, "blob", file.bytes):
                raise ValueError(f"Git blob changed for {file.path}")
            content = process.stdout.read(file.bytes)
            if process.stdout.read(1) != b"\n":
                raise ValueError(f"Git output was truncated for {file.path}")
            yield file, content.decode("utf-8-sig")
    finally:
        process.stdin.close()
        process.stdout.close()
        if process.wait():
            raise ValueError("Git failed while reading source")


def record_kotlin(file, source):
    code = CODE_LITERAL.sub(lambda m: "".join("\n" if c == "\n" else " " for c in m.group()), source)
    package = PACKAGE.search(code)
    file.kotlin_package = package.group(1) if package else ""
    file.imports = [name for name, _ in IMPORT.findall(code)]
    declarations = []
    for kind, pattern in (("type", TYPE), ("function", FUNCTION), ("property", PROPERTY)):
        for match in pattern.finditer(code):
            line_start = code.rfind("\n", 0, match.start()) + 1
            line = code[line_start:match.start()]
            declarations.append({
                "kind": kind if kind == "function" else match.group(1),
                "name": match.group(1 if kind == "function" else 2),
                "line": code.count("\n", 0, line_start) + 1,
                "top_level": not line.startswith((" ", "\t")) and "{" not in line,
            })
    file.declarations = sorted(declarations, key=lambda item: item["line"])
    return code


def record_xml(file, source):
    root = ET.fromstring(source)
    file.xml_root = root.tag.rsplit("}", 1)[-1]
    parts = PurePosixPath(file.path).parts
    if "res" in parts:
        resource_dir = parts[parts.index("res") + 1]
        resource_type, _, file.resource_qualifier = resource_dir.partition("-")
        if resource_type == "values":
            for item in root:
                name = item.get("name")
                if name:
                    file.resources.append(f"{item.get('type', item.tag)}/{name}")
                for attr in item.findall("attr"):
                    if attr.get("name") and ":" not in attr.get("name"):
                        file.resources.append(f"attr/{attr.get('name')}")
        else:
            file.resources.append(f"{resource_type}/{PurePosixPath(file.path).stem}")
    elif file.xml_root == "manifest":
        name_attribute = "{http://schemas.android.com/apk/res/android}name"
        file.declarations = [
            {"kind": item.tag, "name": item.get(name_attribute)}
            for item in root.iter()
            if item.get(name_attribute)
        ]


def index(project, revision):
    files = list(tracked_files(project, revision))
    symbols = defaultdict(set)
    package_symbols = defaultdict(lambda: defaultdict(set))
    kotlin_identifiers = {}
    kotlin_imports = {}
    resources = defaultdict(set)
    resource_links = {}
    inputs = [
        file for file in files
        if file.path.endswith((".kt", ".xml", ".kts")) or "preview-jobs" in file.path
    ]
    by_path = {file.path: file for file in files}
    build_paths = {
        ":" + file.owner: file.path for file in files
        if file.path.endswith("/build.gradle.kts")
    }
    for file in files:
        parts = PurePosixPath(file.path).parts
        if "res" in parts[:-2] and not file.path.endswith(".xml"):
            resource_folder = parts[parts.index("res") + 1]
            kind, _, file.resource_qualifier = resource_folder.partition("-")
            if kind != "values":
                name = parts[-1].split(".", 1)[0]
                file.resources.append(f"{kind}/{name}")
                resources[f"{kind}/{name}"].add(file.path)
    for file, source in source_blobs(project, inputs):
        if file.path.endswith(".kt"):
            code = record_kotlin(file, source)
            file.resource_lookup_patterns = sorted({
                f"{kind}/{name}{'*' if interpolation else ''}"
                for name, interpolation, kind in RESOURCE_LOOKUP.findall(source)
            })
            identifiers = defaultdict(set)
            for line, text in enumerate(code.splitlines(), 1):
                if not text.lstrip().startswith(("package ", "import ")):
                    for name in IDENTIFIER.findall(text):
                        identifiers[name].add(line)
            kotlin_identifiers[file.path] = identifiers
            kotlin_imports[file.path] = IMPORT.findall(code)
            for declaration in file.declarations:
                if file.kotlin_package:
                    symbols[f"{file.kotlin_package}.{declaration['name']}"].add(file.path)
                    if declaration["top_level"]:
                        package_symbols[file.kotlin_package][declaration["name"]].add(file.path)
        elif file.path.endswith(".xml"):
            record_xml(file, source)
            code = source
            for resource in set(file.resources):
                resources[resource].add(file.path)
        elif file.path.endswith(".kts"):
            quoted_path = r'(:[\w:-]+)'
            if PurePosixPath(file.path).name == "settings.gradle.kts":
                file.gradle_projects = sorted(set(
                    module for arguments in re.findall(r'\binclude\(([^)]*)\)', source)
                    for module in re.findall(f'"{quoted_path}"', arguments)
                ))
            else:
                file.gradle_projects = sorted(set(re.findall(rf'\bproject\("{quoted_path}"\)', source)))
            continue
        else:
            for job in json.loads(source):
                if job.get("source") in by_path:
                    by_path[job["source"]].sample_of.append(f"{file.path} / {job['name']}")
            continue
        resource_code = code
        if file.path.endswith(".kt"):
            for imported, alias in kotlin_imports[file.path]:
                if alias and imported.endswith(".R"):
                    resource_code = re.sub(rf"\b{re.escape(alias)}\.", "R.", resource_code)
        resource_links[file.path] = {
            f"{a or c}/{b or d}" for a, b, c, d in RESOURCE_USE.findall(resource_code)
        }

    reverse = defaultdict(set)
    for file in files:
        dependencies = set()
        if file.path in GENERATED_COPY:
            inputs = {
                "tools/localization/generate.py",
                "localization/android-outputs.json",
                *(path for path in by_path if path.startswith("localization/strings/")),
            }
            file.generated_from = sorted(inputs)
            if missing := inputs - by_path.keys():
                raise ValueError(f"Untracked generator inputs for {file.path}: {sorted(missing)}")
            dependencies.update(inputs)
        identifiers = kotlin_identifiers.get(file.path, {})
        found_symbols = set()

        def identify(name, declaration_paths):
            if name in identifiers and len(declaration_paths) == 1:
                declaration_path = next(iter(declaration_paths))
                if declaration_path != file.path:
                    found_symbols.add((name, declaration_path))

        own_types = {declaration["name"] for declaration in file.declarations}
        for name in (identifiers.keys() & package_symbols[file.kotlin_package].keys()) - own_types:
            declarations = package_symbols[file.kotlin_package][name]
            if len(declarations) == 1:
                dependencies.update(declarations)
                identify(name, declarations)
        for imported, alias in kotlin_imports.get(file.path, ()):
            if imported.endswith(".*"):
                available = package_symbols.get(imported[:-2], {})
                for name in (identifiers.keys() & available.keys()) - own_types:
                    declarations = available[name]
                    if len(declarations) == 1:
                        dependencies.update(declarations)
                        identify(name, declarations)
                continue
            candidate = imported
            while candidate:
                if candidate in symbols:
                    declarations = symbols[candidate]
                    dependencies.update(declarations)
                    identify(alias or imported.rsplit(".", 1)[-1], declarations)
                    break
                candidate, _, _ = candidate.rpartition(".")
        file.symbol_references = [
            {"symbol": name, "target": path, "lines": sorted(identifiers[name])}
            for name, path in sorted(found_symbols)
        ]
        for used in resource_links.get(file.path, ()):
            dependencies.update(resources.get(used, ()))
        for pattern in file.resource_lookup_patterns:
            if pattern.endswith("*"):
                for resource, declarations in resources.items():
                    if resource.startswith(pattern[:-1]):
                        dependencies.update(declarations)
            else:
                dependencies.update(resources.get(pattern, ()))
        file.resource_references = sorted(resource_links.get(file.path, ()))
        dependencies.update(build_paths[path] for path in file.gradle_projects if path in build_paths)
        dependencies.discard(file.path)
        file.depends_on = sorted(dependencies)
        for dependency in dependencies:
            reverse[dependency].add(file.path)
    for file in files:
        file.referenced_by = sorted(reverse[file.path])
        file.verified_by = [
            path for path in file.referenced_by
            if (source_set := by_path[path].source_set) == "test" or source_set.endswith("Test")
        ]
    return files


def write_reports(output, revision, files):
    output.mkdir(parents=True, exist_ok=True)
    indexed = [asdict(file) for file in files]
    (output / "file-index.json").write_text(
        json.dumps({"commit": revision, "paths": indexed}, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    root = {}
    for file in files:
        branch = root
        parts = PurePosixPath(file.path).parts
        for part in parts:
            branch = branch.setdefault(part, {})

    def tree(node, depth=0):
        for name, child in sorted(node.items()):
            yield f"{'  ' * depth}{name}{'/' if child else ''}"
            yield from tree(child, depth + 1)

    (output / "file-tree.txt").write_text("\n".join(tree(root)) + "\n", encoding="utf-8")
    groups = defaultdict(list)
    for file in files:
        groups[file.owner].append(file)
    module_pages = []
    for owner, members in groups.items():
        lines = [f"# {owner}", "", f"Source commit `{revision}`", ""]
        lines += ["Links record explicit imports, uniquely identifiable declarations in the same package or wildcard imports, resource identifiers, and generator inputs. Resource lookup patterns include possible consumers of dynamically named Android resources. Runtime behavior is recorded by tests and preview evidence.", ""]
        for file in members:
            url = f"https://github.com/NQTri00/HabiTree/blob/{revision}/{quote(file.path)}"
            lines += [f"## {PurePosixPath(file.path).name}", "", f"[`{file.path}`]({url})", ""]
            lines.append(f"{file.role}.")
            if file.source_set:
                lines.append(f"Source set `{file.source_set}`.")
            if file.kotlin_package:
                lines.append(f"Kotlin package `{file.kotlin_package}`.")
            if file.xml_root:
                lines.append(f"XML root `{file.xml_root}`, qualifier `{file.resource_qualifier or 'default'}`.")
            elif file.resource_qualifier:
                lines.append(f"Android resource qualifier `{file.resource_qualifier}`.")
            for name, values in (
                ("Resources", file.resources),
                ("Resource references", file.resource_references),
                ("Android resource lookup patterns", file.resource_lookup_patterns),
                ("Gradle projects", file.gradle_projects),
                ("Preview manifest scenarios", file.sample_of),
                ("Generator and inputs", file.generated_from),
                ("Source references", file.depends_on),
                ("Referenced by", file.referenced_by),
                ("Linked tests", file.verified_by),
            ):
                if values:
                    lines.append(f"**{name}** " + ", ".join(f"`{value}`" for value in values))
            if file.declarations:
                lines.extend(("", "**Declarations**", ""))
                for declaration in file.declarations:
                    label = f"`{declaration['kind']} {declaration['name']}`"
                    line = declaration.get("line")
                    location = f" at [line {line}]({url}#L{line})" if line else ""
                    lines.append(f"- {label}{location}")
            if file.symbol_references:
                lines.extend(("", "**Symbol occurrences**", ""))
                for reference in file.symbol_references:
                    target = reference["target"]
                    sites = ", ".join(f"[{line}]({url}#L{line})" for line in reference["lines"])
                    lines.append(f"- `{reference['symbol']}` → `{target}` at lines {sites}")
            lines.append("")
        name = re.sub(r"[^\w.-]+", "-", owner).strip("-") or "root"
        page = f"{name}.md"
        (output / page).write_text("\n".join(lines), encoding="utf-8")
        module_pages.append((owner, page, len(members)))

    overview = [
        "# HabiTree Android architecture",
        "",
        f"Source commit `{revision}`",
        "",
        f"{len(files):,} tracked paths across {len(module_pages)} owners.",
        "",
        "- [Directory tree](file-tree.txt)",
        "- [Machine-readable file index](file-index.json)",
        "- [Coverage verification](coverage.json)",
        "",
        "## File details by owner",
        "",
        "| Owner | Tracked paths |",
        "| --- | ---: |",
    ]
    overview += [f"| [{owner}]({quote(page)}) | {count:,} |" for owner, page, count in sorted(module_pages)]
    (output / "README.md").write_text("\n".join(overview) + "\n", encoding="utf-8")

    paths = {file.path for file in files}
    markdown_paths = Counter()
    pattern = re.compile(rf"(?m)^\[`([^`]+)`\]\(https://github\.com/NQTri00/HabiTree/blob/{revision}/([^\n]+)\)$")
    invalid_links = []
    for page in output.glob("*.md"):
        for label, target in pattern.findall(page.read_text(encoding="utf-8")):
            markdown_paths[label] += 1
            if label != unquote(target):
                invalid_links.append({"path": label, "target": target})
    missing = sorted(paths - markdown_paths.keys())
    extras = sorted(markdown_paths.keys() - paths)
    duplicates = sorted(path for path, count in markdown_paths.items() if count != 1)
    coverage = {
        "commit": revision,
        "tracked_paths": len(paths),
        "module_page_entries": sum(markdown_paths.values()),
        "extensions": dict(sorted(Counter(PurePosixPath(path).suffix or "(none)" for path in paths).items())),
        "missing_paths": missing,
        "extra_paths": extras,
        "duplicate_paths": duplicates,
        "invalid_links": invalid_links,
    }
    (output / "coverage.json").write_text(json.dumps(coverage, indent=2), encoding="utf-8")
    if missing or extras or duplicates or invalid_links:
        raise ValueError("Generated module pages do not cover the committed paths exactly")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("project", type=Path)
    parser.add_argument("revision")
    parser.add_argument("output", type=Path)
    parser.add_argument("--verify-index", action="store_true", help="Require the Git index to list exactly the same paths.")
    args = parser.parse_args()
    if args.output.exists() and any(args.output.iterdir()):
        raise ValueError("Use a new or empty hierarchy output directory")
    revision = git(args.project, "rev-parse", "--verify", args.revision + "^{commit}").decode().strip()
    files = index(args.project, revision)
    paths = [file.path for file in files]
    authoritative = git(args.project, "ls-tree", "-rz", "--name-only", revision).decode().rstrip("\x00").split("\x00")
    if len(paths) != len(set(paths)) or set(paths) != set(authoritative):
        raise ValueError("Generated path set differs from the committed tree")
    if args.verify_index:
        index_paths = git(args.project, "ls-files", "-z").decode().rstrip("\x00").split("\x00")
        if len(index_paths) != len(set(index_paths)) or set(paths) != set(index_paths):
            raise ValueError("Generated path set differs from git ls-files")
    write_reports(args.output, revision, files)
    print(f"{len(paths)} tracked paths, {len(set(file.owner for file in files))} owners, no missing or extra paths")


if __name__ == "__main__":
    main()
