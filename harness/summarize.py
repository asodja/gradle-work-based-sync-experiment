#!/usr/bin/env python3
"""Summarize harness runs: summarize.py results/<label> [results/<label2> ...]

Reads <scenario>-<run>.txt files written by the harness and prints, per label and scenario, the
median/min/max of total_ms and action_ms, the task outcome counts and the configuration cache lines.
"""
import os
import re
import statistics
import sys
from collections import defaultdict

SCENARIO_NAMES = {
    "1": "Cold (CC off, IP off)",
    "2": "Warm, plain (CC off, IP off)",
    "3": "Warm, IP (CC + IP on)",
    "4": "Incremental, IP (one script edited)",
    "5": "Warm, IP + model cache (builders only)",
    "6": "Incremental, IP + model cache (builders only)",
    "7": "Full invalidation, IP (gradle.properties edited)",
}


def parse(path):
    d = {"cc": []}
    with open(path, encoding="utf-8") as f:
        for line in f:
            line = line.rstrip("\n")
            m = re.match(r"^(\w+)=(.*)$", line)
            if m and m.group(1) in ("total_ms", "action_ms", "buildEnvironment_ms", "toProjectsLoaded_ms",
                                    "projectsLoadedToBuildFinished_ms", "afterBuildFinished_ms", "status", "tasks", "stall_max_gap_s",
                                    "builds", "gradleVersion"):
                d[m.group(1)] = m.group(2)
            if "Configuration cache entry" in line or "Reusing configuration cache" in line or "problems were found" in line:
                d["cc"].append(line.strip())
    return d


def fmt_ms(values):
    if not values:
        return "-"
    v = [int(x) for x in values]
    return f"{statistics.median(v):.0f} ({min(v)}–{max(v)})"


def main(dirs):
    for d in dirs:
        runs = defaultdict(list)
        for name in sorted(os.listdir(d)):
            m = re.match(r"^(\d)-(\d+)\.txt$", name)
            if m and m.group(2) != "0":  # run 0 is the warm-up
                runs[m.group(1)].append(parse(os.path.join(d, name)))
        print(f"## {os.path.basename(os.path.normpath(d))}\n")
        print("| # | Scenario | Runs | Total ms median (min–max) | Action ms | To projectsLoaded | projectsLoaded→buildFinished | Tasks | CC | Max stall gap s |")
        print("|---|---|---|---|---|---|---|---|---|---|")
        for s in sorted(runs):
            rs = runs[s]
            ok = [r for r in rs if r.get("status") == "OK"]
            cc = sorted({c for r in ok for c in r["cc"] if "Configuration cache entry" in c or "Reusing" in c})
            cc_short = "; ".join(re.sub(r"^.*?(Configuration cache entry \w+|Reusing configuration cache).*$", r"\1", c) for c in cc) or "-"
            tasks = sorted({r.get("tasks", "-") for r in ok}) or ["-"]
            print(f"| {s} | {SCENARIO_NAMES.get(s, '?')} | {len(ok)}/{len(rs)} | "
                  f"{fmt_ms([r['total_ms'] for r in ok if 'total_ms' in r])} | "
                  f"{fmt_ms([r['action_ms'] for r in ok if 'action_ms' in r])} | "
                  f"{fmt_ms([r['toProjectsLoaded_ms'] for r in ok if 'toProjectsLoaded_ms' in r])} | "
                  f"{fmt_ms([r['projectsLoadedToBuildFinished_ms'] for r in ok if 'projectsLoadedToBuildFinished_ms' in r])} | "
                  f"{'; '.join(tasks)} | {cc_short} | {', '.join(r.get('stall_max_gap_s', '-') for r in ok)} |")
        print()


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print(__doc__)
        sys.exit(2)
    main(sys.argv[1:])
