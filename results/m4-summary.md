# M4 comparison matrix: gradle/gradle, four transports (2026-10-15)

Labels (same harness, same client-JVM discipline, `harness/run-scenarios.sh <label> 1 2 3 4 7`, 3 measured runs
per scenario after a warm-up run 0; scenarios 2, 3, 4, 7 run inside one client JVM):

- `m4-builders`: today's IDE: IntelliJ's `GradleModelFetchAction` with all models from model builders.
- `m4-tasks` (`--tasks`): the same action; the 17 ported per-project models come from the `ideProjectModel` task
  bundles, the remaining builders (Gradle-owned `IdeaProject`, `KotlinDslScriptsModel`, `GradleBuild`,
  `GradleBuildScriptClasspathModel`, the Kotlin plugin-option models) still run. The state an otherwise unchanged
  IDE could reach.
- `m4-tasks-only` (`--tasks-only`): the action requests no model builder at all; only the tasks run. The floor.
- `m4-launcher` (`--launcher`): no build action: `GradleBuild` model, then a plain `BuildLauncher` task run of
  `ideProjectModel`, bundles read in the client (D14).

Medians of `total_ms` (client side, from `--out` files; `harness/summarize.py`):

| Scenario | builders | tasks | tasks-only | launcher |
|---|---|---|---|---|
| 1 cold (daemon stopped, CC off, IP off) | 51.6 s | 45.6 s | 30.0 s | 29.2 s |
| 2 warm plain (CC off, IP off) | 21.4 s | 22.4 s | **14.8 s** (−30 %) | 16.6 s (−22 %) |
| 3 warm IP, hit (entry reused) | 1.65 s | 1.75 s | 1.68 s | 3.3 s |
| 3 warm IP, miss, run 0 (same JVM, entry stored) | 25.1 s | 20.0 s | **11.5 s** (−54 %) | 13.5 s (−46 %) |
| 3 warm IP, miss, traced run (fresh JVM) | 19.2 s | 20.0 s | 14.1 s (−26 %) | **2.9 s, a hit** (see below) |
| 4 incremental IP (one build script edited) | 14.1 s | 16.5 s | **10.2 s** (−28 %) | 11.4 s (−19 %) |
| 7 full invalidation IP (settings script edited) | 16.6 s | 15.9 s | **11.5 s** (−31 %) | 11.6 s (−30 %) |

Rows "miss" are single runs (run 0 is excluded from medians by design; the traced run carries the build operation
trace overhead), the other rows are medians of three.

Re-measured after the fidelity fix (D16, `results/m4c-tasks-only/`, 2026-10-16): `tasks-only` warm plain 14.6 s, hit 1.59 s,
edit 9.4 s. The prototype that produced the matrix above differed only in the per-capture builder context (D13 state).

## D17–D19 follow-up (2026-10-16): Gradle-owned models as payloads, network-free, deflated

Final numbers in `results/m4h-NOTES.md` (builders baseline `results/m4g-builders/`, same setup). Medians, s:

| Scenario | builders | tasks | tasks-only | launcher |
|---|---|---|---|---|
| 2 warm plain | 21.9 | 17.9 | **15.3** | 19.6 |
| 3 warm IP hit | **1.47** | 7.7 (3.0–8.6) | 2.16 | 5.3 |
| 4 edit | 12.5 | 13.0 | **11.2** | 16.8 |

Differences to the M4 matrix above: every model, the Gradle-owned `IdeaProject` and `KotlinDslScriptsModel`
included, comes from the per-project task (D17, exact on gradle/gradle: `results/m3-ports/compare-gradle-owned.md`);
the distribution-sources lookup on services.gradle.org that both worlds paid per sync is gone (D18, the `src`
directory of an `-all` distribution); payloads are deflated (D19, 155 → 85 MB per entry). The intermediate rounds
`m4d`–`m4g` and what each one taught are in their `*-NOTES.md`; they were taken on an unstable connection.

## Gate (D15: ≥ 30 % lower median on scenario 2, the warm miss, scenario 4)

| Variant | scenario 2 | warm miss | scenario 4 | Verdict |
|---|---|---|---|---|
| tasks | +5 % | +4 % (traced) / −20 % (run 0) | +17 % | **not met**: no faster than builders |
| tasks-only | −30 % | −26 % (traced) / −54 % (run 0) | −28 % | **borderline**: 26–31 % on every scenario where builders work, 54 % on the only like-for-like miss pair |
| launcher | −22 % | hit instead of miss (2.9 s vs 19–25 s) | −19 % | **not met on the work scenarios; structural win on the miss**: a plain task run's cache key does not depend on the client classloader, so a fresh client JVM (IDE restart) reuses the entry |

