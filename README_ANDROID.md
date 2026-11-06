# Mầm Xanh Android

Mầm Xanh is an offline-first, single-player habit and task game. Trees are the only progression objects; there is no player avatar or character-stat system.

## Architecture

The Android app is organized into data, domain, and presentation layers:

- `domain/model`: task, tree, garden, inventory, and statistics models.
- `domain/repository`: repository contract consumed by the domain/use-case layer.
- `domain/usecase`: application actions for tasks, rewards, planting, and garden progression.
- `data/local`: SQLite persistence and local database operations.
- `data/repository`: offline repository implementation backed by SQLite.
- `presentation/viewmodel`: ViewModel exposes observable screen state and coordinates use cases.
- `presentation/ui`: app shell, theme values, and screen navigation.
- `presentation/ui/screens`: separate task, garden, shop, and statistics screens.

Dependency direction: presentation -> domain; data implements domain repository contracts. UI screens do not query SQLite directly. All gameplay data is stored on-device; no account, backend, or cloud service is required.

## Implemented gameplay

- Repeatable habits, scheduled daily tasks, and one-time to-dos.
- Create, filter, complete, and delete tasks.
- Coin rewards and a daily completion bonus.
- Multiple tree species, seed inventory, planting, naming, growth stages, and fertilizer.
- Statistics for completions, daily/weekly activity, streaks, planted trees, and coins.
- Local persistence across app restarts.

Completing a task grants its configured coins and increases the growth of planted trees.

## Run

Open the repository as a Gradle project in Android Studio and run `:app`.

Requirements: JDK 17, Android SDK 36, Android Gradle Plugin 8.13.2, Kotlin 2.2.20.

## Current limitations

This is a single-player offline game, not a full social/cloud Habitica clone. There are no accounts, parties, guilds, multiplayer, or cloud sync. To-do due dates are currently plain text and do not yet trigger deadline notifications. Tree visuals currently use emoji stage representations rather than custom animated artwork.

Build status: not yet verified by a successful Android Gradle build in CI or on a device.