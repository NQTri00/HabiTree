# HabiTree — Economy & Growth Balancing Specification (v2)

**Status:** Proposed balancing contract; not implemented or playtested. **Scope:** all phases. **Supersedes:** older 150 coins/day cap, 25–350 coin seed prices, 3–50-day growth timelines, large streak coin bonuses, instant +growth fertilizer, and uncapped mature-tree sale suggestions. See Phase 1–5 detailed concepts for feature behavior. All numbers below are initial tuning targets, not existing app values.

## 1. Design targets and precise meaning

- A **free starter seed** is given once to a new player. With daily watering, its tree reaches its final stage **no earlier than 14 elapsed calendar days after planting** and requires **14 separate successful growth days**. It has six visual forms (five timed transitions).
- The **Legendary 12-form seed** costs **4,500 coins**. Starting from zero, without historical wealth, gifts, exploits or sales, even a player who earns the maximum **50 coins/day** needs **90 earning days** to purchase it. At 40 coins/day, it takes 113 earning days (rounded up).
- After Legendary planting, the tree itself takes **at least 126.29256 elapsed calendar days and 127 separate credited growth days under the current daily-credit model** to mature. Buying it and growing it are separate journeys: a new zero-balance player cannot buy and fully mature it within three months.
- A casual but consistent player should normally earn **35–45 coins/day** through 3–5 reasonable tasks; 50/day is a hard maximum, not a mandatory target.
- The player can experience tree growth immediately with the free seed and basic watering can. No paywall, account, ads, microtransactions or forced grind.
- Each species has a finite 6–12 visual-form count; no infinite height, no season mechanics.

## 2. Coin earning

### 2.1 Rewards

| Event | Nominal coins | Conditions |
|---|---:|---|
| CLICK task | 8 | Once per scheduled eligible occurrence; optional time window enforced |
| TIMER 5–19 active minutes | 10 | Once per eligible occurrence |
| TIMER 20–44 active minutes | 15 | Once per eligible occurrence |
| TIMER 45+ active minutes | 20 | Once per eligible occurrence |
| Daily Clear | 8 | All required tasks completed, at least one required |
| 7 successful scheduled days | 10 | Once per milestone; subject to same daily cap |
| 14 successful scheduled days | 15 | Once per milestone; subject to same daily cap |
| 30 successful scheduled days | 20 | Once per milestone; subject to same daily cap |

Rewards are **not repeatable by restarting a task**, pausing/resuming timers or changing the system clock. Existing streak milestones in older docs must be reconciled to the above table; never grant both old and new milestone rewards.

**Hard cap: 50 positive coin income per local day across tasks, Daily Clear, streak bonuses, mature tree sales, achievements, adjustments that represent earnings, and any future positive gameplay rewards.** Purchases do not restore earning capacity. A reward can be partially granted; the ledger records nominal and granted amounts. Genuine restore/migration corrections are audited separately and never used as gameplay earnings.

### 2.2 Daily examples

| Routine | Nominal | Granted |
|---|---:|---:|
| 2 CLICK (16) + 1 TIMER 20m (15) + Daily Clear (8) | 39 | 39 |
| 2 CLICK (16) + 1 TIMER 45m (20) + Daily Clear (8) | 44 | 44 |
| 3 CLICK (24) + 1 TIMER 20m (15) + Daily Clear (8) | 47 | 47 |
| 3 CLICK (24) + 2 TIMER 20m (30) + Daily Clear (8) | 62 | 50 |
| Day already earned 46; sale nominal 100 | 100 | 4 |

**Time-window tasks:** a morning task (e.g. 06:00–09:00) cannot be completed outside its local daily window. A timer task must have sufficient time to meet its minimum before the window closes; revalidate at completion. A missed time window cannot be recovered by rapidly clicking later.

## 3. Four-tier tree catalog (six, nine, twelve visual forms)

Exactly four tiers. Each tree starts in **Form 1 at planting**. A tree with `N` visual forms requires exactly `N - 1` timed transitions; the initial form has no waiting period.

| Tier | Name | Price | Visual forms | Timed transitions | Ideal cumulative growth time | Minimum earning days from zero at 50/day |
|---|---|---:|---:|---:|---:|---:|
| 1 | Common | First seed free; repeat seed 200 coins | 6 | 5 | 14 days | 0 first / 4 repeat |
| 2 | Rare | 900 coins | 9 | 8 | 43.99267 days | 18 |
| 3 | Legendary | 4,500 coins | 12 | 11 | 126.29256 days | 90 |
| 4 | Exclusive | Future paid cosmetic entitlement, currently unavailable | 12 | 11 | 126.29256 days | Not applicable |

