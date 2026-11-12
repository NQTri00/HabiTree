# Phase 4 — Living Garden & Collection: Detailed Gameplay & Product Concept

> **Current four-tier balance:** Common has 6 forms and 14 growth days (first seed free); Rare has 9 forms and 44 distinct credited growth days (900 coins); Legendary has 12 forms and 127 distinct credited growth days (4,500 coins); Exclusive remains unavailable. Precise elapsed floors and universal 1.40× transitions come from economy section 13. Older 5–6-tier tables, 4–12 stage ranges for intermediate tiers, and coin-priced Premium examples are superseded by [Economy & Growth Balancing v2](economy-and-growth-balancing.md). Do not implement payments yet.


> **Economy and progression update (2026-11-11):** [Economy & Growth Balancing v2](economy-and-growth-balancing.md) is authoritative for ALL coin rewards/caps, seed/shop prices, watering-can/fertilizer prices, mature sale payouts, and growth durations. Any older example below (150/day, 25-coin seeds, 3–50-day maturity, +25 instant fertilizer, etc.) is superseded. Free starter: minimum 14 elapsed days **and** 14 growth days. Premium: 4,500-coin seed (90 maximum-earning days from zero), minimum 126.29256 elapsed days **and** 127 distinct credited growth days to mature. Growth credits capped at one per day; upgrades/fertilizer cannot bypass the floor. Existing player wealth is grandfathered.


**Status:** Core implemented on 2026-11-12; [plan and verification evidence](superpowers/plans/2026-11-12-phase-4-execution.md). Optional audio and screenshot export are not shipped; species stage models/animations remain deferred. **Prerequisites:** stable Phase 2 tree identities, species-specific finite stages, watering timers, death/sale/removal rules and transactional inventory. **Goal:** Make the garden feel personal and collectible while remaining offline, peaceful and manageable.

## 1. Core experience

The garden becomes a customizable place rather than only a list of planted trees. The player unlocks plots, arranges owned decorations, inspects individual trees, and gradually completes a botanical collection. Progress is primarily cosmetic and expressive, not competitive.

Loop: earn coins → choose between seed, decoration or plot unlock → preview placement → confirm → enjoy a changed garden → visit encyclopedia for collection goals.

## 2. Garden layout and plots

Use a finite, deterministic grid/scene with stable plot IDs. Phase 4 default: initial unlocked plots as currently migrated; add expandable sections of 4–6 plots. Each plot supports one tree and compatible decoration slots (e.g. ground, border, ambient). Locked plots show price and preview, never accept plants until unlocked.

Camera/viewport: pan within bounds, zoom with limits, reset-to-center, tap to select tree, long press to enter arrange mode. On narrow devices use a vertically scrollable or paged layout rather than tiny touch targets. Persist viewport preference only if useful; never persist transient gesture offsets as gameplay state.

**Arrange mode:** selected object highlights; available positions show clear valid/invalid feedback; drag or tap-to-place; Cancel restores the last committed layout; Save commits a single atomic layout change. Prevent overlapping trees, blocked paths and off-grid placements. Support undo for the current uncommitted edit session.

## 3. Decoration categories

| Category | Examples | Placement | Effect |
|---|---|---|---|
| Ground | grass patch, stone tiles, moss | Ground cells | Cosmetic |
| Border | wooden fence, stepping stones | Plot edges/path | Cosmetic |
| Accent | lantern, small sign, garden stone | Free decoration slot | Cosmetic |
| Ambient | butterflies, petals, fireflies | Scene-wide toggle | Cosmetic, reduced-motion compatible |
| Tree accessory | ribbon, pot style, small charm | Specific tree | Cosmetic |

Every decoration has SKU, name, category, price, owned count, compatibility constraints, footprint, art assets and rarity tier. A decoration may be purchased repeatedly only when it is consumable/stackable; permanent unlocks should not charge twice. Moving an owned decoration never charges coins. Selling/removing returns it to Inventory unless the item is explicitly marked consumable and confirmation is shown.

## 4. Shop and placement preview

Shop adds Decorations category with filters (Ground, Accent, Ambient, Tree) and “Owned” badges. Detail sheet shows image, size, compatibility, price and preview on a sample plot. Buying adds inventory; **buying does not auto-place**. “Place now” enters arrange mode and preserves the chosen item and garden context.

Placement transaction checks: item ownership, target compatibility, no overlap, available slot and current layout version. Failed validation leaves both inventory and garden unchanged. Canceling preview consumes nothing.

## 5. Botanical encyclopedia

A species catalog distinct from Inventory. Each species has:
- Stable species ID, display name, botanical-inspired description (avoid false scientific claims), silhouette preview and rarity.
- Collection status: Undiscovered, Discovered (seen in shop), Owned seed, Planted, Matured, Flourished.
- Stage gallery with locked silhouettes for unseen stages; reveal art only after the player's tree reaches that stage.
- Species-specific stage gallery, watering duration and fertilizer compatibility.
- Personal records: first planted date, first matured date, tallest owned individual, total planted.
- Link to Shop for purchasable species and Garden for owned individuals.

