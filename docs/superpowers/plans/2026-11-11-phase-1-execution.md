# Phase 1 execution plan and instructions

Goal: satisfy docs/phase-1-detailed-concept.md and its acceptance matrix while preserving existing player data and approved mint UI. Economy v2 controls rewards and prices. Phase 2 growth/death/watering-window mechanics remain deferred.

1. Add additive schema migration and shared contracts for task configuration, immutable occurrences, timer sessions, daily plans, capped wallet ledger, purchase receipts and care history.
2. Enforce atomic/idempotent completion, daily rewards, purchase, planting and care; preserve historical wealth and legacy IDs. Normalize legacy verification to CLICK. Centralize balance configuration.
3. Add task creation/editing/details/pause/archive, schedules, optional windows, reminders and foreground-only recoverable timers.
4. Make shared state changes reactive and date refresh lifecycle-driven. Add Home progress/next actions, quantity confirmations and Journal histories.
5. Verify against real SQLite, migration from v3, cap boundaries, retries, schedules, overnight/DST windows, timer restart/background, navigation, screen sizes and offline persistence. Build Android and available KMP targets; publish successful checkpoints.

Policies: the first read materializes a day's required recurring tasks. Midday additions start tomorrow's required snapshot but are immediately optional today; edits preserve existing snapshots. Pausing/archive removes pending obligations without removing audit history. Rest days are neutral. Local dates never move backward for reward eligibility; previously claimed occurrence/bonus keys remain unique. Timer time is credited only by short foreground heartbeats, never inferred from time spent backgrounded or killed. Window anchors and timezone are retained for verification. No user data reset or destructive main-profile QA.

## Verification checkpoint

32 real-SQLite instrumented tests passed on API37, with zero failures/errors/skips. Coverage includes partial reward at46/50, duplicate completion, concurrent taps, purchase quantity/retry, rest days, snapshots, pause/archive, overnight windows and prospective window edits, DST, monotonic timer credit, clock rollback, a v3 migration fixture reopening a file-backed database, catalog price identity and planting every sold seed. Android Debug/Verification, common metadata and iOS ARM64 Kotlin compilation passed together (latest41 seconds). iOS runtime was not executed on Windows.

Emulator checks so far: full task form scroll range and mint controls; create a5-minute Timer; active progress1:58 retained and paused after backgrounding. Home follow-up cards moved below the featured tree. Final phone-size, migration-on-existing-profile and navigation checks are in progress.

Track acceptance evidence here as work proceeds; do not label unexecuted checks as passed. Use isolated verification profiles for mutation tests. Preserve unrelated staged deletions and edits. Render only selected previews when needed.
