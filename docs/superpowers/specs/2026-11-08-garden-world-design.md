# Mam Xanh Garden World Design

## Intent
Make the app feel like one living garden with distinct places, and make opening a tree feel like walking from the wide farm view up to a particular tree. The experience should be warm and illustrative while keeping content readable and the existing custom navigation/state architecture intact.

## Goals
- Give every main destination a recognizable garden setting and a shared illustrated visual language.
- Make Home-to-Tree and Garden-to-Tree transitions feel like a camera push toward the selected tree, with the garden scenery changing into a close seasonal clearing.
- Give the Tree screen an immersive, centered tree presentation inspired by creature-collection encounter screens: the plant is the focal point, with an outdoor environment behind it and care actions grouped near the lower edge.
- Give every changed or newly introduced UI composable a mock-data Compose Preview and use rendered previews to review the visual result.

## Non-goals
- Do not replace the existing AppViewModel navigation state with a navigation framework.
- Do not add a remote image service, game mechanics, or user-controlled camera.
- Do not use emulator screenshots to judge visual quality; review screen appearance with Compose Previews.

## Approaches
1. One garden world with distinct destination scenes (selected): shared palette, painted landscapes and selective botanical framing; directional slides between tabs; a deeper zoom and scenery crossfade for tree entry. This supports the requested atmosphere without making all screens identical.
2. One common backdrop and decorated cards: less change, but screens continue to read as generic cards placed over scenery.
3. Fully bespoke scenes and transitions for every route: most expressive, but substantially more artwork and maintenance than needed for a cohesive first pass.

## Route choreography
- Main-tab changes slide horizontally according to the destination order HOME, TASKS, GARDEN, SHOP, STATS, with a short fade; target duration is about 260 ms.
- Opening a tree from Home uses a shared-element hero move on the selected tree illustration: it grows from the farm's center into the foreground of a close clearing as the wide scene crossfades. Back reverses the transition.
- Opening a tree from Garden uses the same close-in push and clearing crossfade. The current Garden tree tile may use a different illustration, so use a shared-element match only if its rendered plant is the same visual; otherwise use a scene scale/fade with the same timing.
- Home-to-Tasks lifts like a field journal; Home-to-Shop warms into a greenhouse/nursery; Home-to-Stats drifts upward like opening a growth journal. Keep tab changes subtle and use the same transition host rather than separate navigation stacks.
- Tree views are currently opened only from Home and Garden. Back returns to the source destination because AppViewModel retains the active destination while the selected tree is shown; preserve that behavior and preserve scroll state where the current composition allows it.

## Visual direction
- Home remains the wide farm view, with clear garden scenery and its existing interactive pump/tree composition.
- Garden reads as planted beds: keep tree illustrations prominent and add selective soil/leaf framing to collection surfaces.
- Tasks reads as a field journal: use paper-like light surfaces, leaf tabs and small growth/reward details, avoiding a solid green slab that competes with the garden.
- Shop retains its pink nursery identity and seed shelf, with seedling/greenhouse scenery tying it back to the same garden world.
- Stats reads as a growth journal with seasonal/growth-ring motifs around its summary and chart.
- Tree becomes a close outdoor clearing: sky and layered foliage, ground/soil island, species-aware seasonal accents, centered large plant, compact name/growth HUD, and reachable care controls near the bottom. Avoid a large blank portrait card.
- Preserve the existing Water, Fertilize, and all four decoration choices on the Tree screen; arrange them below the focal plant and allow scrolling on short windows.
- Botanical and bark-like edges are accents on selected surfaces, not dense textures behind text. Preserve contrast and touch targets.

## Constraints and implementation choices
- Build with Jetpack Compose in the existing modules; use `SharedTransitionLayout`, `AnimatedContent`, and shared-element APIs from Compose animation, adding an explicit dependency only where needed.
- Keep navigation state in AppViewModel and preserve Back behavior; do not change domain/repository behavior.
- The current Tree screen actions are all retained: Water, Fertilize, and the four decoration choices.
- Current tree entry points are Home and Garden. AppViewModel leaves the active destination in place while a tree is selected, so Back returns to the source without adding a second navigation stack.
- Use deterministic mock data in previews. Every changed or new UI composable in the affected screen/art files must have a dedicated `@Preview`.
- Check layout quality by rendering the Compose Previews at phone size, including larger text where the component supports it. Do not use emulator screenshots for visual acceptance.
- Any platform behavior or API detail left open is an implementation choice; no library beyond Compose is required.
