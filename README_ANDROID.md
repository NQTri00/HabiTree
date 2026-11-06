# Mầm Xanh Android

Mầm Xanh is an offline-first, single-player habit and task game where **trees are the only progression characters**. There are no avatars, heroes, HP, mana, or character equipment.

## Stack

- Kotlin, Jetpack Compose, Material 3
- MVVM-style UI state with AndroidX ViewModel and StateFlow
- SQLite via Android SQLiteOpenHelper for on-device persistence
- No account, backend, network permission, or cloud dependency

## Implemented gameplay

- **Habits:** repeatable actions with categories, coin rewards, and completion streaks.
- **Daily tasks:** repeat every day, on weekdays, or on weekends; can be completed once per day.
- **To-dos:** one-time tasks, optional due-date text, and completed-task history.
- **Task management:** create, filter, complete, and delete tasks.
- **Coins:** task rewards and a daily completion bonus.
- **Living garden:** own multiple trees, name them, and grow them through task completion.
- **Tree stages:** sprout, seedling, young tree, and mature tree.
- **Shop:** purchase five seed species and organic fertilizer.
- **Inventory:** seeds and fertilizer persist locally; seeds can be planted and fertilizer can be applied to a tree.
- **Statistics:** task completion totals, today's and this week's activity, streak, planted tree count, and coin balance.

Completing a task awards its configured coins and adds growth points to every planted tree. Tree growth and all other game state are stored locally in SQLite and survive app restarts.

## Run

Open the repository in Android Studio and run the :app configuration.

Requirements: JDK 17, Android SDK 36, Android Gradle Plugin 8.13.2, Kotlin 2.2.20.

## Current scope and limitations

This is a functional offline single-player foundation, not a cloud/social Habitica clone. It does not include accounts, multiplayer, parties, guilds, cloud sync, push notifications, or remote services. Task due dates are currently stored as user-entered text; deadline notifications and calendar-based overdue handling are not implemented. The garden uses Compose emoji-based tree-stage visuals rather than custom animated tree artwork.
