#!/usr/bin/env python3
"""Merges gradle-profiler benchmark.csv files of a stock and a WBS run into one markdown table.
usage: merge-report.py <output dir containing stock/benchmark.csv and wbs/benchmark.csv>"""
import csv, pathlib, statistics, sys

out = pathlib.Path(sys.argv[1])

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
    return result

def fmt(values):
    if not values:
        return "-"
    return " / ".join(f"{v:.1f}" for v in values) + f" → **{statistics.median(values):.1f} s**"

stock, wbs = load("stock"), load("wbs")
names = list(dict.fromkeys(list(stock) + list(wbs)))
print("| scenario | stock total | WBS total | speed-up (medians) | stock Gradle part | stock IDE part |")
print("|---|---|---|---|---|---|")
for name in names:
    st = stock.get(name, {}).get("total execution time", [])
    wb = wbs.get(name, {}).get("total execution time", [])
    speedup = f"−{100 * (1 - statistics.median(wb) / statistics.median(st)):.0f} %" if st and wb else "-"
    print(f"| {name} | {fmt(st)} | {fmt(wb)} | {speedup} | "
          f"{fmt(stock.get(name, {}).get('Gradle total execution time', []))} | {fmt(stock.get(name, {}).get('IDE execution time', []))} |")
print()
print("Totals are measured identically for both IDEs. The Gradle/IDE split exists only for stock: the profiler's agent "
      "hooks the stock build action, the kit's task run is not seen by it.")
print(f"Raw results: {out}/stock/benchmark.html, {out}/wbs/benchmark.html")
