#!/usr/bin/env python3
"""Merges gradle-profiler benchmark.csv files of a stock and a WBS run into one markdown table.
usage: merge-report.py <output dir containing stock/ and wbs/> [warm-ups] [iterations]
<mode>/gradle-times.txt (written by compare.sh): the Gradle part of every sync in run order, one ms value per line,
(warm-ups + iterations) lines per scenario."""
import csv, pathlib, statistics, sys

out = pathlib.Path(sys.argv[1])
warmups = int(sys.argv[2]) if len(sys.argv) > 2 else 1
iterations = int(sys.argv[3]) if len(sys.argv) > 3 else 3

def load(mode):
    f = out / mode / "benchmark.csv"
    if not f.exists():
        return {}
    data = list(csv.reader(f.open()))
    titles, kinds = data[0][1:], data[3][1:]      # row 0: scenario titles per column, row 3: value kind per column
    measured = [r for r in data[4:] if r and r[0].startswith("measured")]
    result = {}
    for i, (title, kind) in enumerate(zip(titles, kinds)):
        name = title.split(" (")[0]
        values = [float(r[i + 1]) / 1000 for r in measured if len(r) > i + 1 and r[i + 1]]
        result.setdefault(name, {})[kind] = values
    times = out / mode / "gradle-times.txt"
    if times.exists():
        lines = times.read_text().split()
        per = warmups + iterations
        for k, name in enumerate(result):
            chunk = lines[k * per:(k + 1) * per][warmups:]
            if len(chunk) == iterations:
                result[name]["Gradle part"] = [float(v) / 1000 for v in chunk]
    for name, kinds in result.items():
        gradle = kinds.get("Gradle part") or (kinds.get("Gradle total execution time") if mode == "stock" else None)
        total = kinds.get("total execution time")
        if gradle and total and len(gradle) == len(total):
            kinds["Gradle part"] = gradle
            kinds["IDE part"] = [t - g for t, g in zip(total, gradle)]
    return result

def fmt(values):
    if not values:
        return "-"
    return " / ".join(f"{v:.1f}" for v in values) + f" → **{statistics.median(values):.1f} s**"

def split(kinds):
    g, i = kinds.get("Gradle part"), kinds.get("IDE part")
    if not g or not i:
        return "-"
    return f"{statistics.median(g):.1f} / {statistics.median(i):.1f} s"

stock, wbs = load("stock"), load("wbs")
names = list(dict.fromkeys(list(stock) + list(wbs)))
print("| scenario | stock total | WBS total | speed-up (medians) | stock Gradle / IDE | WBS Gradle / IDE |")
print("|---|---|---|---|---|---|")
for name in names:
    st = stock.get(name, {}).get("total execution time", [])
    wb = wbs.get(name, {}).get("total execution time", [])
    speedup = f"−{100 * (1 - statistics.median(wb) / statistics.median(st)):.0f} %" if st and wb else "-"
    print(f"| {name} | {fmt(st)} | {fmt(wb)} | {speedup} | {split(stock.get(name, {}))} | {split(wbs.get(name, {}))} |")
print()
print("Totals are measured identically for both IDEs. Gradle / IDE are medians of the measured syncs: stock's Gradle part "
      "is the profiler's agent timing of the build action, WBS's is the kit's own timing of its task run (idea.log), "
      "as the agent does not see a plain task run. IDE = total − Gradle.")
print(f"Raw results: {out}/stock/benchmark.html, {out}/wbs/benchmark.html")
