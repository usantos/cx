#!/usr/bin/env python3
"""Paired content metrics: unregistered local luminance SSIM7, MAE and exact RGB pixels."""
import argparse,json,pathlib
import numpy as np
from PIL import Image,ImageDraw
p=argparse.ArgumentParser();p.add_argument('root');p.add_argument('--normal',action='store_true');a=p.parse_args()
root=pathlib.Path(a.root);suffix='-normal' if a.normal else '';native=root/f'legacy{suffix}';compose=root/f'compose-after{suffix}';out=root/f'pairs{suffix}';out.mkdir(exist_ok=True)
def mean7(x):
 x=np.pad(x,3,mode='reflect');z=np.pad(x.cumsum(0).cumsum(1),((1,0),(1,0)));return (z[7:,7:]-z[:-7,7:]-z[7:,:-7]+z[:-7,:-7])/49

def ssim(x,y):
 x=np.array(x.convert('L'),dtype=np.float64);y=np.array(y.convert('L'),dtype=np.float64)
 ux,uy=mean7(x),mean7(y);vx=(mean7(x*x)-ux*ux)*49/48;vy=(mean7(y*y)-uy*uy)*49/48;vxy=(mean7(x*y)-ux*uy)*49/48
 score=((2*ux*uy+6.5025)*(2*vxy+58.5225))/((ux*ux+uy*uy+6.5025)*(vx+vy+58.5225))
 return float(score[3:-3,3:-3].mean())
metrics=[]
for state in ['filter-initial','filter-bottom','filter-quantity-dialog','filter-quantity-selected','filter-selection','pool-initial','pool-list','pool-expanded','pool-favorites','pool-modality']:
 npth=native/f'{state if state!="pool-modality" else "pool-modality-initial"}.png';cpth=compose/f'{state}.png'
 n=Image.open(npth).convert('RGB');c=Image.open(cpth).convert('RGB')
 assert n.size==c.size==(1280,2856),(npth,n.size,c.size)
 # Preserva as imagens originais; a comparação omite barras do sistema e a cor da barra de ferramentas alterada intencionalmente.
 top=273 if 'quantity-' in state else 324;box=(0,top,1280,2784);nc=n.crop(box);cc=c.crop(box)
 x=np.array(nc,dtype=np.float32);y=np.array(cc,dtype=np.float32)
 metrics.append(dict(state=state,crop=list(box),ssim7=ssim(nc,cc),mae=float(np.abs(x-y).mean()),exact_pixels=float(np.all(x==y,axis=2).mean())))
 pair=Image.new('RGB',(1200,1379),'#eeeeee');d=ImageDraw.Draw(pair);d.text((10,9),'Legado CX',fill='black');d.text((610,9),'Compose',fill='black')
 pair.paste(n.resize((600,1339)),(0,40));pair.paste(c.resize((600,1339)),(600,40));pair.save(out/f'{state}.png')
 diff=Image.fromarray(np.clip(np.abs(x-y)*4,0,255).astype('uint8'));diff.save(out/f'{state}-diff-x4.png')
 print(state,metrics[-1]['ssim7'],flush=True)
(root/f'paired-metrics{suffix}.json').write_text(json.dumps(metrics,indent=2))
