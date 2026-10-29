#!/usr/bin/env python3
"""Reject source aggregation and cycles between independently compiled modules."""
from pathlib import Path
import argparse
import os
import json
import re
import sys


def cycles(graph: dict[str, set[str]]) -> list[list[str]]:
    visited, active, found = set(), [], []

    def visit(node):
        if node in active:
            cycle = active[active.index(node):] + [node]
            if not any(set(cycle) == set(existing) for existing in found):
                found.append(cycle)
            return
        if node in visited:
            return
        active.append(node)
        for dependency in sorted(graph.get(node, set())):
            visit(dependency)
        active.pop()
        visited.add(node)

    for node in sorted(graph):
        visit(node)
    return found



def check_type_names(root: Path) -> list[str]:
    errors = []
    non_code = re.compile(r'"""[\s\S]*?"""|"(?:\\.|[^"\\])*"|/\*[\s\S]*?\*/|//[^\n]*')
    for module_root in ('app', 'core', 'feature'):
        for folder, directories, names in os.walk(root / module_root):
            directories[:] = [name for name in directories if name not in ('build', '.gradle', '.git')]
            for name in names:
                source = Path(folder) / name
                if source.suffix not in ('.kt', '.java') or 'src' not in source.relative_to(root).parts:
                    continue
                relative = source.relative_to(root)
                if 'legacy' in source.stem.lower():
                    errors.append(f'{relative}: Legacy source filename is forbidden; name the responsibility')
                text = source.read_text()
                if relative.parts[0] == 'feature':
                    expected = 'br.gov.caixa.loterias.feature.' + relative.parts[1].replace('-', '')
                    package = re.search(r'^package\s+([\w.]+)', text, re.M)
                    if package and package[1] != expected and not package[1].startswith(expected + '.'):
                        errors.append(f'{relative}: feature source must use its owning namespace {expected}')
                code = non_code.sub(lambda match: ' ' * len(match[0]), text)
                declarations = re.findall(r'\b(?:enum\s+class|class|object|interface|typealias|enum|record)\s+(`[^`]+`|[A-Za-z_]\w*)', code)
                for annotation in re.finditer(r'@file:\s*(?:kotlin\.jvm\.)?JvmName\s*\(', code):
                    argument = re.match(r'\s*"([^"\n]+)"', text[annotation.end():])
                    if argument:
                        declarations.append(argument[1])
                for declared in declarations:
                    if 'legacy' in declared.lower():
                        errors.append(f'{relative}: Legacy type name {declared} is forbidden; name the responsibility')
    return errors


def check(root: Path, configured: dict | None = None) -> list[str]:
    errors, graph = check_type_names(root), {}
    for folder in ('core', 'feature', 'app'):
        for build in sorted((root / folder).rglob('build.gradle*')):
            if 'build' in build.relative_to(root).parts:
                continue
            text = build.read_text()
            module = ':' + ':'.join(build.parent.relative_to(root).parts)
            graph[module] = set(re.findall(
                r'(?m)^\s*(?:api|implementation|compileOnly)\s*\(?\s*project\s*\(\s*[\'"](:[^\'"]+)', text,
            ))
            declared_dependencies = re.findall(
                r"(?m)^\s*(api|implementation|compileOnly|runtimeOnly|testImplementation|androidTestImplementation)\s*\(?\s*project\s*\(\s*['\"](:[^'\"]+)", text,
            )
            seen = set()
            for configuration, dependency in declared_dependencies:
                declaration = (configuration, dependency)
                if declaration in seen:
                    errors.append(f'{build.relative_to(root)}: duplicate dependency {configuration} {dependency}')
                seen.add(declaration)
            if ':app' in graph[module]:
                errors.append(f'{module}: production dependency on application is forbidden')
            if re.search(r'featureLayerRoots\s*\(|srcDirs\s*\([^\n]*(?:rootProject|\.\./)', text):
                errors.append(f'{build.relative_to(root)}: compiles sources outside its own module')
    if configured is not None:
        graph = {name: set(value['dependencies']) for name, value in configured['projects'].items()}
        for module, dependencies in graph.items():
            if ':app' in dependencies:
                errors.append(f'{module}: production dependency on application is forbidden')
            if module == ':core:domain' or (module.startswith(':feature:') and module.endswith((':domain', ':api'))):
                for dependency in dependencies:
                    if dependency not in (':core:common', ':core:domain') and not (
                            dependency.startswith(':feature:') and dependency.endswith((':domain', ':api'))):
                        errors.append(f'{module}: Domain dependency on outer layer {dependency}')
    for cycle in cycles(graph):
        errors.append('Module dependency cycle: ' + ' -> '.join(cycle))
    for feature in sorted((root / 'feature').iterdir()) if (root / 'feature').exists() else []:
        if not feature.is_dir():
            continue
        for owner in (feature, feature / 'presentation'):
            for legacy in ('domain', 'data', 'platform', 'host'):
                if any((owner / 'src' / legacy).rglob('*.kt')):
                    errors.append(f'{owner.relative_to(root)}/src/{legacy}: aggregated source root remains')
    return errors


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parent.parent)
    parser.add_argument('--graph', type=Path)
    args = parser.parse_args()
    errors = check(args.root, json.loads(args.graph.read_text()) if args.graph else None)
    if errors:
        print('\n'.join(errors), file=sys.stderr)
        sys.exit(1)
    print('Independent feature module ownership and acyclic dependencies verified.')
