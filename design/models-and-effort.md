# Models and effort

## Policy

| Work | Model | Effort | Why |
|---|---|---|---|
| Lead session: planning, gates, design decisions, M2 (contract + first hard port), hard ports in M3, interpreting results, writing REPORT.md | Fable 5.1 (`claude-fable-5-1`) | high | judgment-heavy; mistakes here cost days |
| Mechanical ports (M3 mechanical group), harness code (M1) | Opus 5.5 (`claude-opus-5-5`) | high | well-specified by the contract; the comparison test catches errors |
| Benchmark runs, log aggregation, environment chores | Sonnet 5.5 (`claude-sonnet-5-5`) | medium | procedural, mostly waiting on builds |
| Stuck debugging (same failure after two attempts) | Fable 5.1 | xhigh for that investigation only | raise effort, not retries |
| Anything | Haiku | never | not enough context for this codebase |

Effort levels above `high` are for specific investigations, not for whole sessions. If a port fails its
comparison test twice, hand the question to the `investigator` subagent rather than retrying.

## How it is enforced, and its limits

- `.claude/settings.json` in this repo sets `model: claude-fable-5-1` and `effortLevel: high` for every
  session started in the repo. Project settings override the user's saved `/model` default. A user can
  still switch with `/model` or `/effort` inside a session; a project file cannot lock that (only managed
  settings can). This is acceptable: the policy is a default and a documented expectation, not a lock.
- `.claude/agents/*.md` define the subagents with `model:` and `effort:` in their frontmatter:
  `port-model` (Opus 5.5, high), `benchmark-runner` (Sonnet 5.5, medium), `investigator` (Fable 5.1,
  high). The lead session delegates to these by name instead of ad-hoc subagents, so the policy travels
  with the task.
- Do not set `CLAUDE_CODE_EFFORT_LEVEL` in the environment: it overrides every other effort setting,
  including the per-agent ones.
- Full model IDs are used instead of aliases so the policy means the same model on every provider.
- Personal overrides go in `.claude/settings.local.json` (git-ignored), not in the shared file.

## When to deviate

Write it in `DECISIONS.md`: which work, which model/effort, why. Typical reason: a "mechanical" port turns
out to need design (shared state, reflection into Gradle internals); then it moves to the lead session.
