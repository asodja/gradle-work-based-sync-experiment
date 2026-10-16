# Daemon live set, builders vs tasks-only (2026-10-16, 06:34–06:37; `results/heap-*/`)

One scenario-3 series (store + 3 hits) per variant in a fresh daemon (gradle/gradle's `-Xmx3300m`), then the heap
read with `jstat`, a forced full GC (`jcmd GC.run`) and a class histogram.

| Variant | hits (median, range) | old gen before the forced GC | live set after full GC | top of the histogram |
|---|---|---|---|---|
| builders | 3.6 s (2.0–8.5) | 99.94 %, 1 full GC so far | **2.34 GB** | 227 MB byte[], 221 MB LinkedHashMap entries (5.8 M), 96 MB `DefaultResolvedDependencyResult` (2.5 M) |
| tasks-only | 3.2 s (2.8–7.2) | 92.98 %, 2 full GCs | **1.24 GB** | 196 MB byte[] (deflated payloads), 60 MB Object[], 25 MB Long |

Reading: the builders' daemon keeps more alive than the task world's (the action result with every model and the
resolved dependency graphs behind it), and both daemons run near the top of the heap. The erratic hits seen through
the day (3–8.6 s for `tasks`, 1.3–2.7 s for builders in `m4g`, here 2.0–8.5 s for builders) are full-GC pauses that
land on some hits and not others, not a property of the task transport. For hit measurements the daemon heap should
be raised (or GC time subtracted); the `tasks-only` hit in `m4h` (2.16 s, 2.14–2.65) is the one tight series.
