"""Verifica seleção conservadora do gate incremental a partir do index."""
import importlib.util
from pathlib import Path
import unittest

SPEC = importlib.util.spec_from_file_location("commit_runner", Path(__file__).with_name("validate-pre-commit.py"))
RUNNER = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(RUNNER)


class IncrementalPrecommitTest(unittest.TestCase):
    def test_only_module_files_are_incremental(self):
        roots = ["feature/account/domain", "app", "core/design-system", "sample/designsystem"]
        self.assertTrue(RUNNER.incremental_candidate(["feature/account/domain/src/main/A.kt", "app/build.gradle"], roots))
        self.assertTrue(RUNNER.incremental_candidate(["core/design-system/src/main/icon/Removed.kt"], roots))
        self.assertTrue(RUNNER.incremental_candidate(["sample/designsystem/README.md"], roots))

    def test_shared_unknown_and_empty_changes_require_full_gate(self):
        for path in ["settings.gradle", "gradle/libs.versions.toml", "scripts/tool.py", "config/detekt/detekt.yml", ".github/workflows/build.yml", "docs/README.md", "app-other/src/A.kt"]:
            with self.subTest(path=path):
                self.assertFalse(RUNNER.incremental_candidate(["app/src/A.kt", path], ["app"]))
        self.assertFalse(RUNNER.incremental_candidate([], ["app"]))
