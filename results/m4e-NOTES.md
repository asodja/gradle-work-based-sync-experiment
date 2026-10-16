# m4e: timing round of the final D17 prototype (2026-10-16, 05:18–05:35)

Prototype: idea extension read when applied, every Kotlin DSL script per project (build script, precompiled plugins;
settings and init scripts on the root project), stage-1 accessors warm-up. Scenarios 2, 3, 4, three runs each,
configuration cache directory deleted before each variant.

| Variant | Warm plain (2) | IP hit (3) | Edit (4) |
|---|---|---|---|
| tasks | 23.7 s (23.2–25.9) | 2.5 s (2.1–3.3) | 22.3 s (19.4–27.8) |
| tasks-only | 26.1 s (24.9–46.6) | 6.0 s (3.6–8.7) | 20.2 s (17.3–21.9) |
| launcher | 32.2 s (26.0–34.4) | 4.4 s (2.8–4.7) | 23.7 s (20.0–29.5) |
| m4c floor (tasks-only, no Gradle-owned captures) | 14.6 s | 1.6 s | 9.4 s |

Not the prototype's work but the network: the Kotlin DSL script capture waits for Gradle's distribution-sources
lookup on services.gradle.org (~10 s per build on today's connection, all capture threads queued behind one lock);
attribution and thread samples in LOG 2026-10-16. The slow and erratic cache hits are the 389 MB configuration cache
entry (script payloads), not network. Superseded by `m4f-*` (distribution sources resolved once before the captures).
