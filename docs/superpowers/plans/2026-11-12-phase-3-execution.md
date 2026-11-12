# Phase 3 execution plan — 2026-11-12

## Goal
Deliver the offline habit-engagement loop in docs/phase-3-habits-engagement-detailed-concept.md: optional onboarding, copied templates, durable focus, opt-in reminders, authentic reviews/insights, finite achievements, planned rest/protection and local memories.

## Decisions and constraints
- Economy & Growth Balancing section13 overrides stale Phase3 tier examples. Starter selection offers three Common species; one starter entitlement only. Existing assets, tasks and historical wealth remain intact.
- CLICK/TIMER only. Keep stable mission order, inline timer play/pause, five navigation tabs, compact angled mint UI and large centered static trees. Growth artwork/animation remains deferred.
- Additive schema migration; explicit unique receipts for onboarding/templates, notifications, badges, memories and protection. Badges award no coins. Task-attached focus uses the existing task receipt and50/day cap.
- Focus persists intervals and pauses on background/restart, without counting absent time. Optional breaks pause counting. Early end preserves incomplete effort. Standalone focus has no coin reward.
- Notifications default off, use inexact local alarms, explain permission before requesting, respect quiet hours/global pause/lifecycle/window, and reschedule on reboot/timezone/update. Notification taps route to task insight/focus.
- Rest dates must be planned before the day, maximum two per calendar month, no success or Daily Clear. Protection uses one250-coin ticket for one missed required day and never creates income or a successful day.
- Preserve the current Android-feature/shared-KMP migration boundary instead of introducing cross-feature ViewModel dependencies or unrelated module conversion.

## Implementation sequence
1. Shared definitions/templates and additive schema6 for onboarding receipts, focus ledger/intervals, reminders/delivery receipts, achievements, rest/protection, memories and weekly reflections.
2. Shared transactional engagement engine; integrate real task/ledger/tree events, rest-aware plans, deduplicated awards and historical aggregates. Existing task timer remains authoritative for attached focus.
3. Android feature UI/state: optional onboarding with Back/Skip, template selection, dedicated focus, task insights/calendar, review/reflection, achievements/timeline and settings. Home/Tasks/Journal route via app callbacks while retaining five tabs.
4. Local reminder scheduling/permission explanation/deep links and lifecycle recovery.
5. Real-SQLite regression tests for onboarding/templates, timer recovery/reward uniqueness, rest/protection, event/achievement uniqueness, reviews/history and reminders; rerun existing gameplay suites.
6. Debug/verification APK and KMP compile checks; render selected complete-content previews sequentially, then isolated QA for onboarding, templates, focus controls, settings and review. Never mutate main-player progress for QA.
7. Record evidence and any genuine environmental limitations. Publish only owned files after checks pass; retain unrelated staged deletions.

## Completion criteria
All thirteen Phase3 acceptance cases have implemented behavior and relevant verification. Optional break prompts are supported without forced audio; export, social features, cloud accounts, billing and seasonal mechanics remain out of scope. Android runtime evidence does not imply iOS native runtime verification on Windows.

## Evidence
- Phase3 schema6, shared engagement rules, Android state/UI and secure local reminder scheduling implemented. Main feature callbacks retain five tabs and stable task order.
- Initial suite ran76 tests:72 passed. Corrected migration fixtures to target the current schema and new tests to respect optional-today task semantics; all76 then passed with the Android debug build (1m35s).
- Added overnight-focus, partial-session badge, before-window reminder and version5 migration regressions. The80-test suite passed with debug/verification APKs and shared metadata/iOS ARM64 compilation.
- Emulator QA caught END being overridden by PAUSED for an already paused attached focus. Corrected the transition and added a dedicated regression. Final81-test suite, both APK builds, shared metadata and iOS ARM64 compilation passed together in1m2s (shell-21, exit0).
- Android permissions/security skill read, including the runtime-permission and overview references. Notification permission is explained and requested in context, with denial/settings handling. Receivers are private and alarms use immutable explicit PendingIntents; no exact-alarm or background-service permission added.
- Sequential full-content previews inspected all15 Templates (360×1200dp), Weekly Review (360×1000dp), Focus (320×500dp/font1.3), and Settings (360×720dp). Explicit bounds avoid layoutlib's1920px wrap cap; defining-component previews refreshed stale caches. No mass or concurrent renders.
- Isolated `.verification.phasethree` runtime: selected snake starter and three routines, inspected every onboarding template through the scroll boundary, confirmed choices persisted into exactly three tasks, and completed the water CLICK task for8 coins without moving its row. Attached stretch focus persisted1:48 effort, recovered paused at3:12 after force-stop, and correctly ended incomplete after the fix without another reward.
- Runtime permission explanation preceded the Android prompt; denial displayed a nonblocking message and task navigation remained usable. Settings footer (quiet hours/tree reminders/breaks/reduced motion/privacy) was inspected. Home weekly review showed actual8 coins/1 minute focus and no fabricated completion rate or prior-week trend.
- Final debug APK installed over the main application using `adb install -r`; no data reset, purchases or gameplay mutations were performed on the main profile. QA mutations stayed in the isolated package.

## Acceptance mapping and limits
All implementation steps1–6 are complete. Criteria1–4 are covered by onboarding/template/focus SQL regressions and emulator checks;5 by runtime denial and private permission-gated delivery;6–7 by shared opt-in/window/quiet/receipt tests plus explicit reboot/timezone/update rescheduling receivers;8–12 by actual-record review, unique badge/memory, rest/protection/reflection SQL regressions;13 by local SQLite/alarms and no network-dependent engagement code. Physical reboot/alarm delivery under Doze and iOS simulator runtime were not executed; Android alarm timing remains OS-controlled. Tree model/animation work remains deferred as requested.
