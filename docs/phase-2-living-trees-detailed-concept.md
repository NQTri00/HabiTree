# Phase 2 — Finite-Lifecycle Living Trees: Detailed Gameplay & Product Concept

**Status:** Proposed, not implemented. **Prerequisite:** Phase 1 task/reward ledger and reactive persistence. **Authority:** This specification supersedes the older infinite-height, fixed-six-stage, milliliter-water, instant-growth, nonlethal-neglect and seasonal ideas in previous documents. No seasonal mechanic is in scope.

## 1. Player fantasy and loop

Each tree is a long-term, individually nurtured collectible with a **species-specific finite number of stages**. A cheap, small tree can mature after four stages; premium species can require up to twelve distinct visual stages. Watering starts a temporary **active growth window**; without an active watering window the tree does not gain growth progress. Fertilizer multiplies active growth speed within strict nonstacking limits. Neglect accumulates **hidden death progress**; prolonged neglect kills a tree permanently. A mature, living tree can be sold for coins or removed from a plot. Dead trees can only be removed (no sale or revival).

Daily loop: complete tasks → earn coins → buy a seed or upgraded watering can/fertilizer → plant → water one or all eligible trees → optionally fertilize → observe time-driven stage progression → reach maturity → keep, sell, or remove the tree → reuse the plot.

## 2. Species price, stage counts and maturity

The species catalog defines `stageCount`, price, per-stage required active growth duration, visual asset references, watering duration, fertilizer compatibility, and mature sale value. **Do not derive stage count directly from price at runtime**; price and stage count are authored and balanced together.

| Example tier | Example seed price | Stage count | Suggested active growth to maturity | Intent |
|---|---:|---:|---|---|
| Starter / small | 25 coins | 4 | 3–5 days | Easy first success |
| Common | 50 coins | 5–6 | 6–10 days | Casual collection |
| Uncommon | 100 coins | 7–8 | 12–18 days | Sustained care |
| Rare | 200 coins | 9–10 | 20–30 days | Long project |
| Premium | 350 coins | 11–12 | 35–50 days | Prestige garden piece |

Prices and durations are **proposed tuning examples**; existing saved catalog prices must not silently change without an explicit balancing migration. More expensive trees may have more stages and higher mature sale value, but no requirement that every expensive tree have exactly 12. Each stage needs a **distinct silhouette/anatomy**, not zoom, clipping or a vertical reveal of the final sprite. The final stage is a hard maximum: growth progress stops and cannot exceed the final-stage threshold. **No infinite height, no post-maturity height milestones, no continuing growth.**

Example 4-stage succulent: seed → sprout → small rosette → mature rosette. Example 8-stage flowering tree: seed → germination → sprout → sapling → branched sapling → young tree → budding → full bloom. Example 12-stage premium bonsai: seed → germination → sprout → first needles → small sapling → trunk formation → first branches → canopy building → young bonsai → shaped bonsai → developed bonsai → mature display.

Stage durations are defined per species, e.g. `stageRequiredGrowthSeconds[]`, one threshold per transition. A tree stores cumulative **effective growth seconds** or equivalent integer units; the UI shows only current-stage progress and remaining estimated active watering time, not any death counter. No visible height stat is required.

## 3. Growth state machine and timer

Suggested lifecycle state: `GROWING | MATURE | DEAD`. Tree record includes `treeId`, `speciesId`, `plotId`, `nickname`, `stageIndex`, `growthUnits`, `wateredUntil`, `lastWateredAt`, `lastSimulatedAt`, `activeFertilizerId`, `fertilizerEndsAt`, `fertilizerUsesRemaining`, `hiddenNeglectUnits`, `neglectLastEvaluatedAt`, `deathAt`, `maturedAt` and version.

**Growth occurs only while all conditions hold:** tree alive, not mature, watering window active, and time advances. The watering timer is the growth window. At expiration, growth freezes; no passive growth until next watering. The displayed countdown is `max(0, wateredUntil - now)`. Persist the deadline, never rely on an in-memory timer to own progress.

**Simulation must integrate intervals**, not apply the current multiplier to all elapsed time: split elapsed interval at watering expiration, fertilizer start/end, stage transitions and maturity. Example: water expires at 12:00; app resumes 18:00 → credit growth only through 12:00. A fertilizer boost from 10:00–11:00 multiplies only that hour. Reopening repeatedly at the same timestamp produces zero extra growth.

**Watering while already active:** do not allow infinite deadline extension by tapping repeatedly. Default rule: watering is eligible only after the current water window expires (or an explicit per-tree cooldown), and a single action starts a new fixed-duration window. The tree's visible countdown shows remaining active time. Use local notifications only as optional reminders, not as growth authority.

**Suggested starter duration:** basic watering can activates 8 hours of growth for an eligible tree. All durations are tunable catalog values, not hard-coded assumptions.

**Final stage:** reaching the final threshold sets `MATURE`, caps progress, records `maturedAt`, disables further growth/fertilizer consumption, and enables Sell. A mature tree can remain on display indefinitely if watered sufficiently to stay alive.

