# HabiTree — Phase 1 Detailed Product & Gameplay Specification

> **Economy and progression update (2026-11-11):** [Economy & Growth Balancing v2](economy-and-growth-balancing.md) is authoritative for ALL coin rewards/caps, seed/shop prices, watering-can/fertilizer prices, mature sale payouts, and growth durations. Any older example below (150/day, 25-coin seeds, 3–50-day maturity, +25 instant fertilizer, etc.) is superseded. Free starter: minimum 14 elapsed days **and** 14 growth days. Premium: 4,500-coin seed (90 maximum-earning days from zero), minimum 90 elapsed days **and** 90 growth days to mature. Growth credits capped at one per day; upgrades/fertilizer cannot bypass the floor. Existing player wealth is grandfathered.


**Status:** Android Phase1 implementation verified; [acceptance evidence and scope](superpowers/plans/2026-11-11-phase-1-execution.md). Shared/iOS-target Kotlin compilation passed; iOS runtime remains unverified.
**Target:** `y-tuong/quangtri` (Kotlin Multiplatform + Android Compose).  
**Purpose:** Make the existing offline-first tasks → coins → shop → inventory → garden → tree care loop complete, balanced, reactive and reliable.  
**Out of scope:** Phase 2 species-specific finite growth and timed watering, new species art, garden furniture, multiplayer, player avatars. Seasons are excluded from the entire roadmap.

> This specification builds on `MamXanh-Proposal.md`, `MamXanh-Tasks-And-Trees.md`, and `docs/design-references/current-mics-implementation.md`. Numerical values below are proposed balancing defaults, not measurements of existing behavior. Preserve the approved mint/white Mics visuals and centered featured tree. Do not interpret this document as a claim that features are already implemented.

## 1. Player experience and boundaries

**Core daily loop:** Open Home → see featured tree, today's obligations and coin progress → complete scheduled tasks through the appropriate verification → receive an itemized reward → buy seeds or supplies → plant/care for a tree → review progress in Journal → return next scheduled day.

**Navigation:** Retain the current five primary tabs (Home, Tasks, Garden, Shop, Journal/Stats). Inventory remains a secondary destination, with Items and Seeds tabs. Avoid adding a sixth main tab. Back should return to the invoking context, preserving plot/seed selection where applicable.

**Success criteria:** No duplicate rewards or purchases; state updates immediately across modules; restart-safe persisted transactions; intuitive task planning; meaningful daily goals; no accidental inventory consumption.

## 2. Tasks — concrete features

### 2.1 Types and states

- **Habit:** recurring personal behavior; supports weekdays and individual habit streaks. Active until paused/archived.
- **Daily:** recurring obligation; contributes to today's required plan and Daily Clear when scheduled.
- **To-do:** one-time item; optional due date; pays at most once, stays in history after completion.
- Persist lifecycle `ACTIVE | PAUSED | ARCHIVED`; for one-time tasks, completion is an occurrence state, not a destructive deletion.
- Persist a stable task ID, title, description, category, type, schedule, verification configuration, reminder configuration, creation/update timestamps and archived timestamp.

### 2.2 Create/edit task screen

Fields and validation:
- Title required, trimmed, 1–100 characters.
- Description optional, maximum 500 characters.
- Category: Study/Focus, Health/Exercise, Hygiene/Lifestyle, Housework, Personal.
- Type: Habit, Daily, To-do.
- Recurrence for Habit/Daily: every day, weekdays, weekends, or selected days of week. A custom schedule must contain at least one weekday.
- To-do: optional due date; no recurring schedule.
- Optional completion window: local start/end time; validation must define overnight windows explicitly rather than silently misinterpreting them.
- Verification: Click (single deliberate confirmation), Timer (required active minutes; valid positive bound). Hold-to-complete is not supported.
- Optional local reminder time; permission denial must not prevent task creation.
- Reward calculated by the game; not an unrestricted user-entered amount.
- Save and Cancel. Editing retains prior occurrences, rewards and completion history.
- Schedule edits apply prospectively; history retains the schedule snapshot applicable to earlier dates.
- Archive is the default removal operation; permanent deletion requires explicit confirmation and must preserve an auditable coin ledger.

