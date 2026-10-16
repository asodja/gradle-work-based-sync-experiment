# usage: stable-table.py <dir with <cfg>-<i>.log>
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
    return dict(gradle=g, imp=(r["if"]-r["is"]).total_seconds(), ide=tot-g, total=tot)
runs={}
for p in sorted(glob.glob(os.path.join(sys.argv[1],"*.log"))):
    cfg=os.path.basename(p).rsplit("-",1)[0]; d=parse(p)
    if d: runs.setdefault(cfg,[]).append(d)
order=[c for c in ("stock","tasks","tasks-fixed") if c in runs]
print("| Configuration | runs | Gradle side (resolve stage) | IntelliJ side (rest of the import) | of which data import stage | whole import |")
print("|---|---|---|---|---|---|")
med={}
for c in order:
    rs=runs[c]; med[c]={k:statistics.median(d[k] for d in rs) for k in rs[0]}
    f=lambda k: " / ".join(f"{d[k]:.1f}" for d in rs)+f" → **{med[c][k]:.1f} s**"
    print(f"| {c} | {len(rs)} | {f('gradle')} | {f('ide')} | {f('imp')} | {f('total')} |")
if "stock" in med:
    print("\nSpeed-up vs stock (medians; % less time):\n")
    print("| Configuration | Gradle side | IntelliJ side | whole import |"); print("|---|---|---|---|")
    for c in order[1:]:
        p=lambda k: f"−{(1-med[c][k]/med['stock'][k])*100:.0f} % ({med['stock'][k]:.1f} → {med[c][k]:.1f} s)"
        print(f"| {c} | {p('gradle')} | {p('ide')} | {p('total')} |")
    if "tasks" in med and "tasks-fixed" in med:
        c,b="tasks-fixed","tasks"; p=lambda k: f"−{(1-med[c][k]/med[b][k])*100:.0f} %"
        print(f"| tasks-fixed vs tasks | {p('gradle')} | {p('ide')} | {p('total')} |")
