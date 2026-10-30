#!/usr/bin/env python3
"""Bloqueia commits sem validar integralmente o conteúdo preparado no index."""
import os
from pathlib import Path
import re
import subprocess
import sys


ROOT = Path(__file__).resolve().parents[1]


def git(*arguments):
    result = subprocess.run(["git", *arguments], cwd=ROOT, capture_output=True)
    if result.returncode:
        raise RuntimeError(result.stderr.decode(errors="replace").strip() or "Falha ao verificar o estado Git.")
    return result.stdout


def prepared_tree():
    dirty = subprocess.run(["git", "diff", "--no-ext-diff", "--quiet", "--ignore-submodules=none"], cwd=ROOT)
    if dirty.returncode:
        raise RuntimeError("Existem alterações fora do staging ou não foi possível verificar o workspace. Prepare o conteúdo completo antes do commit.")
    if git("ls-files", "--others", "--exclude-standard", "-z"):
        raise RuntimeError("Existem arquivos não rastreados fora do staging. Prepare ou ignore explicitamente os arquivos antes do commit.")
    return git("write-tree")


def is_java21(home):
    if not home:
        return False
    try:
        for line in (Path(home) / "release").read_text().splitlines():
            if line.startswith("JAVA_VERSION="):
                return re.match(r'^"21(?:[.+-]|"$)', line.partition("=")[2]) is not None
    except (OSError, UnicodeError):
        return False
    return False


def gradle_environment(original, platform, studio_java):
    environment = original.copy()
    if platform == "darwin" and not is_java21(environment.get("JAVA_HOME")) and is_java21(studio_java):
        environment["JAVA_HOME"] = str(studio_java)
    return environment


def main():
    try:
        if len(sys.argv) != 1:
            raise RuntimeError("A validação completa não aceita argumentos, exclusões ou opções de atalho.")
        tree = prepared_tree()
        studio_java = Path("/Applications/Android Studio.app/Contents/jbr/Contents/Home")
        environment = gradle_environment(os.environ, sys.platform, studio_java)
        if environment.get("JAVA_HOME") != os.environ.get("JAVA_HOME"):
            print("Gradle: usando o JBR 21 do Android Studio no lugar do JAVA_HOME herdado.", flush=True)
        command = [str(ROOT / "gradlew"), "preCommitCheck", "--continue", "--console=plain"]
        if environment.get("JAVA_HOME_21_X64"):
            command.append("-Porg.gradle.java.installations.fromEnv=JAVA_HOME_21_X64")
        print("Validando arquitetura, Ktlint, Detekt, Android Lint, testes, cobertura >95%, contratos e APK PRD Debug.", flush=True)
        result = subprocess.run(command, cwd=ROOT, env=environment)
        if result.returncode:
            print("COMMIT BLOCKED: uma ou mais validações falharam. Consulte as etapas e os relatórios acima.", file=sys.stderr)
            return result.returncode if result.returncode > 0 else 128 - result.returncode
        if prepared_tree() != tree:
            raise RuntimeError("O index foi alterado durante a validação. Execute o commit novamente sobre o conteúdo final.")
        print("PRE-COMMIT PASSED\nCOMMIT ALLOWED", flush=True)
        return 0
    except (OSError, RuntimeError) as error:
        print(f"COMMIT BLOCKED: {error}", file=sys.stderr)
        return 1
    except KeyboardInterrupt:
        print("COMMIT BLOCKED: validação interrompida.", file=sys.stderr)
        return 130


if __name__ == "__main__":
    sys.exit(main())
