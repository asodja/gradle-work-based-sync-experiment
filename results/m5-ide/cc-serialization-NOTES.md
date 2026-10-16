# Configuration cache serialization and input snapshotting vs the `ideProjectModel` task state (2026-10-16, read-only investigation)

Sources: Gradle 9.9.0-milestone-2 (`dedc5d14f2d`) at `~/projects/gradle-wbs-benchmark`; IntelliJ master tooling-extension-impl
(raw.githubusercontent.com); the IDE jars. Anchors are `file:line` in the Gradle checkout unless stated. Delegated to the
`investigator` agent; measurements by the main agent are marked. **Not verified** marks claims not confirmed in code.

## Headline correction to D25

The parallel store does **not** give each project file its own string table. Every project file of the work graph
shares **one** `ParallelStringEncoder` (one `.strings.work.bin` per entry), included builds too. The 68 MB (objects)
vs 56 MB (bytes) difference is per-object structure: for every bean the codec writes a tag, an identity id and a
class id, and for every field a tag plus a value or string id. Strings are already shared. Deflate inside a payload
wins because it also compresses the *structure* that repeats across records (the same artifact in the main and test
source sets, in compile and runtime scope), which the codec writes as a fresh ~50-byte bean each time.

On disk (objects-form entry, 70.3 MB, 298 files): `.strings.work.bin` 7.6 MB, 287 project files `_<path>.work.bin`
57.5 MB (min 5.8 KB, median 181 KB, max 661 KB `_docs`), `buildfingerprint.bin` 5.0 MB, `.globals.work.bin` 187 B,
`work.bin` 50 KB. State files are AES-encrypted by default, so only the sizes can be inspected.

## 1. Serialization path of the task state

- A task is written by `TaskNodeCodec.writeTask`: class, project, name, then the task **bean state field by field**
  and the registered properties (`core-serialization-codecs/.../TaskNodeCodec.kt:100-133`). Field selection ignores
  annotations: every non-static, non-transient field of the type hierarchy (`bean-serialization-services/.../BeanSchema.kt:142-168`).
  `@Internal` changes nothing here.
