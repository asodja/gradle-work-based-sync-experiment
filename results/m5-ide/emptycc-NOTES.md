# M5: configuration cache miss without any script change (2026-10-16, 18:46–19:05)

Same setup as `stable-NOTES.md` (gradle/gradle, headless IDE, fresh IDE system dir per run, daemon stopped, three
rounds round-robin), but `.gradle/configuration-cache` is deleted before every run: every Gradle invocation is a miss
("Calculating task graph as no cached configuration is available") while no script changes, so no Kotlin DSL script
is recompiled and the model tasks' execution history stays. Scripts and the stage/step timestamps of every run are in
`emptycc/`.

| Configuration | Gradle side | IntelliJ side | of which data import stage | whole import |
|---|---|---|---|---|
| stock | 54.5 / 51.4 / 61.0 → **54.5 s** | 26.6 / 26.4 / 25.4 → **26.4 s** | 22.9 / 22.9 / 22.9 → **22.9 s** | 1 m 21.1 / 1 m 17.7 / 1 m 26.3 → **1 m 21.1 s** |
| tasks | 39.1 / 38.9 / 39.2 → **39.1 s** | 22.0 / 21.8 / 23.4 → **22.0 s** | 18.8 / 18.5 / 20.3 → **18.8 s** | 1 m 1.1 / 1 m 0.6 / 1 m 2.6 → **1 m 1.1 s** |
| tasks-fixed | 39.2 / 43.5 / 38.9 → **39.2 s** | 13.1 / 14.5 / 12.4 → **13.1 s** | 9.8 / 10.2 / 9.6 → **9.8 s** | 52.3 / 58.0 / 51.3 → **52.3 s** |

Speed-up against stock (medians, % less time):

| Configuration | Gradle side | IntelliJ side | whole import |
|---|---|---|---|
| tasks | **−28 %** (54.5 → 39.1 s) | −16 % (26.4 → 22.0 s) | **−25 %** (1 m 21.1 → 1 m 1.1 s) |
| tasks-fixed | **−28 %** (54.5 → 39.2 s) | **−50 %** (26.4 → 13.1 s) | **−35 %** (1 m 21.1 → 52.3 s) |
| tasks-fixed vs tasks | 0 % | −40 % | −14 % |

The task run on this miss (last run): 7.3 s before the plain run (daemon start, `BuildEnvironment`, `GradleBuild`
with settings evaluation and its own cache store), plain run 29.5 s (all 287 projects configured, 556 actionable
tasks: 27 executed, 529 up to date, **all 287 `ideProjectModel` tasks up to date**, so no model captured), state
assembly 0.1 s, 2.0 s of IDE stage work: 39 s. Against the settings-change miss (`miss-NOTES.md`) the task side is
1.5 s faster (no settings script recompilation) and the stock side 8.6 s faster (its restart series median is 51.8 s,
so the stock miss costs the same with or without a script change, within the warming noise).

Three kinds of miss compared (medians):

| Scenario | stock | tasks | tasks + fixes |
|---|---|---|---|
| restart, cache kept (stock misses, tasks hit) | 1 m 20.4 s | 37.6 s (−53 %) | 29.2 s (−64 %) |
| cache emptied, no script change | 1 m 21.1 s | 1 m 1.1 s (−25 %) | 52.3 s (−35 %) |
| settings script changed | 1 m 32.4 s | 1 m 3.6 s (−31 %) | 52.9 s (−43 %) |

Per step: library dependency data stock 12.3–12.9 s, tasks 10.0–10.8 s, tasks-fixed **4.6 s**; project repository
data 6.7–7.2 / 5.8–6.4 / **2.5–2.9 s**. The data import stage is again shorter than in the restart series for the
task configurations (18.8 vs 24.0 s, 9.8 vs 15.8 s) and not for stock (22.9 vs 23.5 s): the long Gradle phase lets
the warm-up indexing finish before the import (see `miss-NOTES.md`).