### 2.3 Task details

Show title, description, category, schedule, verification method, current completion status, current streak, last completion, seven-day completion history and actions Edit/Pause/Resume/Archive. Paused tasks do not appear as today's required obligations and do not count as failures while paused.

### 2.4 Completion and verification

**Click:** One tap followed by persisted completion; disable while the transaction is in flight.  
**Timer:** Start/Pause/Resume/Finish; store session ID and active intervals; only active elapsed time counts. Recover after process death; do not count paused or unverified background time. A completion window is checked before acceptance. Prevent double claims for the same task and scheduled date.

For recurring tasks, define a unique completion key `(taskId, scheduledLocalDate)`. One-time tasks have a unique terminal completion record. Changes to system clock/timezone cannot reissue an existing reward.

### 2.5 Today list behavior

Sections: In Progress (active timer), Required Today, Optional, Completed Today. Sort: running timer, nearest time-window deadline, overdue one-time items, then other scheduled tasks. Past misses appear in history, not in today's completion denominator. Provide empty states and shortcuts to templates or task creation.

## 3. Coins, rewards and ledger

### 3.1 Proposed reward table

| Action | Coins |
|---|---:|
| Click completion | 8 |
| Timer 5–19 active minutes | 10 |
| Timer 20–44 active minutes | 15 |
| Timer 45+ active minutes | 20 |
| Daily Clear | 8 |
| 7-day streak milestone | 10 |
| 14-day streak milestone | 15 |
| 30-day streak milestone | 20 |

Values are initial balancing proposals. A completion's eligible reward is snapshotted when verification begins; task edits during an active timer do not increase the reward.

### 3.2 Daily cap

Maximum **50 positive gameplay coins per local day, including all bonuses and mature-tree sales** (per original project proposal). Spending does not reduce earned-today accounting. If 46 have been earned and the next nominal reward is 20, grant only 4; record nominal 20, granted 4, cap reduction 16. Task completion and streak progress still count after reaching the cap.

UI: coin balance, `earnedToday/50` meter, remaining earning capacity, and reward receipt with cap explanation.

### 3.3 Ledger and transactions

Every coin mutation has transaction ID, type, related entity ID, timestamp, amount and resulting balance. Purchase and reward mutations must be atomic and idempotent. Completion, reward, daily-cap usage and existing tree-growth side effects must commit together or roll back together. Do not award success animations before persistence confirms success.

A wallet history lists task rewards, Daily Clear, milestones, purchases and adjustments. Existing historic balances/rewards must not be retroactively recalculated during migration.

## 4. Daily plan, Daily Clear and streaks

- Compute required scheduled obligations for each local date; distinguish optional To-dos.
- Snapshot the day's required task IDs when the day is first materialized, with a documented policy for tasks created/edited mid-day.
- **Daily Clear:** once per day, when all required snapshot tasks are completed and at least one required task exists. Newly created tasks after award do not revoke it.
- **Streak:** advances for successful *scheduled* days; unscheduled rest days do not break it. Paused days are neutral. Missed required days break it unless a valid protection item was applied.
- Reset is date-driven and idempotent, not a destructive midnight timer. Recalculate eligibility on app resume/start. Persist the last evaluated day and awarded milestone IDs.
- Define an explicit policy for timezone shifts and backward clock changes; never issue a duplicate daily bonus.

## 5. Home — screen-level additions

Keep the current prominent centered tree and mint/white UI. Add below/around it:
1. Compact wallet badge and daily earning meter.
2. Today's task completion count and progress bar.
3. Streak chip and next milestone hint.
4. Up to three actionable next tasks; click completion inline, timer navigates to session.
5. Tree-care shortcut(s) with available water/fertilizer counts.
6. Empty-state guidance: no tasks → choose a template; no planted trees → plant a seed; no seeds → visit Shop.

**Reactive contract:** task completion immediately updates Home, Tasks, Journal and wallet; purchasing immediately updates Shop and Inventory; planting updates Garden, Inventory and Home; care updates tree, resources and Garden. No manual refresh or re-entering a screen.

## 6. Shop and Inventory — concrete behavior

### 6.1 Shop

