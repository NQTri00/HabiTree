# Simple Mics Redesign Implementation Plan

> **For agentic workers:** Use superpowers:executing-plans inline. Execute immediately; the user explicitly declined questions and approval gates.

**Goal:** Replace the ornate design with the three simple references in mics, targeting at least 90% visual similarity across six screens and three dialogs.

**Architecture:** Preserve feature MVI state, intents, effects, navigation and repository behavior. Replace shared decorative components and artwork first, then adjust each screen composition. Background images contain scenery only; all product text and interactive elements are Compose.

**Tech Stack:** Existing Kotlin, Jetpack Compose, Android resources; no new dependency.
**Spec:** mics/simple-home-tasks-shop.png, mics/simple-garden-care-journal.png, mics/simple-task-seed-planting.png.

## Global constraints
- The local mics references supersede all earlier ornate designs and extraction ledgers.
- Components may be extracted or inspired by the references. Never display a reference screen as app background.
- Smooth cream surfaces, muted forest green, honey wood, simple soft illustration; no photoreal textures, ornate vines or glitter outlines.
- Static source-style trees; growth animation deferred.
- All existing actions, nonreference catalog entries, loading and empty states remain usable.
- User-approved verification: builds and Compose Previews; no new automated test infrastructure.
- Do not change existing economy to match static mockup numbers.
- User authorized periodic commits and GitHub pushes after successful builds. Record checkpoints; do not imply visual acceptance from a build alone.
- Keep preview rendering sequential and limited to changed screens/components. Do not dump image/base64 payloads. Check current heap before another render if Studio slows or a tool times out.

## Review focus
1. Long Vietnamese names and 1.3 font scale: readable wrapping, scrollable forms, no overlaps.
2. Empty trees/inventory: meaningful call to action, no fake populated artwork.
3. Water/fertilizer unavailable: disabled action and accurate live counts.
4. Navigation and selected tree: five tabs remain visible except tree care; back retains state.
5. Catalog and reward actions: real prices/availability, no decorative false controls.

## Task 1 — Shared design system and art
Files: core-ui components GardenBotanicalFrame, GardenPaintedControls, GardenFormControls, GardenJournalPage, GardenForestScene, GardenArtwork, PaintedGardenPlant; app GardenTrailBar.
- [x] Extract/inspire separate scenery and plant assets from local references, save as simple_* resources; record prompts/provenance.
- [x] Replace ornate frames with smooth gradient cream cards and warm wood edges; replace vine buttons with plain green/gold pill controls.
- [x] Simplify journal, navigation, icons and progress material.
- [x] Build and render shared component previews; compare shape, color and density.

## Task 2 — Home, nursery and tree care
Files: home HomeScreen; garden GardenScreen; tree TreeInteractionScreen/TreeInteractionParts; app MamXanhAppContent.
- [x] Home: centered wood title, wallet below, cream garden heading; large tree and bottom name/progress; two small place signs; compact care tray.
- [x] Nursery: compact title/counts; staggered trees on islands; readable rounded name plaques; basket with live seed stock.
- [x] Tree care: back plaque over scenery, cream title, hero tree, progress, care and decoration trays, footer.
- [ ] Build; render each full screen at 360x800 and 1.3 font scale; correct placement/overflow.
  - Verified Tree Care full app shell at normal and 1.3 font scale after moving Back into the forest scene; lower content remains scrollable. Home normal shell refreshed. Nursery final full-screen variants remain pending.

## Task 3 — Tasks, shop and statistics
Files: tasks TasksScreen; shop ShopScreen; stats StatsScreen.
- [x] Tasks: one plain bound journal, compact filters, source-style book/water/boots, right reward badges, completion controls, gold daily reward, green add action.
- [x] Shop: simple mushroom alcove and three wooden shelves; four featured plants, live labels/prices, fertilizer, footer; other seeds below.
- [x] Stats: plain cream journal, counts, compact seven-day chart with axis, weekly grid and footer.
- [ ] Build and compare all screen previews against corresponding mics panel.

## Task 4 — Three dialogs
Files: tasks AddTaskDialog; shop ShopSeedDetailCard; garden PlantingParchment.
- [x] Smooth cream modal with warm wood rim and curved visual header; simplified fields and buttons.
- [x] Keep optional name, reward validation, kind/category/frequency/due, stock and affordability behavior.
- [ ] Render add task daily/TODO, seed detail, planting, and large-text variants; correct overlap.

## Task 5 — Final visual and behavioral audit
Verification ledger: docs/design-references/simple-mics-audit.md records observed cases and missing checks without replacing or narrowing the requirements below.
- [ ] Build :app:assembleDebug; inspect output.
- [ ] Render all six full app screens plus three dialogs after refreshing changed module previews.
- [ ] Compare against mics for composition, art style, palette, typography, control size and density.
- [ ] Verify empty and large text previews; check reachable controls and live state semantics.
- [ ] Record concrete remaining discrepancies. Do not claim the 90% target until the complete visual audit supports it.
