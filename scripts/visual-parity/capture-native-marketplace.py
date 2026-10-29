#!/usr/bin/env python3
"""Run the separate test driver against the unmodified installed CX oracle APK."""
import argparse,datetime,hashlib,json,pathlib,subprocess
p=argparse.ArgumentParser();p.add_argument('--out',required=True);p.add_argument('--october',choices=['true','false'],default='true');p.add_argument('--serial',default='emulator-5558');p.add_argument('--screen',choices=['all','filter','pool'],default='all');a=p.parse_args()
if a.serial!='emulator-5558':raise SystemExit('Use the dedicated parity AVD (emulator-5558).')
root=pathlib.Path(__file__).resolve().parents[2];out=pathlib.Path(a.out);out.mkdir(parents=True,exist_ok=True)
adb=[str(pathlib.Path.home()/'Library/Android/sdk/platform-tools/adb'),'-s',a.serial]
subprocess.run([*adb,'install','-r','--no-incremental',str(root/'build/verification/marketplace-paired-20261015/native-runner/runner.apk')],check=True)
for screen,modality in [('filter','false'),('pool','false'),('pool','true')]:
 if a.screen!='all' and screen!=a.screen:continue
 label='pool-modality' if modality=='true' else screen
 result=subprocess.check_output([*adb,'shell','am','instrument','-w','-e','screen',screen,'-e','october',a.october,'-e','modality',modality,'br.gov.caixa.parity.marketplace/br.gov.caixa.parity.LegacyMarketplaceInstrumentation'])
 (out/f'{label}-instrumentation.log').write_bytes(result)
 if b'INSTRUMENTATION_CODE: -1' not in result:raise RuntimeError(result.decode())
 states=['initial','bottom','quantity-dialog','quantity-selected','selection'] if screen=='filter' else ['initial','list','expanded','favorites']
 for state in states:
  for ext in ['png','json']:
   prefix=f'legacy-{screen}-'+('october' if a.october=='true' else 'normal')
   data=subprocess.check_output([*adb,'exec-out','run-as','br.gov.caixa.loterias.apostas.cxv2validation','cat',f'files/{prefix}-{state}.{ext}'])
   (out/f'{label}-{state}.{ext}').write_bytes(data)
 metadata_file=out/'capture-metadata.json';metadata=json.loads(metadata_file.read_text()) if metadata_file.exists() else {}
 apk=subprocess.check_output([*adb,'shell','pm','path','br.gov.caixa.loterias.apostas.cxv2validation']).decode().strip().split('package:')[-1]
 metadata[label]={'captured_at_utc':datetime.datetime.now(datetime.timezone.utc).isoformat(),'serial':a.serial,
  'installed_oracle_apk_sha256':subprocess.check_output([*adb,'shell','sha256sum',apk]).decode().split()[0],
  'driver_apk_sha256':hashlib.sha256((root/'build/verification/marketplace-paired-20261015/native-runner/runner.apk').read_bytes()).hexdigest(),
  'fixture_sha256':hashlib.sha256((root/'core/network/src/main/resources/marketplace-parity-fixture.json').read_bytes()).hexdigest()}
 metadata_file.write_text(json.dumps(metadata,indent=2));print(label,flush=True)
