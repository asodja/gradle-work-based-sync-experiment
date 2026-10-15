# Environment: this machine

The experiment runs on this machine unless a milestone is impossible here. Measured 2026-10-15.

| | |
|---|---|
| Host | `gradle-ai-workspace`, Ubuntu 26.04, **aarch64** (ARM64), Linux 7.0 |
| CPU / RAM | 8 cores, 31 GB RAM |
| Disk | 89 GB total, **28 GB free** after the 2026-10-15 cleanup (see budget below) |
| JDKs | `/usr/lib/jvm/java-17-openjdk-arm64`, `java-25-openjdk-arm64` (default), `java-8-openjdk-arm64` |
| Network | outbound HTTPS through a proxy (`JAVA_TOOL_OPTIONS` already carries the proxy settings); github.com and download.jetbrains.com reachable |
| IntelliJ | **not installed**. Download IntelliJ IDEA Community for Linux aarch64 from download.jetbrains.com into `~/tools/ideaIC/` (≈1.3 GB download, ≈2.5 GB unpacked). Its `plugins/gradle/lib` and `plugins/Kotlin/lib` provide the tooling jars for the harness. |
| gradle/gradle | main checkout `~/projects/gradle` (master, shared with other sessions; **do not build or sync in it**). Make a dedicated worktree for the benchmark, pinned to one commit: `cd ~/projects/gradle && git worktree add ../gradle-wbs-benchmark <commit>`. Other `~/projects/gradle-*` directories are other sessions' worktrees: do not touch them. |
| Gradle user home | `~/.gradle` (16 GB, shared). Use a separate `-g ~/.gradle-wbs` for benchmark runs so caches and daemons are isolated and reproducible. |
| Remote build cache | not reachable from this sandbox: every `./gradlew` invocation in gradle/gradle needs `--no-build-cache`. |
| Resilient prototype | worktree `~/projects/gradle-Work-based-sync` on branch `asodja/prototype-resilient-task-execution`; out of scope, leave it alone. |

## Disk budget

Needed on top of the current state:

| Item | Size |
|---|---|
| IntelliJ Community download + unpack | ≈4 GB |
| benchmark worktree of gradle/gradle, synced with CC on | ≈3 GB |
| Gradle distribution built from that worktree (`binDist`) | ≈2 GB build output |
| IntelliJ Community fork, shallow clone (M3 onward) | ≈5–8 GB |
| Bazel outputs for a plugin-only build (M5) | unknown, likely 10 GB+ |

So M0–M2 fit in the free space only barely; M3 needs more; M5 does not fit without reclaiming.
Reclaimable, in order of safety:

1. Prunable git worktrees under `/tmp/gradle-provenance-*` (`git worktree prune` in `~/projects/gradle`, small).
2. `/tmp/claude-1000` (5.6 GB): scratch directories of agent sessions. Safe to delete entries older than a
   day; keep the current session's.
3. `~/.cache/JetBrains` (14 GB): inspect what it is before deleting. **Owner approval required.**
4. Build outputs inside other sessions' worktrees (`intTestHomeDir`, `build/`): **owner approval required.**

Record every deletion in LOG.md. If, after items 1–2, M3 or M5 still does not fit, that is a real blocker:
put it in `PROGRESS.md` under "Blocked on owner" with the numbers, and continue with whatever does fit.

## Known sandbox pitfalls

- A grep/sed with an empty file argument blocks on stdin; guard `$(git grep -l …)` results.
- `pkill -f <pattern>` matches your own shell when the pattern is in your command line.
- The Gradle integration test harness runs daemons with `-ea`; see `design/context.md` §4.
- GitHub API calls through `gh` share a rate limit with other sessions; prefer raw.githubusercontent.com
  for reading IntelliJ sources.

## Which milestones are possible here

- M0, M1, M2, M4: yes.
- M3: yes once the IntelliJ fork fits (reclaim first).
- M5: the plugin-only Bazel build is doubtful on 9 GB free and unverified on aarch64; the import-test-harness
  route needs the same checkout plus test dependencies. Treat M5 as "attempt after reclaiming; if it does
  not fit, report the exact shortfall as a blocker" rather than as a given.
