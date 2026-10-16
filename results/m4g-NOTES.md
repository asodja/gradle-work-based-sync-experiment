# m4g: network-free round, partial (2026-10-16, 06:10–06:21)

D18 in place: the benchmark distribution has a `src` directory, so Gradle's script model never looks the
distribution sources up online. Scenarios 2, 3, 4, three runs each.

| Variant | Warm plain (2) | IP hit (3) | Edit (4) | distribution-sources lookups | cache dir |
|---|---|---|---|---|---|
| builders | 21.9 s (19.0–24.7) | 1.47 s (1.34–2.74) | 12.5 s (12.0–13.2) | 0 of 15 runs | 568 MB, 11 entries (~50 MB each) |
| tasks | 27.1 s (24.9–44.7) | 3.7 s (3.4–6.0) | 25.9 s (23.3–28.6) | **12 of 15 runs** | 1.7 GB, 11 entries (~155 MB each) |

The builders row is the network-free baseline (M4: 21.4 / 1.65 / 14.1 s, with the lookup). The tasks row is **not**
network-free: the prototype's warm-up of the lookup (D17 amendment 4) called `SourceDistributionResolver` directly
and bypassed Gradle's `src` check, so it made the very request the directory was meant to avoid, serially during
configuration (20 s before `projectsLoaded`). Fixed (the warm-up now returns when `<gradleHome>/src` exists); the
round was stopped before `tasks-only` and `launcher` and repeated as `m4h-*` together with D19. Two facts stand
regardless: the task world's cache entry is three times the builders' (155 vs 50 MB), and its hit is 3.7 s against
1.47 s, which is what D19 (deflated payloads) targets.
