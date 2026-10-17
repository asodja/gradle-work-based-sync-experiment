# gradle-profiler `idea-sync` with the kit (2026.2.2, headless on this machine, 2026-10-17)

gradle-profiler 0.26.0 supports IntelliJ IDEA 2026.1+ (`idea-sync` scenarios, `--idea-install-dir`, `--idea-sandbox-dir`).
How it sandboxes the IDE (`org.gradle.profiler.ide.*`): it starts the launcher (`Contents/MacOS/idea`, `bin/idea.sh`;
with `headless-starter` when no display) with `IDEA_VM_OPTIONS` pointing at a generated vmoptions file that holds
`-Didea.config.path/-Didea.system.path/-Didea.plugins.path/-Didea.log.path` for `<sandbox>/{config,system,plugins,logs}`,
`-Didea.trust.all.projects=true`, `-Didea.gradle.distributionType=BUNDLED`, `external.system.auto.import.disabled=true`,
its `-javaagent` and the scenario's `ide-jvm-args`; it copies its `gradle-profiler-ide-plugin` into `<sandbox>/plugins`
and removes only that folder afterwards (`IdeSandboxCreator` never cleans, `IdePluginInstaller` touches only its own
directory). Its agent instruments `DefaultPhasedBuildActionExecuter.run` and `DefaultGradleConnector.connect` only.

Consequences for the kit, all handled by `profiler/run-idea-sync.sh` and the static `profiler/{wbs,stock}.scenarios`:
- `IDEA_VM_OPTIONS` replaces the app's `Contents/bin/idea.vmoptions`: the `-Dwbs.*` options go into `ide-jvm-args`.
- The sandbox's plugins dir replaces the user plugin dir: the plugin is copied into `<sandbox>/plugins` beforehand.
- The patched classes are in the WBS app's jars: pass the WBS app as `--idea-install-dir`.
- The agent does not see the plain task run or the model requests: for WBS the "Gradle execution time" is 0 ms and the
  profiler cannot inject `--gradle-version`; the total sync time is measured the same way for both IDEs.
- The IDE stays open across a scenario's iterations: iterations 2+ are same-session re-syncs with a warm daemon, and
  the stock sync hits the configuration cache there too (its action's classloader id only changes on an IDE restart).

## Results (gradle/gradle, Gradle 9.9.0-milestone-2, 1 warm-up + 3 iterations, fresh sandboxes, headless starter)

| Scenario | stock: total (Gradle / IDE) | WBS (tree mode): total (task run) |
|---|---|---|
| `sync_hit`, same session | 35.0 (1.1 / 33.9), 51.5 (1.4 / 50.2), 50.7 (0.8 / 49.9) s | 25.3, 39.5, 40.1 s (1.3–2.5 s) |
| `sync_miss_settings`, same session | 70.8 (14.0 / 56.7), 65.9 (12.5 / 53.4), 65.3 (13.3 / 51.9) s | 43.2, 41.9, 41.7 s (12.0, 10.3, 9.7 s) |
| warm-up (first import, store) | 107.2 (40.5 / 66.6) s hit scenario; 89.7 (33.9 / 55.8) s miss scenario | 111.0 s; 65.2 s |

Reading: on a same-session settings-script miss the kit is 65.9 → 41.9 s (−36 %); the Gradle side is 12.5–14.0 s stock
vs 9.7–12.0 s WBS (warm daemon), the IDE side 52–57 s stock vs ~30 s WBS. On the same-session hit both sides hit the
cache and the difference is the IDE side only. The IDE side of same-session re-syncs is far larger than in the restart
imports (`stable-262-NOTES.md`: 13.5–25 s), for both IDEs: the re-import into an existing project model plus the
sandbox's initial indexing. Three iterations with the sandbox indexing in the background are noisy (e.g. 25 vs 40 s).
No errors in either sandbox log; no `removeContentEntry` error in these re-syncs.
