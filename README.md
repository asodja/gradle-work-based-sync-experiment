# Work-based sync prototype

Experiment: does IntelliJ's Gradle sync get faster when IntelliJ's per-project tooling
models are produced by Gradle **tasks that write files**, instead of tooling model
builders that run inside the daemon's model-building phase?

Benchmark build: **gradle/gradle** (254 projects, Kotlin DSL, 3 included build-logic
builds). Headline metric: a **warm re-sync with the configuration cache on**. Cold
sync is a baseline, not a target; see `design/measurement.md` for why.

Deliverables:

1. `results/REPORT.md`: comparison table for four scenarios, three runs each, model
   builders vs tasks, plus the list of blockers found.
2. The prototype itself on branches `work-based-sync` of the IntelliJ Community fork
   and (only if needed) the Gradle fork.

## How to work on this

Read in this order: `PROGRESS.md`, then `design/plan.md`, `design/context.md`,
`design/task-contract.md`, `design/model-inventory.md`, `design/measurement.md`,
`design/building.md`. Everything learned so far is in those files. Do not rediscover it.

**Work independently.** Make routine decisions yourself and write them down in
`DECISIONS.md` with a one-line rationale. The plan has gates with numeric criteria;
apply them yourself and record the outcome. Ask the owner only for a *real blocker*:
something you cannot resolve with the information and access you have, such as a
missing machine, missing credentials, a design question where the two options lead
to materially different work and the context does not settle it, or a gate whose
criterion is met but the data looks wrong. Put the question in `PROGRESS.md` under
"Blocked on owner", keep working on anything that does not depend on the answer, and
state your assumption if you continue past it.

**Track progress so another agent can continue tomorrow.** Work may be stopped at any
point and resumed by a different agent on a different day. Before ending any session,
and after any milestone or sub-step, update `PROGRESS.md`: what is done, what is in
flight and exactly where (branch, commit, file, failing test), what the next step is,
what is blocked. Append dated entries to `LOG.md` for anything measured, decided or
found. A resuming agent must be able to continue from `PROGRESS.md` alone, without the
previous agent's conversation.

**Rules that are not negotiable**

- Model tasks never pull build work into the sync: no compilation, jars, code generation
  or publishing. Use `getLocationOnly()` for file providers and `@Input` strings for
  paths. Dependencies on other model tasks and on the IDE's own prepare tasks
  (`prepareKotlinIdeaImport`, `prepareKotlinBuildScriptModel`) are allowed; the base task
  class fails on any other task dependency and names it. "Sync never builds" is the
  property the whole experiment rests on; see `design/task-contract.md` for why.
- IntelliJ resolvers and data services stay untouched. Change producers and transport.
- Every ported model has a field-by-field comparison test against the builder output
  on a real import. Never weaken a comparison to make a port pass; record the
  difference and decide whether it matters.
- Never report a cold-sync number as a win. The headline is warm with CC on.
- Builds and benchmark runs go to the background. Never run two Gradle builds in one
  checkout at the same time.
- Commit per port, small, with a subject line that says what changed. Push to the
  fork branches at the end of every session.

**Out of scope**: resilient sync (a separate prototype exists, see `design/context.md`),
Kotlin Multiplatform, Gradle-owned build-scope models, public API, a full IntelliJ
Community build (use plugin-only Bazel targets or the import test harness).

## Layout

```
README.md                 this brief
PROGRESS.md               current state, next step, blockers  (keep current!)
LOG.md                    dated journal of measurements, findings, decisions
DECISIONS.md              design decisions with rationale
design/plan.md            milestones, exit criteria, gates
design/context.md         everything known: IntelliJ sync internals, Gradle internals, pitfalls
design/model-inventory.md every model IntelliJ requests, owner, scope, cost, plan
design/task-contract.md   task naming, inputs, outputs, serialization, registration, transport
design/measurement.md     scenarios, metrics, harness, noise control, gradle/gradle specifics
design/building.md        how to build and wire the Gradle distribution and IntelliJ plugins
harness/                  the Tooling API measurement harness (milestone 1)
results/                  raw numbers per run and the final REPORT.md
```

## License

Apache License 2.0, see `LICENSE`. The files under `ide-plugin/src/patched/` and `ide-plugin/src/patched-262/`
are modified copies of IntelliJ Community sources (Apache 2.0, copyright JetBrains s.r.o. and contributors);
the modifications are marked `wbs` in the code. `dist/` and the kit contain compiled versions of them.
