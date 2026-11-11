# Phase 1 testing strategy

The app uses Compose, Koin and platform SQLite behind shared KMP GameSqlConnection. It previously had no test source set. Production DI is retained. Domain transaction tests use an isolated real in-memory Android SQLite database and an injected clock, without mocking SQLite or modifying player profiles.

Run `./gradlew :data:connectedDebugAndroidTest` on a connected emulator. Tests live in data/src/androidTest/java/com/mamxanh/app/data/PhaseOneTransactionsTest.kt. Build with `:app:assembleDebug :app:assembleVerification -Pmamxanh.verificationSuffix=.verification.phaseone`. Shared/native compilation is checked separately; iOS simulator runtime requires macOS.

Test priorities: occurrence and receipt uniqueness, daily cap including partial grants, spending, timer active intervals and restart, scheduled/rest days, windows and timezones, edits/pause/archive, snapshot Daily Clear, migrations, planting, care resource invariants. UI is checked on the isolated verification package at phone widths and large font settings, scrolling every long screen to its end. No mass preview or screenshot-test framework is added because the user explicitly requires minimal preview heap use. Device interaction and Gradle tasks run sequentially.

Acceptance evidence is maintained in docs/superpowers/plans/2026-11-11-phase-1-execution.md. Failed or unexecuted checks must remain distinguishable from passed ones.
