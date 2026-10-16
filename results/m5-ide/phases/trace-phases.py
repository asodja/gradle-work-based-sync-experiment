# usage: trace-phases.py <trace-log.txt> [min ms] — top-level build operations and their biggest children
import sys,json,collections
path=sys.argv[1]; minms=int(sys.argv[2]) if len(sys.argv)>2 else 200
starts={}; spans={}
with open(path,encoding="utf-8") as f:
    for line in f:
        line=line.strip()
        if not line: continue
        r=json.loads(line)
        if "startTime" in r: starts[r["id"]]=r
        elif "endTime" in r and r["id"] in starts:
            s=starts.pop(r["id"]); spans[r["id"]]=dict(name=s["displayName"],start=s["startTime"],end=r["endTime"],parent=s.get("parentId"),dur=r["endTime"]-s["startTime"])
children=collections.defaultdict(list)
for i,s in spans.items(): children[s["parent"]].append(i)
t0=min(s["start"] for s in spans.values()); t1=max(s["end"] for s in spans.values())
print(f"overall span {t1-t0} ms, {len(spans)} operations")
def show(pid,depth,maxdepth):
    kids=sorted(children.get(pid,[]),key=lambda i: spans[i]["start"])
    for i in kids:
        s=spans[i]
        if s["dur"]<minms: continue
        print(f"{'  '*depth}{s['dur']:7d} ms  +{s['start']-t0:6d}  {s['name'][:110]}")
        if depth<maxdepth: show(i,depth+1,maxdepth)
show(None,0,2)
print("\n== every operation whose name mentions the task graph, task execution or the configuration cache (any duration)")
KEYS=("task graph","Run tasks","Run main tasks","configuration cache","Calculate","Resolve mutations","whenReady","Execute tasks","Load build","Configure build","Notify")
for i,s in sorted(spans.items(), key=lambda kv: kv[1]["start"]):
    if any(k in s["name"] for k in KEYS) and (s["dur"]>=20 or s["parent"] in children.get(None,[])):
        d=0; p=s["parent"]
        while p in spans: d+=1; p=spans[p]["parent"]
        print(f"{s['dur']:7d} ms  +{s['start']-t0:6d}  depth {d}  {s['name'][:100]}")
print("\n== direct children of the root operation (all, >= 50 ms)")
for top in children.get(None,[]):
    for i in sorted(children.get(top,[]),key=lambda i: spans[i]["start"]):
        s=spans[i]
        if s["dur"]>=50: print(f"{s['dur']:7d} ms  +{s['start']-t0:6d}  {s['name'][:100]}")
# aggregate: siblings grouped by name prefix under each depth-1 op, for the parallel/sequential question
print("\n== children of the task graph / configuration ops, grouped by name prefix (sum ms, count, wall of group)")
import re
for i,s in spans.items():
    if any(k in s["name"] for k in ("task graph","Calculate","Configure build","Run main tasks","Load build")) and s["parent"] in children.get(None,[])+[None] or s["parent"] is None and s["dur"]>=minms:
        pass
for top in sorted(children.get(None,[]),key=lambda i: spans[i]["start"]):
    for i in sorted(children.get(top,[]),key=lambda i: spans[i]["start"]):
        s=spans[i]
        if s["dur"]<minms or not children.get(i): continue
        g=collections.defaultdict(lambda:[0,0,10**18,0])
        for c in children[i]:
            cs=spans[c]; key=re.sub(r"[:'][^ ]*|\(.*?\)|\d+","*",cs["name"])[:60]; e=g[key]; e[0]+=cs["dur"]; e[1]+=1; e[2]=min(e[2],cs["start"]); e[3]=max(e[3],cs["end"])
        rows=sorted(g.items(),key=lambda kv:-kv[1][0])[:6]
        if rows and rows[0][1][0]>=minms:
            print(f"\n-- {s['name'][:90]} ({s['dur']} ms)")
            for k,(sm,n,a,b) in rows: print(f"   {sm:7d} ms sum  {n:5d}x  wall {b-a:6d} ms  {k}")
