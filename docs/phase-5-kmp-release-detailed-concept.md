# Phase 5 — KMP Completion, Data Portability & Release Readiness

> **Four-tier update:** Tree tiers are now **Common (4 stages, 14 days, first seed free), Rare (7–8 stages, 35 days, 900 coins), Legendary (12 stages, 90 days, 4,500 coins), and Exclusive (12 stages, 90 days, reserved for a possible future paid cosmetic entitlement; not purchasable now)**. Older 5–6-tier tables, 4–12 stage ranges for intermediate tiers, and coin-priced Premium examples are superseded by [Economy & Growth Balancing v2](economy-and-growth-balancing.md). Do not implement payments yet.


> **Economy and progression update (2026-11-11):** [Economy & Growth Balancing v2](economy-and-growth-balancing.md) is authoritative for ALL coin rewards/caps, seed/shop prices, watering-can/fertilizer prices, mature sale payouts, and growth durations. Any older example below (150/day, 25-coin seeds, 3–50-day maturity, +25 instant fertilizer, etc.) is superseded. Free starter: minimum 14 elapsed days **and** 14 growth days. Premium: 4,500-coin seed (90 maximum-earning days from zero), minimum 90 elapsed days **and** 90 growth days to mature. Growth credits capped at one per day; upgrades/fertilizer cannot bypass the floor. Existing player wealth is grandfathered.


**Status:** Android implementation and local verification completed (2026-11-12); publication checks remain with the release owner. Evidence: [execution plan](superpowers/plans/2026-11-12-phase-5-execution.md), [data/release contract](phase-5-data-and-release.md), [reference measurements](phase-5-reference-benchmark.md). **Prerequisites:** Phase 1–4 gameplay contracts stabilized. **Goal:** Deliver a trustworthy offline-first app with clean KMP ownership, recoverable user data, tested upgrades, accessible UI and repeatable releases. This phase is not a mandate to ship iOS before Android is stable.

## 1. Product promises

- Single-player, no required account or network.
- Local-first data: tasks, history, coins, trees, inventory, garden and preferences persist offline.
- Transparent data ownership: backup/export and restore without a server.
- Clear settings, permissions, privacy disclosures and predictable upgrade behavior.
- Maintainable modular Kotlin Multiplatform code with Android as the first verified production target.

## 2. KMP module ownership contract

**shared/commonMain:** domain models, repository interfaces, use cases, rules, simulation, validation, serialization, SQL queries and migrations where platform-neutral.  
**shared/androidMain:** Android database drivers and other Android-specific adapters.  
**app:** application composition, navigation graph, top-level dependency injection, platform lifecycle and cross-feature orchestration.  
**feature modules** (`home`, `tasks`, `garden`, `shop`, `tree`, `stats`, `inventory`, optional `encyclopedia`): their own screens, ViewModels, UI state, mappers and feature-only UI components.  
**core-ui:** only genuinely cross-feature design tokens and reusable primitives, not feature-specific screens.  
**data:** avoid a second competing owner for persistence; either narrow it to platform adapters or fold shared persistence into `shared` with an explicit dependency direction.

Allowed direction: feature → shared domain contracts/core-ui; app → features; shared must not depend on Android feature modules. Avoid feature → feature ViewModel imports, cyclic Gradle dependencies and global refresh event buses. Prefer repository observation and app-level navigation contracts.

## 3. Module migration procedure

For each module: inventory current classes → identify owner by feature → move domain contracts to `shared/commonMain` only when genuinely platform-neutral → move feature UI/ViewModels to owning module → eliminate duplicate DTOs/mappers where not needed → update Gradle dependencies → compile common metadata and Android → run feature regression tests → only then move next module.

Track every migrated package, old/new path, owner, callers, removed duplicate and test result in a migration checklist. Avoid a big-bang file shuffle. Do not change gameplay rules during a pure module move.

## 4. Offline backup and restore

