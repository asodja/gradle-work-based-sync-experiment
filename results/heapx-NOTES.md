# Scenario 3 with an 8 GB daemon heap (2026-10-16, 07:21–07:25; `results/heapx-*/`)

gradle/gradle sets `-Xmx3300m`; here `--jvm-arg -Xmx8g` was appended for the daemon. One series per variant in a
fresh daemon: store (run 0) and three hits.

| Variant | hits | full GCs in the series | GC total |
|---|---|---|---|
| builders | 2.78, 2.34, 2.18 s (median 2.34) | 0 | 2.4 s |
| tasks-only | 5.01, 2.67, 5.11 s (median 5.0) | 0 | 3.0 s |
| tasks | 3.28, 3.09, 5.99 s (median 3.3) | 0 | (daemon line picked another JVM) |

Reading: the GC explanation from `heap-NOTES.md` does not hold. With no full collection at all, a `tasks-only` hit
still takes 2.7 s in one run and 5.0 s in the next, in the same client JVM against the same daemon, all of it before
`projectsLoaded`. Something in the hit path costs an extra 2–3 s in some runs: the configuration cache fingerprint
check and load, or the daemon-client protocol around the end of the previous build (daemon logs show "Timed out
waiting for finished message from client" entries). A traced hit (build operation trace, `hit-trace-log.run*.txt` in
the session scratchpad, LOG) is the next step and was run right after this series.
