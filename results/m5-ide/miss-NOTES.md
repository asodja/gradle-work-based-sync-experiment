# M5: forced configuration cache miss in the real IDE (2026-10-16, 18:01–18:21)

Same setup as `stable-NOTES.md` (gradle/gradle, headless IDE, fresh IDE system dir per run, daemon stopped, three
rounds round-robin), but the configuration cache dir is emptied before the chain and a comment line with a
timestamp is appended to `settings.gradle.kts` before every run, so every Gradle invocation is a miss
("Calculating task graph as configuration cache cannot be reused because file 'settings.gradle.kts' has changed").
The file is restored afterwards. Scripts and the stage/step timestamps of every run are in `miss/`.

| Configuration | Gradle side | IntelliJ side | of which data import stage | whole import |
|---|---|---|---|---|
| stock | 76.3 / 63.1 / 58.2 → **63.1 s** | 28.8 / 29.4 / 27.0 → **28.8 s** | 25.2 / 25.6 / 23.4 → **25.2 s** | 1 m 45.1 / 1 m 32.4 / 1 m 25.1 → **1 m 32.4 s** |
| tasks | 43.1 / 40.6 / 40.7 → **40.7 s** | 23.6 / 23.0 / 22.4 → **23.0 s** | 20.2 / 19.3 / 19.5 → **19.5 s** | 1 m 6.7 / 1 m 3.6 / 1 m 3.2 → **1 m 3.6 s** |
| tasks-fixed | 41.5 / 40.2 / 39.4 → **40.2 s** | 13.9 / 12.7 / 12.6 → **12.7 s** | 10.5 / 9.5 / 9.6 → **9.6 s** | 55.4 / 52.9 / 52.1 → **52.9 s** |

Speed-up against stock (medians, % less time):

| Configuration | Gradle side | IntelliJ side | whole import |
|---|---|---|---|
| tasks | **−35 %** (63.1 → 40.7 s) | −20 % (28.8 → 23.0 s) | **−31 %** (1 m 32.4 → 1 m 3.6 s) |
| tasks-fixed | **−36 %** (63.1 → 40.2 s) | **−56 %** (28.8 → 12.7 s) | **−43 %** (1 m 32.4 → 52.9 s) |
| tasks-fixed vs tasks | −1 % | −45 % | −17 % |

What the Gradle side of a task run spends on a miss (last run, transport log): `GradleBuild`/`BuildEnvironment`
requests 10 s (daemon start, settings script recompiled and evaluated), plain task run 27.4 s (settings evaluated
again, all 287 projects configured; **all 287 `ideProjectModel` tasks up to date** (574 up-to-date events, two per task; 0 executed): the captured
model fingerprints did not change, so no model was captured), state assembly 2 s. The stock sync configures the
same projects and then runs all 35 builders per project in the build action (about 50 s). So on a miss the task
world saves the model-building work but pays the configuration like everybody else: −35 %, the M4 harness number.

The IntelliJ side is shorter than in the restart scenario for the task configurations (23.0 vs 27.2 s, 12.7 vs
19.0 s) but not for stock (28.8 vs 27.1 s). The likely reason is CPU contention with the warm-up indexing, which
runs from project open: after a 40 s Gradle phase it has finished when the data import starts, after a 10 s one it
has not. For stock the indexing of module content starts only when the modules exist. Per step: library dependency
data stock 12.4–12.9 s, tasks 10.3–10.6 s, tasks-fixed **4.6–5.0 s**; project repository data 7.7–9.4 / 6.0–7.0 /
**2.3–2.8 s**.

The spread of the stock Gradle side across the rounds (76 → 58 s) is the same warming effect as in the restart
series; the task configurations vary by 2–3 s.