Settings offers **Export backup** and **Restore backup** using the platform file picker. No account or cloud requirement. Export should include:
- Schema/export format version, app version, creation timestamp and checksum.
- Tasks, schedules, occurrences, verification sessions, streaks and achievements.
- Wallet ledger, inventory, purchases and entitlements.
- Trees, growth/care history, plots, decorations, encyclopedia, timeline.
- User preferences and reminder definitions (but not OS permission grants).
- Any user-authored notes.

Use a versioned portable archive (e.g. ZIP containing JSON manifest and normalized data files) with a documented schema. Validate size, paths, checksums, IDs and cross-references before import; reject path traversal and malformed data. Export must not leak internal app database paths. Warn that unencrypted backup files may expose personal task history; optional password encryption can be a follow-up, but do not falsely label plain archives secure.

**Restore flow:** choose file → validate and preview summary → explicit warning → create safety snapshot of current data → restore transactionally → rebuild derived state and notification schedules → show counts and success. On any failure, keep original data intact. Initially support **replace all** only; merging two gardens requires a separate identity-conflict design.

## 5. Data migration and integrity

Maintain numbered schema migrations and test from every supported historical database version. Validate invariants: no negative balances, unique reward IDs, unique plot occupancy, nonnegative inventory, valid species IDs or safe fallback, monotonic tree stage, and valid schedule occurrences. Repair routines must be explicit and logged locally; never silently delete user history.

Backup and restore must be idempotent at the snapshot level; restoring the same file twice with replace-all must not duplicate coins or items.

## 6. Settings and privacy

Settings groups:
- Gameplay: daily reset/timezone policy, reminder preferences, quiet hours, equipped watering can, growth timer display preferences, animation and sound (no season setting).
- Accessibility: text scaling, reduced motion, contrast and touch target options.
- Data: export, restore, local data size, clear data (double confirmation).
- About: version/build, open-source licenses, privacy explanation and troubleshooting.

Privacy explanation: no account required; all primary records remain on-device; backups are user-controlled; local notifications require optional permission; no claim of “zero data collection” unless verified against all SDKs, crash reporting and network traffic.

## 7. Accessibility and localization

Ship consistent Vietnamese strings first; prepare resource-based localization for English without hard-coded UI copy. Use semantic labels for icon-only tree-care controls, accessible progress descriptions, minimum touch targets, screen reader ordering, large text and sufficient contrast. Do not rely solely on red/green for task states. All motion and sound can be reduced/disabled.

Test small phones, large text, dark/light appearance if supported, rotation, backgrounding, low-memory process death and keyboard navigation where applicable.

## 8. Performance budgets and diagnostics

Proposed measurable release targets (validate on representative lower-end Android hardware):
- Cold launch to interactive Home: target <=3 s on test device.
- Typical task completion transaction: visible committed update <=300 ms under normal local storage conditions.
- Garden scrolling/panning: target 60 fps on midrange device, graceful 30 fps fallback on lower-end hardware.
- No unbounded memory increase after repeatedly entering/exiting tree screens.
- No main-thread database writes, image decoding or heavy growth simulation.
- App remains usable with 1,000 task occurrences and 100+ care events.

Create a reproducible benchmark scenario and record device/OS/build, not unsupported universal performance claims. Provide local opt-in diagnostic export with sanitized logs; exclude task descriptions and other private text by default.

## 9. Automated quality gates

**Domain tests:** recurrence and timezone edges, rewards and daily cap, streaks, time simulation, inventory conservation, purchases, care and layout validation.  
**Persistence tests:** transaction rollback, process restart, schema upgrades, backup/restore corruption, duplicate request IDs.  
**UI tests:** navigation/back, task timer recovery, shop confirmation, plot selection, tree care, garden placement, accessibility semantics.  
**KMP tests:** `commonTest` for pure rules, Android instrumented integration tests for SQL/platform boundaries, native compilation for supported Apple targets if configured.

