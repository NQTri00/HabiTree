# Gameplay verification

The Compose/Koin app uses platform SQLite behind shared KMP GameSqlConnection. Transaction tests use isolated real SQLite and injected clocks. Production DI and player profiles are preserved.

Run `./gradlew :data:connectedDebugAndroidTest` with a connected emulator. Tests live in:
- `data/src/androidTest/java/com/mamxanh/app/data/PhaseOneTransactionsTest.kt`
- `data/src/androidTest/java/com/mamxanh/app/data/PhaseTwoLifecycleTest.kt`
- `data/src/androidTest/java/com/mamxanh/app/data/PhaseThreeEngagementTest.kt`

Build APKs using `:app:assembleDebug :app:assembleVerification -Pmamxanh.verificationSuffix=.verification.phasethree`. Compile shared targets using `:shared:compileCommonMainKotlinMetadata :shared:compileKotlinIosArm64`. Native simulator runtime requires macOS and is not claimed on this Windows host.

Phase 1 covers task occurrence/receipt uniqueness, the shared daily income cap, partial grants, spending, timer intervals/restart, schedules, windows/timezones, prospective edits, Daily Clear, migrations and planting.

Phase 2 covers exact watering expiry, durable-can ownership/future-only upgrades, Water All eligibility and retries, fertilizer overlap/nonstacking/rolling cooldown, precise neglect/recovery and irreversible death, midnight/DST, pinned planting timezone, restart/offline integration, clock rollback, capped/idempotent sales/removal, concurrent care/disposition, legacy migration grace, and universal 1.4× cumulative stage thresholds. Simulations cover 14/30/90/180 days; Legendary requires 127 distinct credited days plus its precise elapsed floor. Fractional growth effort is tested separately from daily credits.

Phase 3 adds 21 real-SQLite cases for onboarding/free starter preservation, copied template deduplication, durable attached/standalone focus and restart/overnight behavior, no absent-time credit, partial-session badge rules, planned rest/protection receipts, real weekly aggregates/reflections, unique achievements/memories, opt-in quiet/window-aware reminders, rollback and schema5 migration. The combined suite contains 81 tests.

UI verification uses the isolated verification package, phone widths/font scales, and full scroll ranges. Main-profile checks avoid purchases, task completions and tree care/dispositions. Do not add mass preview renders; the user's preview-heap constraint takes precedence over the adaptive skill's suggested screenshot infrastructure. Device actions and Gradle verification run sequentially.

Plans and acceptance evidence:
- `docs/superpowers/plans/2026-11-11-phase-1-execution.md`
- `docs/superpowers/plans/2026-11-11-phase-2-execution.md`
- `docs/superpowers/plans/2026-11-12-phase-3-execution.md`

Tree stage art, growth animations and tree transitions are explicitly deferred by the user. Existing static art is temporary. Distinguish deferred and unexecuted checks from passing checks.
