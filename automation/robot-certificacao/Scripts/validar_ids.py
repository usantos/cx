"""Confere o contrato de IDs entregue e, opcionalmente, suas declarações no app."""
import argparse
from collections import Counter
import json
from pathlib import Path
import re
import sys
ROOT=Path(__file__).resolve().parents[1]

def validar(pacote=ROOT, android_root=None):
    errors=[]
    try:
        items=json.loads((pacote/'TestData/android-ids.json').read_text(encoding='utf-8'))
        locators=json.loads((pacote/'TestData/locators.json').read_text(encoding='utf-8'))
    except (OSError,ValueError):
        return ['Inventário de IDs ou localizadores ausente/inválido.']
    counts=Counter(x['id'] for x in items)
    for name,count in counts.items():
        if count>1 and name!='simlo.dialog.positivo':
            errors.append(name+': ID duplicado em declarações distintas.')
    ids=set(counts)
    legacy_file=pacote/'TestData/legacy-android-ids.json'
    if legacy_file.exists():
        ids.update(x['id'] for x in json.loads(legacy_file.read_text(encoding='utf-8')) if x.get('id'))
    for name,value in locators.items():
        if not isinstance(value,str) or not value.startswith('id=') or value[3:] not in ids:
            errors.append(f'{name}: localizador deve usar id= e existir no inventário Android.')
    for item in items:
        if not re.fullmatch(r'simlo\.[a-z0-9_.-]+', item['id']):
            errors.append('Identificador fora do padrão simlo.<área>.<elemento>.')
        path=Path(item['source'])
        if path.is_absolute() or '..' in path.parts:
            errors.append('Caminho de fonte não pode sair do projeto.')
            continue
        if android_root:
            source=android_root/path
            text=source.read_text(encoding='utf-8') if source.is_file() else ''
            key=item.get('key')
            present=(re.search(r'\.uiId\s*\(\s*"'+re.escape(key)+r'"\s*\)',text) is not None and item['id']=='simlo.'+key) if key else f'"{item["id"]}"' in text
            if not present:
                errors.append(f'{item["id"]}: declaração do app ausente; atualize código, inventário e Pages juntos.')
    if android_root:
        # Detecta IDs novos que não entraram no pacote entregue à certificação.
        declared=Counter()
        for base in ['feature','core/design-system','app']:
            for source in (android_root/base).glob('*/presentation/src/main/**/*.kt' if base=='feature' else 'src/main/**/*.kt'):
                text=source.read_text(encoding='utf-8')
                if source.name != 'AutomationIdentity.kt':
                    for call in re.finditer(r'\.(automationId|uiId)\s*\(',text):
                        pattern=r'\s*"(simlo\.[a-z0-9_.-]+)"' if call[1]=='automationId' else r'\s*"([a-z][a-z0-9_-]*\.\d{4,5})"'
                        if not re.match(pattern,text[call.end():]):
                            errors.append(str(source.relative_to(android_root))+': ID novo deve usar literal estável do catálogo, sem dados de usuário.')
                declared.update(re.findall(r'\.automationId\s*\(\s*"(simlo\.[a-z0-9_.-]+)"\s*\)',text))
                declared.update('simlo.'+key for key in re.findall(r'\.uiId\s*\(\s*"([a-z][a-z0-9_-]*\.\d{4,5})"\s*\)',text))
        for missing in sorted(set(declared)-ids):errors.append(f'{missing}: ID novo não incluído no inventário da certificação.')
        for name,count in counts.items():
            if declared[name]!=count:
                errors.append(name+': quantidade de declarações no app diverge do catálogo; verifique duplicação/remoção.')
        helper=android_root/'core/design-system/src/main/java/br/gov/caixa/loterias/core/designsystem/automation/AutomationIdentity.kt'
        if not helper.exists() or 'testTagsAsResourceId = true' not in helper.read_text():
            errors.append('A exportação dos IDs para UiAutomator não está habilitada.')
    return sorted(set(errors))

def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--android-root',type=Path)
    args=parser.parse_args()
    errors=validar(android_root=args.android_root)
    if errors:
        print('\n'.join(errors),file=sys.stderr);return 1
    print('Contrato de IDs e localizadores validado. Isso não substitui inspeção da árvore nativa no APK.')
    return 0
if __name__=='__main__':sys.exit(main())
