# IntelliJ IDEA 2026.2.2 (262.10315.125): stock sync vs task sync with the import fixes (2026-10-17)

Restart scenario as in `stable-NOTES.md` (fresh IDE system dir per run, Gradle caches and configuration cache kept,
daemon stopped before each run; one shared IDE config dir for both configurations, so the same IDE settings including
2026.2's download-sources default), three rounds round-robin, headless `idea.sh warmup` on gradle/gradle. Stock = the
unpatched 2026.2.2 install; tasks + fixes = the Mac kit's content installed on Linux (`idea-patched-262`): the four
patched classes, the plugin, `-Dwbs.transport.run=only`. Scripts and stage lines: `results/m5-ide/stable-262/`.

| Configuration | Gradle side (resolve stage) | IntelliJ side (rest of the import) | of which data import stage | whole import |
|---|---|---|---|---|
| stock 2026.2.2 | 62.6 / 48.0 / 48.2 → **48.2 s** | 24.2 / 24.8 / 25.4 → **24.8 s** | 20.8 / 21.2 / 22.0 → **21.2 s** | 1 m 26.8 / 1 m 12.7 / 1 m 13.5 → **1 m 13.5 s** |
| tasks + fixes 2026.2.2 (rounds 4–6) | 10.2 / 10.5 / 10.5 → **10.5 s** | 21.6 / 19.4 / 21.9 → **21.6 s** | 16.1 / 16.0 / 16.2 → **16.1 s** | 31.8 / 29.9 / 32.5 → **31.8 s** |
| speed-up (medians) | **−78 %** | −13 % | −24 % | **−57 %** |
| rounds 1–3, one import fix missing (see below) | 10.3 / 10.1 / 10.6 → 10.3 s | 22.8 / 22.0 / 21.7 → 22.0 s | 19.7 / 18.8 / 18.0 → 18.8 s | 33.1 / 32.2 / 32.3 → 32.3 s |

Rounds 1–3 ran with only the `LibraryDependencyDataService` fix active: in the 2026.2 layout both platform classes
live in `lib/intellij.platform.externalSystem.impl.jar`, and `install-patched-ide.sh` re-copied that jar from the
stock IDE for its second overlay, wiping the first (`AbstractIdeModifiableModelsProvider`, the memoized library
lookup). Found by a JFR run (229 of 293 samples of "project repository data" in the unmemoized `findIdeLibrary` scan)
and by the class size in the installed jar; fixed (each jar copied once per run), re-measured as rounds 4–6. The Mac
kit's `install-mac.sh` adds to the copied jar cumulatively and never had the problem.

For comparison, 2025.3 (253.28294.334, `stable-NOTES.md`): stock 51.8 / 27.1 / 1 m 20.4 s; tasks + fixes 10.0 / 19.0 /
29.2 s (−81 % / −30 % / −64 %).

## The two remaining model requests

- `GradleDslBaseScriptModel`, which 2026.2 requests (stock: first thing inside the action; ours: on the plain-run
  connection before the task run, `[wbs] GradleDslBaseScriptModel fetched`): **81 / 83 / 95 ms** on the warm daemon of
  the restart runs. It does not affect the comparison; the Gradle side is 10.3 s on both IDE versions.
- `GradleBuild`: 0.25–0.35 s warm (`phases-NOTES.md`). The plain task run itself: 5.7 / 6.3 / 6.3 s of the 10.3 s
  resolve stage; the rest is daemon start and the two requests on a daemon that was stopped before the run.

Nothing else is requested in the single-invocation mode: the stock action's bootstrap (`GradleDslBaseScriptModel`,
`GradleBuild`, nested builds, `GradleDaemonClassLoaderModel`, which hands the action the daemon-side classloader of
IntelliJ's tooling jars so `ToolingSerializerConverter` can serialize models with IntelliJ's serializers) never runs,
because the action never runs (`GradleModelFetchAction.java` 262, lines 169-196).

## Reading

- Gradle side: the same −79/−81 % on both IDE versions; the task run is the same work.
- IntelliJ side: 2026.2.2 stock is 2.3 s faster than 2025.3 stock (24.8 vs 27.1 s). With both fixes the data import
  stage is 16.1 s (2025.3: 15.8 s) and the per-step picture matches 2025.3: library dependency data 8.6 s, project
  repository data 3.0 s (5.7 s with the memoized lookup missing), library data 1.3 s. The rest of the IntelliJ side
  outside the data import stage is 5.5 s on 2026.2.2 against 3.2 s on 2025.3 (not investigated).
- Whole import: 1 m 13.5 → 31.8 s (−57 %) on 2026.2.2, vs 1 m 20.4 → 29.2 s (−64 %) on 2025.3; the smaller
  percentage is mostly the faster stock (1 m 13.5 vs 1 m 20.4 s).
