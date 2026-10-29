"""Gera resumo fiel do output.xml; dry-run é identificado como validação offline."""
import argparse
from pathlib import Path
from defusedxml import ElementTree
from fpdf import FPDF

def ler_resultados(path):
    root=ElementTree.parse(path).getroot()
    cases=[]
    for test in root.findall('.//test'):
        status=test.find('status')
        if status is None or status.get('status') not in ('PASS','FAIL','SKIP'):
            raise ValueError('Caso sem status Robot reconhecido.')
        cases.append({'name':test.get('name',''), 'status':status.get('status')})
    if not cases:raise ValueError('output.xml não contém casos executados/validados.')
    metadata=root.findall('.//metadata/item')+root.findall('.//meta')
    offline=any('VALIDACAO_OFFLINE' in (m.text or '') for m in metadata)
    appium=any((m.text or '')=='EXECUCAO_APPIUM' for m in metadata)
    return {'cases':cases,'counts':{s:sum(c['status']==s for c in cases) for s in ('PASS','FAIL','SKIP')},'offline':offline,'appium':appium}

def gerar(xml_path, output):
    result=ler_resultados(xml_path)
    pdf=FPDF();pdf.set_auto_page_break(auto=True,margin=18);pdf.add_page()
    pdf.set_font('Helvetica','B',17);pdf.set_text_color(0,92,169)
    pdf.multi_cell(0,10,'SIMLO - Resumo de automacao',new_x='LMARGIN',new_y='NEXT')
    pdf.set_font('Helvetica',size=11);pdf.set_text_color(40,40,40)
    mode='VALIDACAO OFFLINE: nao executou no Android e nao certifica o APK.' if result['offline'] else ('Execucao Appium. Consulte os cenarios, logs e evidencias associados.' if result['appium'] else 'Modo nao identificado no XML; confirmar origem antes de usar como certificacao.')
    pdf.multi_cell(0,7,mode,new_x='LMARGIN',new_y='NEXT')
    pdf.multi_cell(0,7,' | '.join(f'{k}: {v}' for k,v in result['counts'].items()),new_x='LMARGIN',new_y='NEXT')
    pdf.ln(5)
    for case in result['cases']:
        text=f"{case['status']} - {case['name']}".encode('latin-1','replace').decode('latin-1')
        pdf.set_font('Helvetica',size=9)
        pdf.multi_cell(0,6,text,new_x='LMARGIN',new_y='NEXT')
    pdf.ln(5);pdf.multi_cell(0,6,'Este resumo conserva os status individuais; SKIP e dry-run nao equivalem a certificacao aprovada.',new_x='LMARGIN',new_y='NEXT')
    output=Path(output);output.parent.mkdir(parents=True,exist_ok=True);pdf.output(str(output))
    return result

def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('xml',type=Path);parser.add_argument('--output',type=Path,required=True)
    args=parser.parse_args()
    try:gerar(args.xml,args.output)
    except (OSError,ValueError) as error:parser.exit(1,f'Falha ao gerar relatório: {error}\n')
if __name__=='__main__':main()
