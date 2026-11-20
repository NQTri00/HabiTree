"""Render tracked Compose preview jobs on the GitHub Actions host."""

import argparse
from collections import Counter
import json
from pathlib import Path
import re
import subprocess


MANIFESTS = (
    "docs/bio-parity/preview-jobs.json",
    "docs/localization/preview-jobs-en.json",
    "docs/localization/preview-jobs-vi.json",
    "docs/bio-parity/component-preview-jobs.json",
)

def read_render_report(report: Path) -> dict:
    text = report.read_text(encoding="utf-8")
    payload = re.search(r'(?m)^\{\s*"success"\s*:', text)
    if not payload:
        raise ValueError(f"Android CLI did not emit a preview result in {report}")
    data, _ = json.JSONDecoder().raw_decode(text, payload.start())
    return data


def preview_methods(root: Path) -> dict[str, int]:
    methods = {}
    for relative_path in MANIFESTS:
        manifest = root / relative_path
        for job in json.loads(manifest.read_text(encoding="utf-8-sig")):
            source = root / job["source"]
            code = source.read_text(encoding="utf-8-sig")
            package = re.search(r"(?m)^\s*package\s+([\w.]+)", code)
            jvm_name = re.search(r'(?m)^\s*@file:JvmName\("([^"]+)"\)', code)
            function = job["preview"]
            declaration = re.search(
                rf"((?:@Preview\([^\n]*\)\s*)+)@Composable\s+(?:private\s+)?fun\s+{re.escape(function)}\s*\(",
                code,
            )
            if not package or not declaration:
                raise ValueError(f"Cannot resolve {function} in {source}")
            facade = jvm_name.group(1) if jvm_name else source.stem + "Kt"
            fqn = f"{package.group(1)}.{facade}.{function}"
            if fqn in methods:
                raise ValueError(f"Duplicate preview method {fqn}")
            methods[fqn] = declaration.group(1).count("@Preview(")
    return methods


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--cli", type=Path, required=True)
    parser.add_argument("--sdk", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    root = Path(__file__).resolve().parents[2]
    output = args.output.resolve()
    output.mkdir(parents=True, exist_ok=False)
    wanted = preview_methods(root)
    command = [
        str(args.cli.resolve()), "preview", "render", f"--sdk={args.sdk.resolve()}",
        f"gradle://{root.as_posix()}?:app", f"--output={output / 'png'}", "--format=json",
        *(f"--fqn={method}" for method in wanted),
        "--", "--no-daemon", "--max-workers=2",
    ]
    with (output / "render.json").open("w", encoding="utf-8") as report:
        subprocess.run(command, cwd=root, stdout=report, check=True)
    data = read_render_report(output / "render.json")
    present = Counter()
    seen_images = set()
    for result in data["results"]:
        path = result.get("imagePath")
        if not path:
            continue
        image = Path(path).resolve()
        if image.is_file() and image.stat().st_size and image not in seen_images:
            present[result["methodFqn"]] += 1
            seen_images.add(image)
    expected = Counter(wanted)
    if not data.get("success") or present != expected:
        raise ValueError(f"Compose preview coverage differs. Missing: {expected - present}. Unexpected: {present - expected}.")
    print(f"Rendered all {sum(present.values())} Compose previews from {len(wanted)} methods.")


if __name__ == "__main__":
    main()