## 4. Watering cans — no milliliters

**Remove water quantity in ml entirely.** No 50 ml cost, refill-to-100 ml, reservoir or water-volume UI. Watering is a **per-tree action** governed by timer/cooldown and equipped watering-can tier. Watering cans are durable, owned/equipped shop items; they are not consumed per use. Starter can is free/owned by default.

| Watering can | Suggested price | Water window | Water All |
|---|---:|---|---|
| Basic | Free | 8 h | Available |
| Copper | 100 coins | 12 h | Available |
| Silver | 250 coins | 18 h | Available |
| Golden | 500 coins | 24 h | Available |

Numbers are proposed balancing defaults. Upgrades change future watering actions only; upgrading must not retroactively lengthen active windows. Only **one equipped can** determines the duration. Multiple owned cans do not stack. Buying a permanent can twice must be prevented unless duplicates have an explicitly designed purpose.

**Water All** appears in the Garden/Plant List toolbar. It uses the currently equipped can and waters **every eligible living tree with an expired window** in one atomic batch. It skips mature/dead trees according to eligibility rules: mature trees **remain eligible for life-sustaining watering** but do not gain growth. Already-active trees are skipped; dead trees are always skipped. Confirmation sheet shows counts: eligible, already watered, dead, and affected. One tap applies the same deadline policy per tree and updates Garden, Home and tree details immediately. With no consumable water resource, Water All is free and available even with the Basic can; upgrades increase duration, not number of plants. If zero eligible, disable and explain why.

**Mature-tree watering policy:** mature trees still accrue neglect and can die if ignored. Watering them refreshes their survival window but never changes their final-stage progress.

## 5. Fertilizer tiers, speed and anti-stacking

Fertilizer is a **consumable** with fixed growth multiplier and effect duration. Example catalog:

| Fertilizer | Suggested price | Growth multiplier | Active duration |
|---|---:|---:|---|
| Basic | 20 coins | 1.1× | 4 h |
| Rich | 45 coins | 1.2× | 6 h |
| Premium | 90 coins | 1.35× | 8 h |

All values proposed; multiplier affects only time inside an active watering window. Fertilizer **never starts watering**, never revives a dead tree, never increases growth beyond maturity, and does not extend the watering timer.

**Anti-exploit rules:** at most one active fertilizer per tree; no stacking of multiple tiers, no additive/multiplicative compounding, no queueing future fertilizer. Cannot apply while another fertilizer is active. Suggested additional cap: one fertilizer application per tree per 24-hour rolling period, even if the effect expires earlier. A fertilizer action consumes one item only after validation and atomic commit. Applying fertilizer to a mature or dead tree is rejected without consumption. If the tree is currently unwatered, reject fertilizer and ask the player to water first. When the watering window ends before the fertilizer effect ends, growth stops immediately; the remaining fertilizer clock may expire unused and does not reactivate growth.

Effective rate at timestamp `t`: `baseSpeciesRate * activeFertilizerMultiplier` if watered, growing, and fertilizer active; otherwise `baseSpeciesRate` if watered and growing; otherwise `0`. Use deterministic integer/fixed-point arithmetic to avoid rounding exploits.

## 6. Hidden neglect, recovery and irreversible death

**Never show a death meter, death percentage or exact hidden death progress to the player.** Show qualitative care cues only (healthy, thirsty, wilting, critical) without exposing the underlying counter. The player may be told that prolonged neglect can kill a tree.

Recommended initial model: `hiddenNeglectUnits` in **day-equivalent units**, internally clamped `0..30`. It increases by one unit per full 24 hours of **unwatered** elapsed time after the watering window expires; partial-day time is accumulated precisely, not rounded up on every app launch. When it reaches 30, tree becomes `DEAD`, with `deathAt`, and cannot be recovered, watered, fertilized or sold.

**Recovery on watering:** one valid watering action decreases hidden neglect by **3 units** (or 2 for stricter tuning), clamped at zero. This is the chosen interpretation of “counter go down by 2 or 3.” Watering does not immediately reset all neglect, so repeated late care requires multiple cycles. Recovery is applied only on an eligible watering action; no repeated taps during an active water window. Once dead, watering has no effect. A tree at 29 units can still be saved if watered before the threshold is reached; at 30 it is permanently dead.

**Illustrative timeline:** a tree's water window ends on day 0; 15 fully unwatered days accumulate 15 hidden units. Watering on day 15 reduces to 12, starts a new growth window, and growth resumes. If it is abandoned until 30 unwatered day-equivalents accumulate, it dies. Neglect accumulation pauses while a valid water window is active. For deterministic offline catch-up, simulate neglect chronologically and process the first death threshold crossing before accepting a late watering action.

**Feedback without revealing counter:** mild leaf droop after several dry days, wilted tint after longer neglect, stronger warning near critical; exact thresholds can be private balancing constants. Do not lie that a dead tree is recoverable. Death is irreversible but **does not destroy the player's inventory, wallet or other trees**. The dead tree occupies its plot until manually removed; optionally show a one-time “tree withered” event in the Journal.

