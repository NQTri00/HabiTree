# Phase 3 — Habit Engagement: Detailed Gameplay & Product Concept

**Status:** Proposed; not implemented. **Prerequisites:** Phase 1 task recurrence, verification, ledger and streaks; Phase 2 tree milestones. **Goal:** Make the app worth returning to daily without coercive streak pressure, notification spam or reward exploits.

## 1. Player loop

Plan tomorrow → receive an optional gentle reminder → begin a task or focus session → finish and receive clear feedback → review personal consistency → unlock an achievement or memory → adjust goals when life changes. No leaderboard, multiplayer, combat or loot boxes.

## 2. Guided onboarding and task templates

First launch (or accessible later): welcome → choose one of three starter trees → select personal focus (Study, Health, Lifestyle, Housework) → choose 2–5 suggested tasks → confirm schedule/reminder preferences → enter Home. Every step has Back; skip is allowed; never require notification permission or account registration.

Template cards show title, category, frequency, estimated effort, verification method and suggested reward. Templates are **copied into user tasks**, not live-linked to future template edits. Offer at least 12 curated templates drawn from `MamXanh-Tasks-And-Trees.md`, including studying, reading, walking, brushing teeth, hydration, stretching and household chores. Prevent accidental duplicate task creation when tapping rapidly.

## 3. Focus sessions

A dedicated timer experience built on Phase 1 persisted verification sessions:
- Presets 15, 25, 45 and 60 minutes plus custom 5–120 minutes.
- Start, Pause, Resume, End; clear progress ring, remaining active time, task title, session count.
- Session may attach to one timer-verified task. Only eligible active time counts toward completion.
- Background/resume and process death recover session state; avoid relying on an in-memory countdown.
- Optional break prompts (e.g. after 25 minutes), never auto-award task completion during a break.
- Optional gentle sound/vibration; respects silent mode and permission settings.
- End early: save partial session as incomplete, no task reward unless threshold met.
- Completing a timer session does not automatically grant a second coin reward beyond its attached task.
- No punitive “failed focus” screen.

## 4. Reminder system

Task reminders support: Off, at scheduled time, before a time window, and one gentle follow-up if unfinished. Notification text includes task title and a direct route to the task/timer. The player can set quiet hours, per-task reminders, and global pause. Default to no notifications until user opts in.

Offline local scheduling: Android notification permission requested only after explanation; handle denial gracefully. Recompute alarms when schedule changes, timezone changes, device reboots and the app is updated. No cloud push. Avoid duplicate alerts for completed tasks and do not alert on paused/archived tasks. Use platform-appropriate scheduling that does not promise exact minute delivery under battery restrictions.

## 5. Weekly review

Show a weekly review card (user-opened, optional prompt once per week):
- Scheduled vs completed tasks, completion rate and comparison to prior week.
- Most consistent category and one category with room for improvement.
- Total focus minutes, tasks completed, earned coins, trees cared for, stage milestones.
- A neutral reflection prompt (“What made this week easier?”) with optional local-only note.
- Suggested adjustment: reduce schedule frequency, pause a habit or move reminder time. Suggestions are never applied automatically.

The weekly review must derive metrics from the ledger, occurrences and focus sessions, not synthetic data. If history is insufficient, say “not enough data” rather than invent trends.

## 6. Achievements

Achievements are finite, auditable milestones rather than infinite coin generators. Initial set:
| Achievement | Trigger | Reward |
|---|---|---|
| First Step | First valid task completion | Badge only |
| Three Good Days | 3 successful scheduled days | Badge; use existing streak reward, no duplicate bonus |
| Week of Care | 7 scheduled successful days | Badge; existing streak reward only |
| Focus Starter | First completed 25-minute focus session | Badge |
| Study Rhythm | 10 study task occurrences | Badge |
| Green Thumb | Water 10 distinct scheduled days | Badge |
| First Bloom | First tree reaches flourishing | Badge |
| Collector | Own three distinct species | Badge |
| Steady Month | 20 successful scheduled days | Badge |

Each unlock has unique `achievementId + userProfileId` record, awarded once, with timestamp and source event. No hidden repeatable farming. Achievement detail explains criteria and progress. Coin-bearing achievements, if added later, must respect the daily cap and wallet ledger.

## 7. Streak protection and rest days

Add a **planned rest day** action before the day begins, with a bounded frequency (proposed two per calendar month). It excludes that day from required daily-plan obligations but does not grant coins or a “successful day.” Existing skip-day ticket from the original proposal may be implemented as a separate inventory item: one use protects a streak for one missed required day, consumes exactly one ticket, never awards Daily Clear, and is idempotent.

Display three states clearly: successful scheduled day, protected/rest day, missed day. Avoid shame language and do not force the user to buy streak protection.

## 8. Personal insights and task adjustment

Task detail gains 7-/30-day calendar, current streak, longest streak, completion rate, most common completion hour and total active focus minutes where applicable. Suggest reducing a task from daily to weekdays after repeated misses; the user must explicitly confirm. Changing schedules preserves historical completion semantics.

## 9. Memory timeline

A chronological, locally stored feed with significant events: first task, first tree, first bloom, streak milestone, first focus session, new species, achievement. Each event has stable ID, type, related entity, timestamp and optional user note. Events should link to the relevant tree/task and never duplicate when a screen is reopened. Export is optional in Phase 5; no social sharing required.

## 10. UI entry points

Home: one primary “continue task/focus” CTA, compact streak indicator, optional weekly-review card, next tree milestone. Tasks: timer action, reminder icon and task insights. Journal: Focus, Achievements, Timeline and Weekly Review sections. Settings: quiet hours, notification permissions, reduced motion, reminder defaults and privacy note. Keep the five-tab navigation.

## 11. Data / module boundaries

KMP shared: achievement rules, focus session ledger, review aggregations, streak protection, event timeline and template definitions. Android-specific: notifications, alarms, deep links, vibration/sound and Compose focus UI. Keep ownership within the relevant feature module; use shared domain interfaces, not direct cross-feature ViewModel calls.

Store `FocusSession`, `FocusInterval`, `ReminderPreference`, `AchievementUnlock`, `RestDay`, `StreakProtectionUse`, `MemoryEvent`, `WeeklyReflection`. All have stable IDs and appropriate uniqueness constraints. No analytics SDK or remote account needed.

## 12. Acceptance tests

1. Onboarding can be skipped; starter choice and selected templates persist.
2. Repeated template taps do not create accidental duplicates.
3. Focus pause/resume/restart preserves active minutes accurately.
4. One focus completion attached to one task grants at most one task reward.
5. Denying notifications leaves tasks and timers fully functional.
6. Completed/paused tasks do not trigger reminders.
7. Reboot/timezone/schedule changes do not duplicate reminders.
8. Weekly review uses only actual saved records.
9. Achievements unlock once, even after app restart.
10. Planned rest day awards neither coins nor Daily Clear.
11. Skip ticket is consumed once and protects only the intended day.
12. Memory timeline is ordered, persistent and deduplicated.
13. No network access is required for any feature.

**Done when:** a new user can onboard, build a manageable routine, complete focus sessions, receive optional reminders and review authentic progress without punitive engagement mechanics.
