#!/usr/bin/env python3
"""Capture equivalent Compose fixture states on the dedicated parity AVD only."""
import argparse, datetime, hashlib, json, pathlib, re, subprocess, time, xml.etree.ElementTree as ET
p=argparse.ArgumentParser();p.add_argument('--out',required=True);p.add_argument('--serial',default='emulator-5558');p.add_argument('--screen',choices=['all','filter','pool'],default='all');args=p.parse_args()
if args.serial!='emulator-5558':raise SystemExit('Use the dedicated parity AVD (emulator-5558).')
out=pathlib.Path(args.out);out.mkdir(parents=True,exist_ok=True)
adb=str(pathlib.Path.home()/'Library/Android/sdk/platform-tools/adb')
def run(*cmd):return subprocess.check_output([adb,'-s',args.serial,*cmd])
def shell(*cmd):return run('shell',*map(str,cmd))
def dump():
 for _ in range(8):
  shell('rm','-f','/sdcard/parity-window.xml');shell('uiautomator','dump','/sdcard/parity-window.xml')
  try:return ET.fromstring(run('exec-out','cat','/sdcard/parity-window.xml'))
  except (ET.ParseError,subprocess.CalledProcessError):time.sleep(.3)
 raise RuntimeError('No fresh accessibility hierarchy')
def capture(name):
 time.sleep(.6);root=dump()
 if not any(n.get('package')=='br.gov.caixa.loterias.apostas' for n in root.iter('node')):raise RuntimeError(f'App not in foreground for {name}')
 (out/f'{name}.png').write_bytes(run('exec-out','screencap','-p'));ET.ElementTree(root).write(out/f'{name}.xml',encoding='utf-8')
 apk=shell('pm','path','br.gov.caixa.loterias.apostas').decode().strip().split('package:')[-1]
 metadata_file=out/'capture-metadata.json';metadata=json.loads(metadata_file.read_text()) if metadata_file.exists() else {}
 metadata[name]={'captured_at_utc':datetime.datetime.now(datetime.timezone.utc).isoformat(),'serial':args.serial,
  'installed_apk_sha256':shell('sha256sum',apk).decode().split()[0],
  'png_sha256':hashlib.sha256((out/f'{name}.png').read_bytes()).hexdigest(),
  'hierarchy_sha256':hashlib.sha256((out/f'{name}.xml').read_bytes()).hexdigest()}
 metadata_file.write_text(json.dumps(metadata,indent=2));print(name,flush=True)
def tap(label,attribute='text'):
 root=dump()
 nodes=[n for n in root.iter('node') if (label in n.get(attribute,'') if attribute=='content-desc' else n.get(attribute)==label)]
 if not nodes:raise RuntimeError(f'Missing {attribute}={label}')
 node=nodes[0];x1,y1,x2,y2=map(int,re.findall(r'\d+',node.get('bounds')))
 shell('input','tap',(x1+x2)//2,(y1+y2)//2);time.sleep(.4)
def launch(route):
 shell('am','force-stop','br.gov.caixa.loterias.apostas')
 shell('am','start','-W','-n','br.gov.caixa.loterias.apostas/.MainActivity','--es','visual_test_route',route,'--ez','visual_test_authenticated','true');time.sleep(2)
def bottom():
 for _ in range(3):shell('input','swipe',600,2200,600,450,250)
 time.sleep(.4)
if args.screen in ['all','filter']:
 launch('marketplace/filter');tap('mega sena');capture('filter-initial');bottom();capture('filter-bottom')
 tap('Quantidade de Dezenas');capture('filter-quantity-dialog');tap('06');capture('filter-quantity-selected');tap('Confirmar');bottom();capture('filter-selection')
if args.screen in ['all','pool']:
 launch('betting/pools');capture('pool-initial');bottom();capture('pool-list');tap('Ver mais','content-desc');bottom();capture('pool-expanded')
 tap('Lotéricas Favoritas');capture('pool-favorites')
 launch('betting/pools');tap('Filtrar bolões','content-desc');tap('mega sena');bottom();tap('Aplicar');capture('pool-modality')
