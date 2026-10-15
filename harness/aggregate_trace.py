#!/usr/bin/env python3
"""Aggregate a Gradle build operation trace (-Dorg.gradle.internal.operations.trace=<base> -> <base>-log.txt).

Prints Markdown tables: per-model fetch cost (count, total, max, split by target scope), configuration
cost per project, task execution cost, and the overall span. Usage:

    aggregate_trace.py <trace-log.txt> [--top N] [--md <out.md>]
"""
import json
import re
import sys
from collections import defaultdict

FETCH = re.compile(r"^Fetch model '([^']+)' for (.+)$")
CONFIGURE = re.compile(r"^Configure project (.+)$")
TASK = re.compile(r"^Task (:.*)$")


def load(path):
    starts, spans = {}, []
    with open(path, encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            rec = json.loads(line)
            if "startTime" in rec:
                starts[rec["id"]] = rec
            elif "endTime" in rec:
                start = starts.pop(rec["id"], None)
                if start is not None:
                    spans.append((start["displayName"], start["startTime"], rec["endTime"], rec.get("failure")))
    return spans


def scope_of(target):
    if target.startswith("project "):
        return "project"
    if target.startswith("build "):
        return "build"
    return "default"


def table(rows, headers):
    out = ["| " + " | ".join(headers) + " |", "|" + "---|" * len(headers)]
    for r in rows:
        out.append("| " + " | ".join(str(c) for c in r) + " |")
    return "\n".join(out)


def main(argv):
    if len(argv) < 2:
        print(__doc__)
        return 2
    path = argv[1]
    top = 40
    md_out = None
    i = 2
    while i < len(argv):
        if argv[i] == "--top":
            top = int(argv[i + 1]); i += 2
        elif argv[i] == "--md":
            md_out = argv[i + 1]; i += 2
        else:
            i += 1

    spans = load(path)
    if not spans:
        print("no completed operations in", path)
        return 1
    t0 = min(s[1] for s in spans)
    t1 = max(s[2] for s in spans)

    models = defaultdict(lambda: {"count": 0, "total": 0, "max": 0, "scope": None, "failed": 0})
    configure = []
    tasks = []
    for name, start, end, failure in spans:
        d = end - start
        m = FETCH.match(name)
        if m:
            e = models[m.group(1)]
            e["count"] += 1
            e["total"] += d
            e["max"] = max(e["max"], d)
            e["scope"] = scope_of(m.group(2))
            if failure:
                e["failed"] += 1
            continue
        m = CONFIGURE.match(name)
        if m:
            configure.append((m.group(1), d)); continue
        m = TASK.match(name)
        if m:
            tasks.append((m.group(1), d)); continue

    sections = []
    sections.append(f"Trace: `{path}`\n\nOverall span: {t1 - t0} ms, {len(spans)} operations.\n")

    rows = sorted(models.items(), key=lambda kv: -kv[1]["total"])
    sections.append("## Model fetches (wall time of the fetch operation, overlapping when parallel)\n")
    sections.append(table(
        [(k, v["scope"], v["count"], v["total"], v["max"], v["failed"]) for k, v in rows[:top]],
        ["Model", "Scope", "Count", "Total ms", "Max ms", "Failed"]))
    per_scope = defaultdict(int)
    for k, v in rows:
        per_scope[v["scope"]] += v["total"]
    sections.append("\nTotals by scope: " + ", ".join(f"{s}: {t} ms" for s, t in sorted(per_scope.items())) + "\n")

    if configure:
        configure.sort(key=lambda x: -x[1])
        sections.append(f"## Configure project ({len(configure)} projects, sum {sum(d for _, d in configure)} ms)\n")
        sections.append(table(configure[:top], ["Project", "ms"]))
    if tasks:
        tasks.sort(key=lambda x: -x[1])
        sections.append(f"\n## Tasks ({len(tasks)} executed, sum {sum(d for _, d in tasks)} ms)\n")
        sections.append(table(tasks[:top], ["Task", "ms"]))

    text = "\n".join(sections) + "\n"
    print(text)
    if md_out:
        with open(md_out, "w", encoding="utf-8") as f:
            f.write(text)
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