Why `tasks` does not win: the action still waits for the remaining builders, and the two expensive ones are
serial: `KotlinDslScriptsModel` 7.3 s wall for the root build and the first model (`DummyModel`, i.e. configuration)
8.5 s (`results/m4-tasks/2-models.md`). With builders those 7.3 s overlap the parallel per-project model building; in
`tasks` they come on top of the task run. The owner's D14 (Gradle-owned models as tasks) is exactly what removes them.

Why the floor is ~30 % and not more: configuration itself (to `projectsLoaded`: 8.7–9.3 s warm plain, 4.5 s after an
edit) is identical in every variant, and dependency resolution cannot move past the configuration cache store
(D13), so a miss or an edit still computes every model. What the tasks remove is the serial model-building
phases and the transfer/serialization of 113–132 MB of models through the build action.

## Launcher transport, phase split (`results/m4-launcher/*.txt`, ms from start)

| Run | GradleBuild model | task run | bundles read in client | total |
|---|---|---|---|---|
| 2-1 warm plain | 52 | 19 190 | 568 | 19 834 |
| 3-0 warm miss (store) | 145 | 12 350 | 743 | 13 513 |
| 3-1 warm hit | 53 | 3 334 | 585 | 3 995 |
| 4-1 edit | 55 | 11 059 | 560 | 11 713 |
| 7-1 settings edit | 2 366 | 10 514 | 572 | 13 501 |

The launcher hit (3.3 s median) is slower than the action hit (1.65 s): the task run checks 287 tasks' outputs
(132 MB of bundles) and the client re-reads and deserializes them; the action hit replays the whole cached result.

## Task outcomes and cache lines

All 96 runs `status=OK`, no failed tasks, no configuration cache problems, no Isolated Projects problems, no
whole-machine stalls (`stall_max_gap_s=-1` everywhere). Task counts: builders `executed=27, skipped=66,
up-to-date=271` on configuring runs and `up-to-date=1` on hits; task variants add the 287 `ideProjectModel` tasks
(`up-to-date=557/558` on configuring runs, all 287 up to date on hits). Scenario 4 and 7 store a new entry on every
run in every variant (build-scoped invalidation, LOG 2026-10-15).

## Per-scenario summarizer output

## m4-builders

| # | Scenario | Runs | Total ms median (min–max) | Action ms | To projectsLoaded | projectsLoaded→buildFinished | Tasks | CC | Max stall gap s |
|---|---|---|---|---|---|---|---|---|---|
| 1 | Cold (CC off, IP off) | 3/3 | 51602 (49515–60314) | 51271 (49179–59983) | 25363 (23642–25803) | 27397 (23155–34401) | {executed=27, skipped=66, up-to-date=271} | - | -1, -1, -1 |
| 2 | Warm, plain (CC off, IP off) | 3/3 | 21368 (21090–24396) | 21336 (21059–24374) | 9132 (8661–9684) | 12523 (11218–15098) | {executed=27, skipped=66, up-to-date=271} | - | -1, -1, -1 |
| 3 | Warm, IP (CC + IP on) | 3/3 | 1653 (1460–2521) | 1630 (1427–2496) | 1559 (1357–2427) | 70 (69–71) | {up-to-date=1} | Configuration cache entry reused; Reusing configuration cache | -1, -1, -1 |
| 4 | Incremental, IP (one script edited) | 3/3 | 14112 (14042–18891) | 14087 (14010–18866) | 3945 (3694–4188) | 10215 (9624–14708) | {executed=27, skipped=66, up-to-date=271} | Configuration cache entry stored | -1, -1, -1 |
| 7 | Full invalidation, IP (gradle.properties edited) | 3/3 | 16560 (13964–16911) | 16534 (13936–16890) | 6826 (3231–6874) | 9817 (9507–10513) | {executed=27, skipped=66, up-to-date=271} | Configuration cache entry stored | -1, -1, -1 |

## m4-tasks

