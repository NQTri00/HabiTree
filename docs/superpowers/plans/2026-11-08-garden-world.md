# Mam Xanh Garden World Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Give Mam Xanh distinct garden-themed destinations, an immersive tree encounter, and route-aware Compose transitions that make opening a tree feel like moving closer to it.

**Architecture:** Keep AppViewModel's existing route state and place one `SharedTransitionLayout` plus `AnimatedContent` around the app's destination body. Share the same tree illustration between Home and Tree using Compose shared-element APIs; animate other destinations directionally. Add reusable scene backdrops and botanical framing in `:core-ui`, then apply them in the existing feature modules.

**Tech Stack:** Kotlin, Jetpack Compose, Compose animation/shared-element APIs, Android Studio Compose Preview.

**Spec:** `docs/superpowers/specs/2026-11-08-garden-world-design.md`

## Global Constraints

- Preserve all current navigation destinations, selected-tree behavior, Back behavior, feature state, and domain actions.
- Preserve Tree's Water, Fertilize, and four decoration choices.
- No UI visual acceptance from emulator screenshots; use Compose Preview renders.
- Every changed or new UI composable gets a deterministic mock-data `@Preview`.
- Do not add or run automated tests; verify changed previews and build the app.

## Review Focus

- Home tree is missing or the garden is empty: the destination remains renderable and its empty state still invites planting.
- Tree has low/high growth, watered/unwatered status, and decorations: all current actions and growth feedback remain available.
- Long screen copy and large font scale: labels remain readable and cards do not clip horizontally.
- Small phone height: Tree's central plant remains the focal point and lower care controls remain scroll-reachable.
- Switching between distant tabs and entering/backing out of Tree: route direction and source destination remain correct; inspect final screen previews and review the transition-state mapping in code.

---

### Task 1: Add reusable garden scene art

**Files:**
- Create: `core-ui/src/main/java/com/mamxanh/app/presentation/ui/components/GardenSceneBackdrop.kt`
- Create: `core-ui/src/main/java/com/mamxanh/app/presentation/ui/components/GardenBotanicalFrame.kt`
- Modify: `core-ui/src/main/java/com/mamxanh/app/presentation/ui/components/GardenPastoralBackdrop.kt`

**Interfaces:**
- Produces `GardenSceneBackdrop(scene: GardenScene, modifier: Modifier = Modifier)` and a reusable light surface/frame composable with a botanical or soil-edge accent.
- Scene variants: `BEDS`, `JOURNAL`, `NURSERY`, `GROWTH_JOURNAL`, `TREE_CLEARING`; keep Home's existing wide Farm backdrop.
- Both new composables have dedicated previews covering their variants.

- [ ] Add scene enum/backdrop and botanical frame using Compose Canvas/draw primitives, with contrast-safe surfaces.
- [ ] Add previews for each scene and the frame in realistic 360dp layouts.
- [ ] Render the backdrop and frame previews; refine any shapes that compete with content.

