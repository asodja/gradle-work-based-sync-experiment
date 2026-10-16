# Compact record format for the dependency model (D26, 2026-10-17)

Owner's question: try the compact record format; the bundle format could be anything (JSON too) if it speeds things up
and the IDE expands it into its objects later; is IntelliJ's Ion serializer the best choice?

## What was built

- `CompactDependencies` (prototype): `GradleSourceSetDependencyModel` as a per-project string table (`List<String>`:
  paths, coordinates, scopes) plus one varint record per dependency (`byte[]`: kind, string refs, order, flags, nested
  records). Built from IntelliJ's model at capture time, expanded back into `DefaultExternalLibraryDependency` &
  co. by the consumer. An unknown dependency type falls back to the serialized form.
- The task state is now one list of `CapturedModel`s whose state form is per model class: `CompactDependencies` for the
  dependency model, the `KotlinDslScriptPayload` object itself (lists of paths, which the configuration cache stores
  as references into its entry-wide string table), the deflated serialized bytes (`ModelPayload`) for everything else.
  `-PideModelTasks.compact=false` restores the all-bytes state for comparison; the D25 `state=objects` option is gone.
- Bundle entry format `compact-deps`; `ModelPayload.readModel(ClassLoader)` is the one reader for all formats, used by
  the IDE plugin, the harness and the tools (six copies before). Unit test `CompactDependenciesTest` (round trip,
  canonical text, order, nulls). `BundleDump --text-all` dumps one canonical text per bundle in one JVM.

## Offline estimate first (`:core`, `estimate.py` over the Ion text dumps)

| form | deflated (level 1, as the task does) |
|---|---|
| Ion binary (today) | 101 KB |
| plain JSON of the same object shape | 89 KB (format alone: −12 %) |
| artifact table + small records, JSON | 33 KB (**−68 %**); the table is 24 KB of it |
| `ProjectDependencies`: Ion 107 KB → components table + tree 90 KB | −16 %: the graph itself is the content |

`:core` has 1308 dependency records (179 of them Ion back references) for 466 distinct artifact records in 6 source sets.
Format is worth ~10 %; the data model (one table, records as references) is worth 3×; `ProjectDependencies` does not
compact, its lever stays dropping it from the sync.

## Launcher series (gradle/gradle, warm daemon, tasks up to date, one traced miss and hit per form, `chain.out`)

| task state | configure | store | entry | entry string table | hit: fingerprint / load / run | bundles on disk |
|---|---|---|---|---|---|---|
| compact (default) | 4.7 s | 7.2 s | **39 MB** | 2.45 MB | 0.89 / **0.22** / 0.06 s | **43.1 MB** |
| bytes (`compact=false`) | 4.2 s | 6.7 s | 56 MB | 1.25 MB | 0.91 / 0.18 / 0.07 s | 51.9 MB |

Per model in the compact bundles (`bundle-sizes-compact.txt`): `GradleSourceSetDependencyModel` 30.4 MB inflated /
**7.1 MB** stored (Ion: 80.2 / 12.7 MB, −44 %); `:core` 162 KB / 36 KB (Ion 525 KB / 101 KB, −64 %, as estimated;
small projects repeat less). `ProjectDependencies` 14.3 MB and `KotlinDslScriptPayload` 11.0 MB are now the largest
entries of the bundles. The entry shrank by 17 MB: the dependency records as `byte[]` cost no bean structure, and the
script payload's and the dependency model's paths went into the shared string table (2.45 vs 1.25 MB) instead of being
repeated deflated per project.

Fidelity: the expanded compact model and the Ion model have identical canonical text (`ModelText`) for **287 of 287**
projects (`text-compact` vs `text-bytes2`, the latter from Ion bundles written by the bytes form after deleting all
bundles; the first comparison in `chain.out` compared compact with compact because the up-to-date tasks had not
rewritten the bundles). A deleted `:core` bundle is recreated from the compact state on a hit (226 KB).

## IDE series (restart scenario, fresh system dir; run 1 stores, runs 2–3 hit; `part2.out`, `ide-*.log`)

| form | run 1 (store): import / resolve stage | hits: import / resolve stage / plain task run |
|---|---|---|
| compact | 49.8 s / 37.4 s | 27.7, 28.3 s / 9.7, 9.7 s / 5.7, 6.1 s |
| bytes | 53.9 s / 40.6 s (all 287 tasks executed: bundles deleted before) | 27.9, 29.7 s / 10.0, 10.1 s / 6.2, 5.9 s |

Bundle reading after the task run: 0.25–0.38 s in both forms (streaming reads, 287 of 287 during the run).

## Reading

- **Size: yes.** Entry −30 % (56 → 39 MB), bundles −17 %, the dependency model's bytes −44 % (−64 % on a large
  project), with proven fidelity and no IDE-side change outside our plugin.
- **Time: not measurable here.** The hit loads the entry in 0.2 s in both forms (the 1.4–2.5 s loads of the D25 series
  did not recur in this one), the IDE's import is 27.7–28.3 vs 27.9–29.7 s, the store is the capture (7.2 vs 6.7 s,
  noise). On this machine the entry size does not matter for the hit; it would on a slow disk or a smaller heap.
- **Format question.** The bytes' encoding (Ion, JSON, our varints) is worth ~10 %; what matters is the data model: a
  table of distinct strings or artifacts with records as references, and a scope that spans projects (the
  configuration cache's string table gives that for the entry; a bundle-level table would give it for the files the
  IDE reads, today still per project). Ion is fine as the carrier; the gain is in what is carried. The IDE can read any
  format as long as something expands it into its objects, which our plugin now does for `compact-deps`.
- **Next levers in this direction**, in order: drop `ProjectDependencies` from the eager sync (14.3 MB of the bundles,
  28 % of the former entry, Dependency Analyzer only); the script classpath (`KotlinDslScriptPayload` 11 MB +
  `GradleBuildScriptClasspathModel` 4 MB, the same ~800 paths in nearly every project) as one build-level table; only
  then the remaining per-record structure of `GradleTaskModel` and the rest.