| # | Scenario | Runs | Total ms median (min–max) | Action ms | To projectsLoaded | projectsLoaded→buildFinished | Tasks | CC | Max stall gap s |
|---|---|---|---|---|---|---|---|---|---|
| 1 | Cold (CC off, IP off) | 3/3 | 45553 (42679–46438) | 45233 (42355–46115) | 23387 (22396–25607) | 19763 (19385–22522) | {executed=27, skipped=66, up-to-date=558} | - | -1, -1, -1 |
| 2 | Warm, plain (CC off, IP off) | 3/3 | 22418 (22275–24343) | 22400 (22240–24313) | 9288 (8446–10882) | 13271 (12800–13816) | {executed=27, skipped=66, up-to-date=558} | - | -1, -1, -1 |
| 3 | Warm, IP (CC + IP on) | 3/3 | 1750 (1621–2346) | 1723 (1597–2320) | 1669 (1545–2252) | 49 (44–64) | {up-to-date=288} | Configuration cache entry reused; Reusing configuration cache | -1, -1, -1 |
| 4 | Incremental, IP (one script edited) | 3/3 | 16521 (14196–19465) | 16495 (14167–19427) | 4564 (3231–5073) | 11223 (10733–14680) | {executed=27, skipped=66, up-to-date=558} | Configuration cache entry stored | -1, -1, -1 |
| 7 | Full invalidation, IP (gradle.properties edited) | 3/3 | 15926 (14474–21313) | 15908 (14452–21283) | 4204 (4007–5567) | 11682 (10049–15526) | {executed=27, skipped=66, up-to-date=558} | Configuration cache entry stored | -1, -1, -1 |

## m4-tasks-only

| # | Scenario | Runs | Total ms median (min–max) | Action ms | To projectsLoaded | projectsLoaded→buildFinished | Tasks | CC | Max stall gap s |
|---|---|---|---|---|---|---|---|---|---|
| 1 | Cold (CC off, IP off) | 3/3 | 30037 (28916–30495) | 29737 (28614–30193) | 20895 (20345–21785) | 8175 (8067–8603) | {executed=27, skipped=66, up-to-date=557} | - | -1, -1, -1 |
| 2 | Warm, plain (CC off, IP off) | 3/3 | 14843 (13268–14857) | 14825 (13247–14828) | 8733 (7845–9374) | 5243 (5200–5927) | {executed=27, skipped=66, up-to-date=557} | - | -1, -1, -1 |
| 3 | Warm, IP (CC + IP on) | 3/3 | 1675 (1653–1976) | 1647 (1622–1954) | 1599 (1550–1892) | 62 (45–68) | {up-to-date=287} | Configuration cache entry reused; Reusing configuration cache | -1, -1, -1 |
| 4 | Incremental, IP (one script edited) | 3/3 | 10175 (10055–10872) | 10137 (10028–10850) | 4541 (4494–5183) | 5404 (5335–5469) | {executed=27, skipped=66, up-to-date=557} | Configuration cache entry stored | -1, -1, -1 |
| 7 | Full invalidation, IP (gradle.properties edited) | 3/3 | 11504 (10631–11952) | 11472 (10615–11925) | 5716 (5191–6224) | 5483 (5235–5567) | {executed=27, skipped=66, up-to-date=557} | Configuration cache entry stored | -1, -1, -1 |

## m4-launcher

| # | Scenario | Runs | Total ms median (min–max) | Action ms | To projectsLoaded | projectsLoaded→buildFinished | Tasks | CC | Max stall gap s |
|---|---|---|---|---|---|---|---|---|---|
| 1 | Cold (CC off, IP off) | 3/3 | 29210 (29015–30643) | - | - | - | {executed=27, skipped=66, up-to-date=557} | - | -1, -1, -1 |
| 2 | Warm, plain (CC off, IP off) | 3/3 | 16632 (16148–19834) | - | - | - | {executed=27, skipped=66, up-to-date=557} | - | -1, -1, -1 |
| 3 | Warm, IP (CC + IP on) | 3/3 | 3321 (2430–3995) | - | - | - | {up-to-date=287} | Configuration cache entry reused; Reusing configuration cache | -1, -1, -1 |
| 4 | Incremental, IP (one script edited) | 3/3 | 11412 (11013–11713) | - | - | - | {executed=27, skipped=66, up-to-date=557} | Configuration cache entry stored | -1, -1, -1 |
| 7 | Full invalidation, IP (gradle.properties edited) | 3/3 | 11578 (10771–13501) | - | - | - | {executed=27, skipped=66, up-to-date=557} | Configuration cache entry stored | -1, -1, -1 |

