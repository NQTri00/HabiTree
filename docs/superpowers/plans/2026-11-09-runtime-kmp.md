# Runtime recovery and KMP implementation plan

> **For agentic workers:** Use superpowers:executing-plans to implement task-by-task in this session.

**Goal:** Resolve startup/navigation hangs, finish previous plans, and migrate to KMP with Koin and Ktor.
**Architecture:** Shared portable business/data code with platform persistence and HTTP engines; Android Compose remains the UI. Koin owns dependencies; existing MVI contracts remain the interaction boundary.
**Tech stack:** Kotlin Multiplatform, Android Compose, Koin, Ktor, coroutines.
**Spec:** ../specs/2026-11-09-runtime-kmp-design.md

## Constraints
Preserve SQLite data, economy/catalog behavior, six screens and three dialogs, and the prior 90% visual acceptance target. New dependencies are authorized. No invented backend or silent remote writes. Use build/manual runtime verification without new test infrastructure. Commit/push successful checkpoints.

## Review focus
- Cold first navigation must not scan bitmap pixels on the main thread.
- Short windows and large text must retain reachable controls with fixed app navigation.
- Recreation must retain ViewModels and share one storage instance.
- Network errors/cancellation must not block the main thread or mutate offline data.
- Platform separation must preserve date/reset/transaction semantics and saved data.

### 1. Runtime recovery
- [x] Capture historical startup/navigation ANRs and an emulator baseline.
- [x] Move GardenSourceImage and PaintedGardenPlant decode/bounds work to IO; assembleDebug and repeat Shop navigation.
- [x] Give Home a scrollable minimum scene height; build and inspect 360×640 dp top/bottom controls.
- [ ] Exercise all destinations, dialogs, back navigation, recreation and large text; capture logcat/lastanr and record limitations.
- [x] Commit/push layout and pending typed-navigation/preview cleanup after validation (dc2ffe9).

### 2. Koin graph
Files: gradle/libs.versions.toml, app/build.gradle.kts, app/src/main/AndroidManifest.xml, app/src/main/java/com/mamxanh/app/{MamXanhApplication.kt,di/AppModule.kt,MainActivity.kt}.
- [x] Add compatible Koin dependencies; bind singleton GardenDatabase/GameStorage and repository interfaces.
- [x] Bind seven lifecycle ViewModels; start Koin in Application and use koinViewModel in MainActivity; remove manual factories.
- [ ] Assemble/install/launch; navigate and recreate to verify dependency resolution and saved data, then commit/push.

### 3. Shared KMP business/data
Files: new shared/build.gradle.kts and shared/src/{commonMain,androidMain,iosMain}/kotlin/com/mamxanh/shared; root Gradle/catalog/settings; existing data and feature domain/repository files.
- [x] Configure Android and iOS arm64/simulator targets and framework using compatible plugins.
  Evidence: shared/build.gradle.kts uses the Android KMP plugin, iosArm64/iosSimulatorArm64 and MamXanhShared static frameworks. Kotlin upgraded to 2.4.21 to match the existing Gradle 9.5.1/AGP 8.13.2 compatibility range; all Android modules now use compilerOptions. Android assembleDebug and common metadata compilation passed.
  Progress: portable models/catalog, all six feature domain contracts/use cases/models, offline repositories, GameSchema and SqlGameDatabase now run from commonMain. Android source paths are aliases and a small SQLite adapter. iOS adapter/bootstrap source is implemented; native build/runtime remains unverified.
- [x] Move portable models/catalog/game rules and repository/storage contracts into commonMain; replace Android duplicates with shared types/adapters, retaining transactions and date behavior.
- [ ] Connect Android SQLite storage through the common interface; add iOS persistence adapter/bootstrap usable by an iOS consumer.
  Implementation ruling: common GameSchema/SqlGameDatabase own SQL and transaction rules behind GameSqlConnection. Android retains SQLiteOpenHelper and the v2 mam_xanh.db schema; iOS adapts SQLDelight's native SQLite driver (2.4.0) and exposes an isolated IosGarden Koin graph. No SQLDelight code generator or Android driver migration is needed. Native compilation/runtime remain a separate macOS verification requirement.
  Manual verification: verification build type (com.mamxanh.app.verification, Mầm Xanh · QA) isolates economy mutations. Fresh schema, planting, water, seed/fertilizer purchase, fertilizer use, task completion/addition and persisted restart were executed successfully; original app retained 125 coins/cherry. All three real dialogs opened. Add Task IME overlap and legacy week-anchored statistics streak are recorded follow-ups. See runtime-navigation-2026-11-09.md.
- [x] Verify Android build and common metadata, inspect platform import boundaries; document native build commands and unavailable macOS-only verification honestly. Both APK builds, metadata and IDE sync pass; native source inspections report warnings only. Native compilation/link/runtime is not claimed.

### 4. Ktor integration
Files: shared common network configuration/client factory, Android/Darwin engine providers, shared DI module; Android manifest and application DI.
- [x] Add compatible Ktor core/engines and common configuration with timeouts and cancellation; inject it through Koin with explicit ownership/close behavior.
- [x] Expose configurable request execution without fabricated endpoints; preserve offline operation when no service is configured.
  Evidence: Ktor 3.6.1 Android/Darwin factories, SharedModule client onClose, NetworkConfig/RemoteService, optional mamxanh.apiBaseUrl Gradle property. Android/common metadata builds and IDE sync passed; installed app navigated Home/Tasks/Nursery/Shop/Journal offline with saved data. No HTTP request, native engine runtime or client-close runtime assertion is claimed; those checks remain open.
- [ ] Compile platform source sets available on this host, build/run Android and verify offline startup; document backend configuration and iOS verification steps.

### 5. Previous-plan closure
- [ ] Re-audit every unchecked requirement in 2026-11-07-dependency-free-mvi.md and 2026-11-09-simple-mics-redesign.md against current files/runtime.
- [ ] Finish missing behavior/layout/artwork; compare all six screens and three dialogs with the simple-mics PNGs at normal/large text and short height. Record concrete discrepancies rather than inventing a similarity score.
- [ ] Complete broad runtime verification, final build, evidence ledger and commit/push. Mark complete only when all requirements are proven; keep host/backend limitations explicit.
