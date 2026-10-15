# Work-based sync prototype

You are continuing a multi-day experiment. Read `README.md` (the brief and rules), then `PROGRESS.md`
(where the previous agent stopped), and continue from there. Do not re-derive context that is in `design/`.

Work independently. Record decisions in `DECISIONS.md`, measurements and findings in `LOG.md`, and
update `PROGRESS.md` before you stop so the next agent can resume from it alone. Ask the owner only for
a real blocker as defined in `README.md`.

Model and effort policy: `design/models-and-effort.md`. This repo's `.claude/settings.json` and
`.claude/agents/` implement it; use the defined subagents for delegated work instead of ad-hoc ones.

Environment: this machine, see `design/environment.md`. Never run two Gradle builds in one checkout at
once; run long builds and benchmark runs in the background.
