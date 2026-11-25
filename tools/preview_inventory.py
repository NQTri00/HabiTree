"""Inventory runnable preview scenarios against the Android route and popup declarations."""

import argparse
import csv
import json
from pathlib import Path
import re


MANIFESTS = {
    "default": "docs/bio-parity/preview-jobs.json",
    "en": "docs/localization/preview-jobs-en.json",
    "vi": "docs/localization/preview-jobs-vi.json",
}
BIO = Path("app/src/main/java/com/mamxanh/app/presentation/bio")
STRING = r'"([^"]*)"'
FIXTURE = re.compile(r'BioReferenceSurface\s*\(([^\n]*)')
ARGUMENTS = ("route", "route_argument", "popup", "popup_argument", "tabs")
PARAMETERS = dict(zip(("route", "param", "modal", "id", "tab"), ARGUMENTS))


def inventory(root: Path) -> list[dict[str, str]]:
    rows = []
    for batch, manifest in MANIFESTS.items():
        jobs = json.loads((root / manifest).read_text(encoding="utf-8-sig"))
        names = set()
        owners = set()
        for job in jobs:
            source = root / job["source"]
            code = source.read_text(encoding="utf-8-sig")
            function = job["preview"]
            declaration = re.search(rf"\bfun\s+{re.escape(function)}\s*\(", code)
            if declaration is None:
                raise ValueError(f"{batch}: missing {function} in {source}")
            prefix = code[:declaration.start()]
            annotations = list(re.finditer(r"@Preview\(([^\n]*)\)", prefix))
            if not annotations:
                raise ValueError(f"{batch}: missing @Preview for {function}")
            annotation = annotations[-1].group(1)
            owner = (job["source"], function)
            if job["name"] in names or owner in owners:
                raise ValueError(f"{batch}: duplicate scenario {job['name']} or {owner}")
            names.add(job["name"])
            owners.add(owner)
            remaining = code[declaration.end():]
            next_declaration = re.search(r"\bfun\s+\w+\s*\(", remaining)
            if next_declaration:
                remaining = remaining[:next_declaration.start()]
            fixture = FIXTURE.search(remaining)
            row = dict.fromkeys(ARGUMENTS, "")
            if fixture:
                text = fixture.group(1)
                for index, argument in enumerate(text.split(",", maxsplit=len(ARGUMENTS))):
                    match = re.match(r'\s*(?:(\w+)\s*=\s*)?' + STRING, argument)
                    if match is None or index == len(ARGUMENTS):
                        break
                    parameter, value = match.groups()
                    key = PARAMETERS.get(parameter) if parameter else ARGUMENTS[index]
                    if key is not None:
                        row[key] = value
            values = dict(re.findall(r'(\w+)\s*=\s*("[^"]*"|[\d.]+f?)', annotation))
            row.update(
                batch=batch, name=job["name"], source=job["source"], function=function,
                preview_locale=values.get("locale", "").strip('"'),
                width_dp=values.get("widthDp", ""), height_dp=values.get("heightDp", ""),
                font_scale=values.get("fontScale", "1").removesuffix("f"),
                device=values.get("device", "").strip('"'),
                fixture=fixture.group(0).rstrip(" }") if fixture else "",
                web_reference=job.get("web") or "",
            )
            rows.append(row)
    return rows


def popup_triggers(root: Path, popup_ids: set[str]) -> dict[str, str]:
    triggers = {popup: [] for popup in popup_ids}
    for source in sorted((root / BIO).glob("*.kt")):
        if source.name == "BioPreviewFixture.kt":
            continue
        code = source.read_text(encoding="utf-8")
        matches = (
            (r'\bshow\(\s*("([a-z-]+)")', 1),
            (r'^\s*((?:"[a-z-]+"\s*,?\s*)+)\s*->\s*show\(\s*name\s*,\s*id', 1),
            (r'val modal = if \(event\.sell\) ("sell" else "remove")', 1),
        )
        for pattern, group in matches:
            for match in re.finditer(pattern, code, re.MULTILINE):
                number = code.count("\n", 0, match.start(group)) + 1
                for popup in set(re.findall(STRING, match.group(group))) & popup_ids:
                    triggers[popup].append(f"{source.relative_to(root).as_posix()}:{number}")
    return {popup: " | ".join(paths) for popup, paths in triggers.items()}


def coverage(root: Path, rows: list[dict[str, str]]) -> None:
    destinations = (root / BIO / "BioDestination.kt").read_text(encoding="utf-8")
    route_ids = set(re.findall(r'^\s+[A-Z_]+\("' + r'([a-z-]+)"\)[,;]', destinations, re.MULTILINE))
    modal_source = (root / BIO / "BioModal.kt").read_text(encoding="utf-8")
    title = re.search(r"val title\s*=\s*when\s*\(type\)\s*\{(.*?)\belse\s*->", modal_source, re.DOTALL)
    if title is None:
        raise ValueError("Missing popup title declaration in BioModal.kt")
    popup_ids = set(re.findall(r'^\s*' + STRING + r'\s*->', title.group(1), re.MULTILINE))
    triggers = popup_triggers(root, popup_ids)
    missing_triggers = sorted(popup for popup, locations in triggers.items() if not locations)
    if missing_triggers:
        raise ValueError(f"Missing source trigger locations: {missing_triggers}")
    for row in rows:
        row["popup_triggers"] = triggers.get(row["popup"], "")
    for batch in MANIFESTS:
        selected = [row for row in rows if row["batch"] == batch]
        routes = {row["route"] for row in selected} - {""}
        popups = {row["popup"] for row in selected} - {""}
        missing_routes = route_ids - routes
        missing_popups = popup_ids - popups
        extra = (routes - route_ids) | (popups - popup_ids)
        if missing_routes or missing_popups or extra:
            raise ValueError(
                f"{batch}: missing routes {sorted(missing_routes)}, "
                f"missing popups {sorted(missing_popups)}, unknown IDs {sorted(extra)}",
            )
        print(f"{batch}: {len(selected)} scenarios, {len(routes)} routes, {len(popups)} popups")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source", type=Path, default=Path(__file__).resolve().parents[1])
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    rows = inventory(args.source)
    coverage(args.source, rows)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    with args.output.open("w", encoding="utf-8", newline="") as report:
        writer = csv.DictWriter(report, fieldnames=list(rows[0]))
        writer.writeheader()
        writer.writerows(rows)


if __name__ == "__main__":
    main()
