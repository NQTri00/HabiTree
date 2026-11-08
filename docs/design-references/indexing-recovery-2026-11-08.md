# Resume after Android Studio indexing — 2026-11-08

The redesign goal remains active and incomplete. Preserve the original nine references and minimum 80 percent similarity target per screen/dialog. This note supplements progress.md.

## Confirmed changes in this goal turn

- Added GardenArtwork.kt: GardenArt, GardenArtwork, GardenCareAction and GardenWoodenTray.
- Painted assets integrated into GardenTrailBar, Home care shelf, Tasks row icons and Tree care/decoration trays.
- Home companion action moved to the header and retains an accessible description.
- TreeInteractionScreen rewritten with name above the plant, progress beneath it, painted care shelf and decoration tray, compact footer; development growth inspector moved after the main content.
- App shell removed redundant generic headers on ordinary tabs; Tree retains a wooden Back action. Existing route and Back intent handling preserved.
- Added ReferenceScreenPreviews.kt with shared populated, large-text and empty providers for all six routes. Latest assembleDebug passed including this file.
- Added all seven missing static species assets and two scenery/material layers to shared resources; imported assets are NOT all wired yet.
- Saved exact item/species prompts and original filenames in painted-components-2026-11-08.md.
- Normal Home, Tree and navigation previews rendered before indexing stalled. Painted water can, fertilizer, pump, lantern, ribbon, sprout, checklist, journal art visually inspected in these renders. Full-app ReferenceScreenPreview subsequently timed out and is not accepted evidence.

## Authoritative failure and file state

Studio reported dumb/indexing mode. Existing-file replace, multi-replace and whole-file write calls repeatedly timed out after 300 seconds. New-file creation and read_file still worked. The suggested wait_for_index_completion command was tried once, but PowerShell reported it was not found; do not keep retrying that unavailable command.

All tool cells 113, 114, 115, 123, 124, 126, 127, 128 and 129 are terminal. No live shell or generation job remains. Shell-16 ended with exit 1 (unsupported wait command). Latest app:assembleDebug succeeded. Do not restart any supposedly running job from memory.

After those timeouts, read_file confirmed:
- HomeScreen still has GardenWoodSign("Mầm Xanh", width160dp, font22sp): the title wraps and needs width180dp/font20sp.
- PaintedGardenPlant still maps succulent AND aloe to the same succulent sprite, and non-cherry species still fall back to generic tree. The requested new mapping did NOT apply.
- KawaiiPlantIllustration still uses treeSize .22+.78*ease(.18,.93), old unfold ranges, green leaves beneath blossoms, and primitive decoration drawing. Requested tuning did NOT apply.
- GardenArtwork TREE still calls PaintedGardenPlant("ficus"); change it to a generic noncatalog identifier such as "tree" when wiring actual ficus art.
- PaintedGardenPlantPreviews.kt WAS written with a labeled 10-species atlas, but it currently exposes fallback art until mappings are corrected.

A user status question is pending: Studio says indexing and existing edits time out; is indexing still running, finished, or is Studio frozen? This is a request for observed IDE status, not an approval request.

## Next edits, after IDE becomes responsive

1. Re-read the current files before retrying. Do not use shell edits or bypass concurrency checks.
2. Map static species IDs to nursery_monstera/ficus/snake/cactus/aloe/lavender/pilea assets in PaintedGardenPlant, retaining pine/succulent and cherry branches.
3. Fix generic nav TREE to use the generic renderer, not actual ficus.
4. Home brand width180dp/font20sp.
5. Rig visual tuning: treeSize .25+.75*ease(.12,.60); unfold ease(.20+index*.02,.48+index*.02); leaf opacity wood*(1-bloom) to avoid green cast under full pink blossoms. Use core-ui garden_lantern and garden_ribbon image layers for accessories. Preserve hinges and phase-offset motion.
6. Interactive non-cherry species currently remain a generic woody rig; implement species-correct rendering/growth rather than calling this finished because static nursery art exists.
7. Replace flat forest gradient gaps with forest_distant_depth.png as distant layer behind independent stream/arch sprites. Replace plain brown shop backing with nursery_wood_backing.png behind independent frame/shelves/products. They are environmental/material components, not UI screenshots.
8. Rebuild and render the full ReferenceScreenPreview indices: 0 Home, 1 Tasks, 2 Shop, 3 Garden, 4 Tree, 5 Stats. Compare every one to original references, then large-text/empty cases and all three dialogs. No screen has been accepted at 80 percent yet.

## Additional generated layers

- core-ui/src/main/res/drawable-nodpi/forest_distant_depth.png
  Original exec-ace5b793-205f-47b3-ae6d-dc2c919b12b7.png.
  Built-in prompt: Use case illustration-story. Environment depth layer for an enchanted woodland garden game, portrait composition. Deep cool teal forest receding into luminous mist, densely layered ancient tree trunks, hanging emerald leaves, tiny soft golden fireflies, distant leafy platforms, gentle sun rays from upper right. Rich detailed hand-painted storybook art. This is ONLY a distant atmospheric woodland layer, no foreground tree, no river, no waterfalls, no buildings, no shop, no path, no interface, no words, no signs or lettering. Center stays softly detailed to support independent foreground sprites.
- core-ui/src/main/res/drawable-nodpi/nursery_wood_backing.png
  Original exec-db30164d-eec1-46b4-b0a4-18175ff74529.png.
  Built-in prompt: Use case illustration-story. A close view of the inside back wall of a hollow ancient tree nursery cupboard, rich warm golden brown curved wooden grain, shallow knots and bark grooves, tiny tufts of moss at edges, warm lantern light from upper right, very subtle shadowed outlines of a few little glass seed jars tucked along left and right edges. Rich detailed hand-painted enchanted woodland storybook game art. Vertical portrait, no shelves across center, no products, no plants, no lettering, no interface, no full UI screenshot. Reusable material backdrop for separately rendered shelves and live product labels.

Both inspected and copied into the workspace; source originals retained. Neither is integrated yet. No automated tests added or run, per approved plan. No commit or push made.