- `ListProperty<T>`: `ListPropertyCodec` writes the element class and `calculateExecutionTimeValue()` through
  `FixedValueReplacingProviderCodec` (`ProviderCodecs.kt:487-497`, `:106-136`). A `project.provider {}` is a
  `DefaultProvider`, so it is a *fixed* value: evaluated at store (our capture runs inside "Storing configuration for
  <project>") and stored as an `ImmutableList` of values.
- `ModelPayload` (plain `Serializable`, no `writeObject`/`readResolve`): `JavaObjectSerializationCodec` only claims
  types with those hooks (`jos/JavaSerializationEncodingLookup.kt:47-54`), so it falls to **`BeanCodec`**: identity id,
  class id, fields in declaration order, no field names (`graph-serialization/.../BeanCodec.kt:29-36`,
  `BeanPropertyWriter.kt:43-58`). The `byte[]` goes through `KryoBackedEncoder.writeBinary`: length + raw bytes, no
  compression (`platforms/core-runtime/serialization/.../KryoBackedEncoder.java:114-115`).
- Java serialization is never stored as an `ObjectOutputStream` blob: `writeObject` hooks are replayed into the CC
  encoder (`jos/RecordingObjectOutputStream.kt:51-79`, `JavaObjectSerializationCodec.kt:130-196`). None of IntelliJ's
  `org.jetbrains.plugins.gradle.model` classes defines such hooks (javap over the IDE jar), so the objects form is
  bean-encoded throughout. Per `DefaultExternalLibraryDependency` record: ~5 bytes bean header + 9 fields × (tag +
  1–3 bytes) + the nested id bean ≈ 45–55 bytes with all strings already in the table.
- Identity dedup is per isolate = per task (`TaskNodeCodec.kt:345`, `Contexts.kt:416-419`): an object referenced twice
  in one task's state is written once; distinct-but-equal objects are written in full. Nothing in the codec chain
  compresses; files are then encrypted (`DefaultEncryptionService.kt:44`), so deflate-before-store is the only
  compression point.

## 2. String deduplication

One `ParallelStringEncoder` per work-graph state (`DefaultConfigurationCacheIO.kt:265-281`, child contexts get
`baseContext.currentStringEncoder` at `:638-643`, included builds at `:236-239`), on by default
(`org.gradle.internal.configuration-cache.deduplicate-strings`). Every `writeString` goes through it
(`Contexts.kt:103-107`), including `File` paths and class names; ids are varints (2–3 bytes beyond 127/16k strings).
A second mechanism, `writeSharedObject` into `.globals.work.bin`, is used only by codecs that opt in
(`BuildServiceParameterCodec`, `ValueSourceProviderCodec`), not by `BeanCodec`. `ResolvedComponentResult` graphs and
`ImmutableAttributes` are shared per project file, not across projects.

## 3. Input snapshotting

- `@Input Property<String>`: not evaluated at graph time (`DefaultTaskInputs.visitDependencies` only asks for
  producers); at execution unpacked and snapshotted as a `StringValueSnapshot`, the full string stored in the execution
  history (`SnapshotSerializer.java:155-157`). Negligible for our fingerprint.
- `@Internal`: `NoOpPropertyAnnotationHandler`, never unpacked, fingerprinted or validated; costs only entry bytes and
  heap at load.
- `@Nested` at graph calculation: unpacked with `getOrNull()` (`AbstractTypeMetadataWalker.java:137-146`) inside
  `LocalTaskNode.resolveDependencies`, called from the single-threaded `DefaultExecutionPlan.discoverNodeRelationships`
  loop (`DefaultExecutionPlan.java:168-218`). That was the 20 s of D16.
- Providers recomputed at execution (sanctioned): a `ValueSource` not obtained during configuration (stored as class +
  parameters, `DefaultValueSourceProviderFactory.java:243-249`; the fingerprint writer is closed after commit, so an
  execution-time `obtain()` invalidates nothing); `BuildService` (only parameters stored, once, as a shared object;
  instance recreated at first use, `RegisteredBuildServiceProvider.java:137-167`); providers with task producers
  (changing value). A `ValueSource` has no `Project` and cannot resolve dependencies, and its value is recomputed on every
  execution: not a fit for the models.

## 4. Native resolution results in the entry

Documented CC-compatible forms (`configuration_cache_requirements.adoc:124-131`, `configuration_cache_adoption.adoc:314-336`):
`Provider<ResolvedComponentResult>` from `getRootComponent()`, `ResolutionResult`, `ArtifactCollection.getResolvedArtifacts()`.
- `@InputFiles FileCollection`: reduced to file paths at store (`FileCollectionCodec.kt:61-84`); no coordinates.
- `getResolvedArtifacts()`: a `BuildableBackedProvider`; with task producers written as a changing value but the
  `ArtifactCollection` inside is captured at store as `FixedFileArtifactSpec`s (id bean, variant, attributes, file;
  `dm/ArtifactCollectionCodec.kt:52-58, 118-128`); without producers the set is written directly. Resolution happens
  at store either way. As an `@Input*` it would add the jar tasks as dependencies ("sync never builds"); as `@Internal` not.
- `getRootComponent()`: a `DefaultProvider`, fixed at store; the whole graph is written once per configuration per
  project file (`dm/ResolvedGraphResultCodec.kt:34-51`, `DefaultGraphStructureCodec.kt:43-105`), attributes shared per
  project file. Size estimate (**not verified**): ~25 KB per configuration graph, 125–300 KB per project plus ~36 KB of
  artifact results per source set: the same order as IntelliJ's payloads (286 + 177 KB deflated per project).
- Rebuilding `GradleSourceSetDependencyModel` in the action from these: feasible but a reimplementation of IntelliJ's
  resolver (`GradleSourceSetDependencyResolver`, `GradleDependencyResolver`, `AuxiliaryArtifactResolverImpl`: per
  source set compile/runtime/provided classpaths, `LenientConfiguration` iteration order for `classpathOrder`,
  unresolved entries with their exception text, sources/javadoc via 4 reselecting `ArtifactView`s, project
  dependencies mapped to source sets through the shared artifact index of D16). The 287/287 ordered comparison would
  have to be re-earned.

## 5. Options

| | What | Expected effect | Effort / risk | Verified |
|---|---|---|---|---|
| (i) | keep deflated bytes (status quo) | entry 56 MB; store dominated by the capture (20 s of resolution summed), not serialization | none | yes |
| (ii) | compact own record per project at capture (artifact table + small-int records), expanded into IntelliJ objects in the action | gain is structure, strings are already global: a record as ~8–12 bytes instead of ~20–40 (deflated Ion) or ~50 (bean); dependency models 24 MB → 5–8 MB, classpath models 12 MB → ~0.5 MB; entry maybe 56 → 20–25 MB; store CPU unchanged; load somewhat faster | medium: a codec per model class in the prototype; fidelity tools exist | sizes **not verified** |
| (iii) | build-level shared table (BuildService parameters, written once as a shared object; or a root task output) | small on top of (ii): strings are already shared; a service's fields do not survive the store, only its parameters | design **not verified** | parameter sharing verified |
| (iv) | native resolution results in task state, IntelliJ model built in the action | resolution still at store (fixed providers), size the same order as today, IntelliJ's resolver logic reimplemented | high; fidelity risk | codecs verified |
| (v) | codec tricks | `byte[]` raw, nothing compresses, `transient` skipped, `writeObject` replayed: trimming fields means changing IDE classes; shared-object dedup not reachable from user beans | small | verified |
| (vi) | **drop `ProjectDependencies` from the sync** (main agent): populated unconditionally by `JavaGradleProjectResolver.populateDependenciesGraphData`, consumed only by the Dependency Analyzer tool window | −13.7 MB stored (−47 MB inflated, 28 % of the bundle bytes), −4.7 s summed capture; the analyzer would need to fetch it on demand | IDE-side contract change | consumer verified by jar scan |

None of the options changes the 6 s store: the capture (resolution + task realization) runs inside it. Shrinking the
entry helps the hit's load (1.4–2.5 s for 56 MB) and the IDE's read, not the miss.

## Within-project shape of the models (main agent, `:core`)

`GradleSourceSetDependencyModel` of `:core`: 6 source sets, 1295 dependency records for **399 distinct coordinates**
and 183 distinct library files; 2678 absolute path strings (314 KB) of which 959 distinct (109 KB). Fields per record:
id (group, name, version, packaging, classifier), scope, selectionReason, classpathOrder, exported, dependencies,
and per kind file/source/javadoc or projectPath/configurationName/projectDependencyArtifacts(+Sources).
`ProjectDependencies` of `:core`: the full resolution graph per scope of every source set with `ReferenceNode`s
(354 KB inflated, 107 KB deflated, 4 MB as text). This is the model shape, not the task's doing.
