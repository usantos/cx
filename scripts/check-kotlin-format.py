#!/usr/bin/env python3
"""Check the repository's .editorconfig whitespace contract without rewriting sources."""

import sys
from pathlib import Path


def violations(path):
    raw = path.read_bytes()
    try:
        source = raw.decode("utf-8")
    except UnicodeDecodeError:
        return ["UTF-8 required"]
    errors = []
    if raw.startswith(b"\xef\xbb\xbf"):
        errors.append("UTF-8 BOM")
    if "\r" in source:
        errors.append("LF required")
    if source and not source.endswith("\n"):
        errors.append("missing final newline")
    for number, line in enumerate(source.splitlines(), 1):
        if line != line.rstrip(" \t"):
            errors.append(f"line {number}: trailing whitespace")
        if "\t" in line[:len(line) - len(line.lstrip())]:
            errors.append(f"line {number}: tab indentation")
    return errors


def main():
    root = Path(__file__).resolve().parent.parent
    roots = [Path(arg) for arg in sys.argv[1:]] or [root / name for name in ("app", "core", "feature")]
    sources = sorted({path for folder in roots for path in folder.rglob("*.kt") if "build" not in path.relative_to(folder).parts})
    failures = [(path, errors) for path in sources if (errors := violations(path))]
    for path, errors in failures:
        print(f"{path}: {', '.join(errors)}", file=sys.stderr)
    print(f"Kotlin whitespace: {len(sources)} files, {len(failures)} violations")
    return bool(failures)


if __name__ == "__main__":
    sys.exit(main())
