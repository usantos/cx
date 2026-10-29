#!/bin/sh
# Instala somente quando isso não pode substituir um hook do usuário ou a configuração hooksPath.
set -eu

script_directory=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
repository_root=$(git -C "$script_directory" rev-parse --show-toplevel)
cd "$repository_root"

if git config --get core.hooksPath >/dev/null 2>&1; then
    printf '%s\n' 'Refusing installation: core.hooksPath is already configured. Integrate scripts/git-hooks/pre-commit into your existing hook manager explicitly.' >&2
    exit 1
fi

source_hook="$repository_root/scripts/git-hooks/pre-commit"
if [ ! -f "$source_hook" ] || [ ! -f "$repository_root/scripts/validate-pre-commit.py" ]; then
    printf '%s\n' 'Instalação bloqueada: hook ou runner de validação ausente. Nenhum hook foi alterado.' >&2
    exit 1
fi
hooks_directory=$(git rev-parse --git-path hooks)
mkdir -p "$hooks_directory"
target_hook="$hooks_directory/pre-commit"
if [ -e "$target_hook" ] || [ -L "$target_hook" ]; then
    if [ -f "$target_hook" ] && [ -x "$target_hook" ] && cmp -s "$source_hook" "$target_hook"; then
        printf '%s\n' 'Quality gate pre-commit hook is already installed.'
        exit 0
    fi
    printf '%s\n' 'Refusing installation: a pre-commit hook already exists. No hook was changed.' >&2
    exit 1
fi

# A publicação por link físico é atômica e recusa um destino criado simultaneamente.
temporary_hook=$(mktemp "$hooks_directory/.quality-pre-commit.XXXXXX")
trap 'rm -f "$temporary_hook"' EXIT HUP INT TERM
cat "$source_hook" > "$temporary_hook"
chmod 755 "$temporary_hook"
if ! ln "$temporary_hook" "$target_hook"; then
    printf '%s\n' 'Refusing installation: pre-commit appeared during installation. No existing hook was changed.' >&2
    exit 1
fi
printf '%s\n' 'Pre-commit completo instalado. Execute python3 scripts/validate-pre-commit.py após preparar o staging para verificar o ambiente.'
