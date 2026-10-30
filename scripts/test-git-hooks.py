#!/usr/bin/env python3
"""Verifica instalação e commits reais somente em repositórios descartáveis."""
import importlib.util
import os
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[1]


class GitHooksTest(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory(prefix="quality hook fixtures ")
        self.addCleanup(self.temporary.cleanup)
        self.repo = Path(self.temporary.name) / "repository with spaces"
        self.repo.mkdir()
        self.env = {key: value for key, value in os.environ.items() if not key.startswith("GIT_")}
        self.env.pop("JAVA_HOME_17_X64", None)
        self.env.pop("JAVA_HOME_21_X64", None)
        self.env.update(GIT_CONFIG_NOSYSTEM="1", GIT_CONFIG_GLOBAL=os.devnull)
        self.run_command("git", "init", "-q")
        self.run_command("git", "config", "user.name", "Hook Fixture")
        self.run_command("git", "config", "user.email", "fixture@example.invalid")
        self.run_command("git", "config", "commit.gpgsign", "false")
        for relative in ("scripts/git-hooks/pre-commit", "scripts/install-git-hooks.sh"):
            target = self.repo / relative
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(ROOT / relative, target)
        runner = ROOT / "scripts/validate-pre-commit.py"
        shutil.copy2(runner, self.repo / "scripts/validate-pre-commit.py")
        shutil.copy2(ROOT / "scripts/check-tracked-sources.py", self.repo / "scripts/check-tracked-sources.py")
        (self.repo / ".gitignore").write_text("invoked-task\n")
        self.write_gradlew('printf "%s\\n" "$*" > invoked-task\nexit 0\n')
        (self.repo / "tracked.txt").write_text("staged\n")
        self.run_command("git", "add", ".")

    def run_command(self, *args, cwd=None):
        return subprocess.run(args, cwd=cwd or self.repo, env=self.env,
                              text=True, capture_output=True, check=False)

    def write_gradlew(self, body):
        wrapper = self.repo / "gradlew"
        wrapper.write_text("#!/bin/sh\n" + body)
        wrapper.chmod(0o755)
        self.run_command("git", "add", "gradlew")

    def install(self):
        return self.run_command("sh", "scripts/install-git-hooks.sh")

    def test_install_is_reproducible(self):
        self.assertEqual(self.install().returncode, 0)
        hook = self.repo / ".git/hooks/pre-commit"
        contents, modified = hook.read_bytes(), hook.stat().st_mtime_ns
        self.assertTrue(os.access(hook, os.X_OK))
        self.assertEqual(self.install().returncode, 0)
        self.assertEqual((hook.read_bytes(), hook.stat().st_mtime_ns), (contents, modified))

    def test_existing_hook_is_preserved(self):
        hook = self.repo / ".git/hooks/pre-commit"
        hook.write_text("#!/bin/sh\nexit 17\n")
        before = hook.read_bytes(), hook.stat().st_mode
        self.assertNotEqual(self.install().returncode, 0)
        self.assertEqual((hook.read_bytes(), hook.stat().st_mode), before)

    def test_existing_dangling_symlink_is_preserved(self):
        hook = self.repo / ".git/hooks/pre-commit"
        hook.symlink_to("missing-user-hook")
        self.assertNotEqual(self.install().returncode, 0)
        self.assertEqual(os.readlink(hook), "missing-user-hook")

    def test_configured_hooks_path_is_preserved_even_when_empty(self):
        for value in ("custom hooks", ""):
            with self.subTest(value=value):
                self.run_command("git", "config", "core.hooksPath", value)
                self.assertNotEqual(self.install().returncode, 0)
                self.assertEqual(self.run_command("git", "config", "--get", "core.hooksPath").stdout.rstrip("\n"), value)
                self.assertFalse((self.repo / ".git/hooks/pre-commit").exists())

    def test_global_hooks_path_is_preserved(self):
        global_config = Path(self.temporary.name) / "gitconfig"
        global_config.write_text('[core]\n\thooksPath = "global hooks"\n')
        self.env["GIT_CONFIG_GLOBAL"] = str(global_config)
        self.assertNotEqual(self.install().returncode, 0)
        self.assertIn("global hooks", global_config.read_text())
        self.assertFalse((self.repo / ".git/hooks/pre-commit").exists())

    def test_install_and_hook_work_in_linked_worktree(self):
        initial = self.run_command("git", "commit", "-m", "Fixture root")
        self.assertEqual(initial.returncode, 0, initial.stderr)
        linked = Path(self.temporary.name) / "linked worktree"
        created = self.run_command("git", "worktree", "add", "-b", "linked-fixture", str(linked))
        self.assertEqual(created.returncode, 0, created.stderr)
        for relative in ("scripts/git-hooks/pre-commit", "scripts/install-git-hooks.sh", "scripts/validate-pre-commit.py", "scripts/check-tracked-sources.py", "gradlew"):
            target = linked / relative
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(self.repo / relative, target)
        result = self.run_command("sh", "scripts/install-git-hooks.sh", cwd=linked)
        self.assertEqual(result.returncode, 0, result.stderr)
        hook = self.repo / ".git/hooks/pre-commit"
        self.assertTrue(hook.is_file())
        result = self.run_command(str(hook), cwd=linked)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual((linked / "invoked-task").read_text(), "preCommitCheck --continue --console=plain\n")
        self.assertFalse((self.repo / "invoked-task").exists())

    def test_gate_passes_without_changing_worktree_or_staging(self):
        self.assertEqual(self.install().returncode, 0)
        index_before = self.run_command("git", "show", ":tracked.txt").stdout
        contents_before = (self.repo / "tracked.txt").read_bytes()
        nested = self.repo / "nested directory"
        nested.mkdir()
        result = self.run_command(str(self.repo / ".git/hooks/pre-commit"), cwd=nested)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual((self.repo / "invoked-task").read_text(), "preCommitCheck --continue --console=plain\n")
        self.assertEqual(self.run_command("git", "show", ":tracked.txt").stdout, index_before)
        self.assertEqual((self.repo / "tracked.txt").read_bytes(), contents_before)
        self.assertIn("COMMIT ALLOWED", result.stdout)

    def test_gate_preserves_exact_failure_status(self):
        self.write_gradlew('printf "%s\\n" "Ktlint: FAILED" >&2\nexit 42\n')
        result = self.run_command("sh", "scripts/git-hooks/pre-commit")
        self.assertEqual(result.returncode, 42)
        self.assertIn("Ktlint: FAILED", result.stderr)
        self.assertIn("COMMIT BLOCKED", result.stderr)
        self.assertNotIn("COMMIT ALLOWED", result.stdout)

    def test_git_commit_is_blocked_on_gate_failure(self):
        self.assertEqual(self.install().returncode, 0)
        self.write_gradlew("exit 42\n")
        result = self.run_command("git", "commit", "-m", "Must be blocked")
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("COMMIT BLOCKED", result.stderr)
        self.assertNotEqual(self.run_command("git", "rev-parse", "--verify", "HEAD").returncode, 0)

    def test_real_below_threshold_checker_blocks_git_commit(self):
        checker = ROOT / "scripts/verify-jacoco-coverage.py"
        self.assertTrue(checker.is_file(), "Coverage verifier must exist")
        shutil.copy2(checker, self.repo / "verify-jacoco-coverage.py")
        (self.repo / "jacoco.xml").write_text('<report><counter type="LINE" missed="7" covered="93"/></report>')
        self.run_command("git", "add", "verify-jacoco-coverage.py", "jacoco.xml")
        # Estes dados de teste substituem só o Gradle; o cálculo da cobertura usa o verificador real.
        import shlex
        self.write_gradlew(f'exec {shlex.quote(sys.executable)} verify-jacoco-coverage.py --aggregate jacoco.xml\n')
        self.assertEqual(self.install().returncode, 0)
        result = self.run_command("git", "commit", "-m", "93 percent must be blocked")
        self.assertNotEqual(result.returncode, 0)
        output = result.stdout + result.stderr
        self.assertIn("93.00%", output)
        self.assertIn("95.00%", output)
        self.assertIn("COMMIT BLOCKED", output)
        self.assertNotEqual(self.run_command("git", "rev-parse", "--verify", "HEAD").returncode, 0)

    def test_real_git_commit_can_succeed_after_gate_success(self):
        self.assertEqual(self.install().returncode, 0)
        result = self.run_command("git", "commit", "-m", "Fixture gate passes")
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn("COMMIT ALLOWED", result.stdout + result.stderr)
        self.assertEqual(self.run_command("git", "show", "HEAD:tracked.txt").stdout, "staged\n")
        self.assertEqual((self.repo / "tracked.txt").read_text(), "staged\n")

    def test_partial_staging_blocks_commit_before_gradle(self):
        self.assertEqual(self.install().returncode, 0)
        (self.repo / "tracked.txt").write_text("unstaged fix\n")
        before = self.run_command("git", "show", ":tracked.txt").stdout
        result = self.run_command("git", "commit", "-m", "Partial must be blocked")
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("COMMIT BLOCKED", result.stderr)
        self.assertFalse((self.repo / "invoked-task").exists())
        self.assertEqual(self.run_command("git", "show", ":tracked.txt").stdout, before)
        self.assertEqual((self.repo / "tracked.txt").read_text(), "unstaged fix\n")

    def test_untracked_input_blocks_commit(self):
        self.assertEqual(self.install().returncode, 0)
        (self.repo / "untracked.kt").write_text("invalid source\n")
        result = self.run_command("git", "commit", "-m", "Untracked must be blocked")
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("COMMIT BLOCKED", result.stderr)
        self.assertFalse((self.repo / "invoked-task").exists())

    def test_source_changed_during_gate_blocks_commit(self):
        self.write_gradlew('printf "changed\\n" > tracked.txt\nexit 0\n')
        self.assertEqual(self.install().returncode, 0)
        result = self.run_command("git", "commit", "-m", "Changed input must be blocked")
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("COMMIT BLOCKED", result.stderr)
        self.assertNotIn("COMMIT ALLOWED", result.stdout)

    def test_index_changed_during_gate_blocks_commit(self):
        self.write_gradlew('printf "changed\\n" > tracked.txt\ngit add tracked.txt\nexit 0\n')
        self.assertEqual(self.install().returncode, 0)
        result = self.run_command("git", "commit", "-m", "Changed index must be blocked")
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("COMMIT BLOCKED", result.stderr)
        self.assertNotIn("COMMIT ALLOWED", result.stdout)

    def test_runner_rejects_extra_arguments(self):
        result = self.run_command(sys.executable, "scripts/validate-pre-commit.py", "--skip-tests")
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("COMMIT BLOCKED", result.stderr)
        self.assertFalse((self.repo / "invoked-task").exists())

    def test_missing_wrapper_blocks_commit(self):
        self.assertEqual(self.install().returncode, 0)
        (self.repo / "gradlew").unlink()
        self.run_command("git", "add", "-u")
        result = self.run_command("git", "commit", "-m", "Missing tool must be blocked")
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("COMMIT BLOCKED", result.stderr)

    def test_signal_from_gate_blocks_commit(self):
        self.write_gradlew("kill -TERM $$\n")
        result = self.run_command("sh", "scripts/git-hooks/pre-commit")
        self.assertEqual(result.returncode, 143)
        self.assertIn("COMMIT BLOCKED", result.stderr)
        self.assertNotIn("COMMIT ALLOWED", result.stdout)

    def test_new_input_during_gate_blocks_commit(self):
        self.write_gradlew('printf "new\\n" > unexpected.kt\nexit 0\n')
        self.assertEqual(self.install().returncode, 0)
        result = self.run_command("git", "commit", "-m", "New input must be blocked")
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("COMMIT BLOCKED", result.stderr)
        self.assertNotIn("COMMIT ALLOWED", result.stdout)

    def test_ci_jdk21_is_forwarded_without_requiring_jdk17(self):
        self.env.update(JAVA_HOME_21_X64="/fixture/java21")
        result = self.run_command("sh", "scripts/git-hooks/pre-commit")
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual((self.repo / "invoked-task").read_text(), "preCommitCheck --continue --console=plain -Porg.gradle.java.installations.fromEnv=JAVA_HOME_21_X64\n")

    def test_legacy_jdk17_does_not_become_a_required_toolchain(self):
        self.env.update(JAVA_HOME_17_X64="/fixture/java17")
        result = self.run_command("sh", "scripts/git-hooks/pre-commit")
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual((self.repo / "invoked-task").read_text(), "preCommitCheck --continue --console=plain\n")

    def test_ignored_source_blocks_before_gradle_and_staged_source_passes(self):
        self.run_command("git", "config", "core.ignorecase", "true")
        with (self.repo / ".gitignore").open("a") as output:
            output.write("Icon\n")
        source = self.repo / "core/ds/src/main/java/icon/Hidden.kt"
        source.parent.mkdir(parents=True)
        source.write_text("object Hidden\n")
        (self.repo / "core/ds/build.gradle").write_text("// Fixture isolada\n")
        self.run_command("git", "add", ".")
        rejected = self.run_command("sh", "scripts/git-hooks/pre-commit")
        self.assertNotEqual(rejected.returncode, 0)
        self.assertIn("Fonte compilável fora do Git/index", rejected.stderr)
        self.assertFalse((self.repo / "invoked-task").exists())
        self.run_command("git", "add", "-f", str(source))
        accepted = self.run_command("sh", "scripts/git-hooks/pre-commit")
        self.assertEqual(accepted.returncode, 0, accepted.stderr)
        self.assertTrue((self.repo / "invoked-task").is_file())

    def test_missing_source_checker_blocks_runner_and_install(self):
        (self.repo / "scripts/check-tracked-sources.py").unlink()
        self.run_command("git", "add", "-u")
        self.assertNotEqual(self.install().returncode, 0)
        rejected = self.run_command("sh", "scripts/git-hooks/pre-commit")
        self.assertNotEqual(rejected.returncode, 0)
        self.assertIn("Verificador de fontes versionadas ausente", rejected.stderr)
        self.assertFalse((self.repo / "invoked-task").exists())

    def test_install_refuses_missing_runner(self):
        (self.repo / "scripts/validate-pre-commit.py").unlink()
        result = self.install()
        self.assertNotEqual(result.returncode, 0)
        self.assertFalse((self.repo / ".git/hooks/pre-commit").exists())



class GradleJavaEnvironmentTest(unittest.TestCase):
    def setUp(self):
        spec = importlib.util.spec_from_file_location("commit_runner", ROOT / "scripts/validate-pre-commit.py")
        self.runner = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(self.runner)
        self.temporary = tempfile.TemporaryDirectory(prefix="jdk selection fixtures ")
        self.addCleanup(self.temporary.cleanup)
        self.base = Path(self.temporary.name)
        self.studio = self.java_home("studio jbr", "21.0.10")

    def java_home(self, name, version):
        home = self.base / name
        home.mkdir()
        (home / "release").write_text(f'JAVA_VERSION="{version}"\n')
        return home

    def test_stale_java17_uses_verified_studio21_on_macos(self):
        old = self.java_home("old jdk", "17.0.18")
        original = {"JAVA_HOME": str(old), "PATH": "/fixture/bin"}
        actual = self.runner.gradle_environment(original, "darwin", self.studio)
        self.assertEqual(actual["JAVA_HOME"], str(self.studio))
        self.assertEqual(actual["PATH"], original["PATH"])
        self.assertEqual(original["JAVA_HOME"], str(old))

    def test_explicit_java21_is_preserved(self):
        chosen = self.java_home("chosen jdk", "21.0.9+10")
        original = {"JAVA_HOME": str(chosen)}
        self.assertEqual(self.runner.gradle_environment(original, "darwin", self.studio), original)

    def test_absent_java_home_uses_verified_studio21(self):
        self.assertEqual(self.runner.gradle_environment({}, "darwin", self.studio)["JAVA_HOME"], str(self.studio))

    def test_missing_or_wrong_studio_jdk_does_not_replace_existing_home(self):
        wrong = self.java_home("wrong studio", "17.0.18")
        original = {"JAVA_HOME": "/fixture/selected"}
        for candidate in (wrong, self.base / "missing"):
            with self.subTest(candidate=candidate):
                self.assertEqual(self.runner.gradle_environment(original, "darwin", candidate), original)

    def test_windows_and_linux_do_not_use_macos_studio(self):
        original = {"JAVA_HOME": "/fixture/selected"}
        for platform in ("win32", "linux"):
            with self.subTest(platform=platform):
                self.assertEqual(self.runner.gradle_environment(original, platform, self.studio), original)


if __name__ == "__main__":
    unittest.main(verbosity=2)
