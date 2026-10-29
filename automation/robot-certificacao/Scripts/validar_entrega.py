"""Valida a entrega sem telefone, farm, credenciais ou configuração local."""
from pathlib import Path
import subprocess
import sys
import tempfile
ROOT=Path(__file__).resolve().parents[1]

def main():
    for command in [
        [sys.executable,'-m','pip','check'],
        [sys.executable,'Scripts/validar_ids.py'],
        [sys.executable,'-m','unittest','discover','-s','tests','-v'],
    ]:
        code=subprocess.run(command,cwd=ROOT).returncode
        if code:return code
    with tempfile.TemporaryDirectory(prefix='simlo-robot-dryrun-') as folder:
        return subprocess.run([sys.executable,'Scripts/executar.py','--dry-run','--outputdir',folder],cwd=ROOT).returncode
if __name__=='__main__':sys.exit(main())
