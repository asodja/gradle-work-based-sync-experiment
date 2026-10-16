import sys,re,datetime
rows=[]
for l in open(sys.argv[1], errors="replace"):
    if "ConsoleLog" not in l or "STDOUT" in l: continue
    m=re.match(r"(\S+ \S+) \[.*?\] +INFO - #c\.i\.w\.u\.ConsoleLog - \[(?:IDE|Gradle)\]: (?:Gradle: )?(.*)",l.rstrip())
    if not m: continue
    name=re.sub(r"\s+\.+\s+\d+%","",m.group(2)).strip()
    if name.startswith("Importing") or "data import stage" in name: rows.append((datetime.datetime.strptime(m.group(1),"%Y-%m-%d %H:%M:%S,%f"), name))
seen=set(); uniq=[]
for r in rows:
    if r[1] in seen: continue
    seen.add(r[1]); uniq.append(r)
for (t,n),(t2,_) in zip(uniq,uniq[1:]):
    d=(t2-t).total_seconds()
    if d>=float(sys.argv[2]) if len(sys.argv)>2 else 0.5: print(f"{d:6.1f} s  {n[:80]}")
