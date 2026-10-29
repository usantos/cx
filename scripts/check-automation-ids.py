#!/usr/bin/env python3
"""Confere o contrato Robot e IDs explícitos nos controles Compose básicos."""
import importlib.util
from pathlib import Path
import re
import sys
ROOT=Path(__file__).resolve().parents[1]
PROTECTED={
    'core/design-system/src/main/java/br/gov/caixa/loterias/core/designsystem/components/CaixaDSScreenComponents.kt',
    'feature/cart/presentation/src/main/java/br/gov/caixa/loterias/feature/cart/presentation/components/CurrentCartScaffold.kt',
}
TOKEN=re.compile(r'"""[\s\S]*?"""|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'|/\*[\s\S]*?\*/|//[^\n]*')
CONTROLS=re.compile(r'\b(?:CaixaDS)?(?:Button|TextButton|OutlinedButton|IconButton|Checkbox|RadioButton|TextField|BasicTextField|Switch|DropdownMenuItem|Tab)\s*\(')

def mask(source):
    return TOKEN.sub(lambda m:re.sub(r'[^\n]',' ' if m[0].startswith(('//','/*')) else 'x',m[0]),source)

def controle_sem_id(source):
    code=mask(source);missing=[]
    for call in CONTROLS.finditer(code):
        if re.search(r'\bfun\s*$',code[max(0,call.start()-20):call.start()]):continue
        if code[max(0,call.start()-15):call.start()].endswith('android.widget.'):continue
        depth=1;index=call.end();start=index;args=[]
        while index<len(code) and depth:
            c=code[index]
            if c in '([{':depth+=1
            elif c in ')]}':depth-=1
            if (c==',' and depth==1) or depth==0:args.append(code[start:index]);start=index+1
            index+=1
        modifiers=[a for a in args if re.match(r'\s*modifier\s*=',a)]
        if not modifiers:modifiers=[a for a in args if any(tag in a for tag in ('.automationId(', '.legacyAutomationId(', '.uiId('))]
        if not any(any(tag in a for tag in ('.automationId(', '.legacyAutomationId(', '.uiId(')) for a in modifiers):
            missing.append(source.count('\n',0,call.start())+1)
    return missing

def check(root=ROOT):
    errors=[]
    package=root/'automation/robot-certificacao'
    module_path=package/'Scripts/validar_ids.py'
    if not module_path.exists():return ['Pacote Robot ou validador ausente.']
    spec=importlib.util.spec_from_file_location('simlo_robot_ids',module_path)
    mod=importlib.util.module_from_spec(spec);spec.loader.exec_module(mod)
    errors+=mod.validar(package,root)
    sources=list((root/'feature').glob('*/presentation/src/main/**/*.kt'))+list((root/'core/design-system').glob('src/main/**/*.kt'))
    for p in sources:
        rel=p.relative_to(root).as_posix()
        if rel in PROTECTED:continue
        source=p.read_text(encoding='utf-8')
        if '@Composable' not in source:continue
        for line in controle_sem_id(source):errors.append(f'{rel}:{line}: controle sem automationId explícito no Modifier.')
    return errors

if __name__=='__main__':
    errors=check()
    if errors:print('\n'.join(errors),file=sys.stderr)
    else:print('AUTOMATION ID CONTRACT PASSED: IDs, localizadores Robot e controles Compose básicos conferidos.')
    sys.exit(bool(errors))