The **universal base duration** for Form 1 → Form 2 is `14 / (1 + 1.4 + 1.4² + 1.4³ + 1.4⁴) = 1.2790527701` days (about 30 h 42 m). Every subsequent transition is **1.4×** the previous one. **No tier-specific base time or multiplier is allowed.** The growth targets for Rare and Legendary are derived results, not separately fitted constants. See section 13 for the full schedule and implementation guidance.

For the existing *one credited growth-day per calendar day* system, define the maturity floor as `ceil(idealGrowthDays)` **distinct credited growth days** and the precise ideal elapsed-time floor. This gives 14 / 44 / 127 credited growth days for Common / Rare / Legendary (and Exclusive). As a result, a Legendary tree may require approximately **127 calendar days or longer** in practice, which remains within the intended 3–5-month range for consistently cared-for trees. If a different fractional daily-credit scheme is later approved, update this rule explicitly rather than silently discarding the existing anti-acceleration constraints.

Tier 4 is **reserved for a potential future real-money cosmetic entitlement** and must not be purchasable until separately approved monetization is shipped. It must not offer faster growth, easier neglect, extra earnings or resale advantages. Until then show **Coming soon** or hide it; do not invent a coin or cash price or implement billing.

The free starter seed is a one-time entitlement. Repeat Common seeds cost 200 coins; no free-seed sale farming. Species may have different art but the same tier form count and universal growth schedule. Species data stores `tier`, `formCount`, `seedPriceCoins` (nullable for Tier 4), `unlockType` and a growth-balance version. Do not infer form count from price.

The 90-day maximum-earning **coin acquisition floor** belongs to Tier 3 Legendary, independently of its approximately 127-day **growth** journey. Existing owned assets remain valid when migrating catalog definitions.

## 4. Time-based growth without making the player babysit timers

Watering starts a growth window; no water means **zero growth**. Basic watering can = 8 h; Copper = 12 h; Silver = 18 h; Golden = 24 h. The tree accrues growth at a species-defined rate during this window, with fertilizer multiplier applied only to the overlap. However, **a maximum of one growth-day credit per species-local day** may be earned, regardless of watering duration or fertilizer tier. A growth day requires sufficient accumulated active growth units for that day; for the initial baseline, 8 hours of normal active growth = one full credit. A 1.2× fertilizer can reach that day's credit in 6 h 40 min but cannot earn a second day's credit in the same day.

At each local day boundary, the day's credited growth units are finalized (partial units may be retained in that day's record for display, but cannot be rolled forward to create a second credit). The player may water before sleep and receive credit across the day boundary, subject to **one credit per day**. A long can reduces watering frequency and makes routines more forgiving; it does not reduce the number of distinct successful growth days.

A tree becomes mature only if:
1. It is alive.
2. At least the tier's precise ideal cumulative growth duration from section 3 has elapsed since planting.
3. It has earned at least `ceil(idealGrowthDays)` distinct daily credits (14 / 44 / 127 by tier).
4. It has reached its final stage's required growth units.

All four conditions must hold. Growth is clamped at the final threshold. Intermediate forms follow the universal geometric thresholds in section 13, not evenly spaced percentages. Form 1 appears immediately; there are exactly N−1 timed transitions. Use smooth in-form progress during longer late transitions.

**Important implementation detail:** Track fractional effective growth units by timestamp interval; split intervals at midnight, watering expiration, fertilizer expiration and stage thresholds. Prevent double accrual across process restarts. The first eligible calendar day is the planting day, but the tier-specific elapsed-calendar-time floor still applies, so planting-day credit never permits early completion.

## 5. Watering can prices and fertilizer prices

### 5.1 Durable watering cans

| Can | Price | Window | Role |
|---|---:|---:|---|
| Basic | Free starter entitlement | 8 h | All gameplay possible |
| Copper | 300 | 12 h | First attainable convenience upgrade |
| Silver | 900 | 18 h | More flexible care |
| Golden | 2,000 | 24 h | Once-daily convenience |

One equipped can at a time; purchases permanently unlock a tier, not consumable uses. **Water All is available even with Basic**, and uses the equipped can on all eligible living trees in one action. Repeated watering cannot extend an active window indefinitely. Mature trees still need survival watering. Upgrade prices are deliberately lower than Legendary seed cost but represent a meaningful trade-off: convenience versus new species.

### 5.2 Consumable fertilizer

| Tier | Unit price | Speed while watered | Duration | Suggested use |
|---|---:|---:|---|---|
| Basic | 35 | 1.1× | 4 h | Short session boost |
| Rich | 75 | 1.2× | 6 h | More forgiving daily credit |
| Legendary | 140 | 1.35× | 8 h | Maximum convenience |