CI on every PR: formatting/static checks → common metadata compile → unit tests → Android debug/verification assemble → optional native target compile → publish test artifacts and reports. Block release on failed core gates; do not describe an in-progress workflow as passing.

## 10. Release operations

- Stable application ID, versionCode/versionName policy, signing key custody, build variants and reproducible release notes.
- Pre-release internal testing with fresh install, upgrade from prior versions, airplane mode, denied notifications, device restart, clock/timezone changes and backup/restore.
- Store listing with accurate screenshots of real app UI, privacy policy, permission rationale, age/content classification and contact channel.
- Staged rollout where supported; monitor opt-in crash/ANR reports without uploading sensitive gameplay content.
- Rollback strategy: stop rollout and ship a forward-compatible fix; never assume Android data schema can be downgraded automatically.

## 11. Optional iOS readiness (not automatic scope)

After common domain and SQL compile on native targets, audit platform dependencies (notifications, files, image assets, time, lifecycle). Add iOS adapters and SwiftUI/Compose Multiplatform presentation only with a separately approved delivery target. Android completion does not imply iOS UX is already done.

## 12. Acceptance matrix

1. Clean dependency graph with no feature-to-feature ViewModel imports or cycles.
2. `commonMain` contains no Android-only imports.
3. Current Android debug and verification builds pass.
4. Existing user data survives each supported schema upgrade.
5. Export includes every persistent gameplay entity and validates checksum.
6. Invalid backup cannot partially overwrite live data.
7. Replace-all restore yields exact original wallet, trees, tasks and layout.
8. Restoring same snapshot twice does not multiply assets.
9. Offline and denied notification permissions do not block core gameplay.
10. Screen reader can operate task completion, tree care and purchase dialogs.
11. Reduced motion and large text remain functional.
12. Performance tests show no repeated-screen memory leak on reference device.
13. CI publishes verifiable test/build artifacts and release signing is protected.
14. Store copy and privacy claims match actual app behavior.

**Done when:** the Android app can be reliably upgraded, backed up, restored, tested and released; shared KMP rules are portable and module ownership is enforceable.

## 13. Approved gameplay invariants for migration and release (2026-11-11)

- **Tasks:** only CLICK and TIMER; historic HOLD settings migrate to CLICK without losing completion history. Optional local daily completion windows are persisted and enforced on both start and finish; test overnight windows, timezone shifts and process death.
- **Trees:** per-species 4–12 finite stages, final-stage cap, no infinite height and no seasonal system. Preserve old 0–120 growth via a versioned species-relative conversion; do not silently regress trees.
- **Water:** no milliliter wallet, reservoir, refill or per-action ml charge. Backups include durable watering-can ownership/equipment and per-tree `wateredUntil` timestamps. Water All must be idempotent and atomic.
- **Fertilizer:** backups include tier, remaining inventory, active effect start/end, last application and cooldown. One active effect per tree, never stack multipliers; growth accrues only inside a water window.
- **Neglect/death:** hidden neglect state is stored for offline simulation but never displayed as a number. At 30 unwatered day-equivalents a tree dies permanently; valid watering reduces hidden neglect by 3 units. Restoring a backup must not resurrect a tree already marked dead unless the entire backup snapshot predates its death and the user explicitly chooses full replacement.
- **Selling/removing:** mature living trees can be sold once; any tree can be removed without refund; dead trees cannot be sold. The wallet ledger, plot state and historical records commit atomically. Verify anti-farming sale-income limits and no duplicate credits.
- **Backups and privacy:** hidden death progress is private gameplay state, not a secret security credential; it must be included in backup for correct restore but excluded from user-facing UI/diagnostics. Never reset neglect to zero on app restart or ordinary data migration.
- **CI tests:** add timed growth interval integration, watering expiry, Water All, fertilizer anti-stacking, neglect recovery, irreversible death, mature sale, remove, and migration from legacy ml/fixed-growth state.
