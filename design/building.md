# Building and wiring

## Gradle distribution

From a gradle/gradle checkout on the measured commit:

```
./gradlew binDist -q            # add --no-build-cache if the cache is unavailable
# -> packaging/distributions-full/build/distributions/gradle-<version>-bin.zip
```

Full build of all production code; tens of minutes. For a one-jar change (e.g. the request logger in
`:core`), build that jar and patch it into the unpacked distribution instead: `./gradlew :core:jar -q`,
copy `subprojects/core/build/libs/gradle-core-<version>.jar` over `lib/gradle-core-<version>.jar`, then
`gradle --stop`.

Point a project's wrapper at it in `gradle/wrapper/gradle-wrapper.properties`:

```
distributionUrl=file\:///abs/path/gradle-<version>-bin.zip
```

The wrapper caches by URL hash and the daemon by version: after rebuilding with the same version, delete
`~/.gradle/wrapper/dists/gradle-<version>-bin/` and stop daemons. Alternatively `./gradlew install
-Pgradle_installPath=<dir>` and use "Specified location" in IntelliJ.

Never run `./gradlew build` or a `clean` task on gradle/gradle; target specific tasks, use `-q`.

## IntelliJ Community fork

- Shallow clone (`--depth 1 --filter=blob:none` is fine), branch `work-based-sync`.
- The build is Bazel (`bazel-build-all-community.cmd` builds `//...`; do **not** do that). Build only the
  plugin targets you touch: the Gradle plugin modules under `plugins/gradle/` and the Kotlin Gradle
  modules under `plugins/kotlin/gradle/`. Find the exact labels in their `BUILD.bazel` files.
- Drop the built jars into an installed IntelliJ (same major version as the fork) under
  `plugins/gradle/lib/` and `plugins/Kotlin/lib/`, keep backups, restart the IDE. For the harness, point its
  classpath at the fork's built jars instead.
- Alternative for end-to-end numbers without an IDE build: IntelliJ's Gradle import test harness
  (`plugins/gradle/testSources`, `GradleImportingTestCase` and friends) runs real syncs with a real Gradle
  distribution; it is driven by `tests.cmd`. Check how it picks the Gradle version before relying on it.
- A full Community build is a last resort. It is a Bazel build of the whole monorepo with no documented RAM
  requirement; expect 16 GB minimum and 32 GB comfortable, plus tens of GB of disk. Record it as a blocker
  if it becomes necessary.

## Machine

- Milestones 0–4: anything that syncs gradle/gradle comfortably (16 GB RAM, fast disk). The daemon heap is
  dictated by gradle/gradle's `gradle.properties`.
- Milestone 5: plus the plugin-only Bazel build (estimate 8 GB) or the import test harness.
- Java: the gradle/gradle build needs the JDKs its `gradle.properties`/toolchains ask for; the harness runs
  on 17+.

## Checks before submitting Gradle changes (only if the Gradle fork is touched)

`./gradlew sanityCheck` is the full repository check; targeted: `:<module>:checkstyleMain`,
`:<module>:test --tests …`, `:<module>:forkingIntegTest --tests …`. Integration tests run daemons with
`-ea`; see `design/context.md` §4.