At most one active fertilizer effect per tree; one application per tree per rolling 24 hours; no tier stacking, additive bonuses or queued effects. Fertilizer cannot activate growth when dry, bypass distinct-day credits, reduce the minimum calendar age, prevent death, or exceed final-stage growth. Never consume fertilizer on a mature/dead tree or when watering is inactive. Because fertilizer costs coins and cannot shorten the hard day floor, it is **optional convenience**, not mandatory progression.

## 6. Other shop prices

| Item / unlock | Proposed price | Notes |
|---|---:|---|
| Starter decoration | 100–200 | Cosmetic |
| Common decoration | 250–450 | Cosmetic |
| Rare decoration | 700–1,200 | Cosmetic |
| Premium decoration | 1,800–3,000 | Cosmetic |
| Additional garden plot | 400 first; +200 per subsequent plot | Finite progression; preview cost |
| Streak protection ticket | 250 | At most one use for one missed required day; no Daily Clear |
| Special garden theme | 1,500 | Nonseasonal, cosmetic |
| Extra starter-species seed | 150 | Only if repeat planting is allowed |

Do not sell consumable water ml, refill bottles or seasonal items. Shop UI must display estimated days to afford at current **7-day average earning rate**, with a neutral fallback at 40/day if insufficient history; label the estimate and avoid implying it is guaranteed.

## 7. Neglect and death remain meaningful but not excessively punishing

Unwatered elapsed time after the water window expires accumulates **hidden neglect**, one unit per full 24 h; watering reduces by 3 units (tunable to 2); 30 units causes irreversible death. The hidden value is never shown, but qualitative thirsty/wilting cues and gentle reminders are allowed. Growth stops when dry. Long watering cans reduce how often players need to intervene; they **do not erase accumulated neglect** or extend the 30-unit death threshold. No paid recovery, revival or death-prevention purchase is introduced.

Because a 90-day tree represents substantial effort, the UI should warn qualitatively well before critical neglect without revealing the exact counter. Reminders are optional and must respect notification permission and quiet hours.

## 8. Mature-tree sale and anti-inflation

Mature **living** trees may be sold once; dead trees cannot. A sale removes the active tree from its plot, records a historical disposition and grants coins **subject to the same 50/day positive-income cap**. Suggested resale prices (nominal before cap): free starter = 0; Common = 40; Rare = 180; Legendary = 900; Exclusive = 0 (non-sellable while paid entitlement is unimplemented). This is **20% of seed price**, except the free starter, and is intentionally not a profitable loop. Selling before maturity is not allowed. Any tree may be removed for zero coins at any stage.

**Cap behavior:** show both nominal sale value and actual coins receivable today before confirmation. If the daily cap leaves only 10 coins available, selling a 900-coin Legendary tree yields **10 coins** and forfeits the remainder; no deferred claim. Provide a clear warning and let the user cancel. This rule prevents mature sales from breaking the Legendary 90-day earning floor, but makes sales economically weak. If playtesting shows this feels unfair, redesign with a separately capped, non-transferable cosmetic reward—not an uncapped coin loophole.

Purchases, refunds and sales must be atomic and idempotent. No positive coin source, including onboarding, achievement or referral bonuses, may bypass the 50/day cap without an explicit separately approved design.

## 9. Economy UX and player motivation

- Home: coin balance, earned today (e.g. 39/50), one next affordable goal, next tree stage progress and watering countdown.
- Shop: price, owned status, tier, stage count, minimum maturity, estimated earning days and clear affordability.
- Garden: stage progress, days of care achieved / required (or visual progress), timer, Water All and fertilizer effect; **never display numeric death progress**.
- Weekly review: coins earned, spending, next affordable unlock and stage milestones.
- Free path: no coins required to plant and mature the first tree; basic watering and Water All always available.
- Do not require 5+ tasks/day to access the main game; 2–4 tasks/day should feel worthwhile.
- Show progress milestones during long approximately 127-day growth (12 visual forms) and allow player to keep a small/common tree growing alongside a premium tree.
- No artificial waiting screen while a timer is active; normal tasks and navigation remain available.

## 10. Tuning / QA scenarios

1. New profile gets one free seed and Basic can; cannot repeatedly claim the free seed.
2. Common tree has 6 forms and cannot mature before 14 elapsed days or 14 credited growth days.
3. Legendary tree has 12 forms and cannot mature before 126.29256 elapsed days or 127 distinct credited growth days, even with Golden can + Premium fertilizer.
4. At maximum earning 50/day and zero initial balance, 4,500-coin seed cannot be bought before 90 earning days.
5. Typical 40/day player needs 113 earning days for premium seed, not a week.
6. A 3-CLICK + 1-TIMER 20m + Daily Clear day nominal 47 grants 47.
7. A nominal 62-coin day grants 50, and the task completions still count.
8. One watering window overlapping midnight cannot award more than one credit per calendar day.
9. Fertilizer changes speed within the window but never awards >1 credit per day or bypasses minimum age.
10. Water All does not stack timers or create free extra growth.
11. Mature sale is capped and credits once; free starter resale is zero.
12. Clock rollback, app restart and offline catch-up cannot duplicate credits, coins or sale proceeds.
13. Existing wallets, seeds, trees, purchases and plots migrate without loss; historical income is not retroactively clawed back.
14. All shop prices and reward numbers come from versioned configuration, not scattered UI constants.
15. Run simulation of 14/30/90/180 days for casual (20/day), typical (40/day) and maximal (50/day) profiles before treating these values as final.