### Task 2: Animate app destination changes and connect the tree hero

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/java/com/mamxanh/app/presentation/ui/MamXanhApp.kt`
- Modify: `home/src/main/java/com/mamxanh/app/feature/home/presentation/HomeScreen.kt`
- Modify: `tree/src/main/java/com/mamxanh/app/feature/tree/presentation/TreeInteractionScreen.kt`

**Interfaces:**
- App host owns `SharedTransitionLayout` and `AnimatedContent`; keep the shared animation scope types inside `:app`.
- Add an optional `Modifier` parameter for the matching tree illustration to Home and Tree screens, defaulting to `Modifier`, so their existing independent previews remain simple.
- Use a stable shared key containing the selected tree ID. Use about 450ms for the Home↔Tree hero move and about 260ms for tab transitions.
- Use horizontal motion for ordinary tab moves, a gentle upward journal motion for Home↔Tasks and Home↔Stats, and the tree hero scale/scene change for Tree. Keep existing route order HOME, TASKS, GARDEN, SHOP, STATS for direction decisions.
- Only Home and Garden open Tree in current code; preserve the current destination so Back returns to the source.

- [ ] Add the Compose animation catalog alias and app dependency required for `SharedTransitionLayout`, `sharedElement`, and `AnimatedContent`.
- [ ] Wrap destination body in a route-keyed transition host; include incoming/outgoing fades/slides/scales and coordinate the app bars' visibility with Tree entry.
- [ ] Pass the optional hero modifier to the existing `KawaiiPlantIllustration` in Home and to Tree's matching illustration.
- [ ] Extract the visual Scaffold/route host into a state-driven content composable so the app shell has a mock-data Preview without constructing ViewModels.
- [ ] Add `HomeScreenPreview` and focused populated/empty `HomeCenterPlantPreview` wrappers with deterministic mock data; retain the existing Tree screen preview.
- [ ] Render Home, app-shell, and Tree previews; build the app module to catch shared-transition API or dependency errors.

### Task 3: Give Garden and Tasks distinct garden-place surfaces

**Files:**
- Modify: `garden/src/main/java/com/mamxanh/app/feature/garden/presentation/GardenScreen.kt`
- Modify: `tasks/src/main/java/com/mamxanh/app/feature/tasks/presentation/TasksScreen.kt`

**Interfaces:**
- Garden uses `GardenScene.BEDS`; Tasks uses `GardenScene.JOURNAL`.
- Reuse core-ui art components; preserve existing UI state models, event callbacks, lists, dialogs, rewards, and accessibility/touch behavior.
- Keep or add a dedicated Preview for every UI composable in each affected screen file, including helpers, using deterministic mock data.

- [ ] Apply planted-bed scenery and selective soil/leaf frame accents to Garden collection surfaces.
- [ ] Apply the light field-journal treatment to Tasks, replacing the dominant solid-green panel feel while preserving task reward hierarchy.
- [ ] Render populated, empty, large-text, and changed helper previews; refine any clipping, contrast, or noisy decoration.

### Task 4: Tie Shop and Stats into the garden art language

**Files:**
- Modify: `shop/src/main/java/com/mamxanh/app/feature/shop/presentation/ShopScreen.kt`
- Modify: `stats/src/main/java/com/mamxanh/app/feature/stats/presentation/StatsScreen.kt`

**Interfaces:**
- Shop uses `GardenScene.NURSERY` while retaining the existing pink seed-tile identity and shelf illustrations.
- Stats uses `GardenScene.GROWTH_JOURNAL`; preserve all existing statistics values and chart semantics.

- [ ] Add restrained nursery scenery/frame detail that joins Shop to the garden without obscuring its seed grid.
- [ ] Add growth-ring and seasonal journal details around Stats summary/chart surfaces.
- [ ] Ensure every modified/new UI composable has a mock-data Preview.
- [ ] Render Shop and Stats root and changed helper previews; refine density/readability.

### Task 5: Rebuild Tree as the garden focal scene

**Files:**
- Modify: `tree/src/main/java/com/mamxanh/app/feature/tree/presentation/TreeInteractionScreen.kt`
- Modify: `tree/src/main/java/com/mamxanh/app/feature/tree/presentation/components/TreeInteractionParts.kt`

**Interfaces:**
- Tree uses `GardenScene.TREE_CLEARING` and the shared hero illustration modifier.
- Preserve `TreeCareActions`, `DecorationPicker`, the same callbacks, and feedback copy; reposition but do not remove them.
- Scene accents can vary deterministically by species (blossom tree, evergreen tree, general leafy tree) so previews remain stable.

- [ ] Replace the oversized white portrait card with a layered outdoor clearing, a prominent central plant, compact name/growth HUD, and ground/soil framing.
- [ ] Group watering/fertilizer controls and decoration chips below the focal plant; allow scroll on compact screens.
- [ ] Add previews for every changed/new UI composable and cover cherry/pine, watered/unwatered, and selected decoration states.
- [ ] Render Tree root, portrait/scene, action, and decoration previews; iterate until tree is clearly the focal point without clipping actions.

### Task 6: Review all destination previews and compile

**Files:**
- All UI files listed above.

- [ ] Render the final Home, app-shell, Tasks, Garden, Shop, Stats, and Tree screen previews, plus every changed/new shared/helper composable preview.
- [ ] Audit every `@Composable` in the affected screen and art files; each has a dedicated `@Preview`. Add any missing wrappers with deterministic mock data.
- [ ] Inspect images and semantics; fix visual issues and re-render affected previews until they read as one garden world with distinct destinations.
- [ ] Run `:app:assembleDebug`; resolve compile errors.
- [ ] Do not use emulator screenshots for visual evaluation.
