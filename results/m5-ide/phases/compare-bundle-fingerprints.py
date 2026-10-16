# usage: compare-bundle-fingerprints.py <before dir> <project root>  — per model fingerprints of every bundle
import struct,glob,os,sys,collections
def read(path):
    b=open(path,"rb").read(); off=4; out={}
    cnt=struct.unpack(">i",b[off:off+4])[0]; off+=4
    for _ in range(cnt):
        vals=[]
        for _ in range(3):
            l=struct.unpack(">H",b[off:off+2])[0]; off+=2; vals.append(b[off:off+l].decode("utf-8","replace")); off+=l
        length,dl=struct.unpack(">ii",b[off:off+8]); off+=8; out[vals[0]]=(vals[1],vals[2],length,dl); off+=dl
    return out
before_root,root=sys.argv[1],sys.argv[2]
same=diff=missing=extra=0; diffs=collections.Counter(); sizes=[0,0]
for p in glob.glob(os.path.join(before_root,"**/build/ide/models/ideProjectModel.bin"),recursive=True):
    rel=os.path.relpath(p,before_root); now=os.path.join(root,rel)
    if not os.path.exists(now): missing+=1; continue
    a,b=read(p),read(now)
    for k,v in a.items():
        if k not in b: diffs["missing "+k.split(".")[-1]]+=1; continue
        if v[1]==b[k][1]: same+=1
        else: diff+=1; diffs[k.split(".")[-1]]+=1
        sizes[0]+=v[3]; sizes[1]+=b[k][3]
    extra+=len(set(b)-set(a))
print(f"payloads with the same fingerprint: {same}, different: {diff}, missing bundles: {missing}, extra payloads: {extra}")
print("differences by model:", diffs.most_common(10))
print(f"deflated bytes before {sizes[0]/1048576:.1f} MB, now {sizes[1]/1048576:.1f} MB")