## 11. Balancing caveats and rollout

The proposed 50/day cap and 4,500 premium price establish the 90-day *coin* floor only for a new zero-balance player without legacy wealth. Existing players may already have coins or seeds; do not confiscate them to enforce new pacing. Previously purchased seeds remain owned. When changing prices, never retroactively charge for purchases. Expose catalog/reward balance version in data and tests. Gameplay tuning should follow actual playtests; adjust intermediate tiers before undermining the 14-day Common growth floor, approximately 127-day Legendary growth floor and 90-day Legendary coin-acquisition floor.

## 12. Four-tier consolidation note (2026-11-11)

Any reference to **Uncommon, Epic or a coin-priced Premium seed** in earlier phase documents is obsolete. The only supported tree tiers are **Common, Rare, Legendary and Exclusive**. The fourth tier is reserved for a possible future real-money cosmetic unlock; no payments or premium purchase UI should be built yet. Existing owned seeds and planted trees must remain usable when catalog entries are remapped to these four tiers. No existing user assets should be removed or retroactively charged.

## 13. Universal 1.40× transition progression (2026-11-11; supersedes previous 2× rule)

**Current approved design:** one universal base time, one universal **1.40× multiplier**, with **6 / 9 / 12 / 12 visual forms** for Common / Rare / Legendary / Exclusive. The previous 2× growth rule, 4-form Common, 7–8-form Rare and 90-day Legendary growth duration are **obsolete**. The 90-day Legendary *coin-earning floor* remains unchanged.

Let `r = 1.4`, `B = 14 / Σ(r^k, k=0..4) = 1.2790527700628567 days`, and `N = formCount`:

- Form 1 is available immediately at planting; there are `N−1` transitions.
- Duration of transition `i` (Form i → Form i+1, 1-based): `D_i = B * r^(i−1)`.
- Cumulative ideal growth budget to reach Form `i+1`: `C_i = B * (r^i − 1) / (r − 1)`.
- Ideal total to maturity: `T(N) = B * (r^(N−1) − 1) / (r − 1)`.
- Use precise, versioned fixed-point growth units for thresholds; calculate **cumulative** thresholds then round once, rather than independently rounding every transition.

| Transition | Duration (days) | Cumulative (days) | Milestone |
|---|---:|---:|---|
| Form 1 → 2 | 1.27905 | 1.27905 | |
| Form 2 → 3 | 1.79067 | 3.06973 | |
| Form 3 → 4 | 2.50694 | 5.57667 | |
| Form 4 → 5 | 3.50972 | 9.08639 | |
| Form 5 → 6 | 4.91361 | **14.00000** | Common mature |
| Form 6 → 7 | 6.87905 | 20.87905 | |
| Form 7 → 8 | 9.63067 | 30.50973 | |
| Form 8 → 9 | 13.48294 | **43.99267** | Rare mature |
| Form 9 → 10 | 18.87612 | 62.86879 | |
| Form 10 → 11 | 26.42657 | 89.29536 | |
| Form 11 → 12 | 36.99720 | **126.29256** | Legendary / Exclusive mature |

**Interpretation and care mechanics:** durations represent the idealized effective growth budget. Watering starts an active growth window; no water means no progress. The current daily-credit design allows at most one full credited growth day per local calendar day, so actual completion can be later than the idealized cumulative time. Fertilizer and longer cans can make it easier to earn a day's credit but cannot bypass the distinct-day and elapsed-time floors. With this model, require at least 14 / 44 / 127 distinct credited days respectively. Do not promise a guaranteed maturity date after missed watering.

**Consistency requirement:** the engine must keep fractional growth effort for intermediate form thresholds **separate from** the integer count of successful daily credits used for final maturity eligibility. A single credited day must never be awarded twice, and a fractional partial day must not be rounded up to a whole credited day. The last form only becomes visible when both its effective-growth threshold and the final maturity conditions are met. Split offline integration at local-day boundaries and effect/window expirations; store the balance version for deterministic migrations.

**Art:** each form is a distinct species-specific anatomical appearance, never a simple zoom or vertical reveal. No infinite growth or seasons.
