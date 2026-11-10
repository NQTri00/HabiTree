# Current Mics implementation — 2026-11-11

## Goal and authority
Implement the approved redesign from mics/01.png through 09.png. These nine mint/white annotated concepts supersede earlier ornate and composite artwork instructions. Preserve saved data, catalog prices, MVI boundaries, Koin/Ktor and persistence.

## Working instructions
- Extract/reuse matching isolated artwork first. Build gradients, cards, text, forms and controls in Compose; never use a full mockup as UI.
- Fixed headers/wallet/navigation; lazy scrolling collections. Backgrounds extend without stretched scene images.
- Constraint-based dp/sp sizing, minimum 48dp targets, wrapping text, safe/IME insets once. Preserve drafts/filters/scroll through recreation.
- Inspect actual scroll boundaries separately from physical viewports. Sequential targeted previews only; no atlas or simultaneous build/render; existing 4GiB IDE heap cap.
- Use isolated verification app for mutations; never reset user data. Build checkpoints, explicitly stage intended changes, push configured branch after verification.
- Delete only proven unused code/assets. Preserve source images and relevant provenance.

## Order
1. Shared mint surfaces, typography, controls, headers and navigation.
2. Home hub, Tasks list, Shop grid, Garden slots, Care tabs, Journal cards.
3. Task/seed/planting forms and outcomes.
4. Full-scroll, window/density/text, IME/recreation and mutation verification.
5. Cleanup, final build/install, visible running app and push.

## Defaults
Existing catalog prices and 50-coin plot unlock remain authoritative. Do not invent mature-plant products. Journal uses a 0–3 plot with real labels and overflow indication. Keep all species and persisted stable-root growth. No new automated test framework.

## Evidence
- Preserved initial staged doc/composite removals, numbered reference renames, and Garden/doc edits.
- Baseline build failed on an extra GardenScreen closing brace; removed that single stray brace before redesign.
- Final visual acceptance remains pending. Builds are not fidelity measurements.
