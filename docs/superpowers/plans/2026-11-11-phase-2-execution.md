# Phase 2 execution plan — GitHub revision 78892d5

Goal: complete the living-tree lifecycle, inventory, care and sale experience using the updated authoritative economy document. Preserve player assets and the approved mint UI; verify gameplay, migrations, builds and responsive screens before publishing.

GitHub plan synchronized on 2026-11-11. The latest universal 1.40× progression supersedes earlier 2× weights and 4/7/12 forms. No permission block remains: IDE writes have succeeded.

## Approved scope and acceptance goals

1. Author Common / Rare / Legendary profiles with 6 / 9 / 12 forms and prices 200 / 900 / 4,500. Reserve Exclusive (12 forms) for a future cosmetic entitlement; do not offer billing or purchases. First Common seed remains a one-time free entitlement.
2. Use one universal transition base: 14 / sum(1.4^k, k=0..4) days. Store cumulative thresholds rounded once in fixed-point units, with balance version. Ideal elapsed floors are 14 / 43.99267 / 126.29256 days; distinct daily-credit floors are 14 / 44 / 127. Fractional growth effort and whole credited days remain separate.
3. Finish additive schema migration, legacy relative-progress conversion, fair 8h survival grace and owned Basic can. Preserve wallet, inventory, tree IDs, plots and historical records. Remove volume/refill and task-driven instant growth.
4. Complete atomic watering, Water All, owned/equipped cans, fertilizer overlap/cooldown, irreversible neglect, mature capped sales and zero-refund removal. Validate before consuming items and prevent retries/clock rollback from duplicating effects.
5. Integrate Home, Garden filters/Water All, tree detail, Inventory and Shop. Show stage progress, care days, qualitative status, countdowns and upgrade choices. Keep centered half-screen trees, compact round controls, three-column catalogs, angled gradients and scroll edges. Keep Tasks simplified and no visible logs.
6. Verify real SQLite boundaries, universal thresholds, 14/30/90/180-day simulations, midnight/DST, restart, migration, retries and capped sales. Build Android plus shared metadata/iOS Kotlin; inspect full scroll ranges on small and larger phone configurations using an isolated QA profile. Publish only tested implementation files.

## Constraints

- User explicitly deferred growth artwork, animation and transitions until models are ready. Use existing static images temporarily; stage gameplay remains functional. Do not claim distinct stage art is delivered.
- Growth only while watered; maximum 8h-equivalent effort and one full credit per pinned local day. Partial effort must never round into an extra daily credit. Maturity requires units, age and distinct credits together.
- Basic/Copper/Silver/Gold windows: 8/12/18/24h. Water All is free and includes living mature trees for survival, skipping active/dead trees.
- Fertilizer: 35/75/140 coins, 1.1/1.2/1.35× for 4/6/8h, one application per rolling 24h, no stacking.
- Hidden precise dry time causes irreversible death at 30 day-equivalents; eligible watering recovers exactly 3. Show qualitative cues only.
- Sale income shares the 50/day cap; confirmation exposes nominal/granted/forfeited values. Removal grants no coins or seeds; preserve disposition history.
- Legacy full-grown trees remain grandfathered mature. Existing assets are remapped without charges or confiscation. Preserve unrelated user changes/staged deletions.
- Main player profile is not used for destructive QA. No mass preview rendering. Fully offline deliberate forward-clock abuse cannot be completely prevented.

## Evidence

- GitHub documentation fast-forwarded from c6f77cd to 78892d5; all unrelated staged removals and user edits restored unchanged. IDE writes are available.
- Universal schedule is version 3 in persisted tree state. Common/Rare/Legendary share cumulative thresholds; final units and elapsed floors are fractional, while credits remain 14/44/127. Exclusive stays unavailable.
- 60 real-SQLite Android tests passed (shell-309, 2026-11-11); Android debug/verification APKs, common metadata and iOS ARM64 Kotlin compiled successfully. Tests include schema v3/v4 migration, legacy mature preservation and 8h grace, file-backed restart/expiry/fertilizer, concurrent watering/fertilizing/selling, all cap/clock/DST and 14/30/90/180-day scenarios.
- Isolated QA package com.mamxanh.app.verification.phasetwo: starts at 0 coins with one free Common seed and Basic can. Planting consumes the seed into plot01. Free Water All starts an8h window and disables repeat watering. Detail is stage1/6, has small round controls and scrolling care/removal sections.
- Reinstall and force-stop/relaunch preserve that tree, consumed stock and the same watering deadline (7h53m remaining on inspection). No extra growth credit or window is awarded.
- Runtime inspected full tree care panel (all three fertilizers and shop link), full eleven-seed catalog, unavailable Exclusive note, footer and Legendary confirmation. Price4,500,12 forms,127 care-day requirement and113-day affordability estimate at40/day are consistent; insufficient-funds purchase is disabled.
- 320dp/font1.3 uses two catalog columns with wrapping labels. Durable-can confirmation has no quantity selector; its body scrolls and footer remains visible. Short640×360dp/font1.3 landscape keeps close/cancel/purchase visible. Normal phone layouts retain three columns.
- No preview atlas or stage animation was rendered. Stage artwork and transitions remain the explicitly requested deferral; iOS native runtime remains unverified on Windows.
- Final UI-only debug/verification APK build passed in34s (shell-315). At412dp, Home's complete lower content and goal/shop cards are reachable; catalog layout retains three columns. Switching scrolled Seeds→Items starts Items at its independent position.
- Main app installed successfully over existing data. Wallet50 coins,4 trees,6 unlocked plots,1 fertilizer and1 succulent seed retained. Cherry and pine remain mature at9/9 and12/12; the partial succulent maps to3/6 with5/14 credited care days. Basic can is equipped; legacy trees receive8h grace and no retroactive neglect. Main-profile QA used navigation only.
- Device restored to physical720×1280/density320/font1.0. No old volume/refill API references remain in Kotlin source. git diff --check passed; user-owned deletions and old annotated-plan edits are excluded from the implementation commit.
- All implementation/verification goals above are complete within the explicit static-art and Windows/iOS-runtime constraints. Publication is recorded by the branch commit containing this plan.
