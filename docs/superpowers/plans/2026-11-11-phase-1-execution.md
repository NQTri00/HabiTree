# Phase 1 execution plan and instructions

Goal: satisfy docs/phase-1-detailed-concept.md and its acceptance matrix while preserving existing player data and approved mint UI. Economy v2 controls rewards and prices. Phase 2 growth/death/watering-window mechanics remain deferred.

1. Add additive schema migration and shared contracts for task configuration, immutable occurrences, timer sessions, daily plans, capped wallet ledger, purchase receipts and care history.
2. Enforce atomic/idempotent completion, daily rewards, purchase, planting and care; preserve historical wealth and legacy IDs. Normalize legacy verification to CLICK. Centralize balance configuration.
3. Add task creation/editing/details/pause/archive, schedules, optional windows, reminders and foreground-only recoverable timers.
4. Make shared state changes reactive and date refresh lifecycle-driven. Add Home progress/next actions, quantity confirmations and Journal histories.
5. Verify against real SQLite, migration from v3, cap boundaries, retries, schedules, overnight/DST windows, timer restart/background, navigation, screen sizes and offline persistence. Build Android and available KMP targets; publish successful checkpoints.

Policies: the first read materializes a day's required recurring tasks. Midday additions start tomorrow's required snapshot but are immediately optional today; edits preserve existing snapshots. Pausing/archive removes pending obligations without removing audit history. Rest days are neutral. Local dates never move backward for reward eligibility; previously claimed occurrence/bonus keys remain unique. Timer time is credited only by short foreground heartbeats, never inferred from time spent backgrounded or killed. Window anchors and timezone are retained for verification. No user data reset or destructive main-profile QA.

## Verification checkpoint

35 real-SQLite instrumented tests passed on API37, with zero failures/errors/skips. Coverage includes partial reward at46/50, duplicate completion, concurrent taps, purchase quantity/retry, rest days, snapshots, pause/archive, overnight windows and prospective window edits, DST, monotonic timer credit, clock rollback, a v3 migration fixture, reopening a file-backed database, catalog price identity and planting every sold seed. Android Debug/Verification, common metadata and iOS ARM64 Kotlin compilation passed together (latest42 seconds). iOS runtime was not executed on Windows.

Emulator checks so far: full task form scroll range and mint controls; create a5-minute Timer; active progress1:58 retained and paused after backgrounding. Home follow-up cards moved below the featured tree. Final device checks passed:360dp task creation/details and full form scroll;320dp/font1.3× Shop confirmation scrolled to both action buttons; restored720×1280/font1×. Main profile reopened with115 coins,4 trees and6 unlocked plots; centered half-window tree retained. Inventory Back returned to Garden. Journal was inspected from header through its final footer, including legacy occurrences/ledger. Quantity2 fertilizer preview showed70 total and45 remaining from115; canceled without purchase. Isolated completion granted8 and Journal immediately showed1 completion and20% (1/5), without refresh. Main profile received no completion, purchase, planting or care mutations during QA.

## Acceptance matrix evidence — complete for the Phase1 compatibility scope

| Phase 1 scenario | Evidence / result |
|---|---|
| Weekday habit; neutral weekends | weekdayWeekendNeutralAndCustom; scheduledStreakSkipsRestDaysAndMilestoneIsUnique — passed |
| Edit completed habit, retain history | editingDoesNotErasePastOccurrence — passed |
| Complete twice / concurrent taps | clickExactlyOnceAndAtomicLedger; concurrentDoubleTapCommitsOnce — passed |
| Timer background/restart | pausedTimeNeverCountsAndRestartRetainsSession; real background pause at1:58; monotonic-clock test — passed |
|46/50 reward | capAt46GrantsFour — passed, nominal20/granted4 |
| Daily Clear once | dailyClearOnceWithSnapshotAndMiddayTask — passed |
| No required tasks | noRequiredTasksNoClear — passed |
| Pause recurring task | pausedObligationNeutralAndHistoryPreserved — passed |
| Purchase quantity2 and retry | quantityAndRetryAreAtomic; UI confirmation arithmetic — passed |
| Insufficient funds | unaffordableLeavesEverythingUnchanged; narrow-phone disabled confirmation — passed |
| Exact seed / exact plot | plantingExactPlotAndConcurrentRetry; all11 catalog seeds plant — passed |
| Cancel planting | Read-only selection leaves garden/inventory equal; existing confirmation path performs no mutation until confirm — passed |
| Repeated watering | careAtCapAndWaterRetry — passed |
| Fertilize at cap | careAtCapAndWaterRetry — passed; stock retained |
| Inventory Back | Existing profile Inventory→Back→Garden — passed |
| Kill/restart confirmed data | restartReopensConfirmedStateFromDisk; actual profile update/reopen — passed |
| Clock rollback | clockRollbackDoesNotReissue; timezone/DST/overnight cases — passed |
| Journal reactive update | Actual isolated completion8 coins→Journal1 completion/20%, no refresh — passed |
| Existing installation | existingVersionThreeMigratesWithoutLoss plus actual115-coin/4-tree/6-plot profile — passed |

Overnight Daily Clear has a one-day claim grace: the reward key remains the anchored plan date, while income is subject to the actual claim day's cap. Additional regressions cover that grace, same-day optional schedule edits, active timer edits, window expiry, seventh-day milestone uniqueness and UI/transaction catalog identity. No preview batch was rendered.

Scope resolution: Phase1 sections7 and12 explicitly retain legacy growth/water compatibility. Species stages,14/90-day growth credits, watering-can tiers, timed fertilizer and mature sales remain Phase2; this checkpoint does not claim those growth mechanics. Coins, caps, catalog prices, starter entitlement and plot costs use balance v2 now.

Track acceptance evidence here as work proceeds; do not label unexecuted checks as passed. Use isolated verification profiles for mutation tests. Preserve unrelated staged deletions and edits. Render only selected previews when needed.
