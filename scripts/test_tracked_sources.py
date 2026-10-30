"""Regressões com Git real em fixtures isoladas para fontes ocultas por ignore e capitalização."""
import importlib.util
import os
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[1]
SPEC = importlib.util.spec_from_file_location('tracked_sources', ROOT / 'scripts/check-tracked-sources.py')
CHECK = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(CHECK)


class TrackedSourcesTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix='source tracking ')
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.env = {k: v for k, v in os.environ.items() if not k.startswith('GIT_')}
        self.env.update(GIT_CONFIG_GLOBAL=os.devnull, GIT_CONFIG_NOSYSTEM='1')
        environment = patch.dict(os.environ, self.env, clear=True)
        environment.start()
        self.addCleanup(environment.stop)
        self.git('init', '-q')
        self.source = self.root / 'core/ds/src/main/java/icon'
        self.source.mkdir(parents=True)
        self.file = self.source / 'CaixaDSIcons.kt'
        self.file.write_text('object CaixaDSIcons\n')

    def git(self, *args):
        return subprocess.run(['git', '-C', str(self.root), *args], check=True, capture_output=True, env=self.env)

    def problems(self):
        return CHECK.source_problems(self.root, ['core/ds/src'])

    def test_staged_sources_pass_without_requiring_commit(self):
        self.git('add', '.')
        self.assertEqual(self.problems(), [])

    def test_untracked_source_is_rejected(self):
        self.assertIn('CaixaDSIcons.kt', '\n'.join(self.problems()))

    def test_ignored_icon_package_is_rejected(self):
        self.git('config', 'core.ignorecase', 'true')
        (self.root / '.gitignore').write_text('Icon\n')
        self.git('add', '.')
        self.assertIn('CaixaDSIcons.kt', '\n'.join(self.problems()))

    def test_repository_ignore_preserves_icon_sources_and_ignores_finder_metadata(self):
        (self.root / '.gitignore').write_bytes((ROOT / '.gitignore').read_bytes())
        for case in ('true', 'false'):
            with self.subTest(ignorecase=case):
                self.git('config', 'core.ignorecase', case)
                source = subprocess.run(['git', '-C', str(self.root), 'check-ignore', '--no-index', str(self.file)], capture_output=True, env=self.env)
                self.assertEqual(source.returncode, 1, source.stdout)
                finder = subprocess.run(['git', '-C', str(self.root), 'check-ignore', '--no-index', '--stdin', '-z'],
                                        input=b'Icon\r\0', capture_output=True, env=self.env)
                self.assertEqual(finder.returncode, 0, finder.stderr)

    def test_case_colliding_index_sources_are_rejected(self):
        self.git('add', '.')
        blob = self.git('hash-object', '-w', str(self.file)).stdout.decode().strip()
        self.git('update-index', '--add', '--cacheinfo', f'100644,{blob},core/ds/src/main/java/icon/caixadsicons.kt')
        self.assertIn('Colisão de nomes no Windows', '\n'.join(self.problems()))

    def test_case_only_alias_is_accepted_only_when_it_is_the_same_filesystem_file(self):
        self.git('add', '.')
        original = self.file.relative_to(self.root).as_posix()
        renamed = original.replace('CaixaDSIcons.kt', 'caixadsicons.kt')
        blob = self.git('hash-object', '-w', str(self.file)).stdout.decode().strip()
        self.git('update-index', '--force-remove', original)
        self.git('update-index', '--add', '--cacheinfo', f'100644,{blob},{renamed}')
        alias = self.root / renamed
        if alias.exists() and alias.samefile(self.file):
            self.assertEqual(self.problems(), [])
        else:
            self.assertIn('Fonte compilável fora do Git/index', '\n'.join(self.problems()))

    def test_generated_outputs_outside_source_roots_are_not_treated_as_manual_sources(self):
        self.git('add', '.')
        generated = self.root / 'core/ds/build/generated/generated.kt'
        generated.parent.mkdir(parents=True)
        generated.write_text('object Generated\n')
        self.assertEqual(self.problems(), [])

    def test_discovery_ignores_build_outputs_and_finds_gradle_modules(self):
        (self.root / 'core/ds/build.gradle').write_text('')
        generated = self.root / 'core/ds/build/fixture/src'
        generated.mkdir(parents=True)
        (generated.parent / 'build.gradle').write_text('')
        self.assertEqual(CHECK.discover_source_roots(self.root), ['core/ds/src'])

    def test_source_root_outside_checkout_is_rejected(self):
        with self.assertRaisesRegex(ValueError, 'fora do repositório'):
            CHECK.source_problems(self.root, ['../outside'])


if __name__ == '__main__':
    unittest.main(verbosity=2)