**Clock abuse:** persist timestamps and last evaluated values; monotonic in-session time where available, reject backwards simulation, do not create repeated watering or recovery via clock rollback. Fully offline apps cannot reliably prevent all deliberate clock manipulation; document this limitation.

## 7. Mature tree sale and removal

**Sell** available only when `MATURE` and alive. Confirmation shows tree name, species, stage, sale value, and that the tree will be permanently removed from the plot. Suggested mature sale price is a per-species configured value (e.g. 40–70% of seed cost, rounded), **not a guaranteed profit**. If growth investment is rewarded with profitable sale values, model coin inflation carefully against the daily cap. Selling credits coins to wallet and removes tree/plot assignment atomically; the sale is idempotent and recorded in ledger and tree history. Mature sales are **wallet income** and need a separate explicit economic cap/anti-farming rule; recommended default is to count sale proceeds toward the same 150 coins/day earned cap, or define a dedicated bounded resale allowance. Do not leave an unlimited uncapped sale loop.

**Remove tree** available at any stage (growing, mature, dead). Confirmation clearly states: no coins, seed not refunded, progress lost, plot freed. A dead tree can be removed but **never sold**. A sale/removal should preserve a historical record for encyclopedia and Journal, while deleting only the active plot assignment. Prevent duplicate sale/refund from repeated taps or stale screens.

**No transplant feature** in this phase; moving a tree to another plot would need a separate explicit design. Show a clear distinction between “Sell mature tree” and “Remove tree.”

## 8. Tree detail and Garden UI

Tree detail shows species, nickname, current stage (e.g. 3/8), stage progress, watering timer, growth state, fertilizer tier and remaining effect, last watered time, and care history. **Do not show height, infinite growth, death counter or season selector.**

Garden/Plant List shows each tree card with stage label, watering state, remaining timer, fertilizer badge, and action shortcuts. Toolbar: **Water All** plus filter (All / Needs Water / Growing / Mature / Dead). Tree care controls remain compact circles in the approved Mics style. A dead tree is visibly withered, has “Dead” state and only “Remove” action. A mature tree shows “Fully grown,” “Sell,” “Remove,” and watering for survival.

For each species, art assets must be stage-specific; a 12-stage species requires 12 distinguishable appearances or a rigorously designed layer-combination system that genuinely changes anatomy. Provide static fallback and reduced-motion support. No seasonal sprites or ambient season effects.

## 9. Storage, operations and migration

KMP shared models: `SpeciesLifecycleProfile`, `TreeInstance`, `TreeGrowthState`, `WateringCanTier`, `FertilizerTier`, `TreeCareEvent`, `TreeDispositionEvent`. Operations: `SimulateTreeUntil`, `WaterTree`, `WaterAllEligibleTrees`, `ApplyFertilizer`, `EquipWateringCan`, `SellMatureTree`, `RemoveTree`, `ObserveGarden`, `ObserveTree`. Android UI/timers and art stay in owning feature module; time integration and atomic SQL writes belong in shared KMP.

Migrate existing 0–120 growth to **species-relative finite progress**; preserve existing IDs, species, plot assignments, coins, seeds and fertilizer. Remove ml-based water reservoir and refill UI after migration; do not convert obsolete water balances into unbounded watering advantages. Grant starter can to existing profiles. For preexisting trees lacking last-watered history, initialize a fair grace period rather than retroactively killing them on update. All migrations versioned and idempotent.

## 10. Acceptance matrix

1. A 4-stage species has exactly four stage visuals; a 12-stage species has twelve distinct visuals.
2. Stage progress caps at the final stage; no post-maturity height growth.
3. Without an active watering window, progress remains unchanged over elapsed time.
4. Watering activates growth for the equipped can's duration; expiration stops growth exactly.
5. Upgrading can changes future watering duration, not existing timers; cans do not stack.
6. Water All waters eligible living trees once, skips already-active and dead trees, and reports counts.
7. Fertilizer applies 1.1×/1.2×/configured multiplier only while watered; never stacks.
8. A second fertilizer before cooldown/effect expiry is rejected without item consumption.
9. Fifteen fully dry days followed by watering reduces hidden neglect by 3 units and resumes growth.
10. Thirty unwatered day-equivalents cause permanent death; no revival, fertilizer or sale.
11. Death progress is never shown as a numeric meter or value.
12. A living mature tree can be sold exactly once; coin ledger and plot update atomically.
13. Any tree can be removed, freeing the plot without coins or seed refund.
14. A dead tree can be removed but cannot be sold.
15. Process death/offline catch-up integrates watering/fertilizer/neglect intervals without duplicate progress.
16. Old planted trees and inventory survive migration with fair neglect grace.
17. No ml resource, water refill UI, season mechanic or infinite-height progression remains.
18. All actions refresh Garden, Home, Inventory, Shop and Journal without manual reload.

**Done when:** species-specific 4–12-stage finite growth, watering-window timers, tiered cans and fertilizers, hidden recoverable neglect with irreversible death, Water All, mature selling and tree removal are implemented and covered by offline migration/invariant tests.