Seed/item detail or confirmation sheet shows artwork, name, description, unit price, owned quantity, quantity selector, current balance, total and resulting balance. Disable confirmation if unaffordable, invalid stock or already submitting. On success show an itemized receipt and update inventory immediately. On failure show a reason without consuming coins.

Avoid inventing a new catalog in Phase 1; use the existing seed and item definitions and reconcile catalog/species identifiers so every sold seed can be planted and rendered.

### 6.2 Inventory

Retain Items and Seeds grids. Show quantity badges, empty-state shortcuts to Shop, and context-sensitive actions. When opened from an empty garden plot, default to Seeds; choosing a seed returns to the same plot's planting confirmation. Canceling never consumes a seed. If stock changes between selection and confirmation, revalidate against the latest persisted quantity.

## 7. Garden and care — concrete behavior

### 7.1 Plot states

- Empty unlocked: planting affordance.
- Occupied: tree artwork, name, current growth, open-tree action.
- Locked: lock treatment and unlock cost; show affordability before unlocking.

Plant confirmation shows species, selected plot and owned seed count. A successful transaction consumes exactly one seed and assigns exactly one tree to the plot. Reject a concurrent second plant into the same plot.

### 7.2 Care states using existing growth model

**Water (Phase 1 compatibility):** use the existing implementation until Phase 2 migration; do not add any new ml reservoir or refill mechanic. Phase 2 replaces watering with tiered, time-limited growth windows and Water All.  
**Fertilize (Phase 1 compatibility):** preserve existing stock and transaction correctness; Phase 2 replaces instant +25 growth with bounded, nonstacking 1.1×/1.2× tiered growth boosts.  
**Decorate:** preview owned decoration, apply and persist; do not charge again for owned items.

Care buttons show available, unavailable and in-progress states, with reason text (already watered, insufficient water, no fertilizer, full growth). Keep circular care controls from current Mics implementation. Add a minimal care event history (type, tree ID, timestamp, resource change, growth change). The advanced unlimited-height model is deliberately deferred.

## 8. Journal/Stats

Extend current seven-day view with:
- Weekly task count, completion rate and streak.
- Daily history/calendar with eligible count, completed count, Daily Clear and earned coins.
- Category breakdown (Study, Health, Lifestyle, Housework, Personal).
- Per-task seven-day completion records.
- Wallet ledger and optional tree-care history entry points.

**Completion rate:** completed eligible scheduled occurrences / eligible scheduled occurrences for the period. Days with no scheduled obligations are neutral, not failures. Statistics derive from persisted records and refresh automatically.

## 9. Storage and cross-module contracts

Recommended durable entities (adapt to existing SQL/KMP conventions; avoid duplicating models across modules):
- `Task`, `TaskSchedule`, `TaskOccurrence`, `VerificationSession`
- `DailyPlanSnapshot`, `DailyRewardSummary`, `StreakState`
- `WalletTransaction`, `PurchaseReceipt`
- `InventoryStack`, `PlotAssignment`, `TreeCareEvent`

Recommended domain operations:
- `CreateTask`, `EditTask`, `PauseTask`, `ArchiveTask`
- `StartVerification`, `PauseVerification`, `ResumeVerification`, `CompleteTask`
- `GetTodayPlan`, `AwardDailyClear`, `EvaluateStreak`
- `PurchaseItem`, `PlantSeed`, `WaterTree`, `FertilizeTree`
- `ObserveWallet`, `ObserveInventory`, `ObserveGarden`, `ObserveTodayPlan`, `ObserveStats`

Shared domain contracts and SQL persistence belong in KMP `shared/commonMain` where platform-independent. Feature-specific UI, ViewModels, UI state and components remain inside their Android feature modules. Platform notification scheduling and Android-specific timer UI stay under Android-specific sources. Prefer reactive repository streams to cross-module manual refresh events. Cross-feature integration occurs through domain contracts and the app coordinator, not feature-to-feature ViewModel imports.

Migration requirements: additive schema migration, preserve existing task IDs, wallets, seeds, plot assignments, trees and histories, and verify old installations can reopen. Handle operation retries, app termination and concurrent taps. Never silently reset the database.

## 10. Acceptance matrix

