# IntelliJ IDEA 2026.2.2: forced configuration cache miss and the tasks-only configuration (2026-10-17)

Same setup as `stable-262-NOTES.md` (headless `idea.sh warmup`, gradle/gradle, fresh IDE system dir per run, shared
IDE config dir, daemon stopped before each run), three rounds round-robin over three configurations: stock 2026.2.2;
the patched IDE with the task sync but without the two import fixes (`WBS_PATCH_IDE_IMPORT=0`); the same with the
fixes (the Mac kit). Scripts, outputs and stage lines in `results/m5-ide/miss-262/`.

## Forced miss: the settings script's content changed before every run

Every configuration configures the build and stores a configuration cache entry; the task run's 37–39.5 s is a store.

| Configuration | Gradle side | IntelliJ side | of which data import stage | whole import |
|---|---|---|---|---|
| stock | 53.3 / 51.7 / 54.1 → **53.3 s** | 26.0 / 25.3 / 25.2 → **25.3 s** | 22.5 / 21.6 / 21.5 → **21.6 s** | 79.3 / 77.0 / 79.2 → **1 m 19.2 s** |
| tasks | 40.8 / 41.3 / 41.4 → **41.3 s** | 21.6 / 22.6 / 22.2 → **22.2 s** | 18.6 / 19.3 / 18.8 → **18.8 s** | 62.4 / 63.9 / 63.6 → **1 m 3.6 s** |
| tasks + fixes | 41.3 / 42.9 / 40.5 → **41.3 s** | 13.5 / 13.9 / 12.9 → **13.5 s** | 10.1 / 10.2 / 9.8 → **10.1 s** | 54.8 / 56.7 / 53.4 → **54.8 s** |
| speed-up, tasks | −22 % | −12 % | −13 % | −20 % |
| speed-up, tasks + fixes | −22 % | −47 % | −53 % | **−31 %** |

2025.3 for comparison (`miss-NOTES.md`): stock 1 m 32.4 s, tasks 1 m 3.6 s (−31 %), tasks + fixes 52.9 s (−43 %).
2026.2.2's stock miss is 13 s faster than 2025.3's; the task configurations are the same.

## Restart (cache kept): the tasks hit, stock misses

`tasks` rows from `d27d` (three hits after a warm-up store), the others from `stable-262-NOTES.md`.

| Configuration | Gradle side | IntelliJ side | of which data import stage | whole import |
|---|---|---|---|---|
| stock | 62.6 / 48.0 / 48.2 → **48.2 s** | 24.2 / 24.8 / 25.4 → **24.8 s** | 20.8 / 21.2 / 22.0 → **21.2 s** | 86.8 / 72.7 / 73.5 → **1 m 13.5 s** |
| tasks | 11.0 / 10.2 / 10.9 → **10.9 s** | 30.6 / 26.9 / 27.0 → **27.0 s** | 26.6 / 23.3 / 23.9 → **23.9 s** | 41.6 / 37.1 / 38.0 → **38.0 s** |
| tasks + fixes | 10.2 / 10.5 / 10.5 → **10.5 s** | 21.6 / 19.4 / 21.9 → **21.6 s** | 16.1 / 16.0 / 16.2 → **16.1 s** | 31.8 / 29.9 / 32.5 → **31.8 s** |
| speed-up, tasks | −77 % | +9 % | +13 % | −48 % |
| speed-up, tasks + fixes | −78 % | −13 % | −24 % | **−57 %** |

## Reading

- The Gradle side is what the tasks change: −22 % on a miss (the capture is the same work as the builders, minus the
  action's serialization and streaming), −78 % on a hit.
- The IntelliJ side depends on when the import runs, not on the transport. In the headless runs the IDE's warm-up
  indexing starts at project open; after a 41–53 s Gradle side it is finished when the data import begins, after a
  10 s hit it is still running and slows the single-threaded import: the same import is 13.5 s (miss) vs 21.6 s (hit)
  with the fixes, 22.2 vs 27.0 s without. That is why the tasks-only hit shows a slower IntelliJ side than stock.
  The fixes themselves are worth 8.7 s on a miss (22.2 → 13.5 s) and 5.4 s on a hit under that contention. In an
  interactive IDE with a warm index, the miss-column IntelliJ side is the relevant one (`baseline-NOTES.md`, warm-index
  re-import: data import 10–11 s).
- Whole import on a miss: 1 m 19.2 → 54.8 s (−31 %). Whole import after a restart: 1 m 13.5 → 31.8 s (−57 %), and
  with a warm index the hit case would be about 24 s.
