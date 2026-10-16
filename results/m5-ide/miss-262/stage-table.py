# usage: stage-table.py <dir> <cfg:glob> ...  e.g. stock:d27c/stock-*.log
import sys,re,glob,os,datetime,statistics
def ts(s): return datetime.datetime.strptime(s,"%Y-%m-%d %H:%M:%S,%f")
def parse(path):
    r={}
    for l in open(path,errors="replace"):
        if "ConsoleLog" not in l or "STDOUT" in l: continue
        m=re.match(r"(\S+ \S+) \[.*?\] +INFO - #c\.i\.w\.u\.ConsoleLog - (.*)",l)
        if not m: continue
        t,msg=ts(m.group(1)),m.group(2)
        if "Gradle resolve stage started" in msg: r.setdefault("rs",t)
        elif "Gradle resolve stage finished" in msg: r["rf"]=t
        elif "Gradle data import stage started" in msg: r["is"]=t
        elif "Gradle data import stage finished" in msg: r["if"]=t
        elif "Gradle Project' started" in msg: r.setdefault("ts",t)
        elif "Gradle Project' ended in" in msg: r["te"]=t
    if not all(k in r for k in ("rs","rf","is","if","ts","te")): return None
    g=(r["rf"]-r["rs"]).total_seconds(); tot=(r["te"]-r["ts"]).total_seconds()
    return dict(gradle=g, ide=tot-g, imp=(r["if"]-r["is"]).total_seconds(), total=tot)
def fmt(v): return f"{int(v//60)} m {v%60:.1f} s" if v >= 60 else f"{v:.1f} s"
rows=[]
for spec in sys.argv[1:]:
    cfg,pat=spec.split(":",1); rs=[parse(p) for p in sorted(glob.glob(pat))]; rs=[r for r in rs if r]
    med={k:statistics.median(d[k] for d in rs) for k in ("gradle","ide","imp","total")}; rows.append((cfg,rs,med))
print("| Configuration | Gradle side | IntelliJ side | of which data import stage | whole import |"); print("|---|---|---|---|---|")
for cfg,rs,med in rows:
    print(f"| {cfg} | " + " | ".join(" / ".join(f"{d[k]:.1f}" for d in rs) + f" → **{fmt(med[k])}**" for k in ("gradle","ide","imp","total")) + " |")
base=rows[0][2]
for cfg,rs,med in rows[1:]:
    print(f"| speed-up {cfg} vs {rows[0][0]} | " + " | ".join(f"−{100*(1-med[k]/base[k]):.0f} %" for k in ("gradle","ide","imp","total")) + " |")