| Scenario | Required outcome |
|---|---|
| Create weekday reading habit | Required Monday–Friday; weekends neutral |
| Edit completed habit | Future configuration changes; past records retained |
| Complete task twice | Exactly one completion and reward |
| Timer app background/restart | Recover persisted session and active intervals |
| Reward at 46/50 daily earned | Grant 4, show reduction, preserve task completion |
| Complete all required tasks | Daily Clear once |
| No required tasks | No automatic Daily Clear |
| Pause recurring task | Excluded while paused; not marked missed |
| Buy quantity two | Deduct exact total; add two items once |
| Buy with insufficient coins | No balance or inventory mutation |
| Plant selected seed | Exactly one consumed; exact selected plot occupied |
| Cancel planting | No seed/plot mutation |
| Repeated watering | Cannot duplicate one watering transaction; Phase 2 determines timer-based eligibility |
| Fertilize at growth cap | No item consumed for zero effect; Phase 2 applies timed multipliers instead |
| Navigate back from Inventory | Return to invoking context without stale plot selection |
| Kill/restart app | All confirmed task, wallet, inventory and tree data retained |
| Set clock backward | No duplicate task/day reward |
| Open Journal after completion | New statistics visible without manual refresh |
| Existing user updates app | Previous data preserved by migration |

## 11. Implementation order and definition of done

1. **Persistence and invariants:** migrations, occurrence uniqueness, transaction ledger, daily cap, idempotency tests.
2. **Tasks:** type/schedule/edit/archive, verification and persisted timer sessions.
3. **Daily progression:** plan snapshot, Daily Clear, streaks and date-boundary tests.
4. **Reactive integration:** wallet/inventory/garden/tree/home/stats streams; remove stale-refresh dependencies.
5. **Shop/Garden polish:** confirmations, contextual Inventory, care feedback, empty states.
6. **Journal:** daily history, completion rates, category breakdown, ledger.
7. **QA:** Android and KMP compilation, existing-user migration, process-death, offline and double-tap tests.

**Phase 1 is done only when the acceptance matrix passes and the current app's primary navigation, tree art and saved game state are preserved.** No Phase 2 growth engine or unapproved visual redesign should be bundled into this work.

## 12. Approved design changes and cross-phase rules (2026-11-11)

This section is normative wherever older Phase 1 examples disagree:

1. **Task verification has exactly two modes: Click and Timer.** Remove Hold from creation, editing, templates, rewards, task cards, tests and saved enum options. Migrate legacy HOLD tasks to CLICK without deleting their history.
2. **Time-window eligibility:** any task may optionally require completion during a specific local daily time frame (e.g. brushing teeth 06:00–09:00, evening study 18:00–23:00). The task card shows “Available 18:00–23:00,” disables Click outside the window, and prevents starting a Timer that cannot satisfy the minimum active duration before the window ends. Revalidate the window at completion; crossing the end while timing cannot produce a reward. Explicitly define overnight windows (e.g. 22:00–02:00) by anchored scheduled date. Store the window with the task schedule snapshot and test DST/timezone changes. Time restrictions are optional, not universal.
3. **Do not expand ml watering in Phase 1.** The existing legacy watering code may remain temporarily for compatibility, but the final user-facing model in Phase 2 has no water-volume units or refill. Do not add new water tanks, ml purchases or UI.
4. **Finite tree stages and irreversible death** belong to Phase 2. Phase 1 should keep atomic transactions and species IDs ready for species-specific 4–12-stage profiles, hidden neglect, mature sale, removal and Water All. Avoid assuming a universal 120-point cap or a universal stage count in new shared contracts.
5. **No seasons anywhere in the roadmap.** Do not add season settings, season art or seasonal growth modifiers.

### Updated task verification example

- “Đánh răng buổi sáng”: CLICK, available 06:00–09:00, 5 coins.
- “Đọc sách 20 phút”: TIMER, minimum 20 active minutes, available 18:00–23:00 if configured, 10 coins.
- “Đi bộ 20 phút”: TIMER, minimum 20 active minutes, unrestricted or a player-selected window.
- “Uống đủ nước”: CLICK, unrestricted.

A task completed outside its configured time window remains incomplete and grants no coins. The UI must explain when the task becomes available rather than silently failing.
