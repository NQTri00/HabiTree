# Dependency-Free MVI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox syntax for tracking.

**Goal:** Rework all six MamXanh feature modules and the app shell to use dependency-free MVI with immutable state, typed intents, and one-off effects.

**Architecture:** Each feature ViewModel owns a single immutable UiState flow and a typed intent dispatcher. A shell state holder owns navigation and global transient UI state, coordinates existing feature refreshes, and collects feature effects. Existing repositories and domain operations remain intact.

**Tech Stack:** Kotlin, Jetpack Compose, AndroidX Lifecycle ViewModel, StateFlow, coroutines.

**Spec:** docs/superpowers/specs/2026-11-07-dependency-free-mvi-design.md

## Global Constraints

- Do not add an MVI library or dependency.
- Keep the existing nine Gradle modules and their dependency direction.
- Preserve current repository contracts, domain operations, persistence schema, screen design, and user-visible behavior.
- Keep data mutations on IO dispatchers and expose presentation state as read-only StateFlow.
- Feature screens consume a feature UiState and emit typed intents through one callback.
- The app shell owns destination/back stack, selected tree, add-task visibility, and global feedback.
- Do not add or run tests; verify with Android Studio analysis and app compilation.

## Review Focus

- Rapid repeated intents must not leave stale data or launch duplicate mutations; task is verified in Tasks and Shop migration by code inspection and compilation.
- Feature effects must be collected once and not replay stale feedback after configuration changes; task is verified in root effect collection by code inspection.
- Refresh after cross-feature mutations must update all dependent features; task is verified in app-shell coordination by code inspection.
- Navigation back from a selected tree and nested destination must preserve current behavior; task is verified in shell state reducer and app compilation.
- Empty/initial data must still render sensibly while loading; task is verified in each UiState default and screen rendering path by code inspection.

---

### Task 1: Establish feature MVI contracts for Tasks and Shop

**Files:**
- Create tasks/src/main/java/com/mamxanh/app/feature/tasks/presentation/TasksContract.kt
- Modify tasks/src/main/java/com/mamxanh/app/feature/tasks/presentation/TasksViewModel.kt
- Modify tasks/src/main/java/com/mamxanh/app/feature/tasks/presentation/TasksScreen.kt
- Create shop/src/main/java/com/mamxanh/app/feature/shop/presentation/ShopContract.kt
- Modify shop/src/main/java/com/mamxanh/app/feature/shop/presentation/ShopViewModel.kt
- Modify shop/src/main/java/com/mamxanh/app/feature/shop/presentation/ShopScreen.kt

**Interfaces:**
- Tasks contract: TasksUiState(tasks: List<Task>, isLoading: Boolean, filter: TaskFilter), sealed TasksIntent for refresh/filter/add/complete/delete/claim bonus, sealed TasksEffect for feedback.
- Shop contract: ShopUiState(wallet: ShopWallet, isLoading: Boolean), sealed ShopIntent for refresh/buy seed/buy fertilizer, sealed ShopEffect for feedback and inventory change.
- Each ViewModel exposes StateFlow of UiState, Flow of Effect, and dispatch(intent).

- [x] Create contracts with immutable default state and typed event payloads.
- [x] Route existing operations through intent dispatch; retain IO work and repository/use-case calls.
- [x] Keep filter selection in TasksUiState and move transient delete confirmation to stable item UI or screen-local presentation state as appropriate.
- [x] Update screens to receive state and dispatch intents; preserve existing layout and behavior.

### Task 2: Migrate Garden and Tree to MVI

**Files:**
- Create garden/src/main/java/com/mamxanh/app/feature/garden/presentation/GardenContract.kt
- Modify garden/src/main/java/com/mamxanh/app/feature/garden/presentation/GardenViewModel.kt
- Modify garden/src/main/java/com/mamxanh/app/feature/garden/presentation/GardenScreen.kt
- Create tree/src/main/java/com/mamxanh/app/feature/tree/presentation/TreeContract.kt
- Modify tree/src/main/java/com/mamxanh/app/feature/tree/presentation/TreeViewModel.kt
- Modify tree/src/main/java/com/mamxanh/app/feature/tree/presentation/TreeInteractionScreen.kt

**Interfaces:**
- Garden state includes Garden and loading status; intents cover refresh and planting.
- Tree state carries effect stream for feedback; intents cover watering, fertilizing, and decoration. Tree display model remains supplied by app shell from the selected garden item.

- [x] Add contracts and convert ViewModels to single state plus typed intent dispatch.
- [x] Preserve mutation outcomes and cross-feature refresh signals as effects.
- [x] Update feature screens to consume state (Garden) or accept state-derived tree details with intent dispatch (Tree).

### Task 3: Migrate Home and Stats to MVI

**Files:**
- Create home/src/main/java/com/mamxanh/app/feature/home/presentation/HomeContract.kt
- Modify home/src/main/java/com/mamxanh/app/feature/home/presentation/HomeViewModel.kt
- Modify home/src/main/java/com/mamxanh/app/feature/home/presentation/HomeScreen.kt
- Create stats/src/main/java/com/mamxanh/app/feature/stats/presentation/StatsContract.kt
- Modify stats/src/main/java/com/mamxanh/app/feature/stats/presentation/StatsViewModel.kt
- Modify stats/src/main/java/com/mamxanh/app/feature/stats/presentation/StatsScreen.kt

**Interfaces:**
- Home state includes HomeDashboard and loading status; intent covers refresh.
- Stats state includes GardenStatistics and loading status; intent covers refresh.
- Home's transient local visual feedback and counters remain local screen presentation state unless backed by domain data; tree mutations dispatch Tree intents.

- [x] Add contracts and convert ViewModels to single state plus typed intent dispatch.
- [x] Update screens to consume the UiState while preserving current visual local state and callbacks.

### Task 4: Introduce app-shell MVI and migrate wiring

**Files:**
- Create app/src/main/java/com/mamxanh/app/presentation/ui/AppContract.kt
- Create app/src/main/java/com/mamxanh/app/presentation/ui/AppViewModel.kt
- Modify app/src/main/java/com/mamxanh/app/presentation/ui/MamXanhApp.kt
- Modify app/src/main/java/com/mamxanh/app/MainActivity.kt

**Interfaces:**
- AppUiState owns destination, back stack, selected tree id, add-task dialog visibility, and active global feedback.
- AppIntent covers navigation, back, opening/closing add-task, feedback dismissal, and feature-changed refresh.
- AppViewModel receives feature ViewModels or callbacks needed for coordination and exposes a single state flow/intent dispatch boundary.

- [x] Move navigation and global dialog/message state out of MamXanhApp's mutable Compose state.
- [x] Collect task/shop/tree effects once and map them to app feedback or refresh intents.
- [x] Centralize refresh dependencies currently spread across MainActivity callbacks and MamXanhApp LaunchedEffects.
- [x] Update feature Composable calls to pass UiState and dispatch callbacks.
- [x] Remove obsolete individual flow collection and direct action calls from app shell.

### Task 5: Verify MVI consistency and build

**Files:**
- Review all migrated contract, ViewModel, screen, app-shell, and MainActivity files.

- [x] Use Android Studio inspections on changed Kotlin files and resolve errors.
- [x] Run the app module Gradle compile task through Android Studio's shell tooling.
- [x] Search the six feature ViewModels and app shell for legacy split state flows, public action methods, and composable-owned navigation/global feedback; resolve remaining cases.
- [x] Confirm no dependency was added and the final diff matches the design acceptance criteria.
