# M3 comparison, hard group: builder output vs task bundles on gradle/gradle (2026-10-16)

Models captured at configuration time with their IntelliJ builders (D13): `GradleSourceSetDependencyModel`,
`ProjectDependencies`, `KotlinGradleModel`. Dumps from one sync each (fresh client JVM, project defaults, `--tasks`
transport), builder dump and task dump taken on the same day (gradle/gradle's own version string carries the date,
`9.9.0-2026101X000000+0000`, which made a first comparison across midnight differ in every project).

## Method

IntelliJ's Ion serializers number objects per writer across all documents of a sync and the IDE deserializes all
models of a class in consumption order with one serializer, so a per-document comparison of the two dependency
models shows only `objectID` renumbering. `prototype/compare-ordered.sh` deserializes every document of a class in
the recorded consumption order (`order.txt` of the dump) with one serializer, prints a canonical reflective text per
project and diffs it, ignoring the per-run node `id` numbers of `ProjectDependencies`.

Control: two builder dumps compared the same way are identical in every project for both models (287/287 and
272/272), so the method has no noise of its own.

## Result (prototype with D16: shared builder context, artifact index and source-set state per project at `afterProject`)

| Model | identical | different | note |
|---|---|---|---|
| GradleSourceSetDependencyModel | 287 | 0 | |
| ProjectDependencies | 263–268 | 4–9 | same nodes in every differing project (sorted lines identical); sibling order inside the dependency graphs differs, and it differs between two task-mode runs as well (268/4), while two builder runs agree: the order of `ResolvedComponentResult.getDependencies()` depends on the order in which the configurations were resolved, which the parallel captures do not fix |
| KotlinGradleModel | 60 | 0 | per-document comparison (`compare-mechanical.md` method) |

Before D16 (per-capture context, M4 state): `GradleSourceSetDependencyModel` 23 identical / 264 different, all from
the missing index entries of other projects: compile-classpath artifacts of project dependencies stayed classes
directories instead of being folded into the jar entry, so IntelliJ's compile/runtime merge did not happen (one
`COMPILE` dependency became `PROVIDED` + `RUNTIME`), `projectDependencyArtifactsSources` were empty, and the
runtime output-directory file dependencies were missing. `ProjectDependencies` 265 / 7 by the same ordered method.

`GradleSourceSetModel` (mechanical group, re-checked with the same-day dumps): 285 / 2 against the task bundles, but also
283 / 4 between the two builder dumps themselves (`:docs`, `:internal-build-reports`, two more): empty
`configurationArtifacts` entries for configurations that only exist once certain tasks have been realized (the
asciidoctor plugin's per-task `__$$asciidoctorj_<task>$$__d/r` configurations, report-path configurations). Which
of them exist when the model is built depends on task realization order in that run; empty entries, no IDE effect.
Not a port difference.

## Stability

The first D16 build lost the dependency model of 240 projects in two of six timing runs: reading another project's
`SourceSetOutput.getDirs()` through the index realizes that project's lazily registered tasks
(`output.dir(taskProvider)`), and 240 captures did that concurrently without the project's lock
(`TaskCreationException: Could not create task ':internal-distribution-testing:copyTestedVersionsInfo'`). The model
tasks then re-executed on the next run because the bundle content changed. IntelliJ avoids the same race with its
task warm-up request, which realizes every task before any builder runs. The prototype now evaluates the source
sets' source dirs and output dirs in each project's own `afterProject` hook, before the index entry is published.
Verified with `results/m4c-tasks-only/` (12 timed runs, every `ideProjectModel` up to date on repeat, only the 9 known
`apiStubElements` failures per run) and two further task dumps (287/287 for the dependency model).