Collection percentage must define its denominator (e.g. discoverable species in the currently shipped catalog). Removing a tree does not erase its historical encyclopedia milestones. Species added in updates change the denominator predictably; never silently lose previous discoveries.

## 6. Garden environment themes (nonseasonal)

**No season system.** Do not offer Auto/Spring/Summer/Autumn/Winter modes, hemisphere settings, seasonal sprites, weather-driven appearance or season-based growth. The environment may offer purely cosmetic, manually selected static themes such as Meadow, Stone Courtyard or Night Garden, independent of calendar date. These themes never change growth, neglect, fertilizer, inventory or sale values. Theme unlocks are permanent cosmetic entitlements and use the normal shop/ledger rules.

## 7. Garden ambience and accessibility

Add optional subtle environmental motion: leaves swaying, petals drifting, butterflies and soft light. Audio defaults off; ambient sounds opt-in and pause when app backgrounds. Provide Reduced Motion, Particle Density (Off/Low/Normal), Battery Saver and mute controls. No animation should interfere with reading task cards or selecting care buttons.

Render large trees with stage-appropriate sprite layers and texture budgets. Use asset atlases or caching, avoid re-decoding images on every recomposition, and release offscreen assets. Test on an 8 GB RAM Android device with limited GPU resources.

## 8. Collection milestones and cosmetic rewards

Initial goals:
- Plant 3 distinct species → collection badge.
- Mature 3 species → decorative sign.
- Flourish first blossom tree → petal ambient toggle.
- Unlock first expansion section → stone path pattern.
- Fill one garden section with unique species → display plaque.

Rewards are permanent cosmetic entitlements recorded once, not repeatable coin farming. Provide progress and unlock criteria before acquisition. No randomized paid loot boxes or limited-time fear-of-missing-out mechanics.

## 9. Garden history and personal expression

Garden details page: garden nickname, creation date, species count, mature living tree count, decoration count and chosen environment theme. A “Garden Moments” timeline captures first plant, first bloom, plot expansion and collection milestones, linking to Phase 3 memory timeline.

Optional **local screenshot** export can be offered after permission/OS share-sheet integration, but no online profile or public gallery. The scene should hide interactive controls during screenshot capture and respect image ownership/license.

## 10. Data architecture and migrations

Suggested entities: `GardenSection`, `GardenPlot`, `PlacedDecoration`, `DecorationEntitlement`, `SpeciesDiscovery`, `SpeciesMilestone`, `GardenAppearanceSettings`, `CollectionReward`. Shared KMP validates layout and owns persistence; Garden feature owns scene, placement UI and rendering; Shop owns catalog UI; Inventory owns item presentation; Encyclopedia may be its own feature module if sufficiently independent.

Preserve old plot IDs and tree assignments. New section layout must not shift trees into different slots. Migrate existing decoration strings to stable SKU/entitlement records. Unknown future catalog IDs render fallback placeholders rather than crashing.

## 11. Acceptance tests

1. Existing trees remain in the same plots after upgrade.
2. Locked plots cannot be planted or decorated.
3. Dragging a decoration then Cancel changes nothing.
4. Save places one owned item in one compatible slot.
5. Invalid overlap does not consume an item.
6. Moving a permanent decoration never charges again.
7. Buying decoration updates Inventory immediately.
8. Encyclopedia stage unlock matches actual saved tree milestone.
9. Removing a tree does not erase discovery history.
10. Nongarden environment theme changes appearance only.
11. Reduced Motion disables particles without losing content.
12. Garden remains responsive with many trees and decorations on target Android hardware.
13. Offline use and app restart preserve exact layout.

**Done when:** the player can design and save a distinctive garden, browse a reliable species encyclopedia and experience accessible nongarden ambience without affecting progression integrity.

## 12. Approved lifecycle integration (2026-11-11)

- **Variable species stages:** Encyclopedia and collection milestones read each species' `stageCount` (6/9/12), not a fixed six-stage array. Final-stage completion is `MATURE`, with no further height/progress.
- **Water All:** Garden/Plant List toolbar includes a prominent Water All action, which uses the equipped durable watering can and starts water windows for all eligible living trees. Show eligible/already-watered/dead counts; do not use ml balances. Tree cards display percentage progress; growth time stays an internal calculation, following the user's approved UI requirement.
- **Tree dispositions:** Fully grown living trees have Sell and Remove. Growing trees have Remove. Dead trees have Remove only. Sell is a wallet-credit transaction; Remove grants no coins/seed refund. Both free the plot and preserve historical encyclopedia achievements.
- **Death:** A dead tree remains visible as a withered plot occupant until removed; cannot be watered, fertilized, sold or revived. No numeric death counter is exposed. Existing Garden Moments may record death and sale.
- **Shop additions:** durable, nonstacking watering-can tiers (8/12/18/24 h proposed) and consumable, nonstacking fertilizer tiers (1.1×/1.2×/1.35× proposed) are part of the shop/inventory catalog. Only one watering can is equipped; at most one fertilizer effect per tree with cooldown.
- **No seasons:** no seasonal gallery, environment, art, collection requirement or date-based season preference.
