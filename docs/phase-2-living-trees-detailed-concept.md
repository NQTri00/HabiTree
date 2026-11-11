# Phase 2 — Living Trees: Detailed Gameplay & Product Concept

**Status:** Proposed; not implemented. **Prerequisite:** Phase 1 economy, idempotent transactions and reactive persistence. **Scope:** Replace short 0–120 tree growth with long-lived, individualized, time-based trees while preserving current gardens and owned items.

## 1. Player fantasy and daily loop

Each planted tree is a persistent living individual. The player chooses which tree to nurture, sees subtle changes across days, watches it develop recognizable new shapes, and remembers when it was planted. Tasks create **care energy/coins**, but do not instantly grow every tree. The player should never lose a mature tree because they skipped a day.

Loop: complete tasks → earn coins and bounded care energy → open garden → inspect each tree's needs and next milestone → water/fertilize one chosen tree → watch a small reaction → return tomorrow for time-based growth → unlock new appearance stage.

## 2. Tree identity and planting

Every planted tree has a unique ID, species ID, player-editable nickname (1–40 characters), planted timestamp, plot ID, current stage, continuous height, growth experience, hydration, vitality, last simulated timestamp, last watered date, care history and cosmetic variant ID. Multiple trees of the same species grow independently. Renaming does not alter species or records.

Plant confirmation: species preview, plot, seed quantity, first-stage preview, and a note that growth takes real days. Confirm consumes exactly one seed and creates one tree atomically. Existing planted trees must migrate without being recreated or reset.

## 3. Six development stages

| Stage | Default entry | Visible change | Player feedback |
|---|---|---|---|
| Seed | On planting | Seed/soil with tiny emerging tip | First-day welcome |
| Sprout | ~Day 1–2 eligible growth | Stem and first leaves | “First leaves” |
| Sapling | ~Day 3–5 | Distinct young silhouette, branches | Growth milestone |
| Young tree | ~Day 6–9 | Developing canopy/trunk | New detail layer |
| Mature tree | ~Day 10–16 | Full species silhouette | Major reveal |
| Flourishing | ~Day 17+ | Blossoms/fruit/ornamental details | Long-term care begins |

Times are initial design targets, not exact guaranteed deadlines. Growth must depend on bounded time and care, not fixed elapsed calendar days alone. Species can have different visual thresholds; tuning data must be versioned. Mature trees continue to gain height and milestones without requiring infinitely many bespoke art stages.

**Stage transitions:** only move forward; no stage regression due to skipped care. Record milestone date, trigger animation once, and show milestone in tree history. A migrated tree's old growth maps to a sensible stage without moving backward visually.

## 4. Continuous growth and height

Persist `heightMm` (integer) and `growthXp` (integer) separately. Height is a meaningful long-term number, not a raw UI percentage. Render scale from species-specific curves, not direct linear scaling that makes mature art gigantic.

Proposed initial stage thresholds (species can override): seed 0–9 cm, sprout 10–24 cm, sapling 25–49 cm, young 50–89 cm, mature 90–119 cm, flourishing >=120 cm. These are *illustrative tuning thresholds* and must be reconciled with current saved growth.

**Simulation:** compute growth only from elapsed real time since `lastSimulatedAt`, bounded by a maximum catch-up interval (e.g. seven days) and a maximum height gain per local day (original concept suggests 15 cm). Use a deterministic function of elapsed time, hydration/vitality and species growth profile; round consistently to avoid repeated fractional gain from frequent app opens. No growth from simply reopening or navigating. Store `lastSimulatedAt` in the same transaction as height changes. A skipped day may reduce growth speed but never destroy the tree.

**Long tail:** beyond flourishing, growth slows via a species curve; achievements at species-appropriate height milestones. Do not promise biologically unrealistic 10 m bonsai without explaining stylized scale.

## 5. Care resources and limits

- **Water:** per-tree hydration 0–100. Water adds hydration and a small bounded immediate visual reaction; does not bypass daily height cap. Existing 50 ml watering cost should be preserved initially, then tuned.
- **Fertilizer:** increases growth efficiency for a bounded duration or number of growth ticks; no unlimited instant height. Existing fertilizer inventory migrates intact.
- **Care energy:** optional new task-earned resource, earned once per valid task completion with a daily cap, spent on targeted care. If introduced, display separately from coins and prevent double conversion of legacy task rewards.
- **No punishment spiral:** missed care means slower growth, not tree death, inventory loss or negative wallet.
- **Already full:** prevent wasting water/fertilizer if the effect would be zero; show reason.

