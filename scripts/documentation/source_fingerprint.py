from pathlib import Path
import hashlib,json,os,sys
root=Path.cwd();files={}
for directory in ['app','core','feature','sample']:
 for folder,dirs,names in os.walk(root/directory):
  dirs[:]=[x for x in dirs if x not in ('build','.gradle','.git')]
  for name in names:
   path=Path(folder)/name;rel=path.relative_to(root)
   if 'src' in rel.parts or name in ('build.gradle','build.gradle.kts'):
    files[str(rel)]=hashlib.sha256(path.read_bytes()).hexdigest()
for name in ['build.gradle','settings.gradle','gradle/libs.versions.toml','gradle/aggregate-test-reports.gradle','gradle/quality-gate.gradle','gradle/compose-platform-resources.gradle','config/ktlint/baseline.xml','config/detekt/baseline.xml']:
 files[name]=hashlib.sha256((root/name).read_bytes()).hexdigest()
encoded=json.dumps(files,sort_keys=True).encode();out={'sourceSha256':hashlib.sha256(encoded).hexdigest(),'files':files}
Path(sys.argv[1]).write_text(json.dumps(out,indent=2));print(out['sourceSha256'],len(files))
