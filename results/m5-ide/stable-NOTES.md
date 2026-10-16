# M5: stable comparison in the real IDE, restart scenario (2026-10-16, 15:44–15:59)

gradle/gradle (287 projects), headless IDE (`idea.sh warmup`), restart scenario: fresh IDE system dir, Gradle
configuration cache kept, daemon stopped before every run. Three rounds, round-robin over the configurations so
machine drift hits all of them alike. Scripts and the stage/step timestamps of every run are in `stable/`.

- **stock**: IntelliJ IDEA 2025.3 as installed, stock Gradle sync (build action; a cache miss on every restart).
- **tasks**: patched IDE, one Gradle invocation per sync (D23), the IDE's import classes untouched.
- **tasks-fixed**: the same plus the two import fixes of D24 (`findIdeLibrary` by name, `findLibraryOrderEntry` by id).

Gradle side = the IDE's "Gradle resolve stage" (connection, model requests or plain task run, state assembly).
IntelliJ side = everything else in the import task (data import stage plus the IDE's setup and finishing work).

| Configuration | runs | Gradle side | IntelliJ side | of which data import stage | whole import |
|---|---|---|---|---|---|
| stock | 58.9 / 51.8 / 48.8 | **51.8 s** | 26.2 / 28.6 / 27.1 → **27.1 s** | 23.4 / 25.1 / 23.5 → **23.5 s** | 1 m 25.1 / 1 m 20.4 / 1 m 15.8 → **1 m 20.4 s** |
| tasks | 10.3 / 13.7 / 10.1 | **10.3 s** | 27.0 / 27.2 / 27.5 → **27.2 s** | 24.0 / 24.0 / 23.7 → **24.0 s** | 37.4 / 41.0 / 37.6 → **37.6 s** |
| tasks-fixed | 10.0 / 11.3 / 9.8 | **10.0 s** | 19.2 / 18.8 / 19.0 → **19.0 s** | 16.1 / 15.8 / 15.7 → **15.8 s** | 29.2 / 30.1 / 28.8 → **29.2 s** |

Speed-up against stock (medians, % less time):

| Configuration | Gradle side | IntelliJ side | whole import |
|---|---|---|---|
| tasks | **−80 %** (51.8 → 10.3 s) | 0 % (27.1 → 27.2 s) | **−53 %** (1 m 20.4 → 37.6 s) |
| tasks-fixed | **−81 %** (51.8 → 10.0 s) | **−30 %** (27.1 → 19.0 s) | **−64 %** (1 m 20.4 → 29.2 s) |
| tasks-fixed vs tasks | −3 % | −30 % | −22 % |

Per step (every run): library dependency data stock 12.4–13.3 s, tasks 12.9–13.3 s, tasks-fixed **8.0–8.2 s**;
project repository data stock 7.5–8.3 s, tasks 5.4–6.2 s, tasks-fixed **2.6–3.2 s**. The plain task run was a cache
hit in all six task runs (5.9–8.0 s for 287 projects); the stock sync stored a new entry in all three of its runs.

Spread: the stock Gradle side varies by 10 s between rounds (58.9 → 48.8 s, warming of the daemon's JIT and file
caches across the rounds), the task configurations by 1–4 s; the IntelliJ side varies by at most 2.4 s. The medians
above are therefore conservative for stock. The project model is identical in all three configurations (storage diffs
of 2026-10-16, 1370 of 1370 files).
