# No-change hits: where the time goes (2026-10-16, consolidated; `results/heap-*`, `results/heapx-*`, session traces)

## Cost structure of a hit (build operation traces, tasks-only, one client JVM, warm daemon)

| hit | total | Check configuration cache fingerprint | Load configuration cache state (own) | rest |
|---|---|---|---|---|
| 1 | 3.0 s | 0.61 s | 2.03 s (1.75) | 0.3 s |
| 2 | 4.0 s | 0.61 s | 3.07 s (2.86) | 0.3 s |
| fresh daemon, hits 1–3 | 4.5 / 1.8 / 1.3 s | 0.68 / 0.60 / 0.45 s | 3.43 (3.13) / 0.91 (0.58) / 0.60 (0.24) s | 0.3 s |

Launcher (plain task run, no action), fresh daemon: hits 4.5 / 4.7 / 5.2 s; warm daemon: 2.7 / 3.0 s; one series
right after a store in the same JVM showed loads of 13.6–22.9 s own time with "Configuration cache entry reused"
and no child operations of note (287 "Loading configuration for" in 156 ms); not reproduced in two later series
and left as an open outlier.

## What it is not

- Not GC: with an 8 GB daemon heap no series had a full collection and the spread stayed (builders 2.18–2.78 s,
  tasks-only 2.67–5.11 s, tasks 3.09–5.99 s). GC log of the fresh-daemon launcher series, hits only: 8 pauses, 0.12 s in total, pauses over 50 ms: none.
  GC log of the tasks-only series (store + 3 hits): 2.0 s of pauses in total.
- Not the network: D18 removed the only lookup; the hit path has none.

## What it is

- The entry load. Entry composition (one store each): **builders 114 MB**, of which 113.8 MB `modelsideeffects-1.bin`
  (the models the action streamed to the client, serialized by the Tooling API and replayed on a hit;
  `DefaultBuildController.dispatch` runs every `send` as an isolatable side effect). **tasks-only 186 MB**: 141 MB
  of the same side effects (the harness re-streams the models it read from the bundles) plus ~45 MB of task state
  (287 `*.work.bin`, the deflated payloads). **launcher 52 MB**: task state only. Load own time tracks entry size
  and daemon warmth; the first hits after a daemon start are the slow ones (JIT and caches).
- The builders' daemon holds the larger live set (2.34 GB after a full GC vs 1.24 GB), so memory is not the task
  world's problem either.

## Consequences

- For M5 the IDE should read the bundles itself (file transport, as the launcher does) and not push the models
  back through the action: no model side effects, the entry shrinks to the task state (52 MB), and the hit is the
  load of that state plus the fingerprint check: 2.2–3 s in a warm daemon today against 1.5–2.3 s for builders.
- The client-side reading of 287 deflated bundles is 0.23 s with one worker per core (was 2–3 s single-threaded).
- Hit medians from three runs in a fresh daemon are not comparable between series; measure hits in a warm daemon
  or with more runs.
