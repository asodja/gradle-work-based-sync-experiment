# m4f: aborted round (2026-10-16, 06:00–06:08), only `m4f-tasks/`

Prototype as `m4e` plus the distribution-sources warm-up in the root project's `afterProject`. `tasks`: warm plain
32.7 s (23.4–33.8), hit 4.0 s (3.3–4.6), edit 32.4 s (27.3–33.2); 25 s of the warm plain run pass before
`projectsLoaded` (9 s in `m4e`): the network lookup of the Gradle distribution sources now runs serially during
configuration and the connection had degraded to ~16 s per request. The script capture itself was fine (10 s summed
over 287 projects, max 0.83 s). Aborted; the lookup is removed in D18 and the round repeated as `m4g-*` with all four
variants.
