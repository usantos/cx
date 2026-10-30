#!/usr/bin/env python3
"""Recusa fontes locais ausentes do index e colisões de nomes incompatíveis com Windows."""
import argparse
import os
from pathlib import Path
import subprocess


def discover_source_roots(root):
    roots = []
    excluded = {'.git', '.gradle', '.kotlin', '.idea', '.local', 'build', 'node_modules', '.venv', '__pycache__'}
    for directory, children, files in os.walk(root):
        children[:] = [name for name in children if name not in excluded]
        if 'build.gradle' in files or 'build.gradle.kts' in files:
            source = Path(directory) / 'src'
            if source.is_dir():
                roots.append(source.relative_to(root).as_posix())
                children[:] = [name for name in children if name != 'src']
    return sorted(roots)


def source_problems(root, source_roots):
    root = Path(root).resolve()
    result = subprocess.run(['git', '-C', str(root), 'ls-files', '--cached', '-z'],
                            capture_output=True, check=True)
    tracked = set(result.stdout.decode('utf-8').split('\0')) - {''}
    problems, roots, actual_names = [], [], {}
    folded = {name.casefold(): name for name in tracked}
    for value in source_roots:
        source = (root / value).resolve()
        if not source.is_relative_to(root):
            raise ValueError('Raiz de fontes fora do repositório')
        roots.append(source)
        if not source.is_dir():
            continue
        for file in source.rglob('*'):
            if file.is_file() and file.suffix in ('.kt', '.java'):
                name = file.relative_to(root).as_posix()
                indexed = name in tracked
                if not indexed and name.casefold() in folded:
                    try:
                        indexed = file.samefile(root / folded[name.casefold()])
                    except OSError:
                        indexed = False
                if not indexed:
                    problems.append(f'Fonte compilável fora do Git/index: {name}')
                previous = actual_names.setdefault(name.casefold(), name)
                if previous != name:
                    problems.append(f'Colisão de fontes no workspace: {previous} / {name}')
    names = {}
    for name in sorted(tracked):
        path = root / name
        if path.suffix not in ('.kt', '.java') or not any(path.as_posix().casefold().startswith(source.as_posix().casefold().rstrip('/') + '/') for source in roots):
            continue
        previous = names.setdefault(name.casefold(), name)
        if previous != name:
            problems.append(f'Colisão de nomes no Windows: {previous} / {name}')
    return sorted(set(problems))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--root', type=Path, required=True)
    parser.add_argument('--source-root', action='append')
    args = parser.parse_args()
    try:
        problems = source_problems(args.root, args.source_root or discover_source_roots(args.root.resolve()))
    except (OSError, ValueError, subprocess.CalledProcessError) as failure:
        parser.exit(1, f'Não foi possível verificar as fontes versionadas: {failure}\n')
    if problems:
        parser.exit(1, '\n'.join(problems) + '\n')
    print('Fontes Kotlin/Java versionadas e ausência de colisões de maiúsculas verificadas.')


if __name__ == '__main__':
    main()
