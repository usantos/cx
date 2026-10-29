import re,json,pathlib
ROOT=pathlib.Path(__file__).resolve().parents[2]
def balanced(s,start,opening='(',closing=')'):
 d=0;quote=False;esc=False
 for i in range(start,len(s)):
  c=s[i]
  if c=='"' and not esc:quote=not quote
  if not quote:
   if c==opening:d+=1
   if c==closing:
    d-=1
    if d==0:return i
  esc=(c=='\\' and not esc)
 return len(s)-1
apis={}
for p in sorted(list(ROOT.glob('feature/**/src/main/**/*.kt'))+list(ROOT.glob('core/network/src/main/**/*.kt'))):
 s=p.read_text()
 for m in re.finditer(r'@(GET|POST|PUT|PATCH|DELETE|HEAD)\("([^"\n]+)"\)',s):
  n=re.search(r'(?:suspend\s+)?fun\s+(\w+)\s*\(',s[m.end():])
  if not n:continue
  st=m.end()+n.end()-1; end=balanced(s,st)
  tail=s[end+1:].split('\n',1)[0].strip();ret=tail.lstrip(': ').split('=')[0].strip()
  op={'method':m[1],'path':m[2],'function':n[1], 'parameters':s[st+1:end].strip(),'return':ret,'source':str(p.relative_to(ROOT))}
  key=(m[1],m[2]); old=apis.get(key)
  op['variants']=(old.get('variants',[{k:old[k] for k in ['function','parameters','return','source']}]) if old else [])+[{k:op[k] for k in ['function','parameters','return','source']}]
  op['origins']=list(dict.fromkeys((old.get('origins',[old['source']]) if old else [])+[op['source']]))
  if old is None or (old['source'].startswith('core/') and op['source'].startswith('feature/')):apis[key]=op
  elif old:
   old['origins']=op['origins'];old['variants']=op['variants']
baseline=[tuple(x.split('\t')) for x in (ROOT/'docs/LEGACY_BACKEND_BASELINE.tsv').read_text().splitlines()[1:] if x]
missing=[x for x in baseline if x not in apis]
# Esquemas de modelo são extraídos de declarações de DTO independentes de transporte/domínio, nunca de configurações locais.
models={}
for p in sorted(list(ROOT.glob('feature/**/transport/src/main/**/*.kt'))+list(ROOT.glob('core/network/src/main/**/model/*.kt'))+list(ROOT.glob('core/network/src/main/**/api/*.kt'))):
 s=p.read_text()
 for m in re.finditer(r'(?:data\s+)?class\s+(\w+)(?:<[^>]+>)?\s*\(',s):
  end=balanced(s,m.end()-1);body=s[m.end():end]
  fields=[]
  for f in re.finditer(r'(?:@SerializedName\(\s*(?:value\s*=\s*)?"([^"]+)"[^)]*\)\s*)?(?:override\s+)?val\s+(\w+)\s*:\s*([^=\n]+?)(?=\s*=|,\s*\n|\n|$)',body):
   fields.append({'name':f[1] or f[2], 'type':f[3].strip().rstrip(',')})
  models[m[1]]={'fields':fields,'source':str(p.relative_to(ROOT))}
def sample(t,seen=()):
 t=t.strip().rstrip('?')
 if t.startswith('Response<'):return sample(t[9:-1],seen)
 if t.startswith('ApiEnvelope<'):return {'payload':sample(t[12:-1],seen),'redirect':None,'mensagem':None,'dataHoraServidor':'2026-10-22T12:00:00'}
 if t.startswith(('List<','Set<','Collection<')):return [sample(t[t.index('<')+1:-1],seen)]
 if t.startswith('Map<'):return {'chave':'valor demonstrativo'}
 if t in ('String','Char'):return 'exemplo'
 if t in ('Int','Long','Short','Double','Float','BigDecimal','Number'):return 1
 if t=='Boolean':return False
 if t=='Unit':return None
 if t=='JsonArray':return []
 if t in ('JsonElement','JsonObject'):return {'_contrato_aberto':'consultar o mapper indicado; exemplo nao executavel'}
 if t in ('ResponseBody','ByteArray'):return '<conteudo binario>'
 if '<' in t and t.split('<')[0] in models:
  base=t.split('<')[0];inner=t[t.index('<')+1:-1]
  if base in seen:return '<recursive>'
  return {x['name']:sample(x['type'].replace('T',inner) if x['type'] in ('T','T?') else x['type'],seen+(base,)) for x in models[base]['fields']}
 if t in models and t not in seen and len(seen)<5:return {x['name']:sample(x['type'],seen+(t,)) for x in models[t]['fields']}
 return '<'+t+'>'
for op in apis.values():
 b=re.search(r'@Body\s+(?:\w+)\s*:\s*([\w<>?, ]+)',op['parameters']);op['bodyType']= b[1].strip().rstrip(',') if b else None
 op['requestExample']=sample(op['bodyType']) if b else None
 op['responseExample']=sample(op['return'])
 op['pathParams']=re.findall(r'@Path\("([^"]+)"\)',op['parameters'])
 op['queryParams']=re.findall(r'@Query\("([^"]+)"\)',op['parameters'])
 op['headers']=re.findall(r'@Header\("([^"]+)"\)',op['parameters'])
 op['queryMap']='@QueryMap' in op['parameters']
result={'operations':sorted(apis.values(),key=lambda x:(x['path'],x['method'])),'baselineCount':len(baseline),'missingBaseline':missing,'models':models}
(ROOT/'docs/technical-guide/api-contracts.json').write_text(json.dumps(result,ensure_ascii=False,indent=2))
print('Operations',len(apis),'baseline',len(baseline),'missing',missing,'DTOs',len(models))
