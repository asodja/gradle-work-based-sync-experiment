# m4h: D17 + D18 + D19, network-free, deflated payloads (2026-10-16, 06:22–06:34)

Prototype: every IntelliJ model and both Gradle-owned models as per-project payloads (D17), no distribution-sources
lookup (D18, `src` dir in the distribution; the warm-up honours it), payloads deflated (D19). Scenarios 2, 3, 4,
three runs each, cache directory deleted before each variant. Builders baseline from the same network-free setup:
`results/m4g-builders/`.

| Variant | Warm plain (2) | IP hit (3) | Edit (4) | cache dir after the variant |
|---|---|---|---|---|
| builders (`m4g`) | 21.9 s (19.0–24.7) | 1.47 s (1.34–2.74) | 12.5 s (12.0–13.2) | 568 MB, 11 entries |
| tasks | 17.9 s (16.5–18.2) | 7.7 s (3.0–8.6) | 13.0 s (12.6–13.4) | 935 MB, 11 entries |
| tasks-only | **15.3 s** (15.1–16.2) | 2.16 s (2.14–2.65) | **11.2 s** (11.2–15.8) | 930 MB, 11 entries |
| launcher | 19.6 s (19.4–21.1) | 5.3 s (4.5–10.7) | 16.8 s (15.0–37.6) | 104 MB, 8 entries |
| m4c floor (tasks-only without the Gradle-owned captures, with the lookup) | 14.6 s | 1.59 s | 9.4 s | |

Reading:
- `tasks-only` vs builders: −30 % warm plain, −10 % on the edit, hit +0.7 s. The two Gradle-owned captures cost
  0.7 s warm and 1.8 s on the edit over the `m4c` floor; their payloads are exact (compare-gradle-owned.md).
- `tasks` (IntelliJ's action still running, with every model from tasks) is now **faster than builders** on the
  warm plain run (17.9 vs 21.9 s) and equal on the edit; in M4 it was slower (22.4 s). Its hit is erratic (3.0–8.6 s):
  the daemon's old generation sits at 85–97 % of the 3.3 GB heap during these runs with a full collection every
  ~45 s (sampler, LOG). The live-set comparison (`results/heap-NOTES.md`) shows the builders' daemon holds more
  (2.34 vs 1.24 GB) and gets the same erratic hits in its own series: GC pauses of the 3.3 GB daemon, both worlds.
- Entries: 85 MB per task-world entry after D19 (155 MB before), 50 MB for builders. The launcher's plain task run
  stores only ~13 MB per entry.
- The launcher was slower than `tasks-only` here; its client read and inflated 287 bundles single-threaded after the
  build. With one reader per core that step is 0.23 s (`results/launcher-par/`); the rest of its hit is the entry
  load, see `results/hit-NOTES.md`.
