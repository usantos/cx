"""Executa Robot de modo portável e mantém o código de saída da suíte."""
import argparse
import datetime
from pathlib import Path
import subprocess
import sys
ROOT = Path(__file__).resolve().parents[1]

def main(argv=None):
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--dry-run',action='store_true',help='Confere sintaxe e keywords sem dispositivo; não certifica o APK.')
    parser.add_argument('--test',help='Nome exato ou padrão de caso Robot.')
    parser.add_argument('--suite',choices=['login','resultados'],help='Executa uma funcionalidade; padrão: todas.')
    parser.add_argument('--outputdir',type=Path)
    args=parser.parse_args(argv)
    folder=args.outputdir or ROOT/'results'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S-%f')
    command=[sys.executable,'-m','robot','--language','pt-BR','--exitonfailure','--outputdir',str(folder.resolve()),'--loglevel','INFO']
    if args.dry_run:command+=['--dryrun','--metadata','Modo:VALIDACAO_OFFLINE_SEM_APARELHO']
    else:command+=['--metadata','Modo:EXECUCAO_APPIUM']
    if args.test:command+=['--test',args.test]
    command.append(str(ROOT/'Testes'/f'SUTA_{args.suite}.robot') if args.suite else str(ROOT/'Testes'))
    return subprocess.run(command,cwd=ROOT).returncode

if __name__=='__main__':sys.exit(main())