Care controls remain small circular buttons around/below the featured tree. Show resource count, eligibility, expected benefit, and successful change. Actions must be atomic and idempotent.

## 6. Species individuality

Each species defines: seed asset, six stage assets/layer presets, growth curve, target heights, water response, fertilizer response, seasonal support, flowering/fruit stage, and optional ambient motion. Start with **three complete species** (cherry/peach blossom, mandarin, bonsai pine), then migrate remaining catalog entries to a graceful generic growth profile. Reconcile the shop species list and tree species registry before rollout; no purchasable seed may resolve to missing artwork or species data.

Species-specific details:
- **Blossom tree:** visible branch expansion and blossom clusters at flourishing.
- **Mandarin:** glossy foliage, flowers then green/orange fruit; fruit is cosmetic in this phase, not a harvest economy.
- **Bonsai pine:** branch structure and needle density; no fruit.

## 7. Visual growth, animation and asset contract

A growth stage must use **different anatomy/art**, not zoom, clipping, vertical reveal, or crossfade of one mature tree image. Provide distinct stage silhouettes and transparent layered assets: trunk, branches, leaves, flowers/fruit, optional foreground details. Keep the approved Mics mint/white UI and centered tree composition.

Animated actions: subtle sway at idle; watering droplets and leaf response (1–2 s); fertilizer glow/particles (1–2 s); stage transition with short reveal (2–4 s). Reduced-motion setting disables nonessential movement. Animation state never owns authoritative growth data. Offline, low-memory fallback is a static sprite.

Art acceptance: compare all six stages side by side; silhouette and branch count must visibly differ. Test on small Android devices and avoid large texture allocations.

## 8. Tree detail and garden interactions

Tree detail: nickname/species, planted date, age in days, stage, current height, progress to next stage, hydration, vitality, last care, upcoming milestone and timeline. Tap stage preview for “what changes next”; do not show a fake exact completion date when growth depends on care.

Garden plots display miniature stage-appropriate artwork, care-ready indicator and name. Featured tree selection persists across restarts. The garden list and Home must update immediately when growth simulation commits.

## 9. Seasons (foundational support, not full Phase 4 decoration)

Store a user-selectable visual season mode: Auto (based on configured locale/date), Spring, Summer, Autumn, Winter, or Off. Default Auto should not assume Vietnam has four equally distinct seasons. In Phase 2 implement seasonal hooks for the initial three species; complete decorative environmental themes can wait for Phase 4. Seasonal appearance must not alter saved growth or species identity.

## 10. Data and architecture

Suggested KMP models: `TreeInstance`, `SpeciesGrowthProfile`, `TreeGrowthSnapshot`, `CareEffect`, `TreeMilestone`, `TreeAppearanceVariant`, `TreeCareEvent`. Core operations: `PlantTree`, `SimulateGrowthUntil`, `WaterTree`, `FertilizeTree`, `RenameTree`, `ObserveTree`, `ObserveGarden`. Simulation and storage live in `shared/commonMain`; Android Compose rendering, animation and resources stay in the owning feature module. Do not share feature ViewModels across modules.

Migration: preserve tree IDs, plot assignments, species, old growth, water/fertilizer inventory and cosmetics. Convert old growth 0–120 into a stable new baseline and mark migration version; rerunning migration must not award extra height. Do not infer real planting timestamps where none exist; mark estimates.

## 11. Acceptance tests

1. Two trees of same species grow and receive care independently.
2. Completing one task does **not** automatically increase every tree's height.
3. Reopening the app repeatedly in one minute adds no duplicate growth.
4. Seven offline days yield at most the bounded catch-up growth, exactly once.
5. Clock rollback cannot reverse stages or create repeated daily care.
6. Water/fertilizer consumption and effects commit atomically.
7. Fertilizer at zero possible benefit is not consumed.
8. Each of six stages has distinct anatomical artwork.
9. Old trees, inventory and plots survive migration.
10. Reduced motion and low-memory static rendering remain usable.
11. Season changes affect appearance, not height or transaction history.
12. A mature tree continues long-term progression without stage regression.

**Done when:** three species have fully working six-stage visual growth, all planted trees have persistent identity and bounded time-based growth, and existing saves remain intact.
