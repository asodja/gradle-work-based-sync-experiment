# M0: where sync time goes on gradle/gradle (builder version)

gradle/gradle `dedc5d14f2d`, Gradle 9.9.0-milestone-2, IntelliJ 2025.3 jars, harness (see `harness/README.md`),
this machine (`NOTES.md`). 4 builds, 287 projects, 5755 models per sync (113 MB serialized: ProjectDependencies 51.6 MB,
GradleSourceSetDependencyModel 28.9 MB, IdeaProject 9.5 MB, GradleTaskModel 9.0 MB, KotlinDslScriptsModel 4.2 MB,
GradleSourceSetModel 3.1 MB, the rest spread over the other ~4600 small ADDITIONAL-phase models). Raw per-run files: `<scenario>-<run>.txt`; run 0 is the warm-up.

## Scenario totals (median of 3, ms)

| # | Scenario | Total | To projects loaded | Projects loaded → build finished | Note |
|---|---|---|---|---|---|
| 1 | Cold: daemon stopped, CC off, IP off | 34696 | 19968 | 14334 | daemon start + JIT + configuration |
| 2 | Warm, plain: CC off, IP off | 16895 | 7269 | 9450 | builders run in parallel (`org.gradle.tooling.parallel=true`) |
| 3 | Warm, IP: CC + IP on, entry reused | 1445 | 1360 | 80 | **whole action result from the CC entry, no builder runs** |
| 3' | Warm **miss**, IP (traced run `3-trace`, fresh client JVM) | 21909 | 7460 | 13967 | what an IDE restart or any invalidation costs |
| 4 | Incremental, IP: one build script edited, no model cache | 14933 | 4498 | 10009 | entry invalidated, every model rebuilt |
| 5 | Warm, IP + opt-in model cache, entry reused | 1278 | 1134 | 83 | same as 3 |
| 6 | Incremental, IP + opt-in model cache | failed | | | `CONFIGURE FAILED`: Gradle cannot instantiate `KotlinUsages.KotlinUsagesCompatibility` (private) when updating the entry; see LOG 2026-10-15 |

Phase wall times (median run; ms from action start): scenario 2: projects loaded 7141, source sets 7962,
dependencies 10256, additional 16533. Scenario 4: 4518, 5380, 7850, 14745. Scenario 3 (hit): 1380, 1382, 1397,
1443. The ADDITIONAL phase includes `KotlinDslScriptsModel` (Gradle-owned, 4 builds, ~4–6 s wall) and
`KotlinGradleModel`.

## Per-model cost (ms, summed over all requests; builders run in parallel so sums exceed wall time)

Columns: S2 warm plain (vintage mode, parallel), S3 warm miss under IP (traced run), S4 incremental under IP.

