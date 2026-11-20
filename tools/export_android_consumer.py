"""Export an Android app or library checkout whose external HabiTree dependencies resolve from Maven."""

import argparse
import json
from pathlib import Path
import re
import subprocess
import zipfile


GROUP = "com.mamxanh.habitree.android"
VERSION_ENTRY = re.compile(r'^\s*"(:[a-z-]+)" to "([^"]+)",$', re.MULTILINE)
PROJECT_DEPENDENCY = re.compile(r'project\("(:[a-z:-]+)"\)')


def export(source: Path, ref: str, output: Path, repository: Path, module: str = "app") -> None:
    staging_archive = output.with_name(output.name + ".zip")
    if output.exists() or staging_archive.exists():
        raise FileExistsError(f"Use a new export directory: {output}")
    if output.is_relative_to(source):
        raise ValueError("Export to a directory outside the source checkout.")

    revision = subprocess.check_output(
        ["git", "rev-parse", "--verify", ref + "^{commit}"], cwd=source, text=True,
    ).strip()
    def read_source(path: str) -> str:
        return subprocess.check_output(
            ["git", "show", f"{revision}:{path}"], cwd=source,
        ).decode("utf-8")

    original = read_source("build.gradle.kts")
    publication = original[original.index("val androidArtifactVersions"):original.index("val localPublicationRepository")]
    versions = dict(VERSION_ENTRY.findall(publication))
    settings_text = read_source("settings.gradle.kts")
    feature_names = set(re.findall(r'include\("(:[a-z-]+):domain"', settings_text))
    owner = ":" + module
    if module != "app" and owner not in versions:
        raise ValueError(f"Unknown Android module: {module}")

    directory = "data" if module == "core-storage" else module
    local_projects = {owner}
    build_files = [f"{directory}/build.gradle.kts"]
    if owner in feature_names:
        local_projects.update((owner + ":domain", owner + ":data"))
        build_files.extend(f"{directory}/{layer}/build.gradle.kts" for layer in ("domain", "data"))
    extra_inputs = ["mics/tree growth.gif"] if module == "core-ui" else []

    output.parent.mkdir(parents=True, exist_ok=True)
    try:
        subprocess.run(
            ["git", "archive", "--format=zip", f"--output={staging_archive}", revision,
             directory, "gradle", "gradlew", "gradlew.bat", "gradle.properties",
             "settings.gradle.kts", "build.gradle.kts", *extra_inputs],
            cwd=source, check=True,
        )
        with zipfile.ZipFile(staging_archive) as archive:
            archive.extractall(output)
    finally:
        staging_archive.unlink(missing_ok=True)

    root_build = output / "build.gradle.kts"
    if module == "app":
        marker = "// Each feature ships three artifacts"
        root_build.write_text(
            original[original.index("plugins {"):original.index(marker)].rstrip() + "\n", encoding="utf-8",
        )
    else:
        marker = "/** Enforce dependency direction"
        root_build.write_text(original[:original.index(marker)].rstrip() + "\n", encoding="utf-8")

    settings = output / "settings.gradle.kts"
    repo_uri = json.dumps(repository.as_uri())
    prefix, section, remainder = settings_text.partition("dependencyResolutionManagement {")
    if not section:
        raise ValueError("The source is missing centralized dependency repositories.")
    remainder = remainder.replace(
        "repositories { google(); mavenCentral() }",
        f"repositories {{ google(); mavenCentral(); maven {{ url = uri({repo_uri}) }} }}",
        1,
    )
    text = prefix + section + remainder
    start = text.index("rootProject.name")
    end = text.index("\n", start)
    includes = ", ".join(json.dumps(path) for path in sorted(local_projects))
    overrides = '\nproject(":core-storage").projectDir = file("data")' if module == "core-storage" else ""
    settings.write_text(text[:end] + f"\ninclude({includes}){overrides}\n", encoding="utf-8")

    def coordinate(match: re.Match[str]) -> str:
        path = match.group(1)
        if path in local_projects:
            return match.group(0)
        dependency_owner = ":" + path.split(":")[1]
        artifact = path.removeprefix(":").replace(":", "-")
        if path in feature_names:
            artifact += "-presentation"
        return f'"{GROUP}:{artifact}:{versions[dependency_owner]}"'

    count = 0
    for relative_path in build_files:
        build_file = output / relative_path
        text = build_file.read_text(encoding="utf-8")
        rewritten, replacements = PROJECT_DEPENDENCY.subn(coordinate, text)
        unresolved = set(PROJECT_DEPENDENCY.findall(rewritten)) - local_projects
        if unresolved or "project(" in PROJECT_DEPENDENCY.sub("", rewritten):
            raise ValueError(f"Unresolved project dependencies in {relative_path}: {sorted(unresolved)}")
        count += replacements - len(PROJECT_DEPENDENCY.findall(rewritten))
        build_file.write_text(rewritten, encoding="utf-8")
    (output / "source-revision.txt").write_text(revision + "\n", encoding="utf-8")
    print(f"Exported {revision} with {count} pinned library dependencies to {output}")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source", type=Path, default=Path(__file__).resolve().parents[1])
    parser.add_argument("--ref", default="HEAD")
    parser.add_argument("--module", default="app", help="App, feature, or core project name")
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--repository", type=Path, required=True)
    args = parser.parse_args()
    export(args.source.resolve(), args.ref, args.output.resolve(), args.repository.resolve(), args.module)


if __name__ == "__main__":
    main()
