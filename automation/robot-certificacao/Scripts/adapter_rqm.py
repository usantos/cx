"""Executa Robot e prepara anexos locais; não publica nem autentica no RQM."""
import datetime
import json
import os
from pathlib import Path
import subprocess
import sys
import uuid
ROOT=Path(__file__).resolve().parents[1]

def main():
    folder=ROOT/'results'/('rqm-'+datetime.datetime.now().strftime('%Y%m%d-%H%M%S')+'-'+uuid.uuid4().hex[:8])
    code=subprocess.run([sys.executable,str(ROOT/'Scripts/executar.py'),'--outputdir',str(folder)],cwd=ROOT).returncode
    xml=folder/'output.xml'
    if xml.exists():
        result=subprocess.run([sys.executable,str(ROOT/'Scripts/gerar_relatorio_pdf.py'),str(xml),'--output',str(folder/'relatorio.pdf')],cwd=ROOT).returncode
        if code==0:code=result
    files=[str(p.resolve()) for p in folder.iterdir() if p.suffix in ('.xml','.html','.pdf','.png')] if folder.exists() else []
    if folder.exists():
        (folder/'anexos.json').write_text(json.dumps({'robotExitCode':code,'attachments':files},ensure_ascii=False,indent=2),encoding='utf-8')
    # O formato abaixo é uma lista UTF-8, um caminho por linha. O adapter RQM da equipe
    # deve confirmar esse contrato antes de habilitar qm_AttachmentsFile na integração.
    destination=os.environ.get('qm_AttachmentsFile')
    if destination:
        try:Path(destination).write_text('\n'.join(files)+'\n',encoding='utf-8')
        except OSError:
            print('Não foi possível preparar a lista de anexos RQM.',file=sys.stderr)
            if code==0:code=1
    return code
if __name__=='__main__':sys.exit(main())