| Model | Owner | Scope | Count | S2 sum | S2 max | S3 sum | S3 max | S4 sum |
|---|---|---|---|---|---|---|---|---|
| GradleSourceSetDependencyModel | IJ | project | 287 | 20080 | 597 | 29814 | 866 | 24495 |
| DummyModel | IJ plumbing | default | 1 | 7856 | 7856 | 6246 | 6246 | 4306 |
| KotlinDslScriptsModel | Gradle | project | 4 | 4898 | 4803 | 5855 | 5818 | 3973 |
| GradleTaskWarmUpRequest | IJ plumbing | project | 287 | 4281 | 238 | 5290 | 308 | 4776 |
| KotlinGradleModel | IJ | project | 287 | 3291 | 299 | 6130 | 532 | 3573 |
| GradleSourceSetModel | IJ | project | 287 | 1709 | 170 | 5193 | 328 | 4301 |
| GradleBuild | Gradle | default | 1 | 304 | 304 | 444 | 444 | 243 |
| GradleBuildScriptClasspathModel | IJ | project | 287 | 300 | 38 | 314 | 26 | 801 |
| IdeaProject | Gradle | build | 4 | 387 | 352 | 191 | 157 | 143 |
| ProjectDependencies | IJ | project | 287 | 189 | 4 | 327 | 23 | 217 |
| AnnotationProcessingModel | IJ | project | 287 | 227 | 33 | 213 | 4 | 215 |
| GradleTaskModel | IJ | project | 287 | 47 | 11 | 129 | 11 | 28 |
| ExternalTestsModel | IJ | project | 287 | 42 | 1 | 61 | 7 | 64 |
| GradleExtensions | IJ | project | 287 | 24 | 1 | 62 | 12 | 36 |
| AllOpenModel | IJ | project | 287 | 27 | 1 | 33 | 1 | 28 |
| NoArgModel | IJ | project | 287 | 27 | 1 | 31 | 1 | 24 |
| AssignmentModel | IJ | project | 287 | 28 | 1 | 27 | 1 | 35 |
| SamWithReceiverModel | IJ | project | 287 | 28 | 1 | 27 | 1 | 27 |
| KaptGradleModel | IJ | project | 287 | 24 | 1 | 27 | 1 | 27 |
| GradleSourceSetArtifactBuildRequest | IJ plumbing | project | 287 | 12 | 1 | 34 | 6 | 14 |
| ParcelizeGradleModel | IJ | project | 287 | 21 | 1 | 21 | 1 | 17 |
| PrepareKotlinIdeImportTaskModel | IJ plumbing | project | 287 | 12 | 2 | 29 | 3 | 14 |
| LombokModel | IJ | project | 287 | 18 | 1 | 20 | 1 | 20 |
| ExternalProject | IJ | project | 287 | 4 | 2 | 25 | 3 | 6 |
| VersionCatalogsModel | IJ | project | 287 | 9 | 1 | 14 | 3 | 11 |
| IntelliJProjectSettings | IJ | project | 287 | 8 | 6 | 13 | 10 | 8 |
| GradleDependencyDownloadPolicy | IJ plumbing | project | 287 | 5 | 1 | 8 | 1 | 6 |
| RepositoryModels | IJ | project | 287 | 5 | 1 | 7 | 1 | 3 |
| AnnotationBasedPluginModel | IJ | project | 287 | 4 | 1 | 4 | 1 | 4 |
| IntelliJSettings | IJ | project | 287 | 3 | 1 | 5 | 2 | 3 |
| KotlinMPPGradleModel | IJ | project | 287 | 2 | 1 | 5 | 1 | 3 |
| IdeaKpmProjectContainer | IJ | project | 287 | 4 | 1 | 3 | 1 | 4 |
| JavaGradleManifestModel | IJ | project | 287 | 2 | 1 | 5 | 1 | 3 |
| IdeaKotlinDependency | IJ | project | 287 | 3 | 1 | 3 | 1 | 4 |
| ProjectRepositoriesModel | IJ | project | 287 | 3 | 1 | 3 | 1 | 4 |
| DependencyAccessorsModel | IJ | project | 287 | 1 | 1 | 4 | 1 | 3 |
| Extras | IJ | project | 287 | 3 | 1 | 2 | 1 | 4 |
| KotlinTarget | IJ | project | 287 | 3 | 1 | 2 | 1 | 0 |
| KotlinDslScriptAdditionalTask | IJ plumbing | build | 4 | 0 | 0 | 0 | 0 | 0 |
| TurnOffDefaultTasks | IJ plumbing | default | 1 | 0 | 0 | 0 | 0 | 0 |

Summed fetch time of per-project models not owned by Gradle: S2 30446 ms, S3 47885 ms, S4 38778 ms
(8 cores; wall share roughly a quarter to a third of that). `DummyModel` is IntelliJ's first request and absorbs
the wait for configuration of the root build; `GradleTaskWarmUpRequest` realizes every task so later builders see
them; both disappear in a task-based design.

## Gate (design/plan.md, after M0)

Per-project IntelliJ-owned models > 3 s on a warm sync: **met on a warm miss** (~48 s summed, ~14 s wall in
scenario 3'; ~30 s summed in scenario 2), **not applicable on a warm hit** (scenario 3: nothing is built). See
`DECISIONS.md` D6 and `PROGRESS.md` "Blocked on owner".
