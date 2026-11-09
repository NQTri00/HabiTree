# Reference-driven UI implementation progress

## Active scope — simple mics redesign, 2026-11-09

The authoritative references are the three simple PNG sheets in mics. These supersede every ornate-design note below. The user permits extracted OR reference-inspired components, requires live text/controls and at least 90% visual similarity. Full-screen reference images are never used as backgrounds.

Plan: docs/superpowers/plans/2026-11-09-simple-mics-redesign.md.
Art provenance and exact generation prompts: docs/design-references/simple-mics-assets.md.

Implemented: smooth cream panels, plain green buttons, honey wood signs/trays, simple bound journal, underline navigation, native simplified icons, separate reference-inspired cherry/pine/succulent/mandarin, watering can, seed and scenery resources. Home composition, Tasks rewards/progress, Shop labels/shelves, Nursery basket/labels, Tree Care and Stats chart were revised. MVI state and intents remain live.

Evidence: clean build passed; subsequent :app:assembleDebug passed after shared API changes. Normal Compose Previews rendered for all six feature screens, three dialogs, navigation and potted specimens. Preview class-loader caching caused NoSuchMethodError despite matching compiled bytecode; explicit JVM file names isolate the new panel/specimen components. Feature previews must be refreshed before full-shell comparisons.

Checkpoint acb3068 was built and pushed to origin/y-tuong/quangtri after verifying that the connected GitHub account owns the repository. Automatic push review initially lacked destination trust; authenticated owner/admin/push metadata established the destination and the explicit-branch push succeeded.

Review fixes are applied: bounded Home names, measured packet art/labels with independent counts, species-specific legacy artwork, visible cherry flower decoration and stacked form fields at large text sizes. Targeted previews confirmed the long-name plaque and heading, packet labels/counts at 1.3 font scale, and the scrollable TODO form. Home camera origins now match the moved destination signs. :app:assembleDebug passed after the latest source changes.

Memory discipline: previews were sequential and limited to affected cases; no image atlas was rendered. Heap checks interrupted rendering when allocation approached the 4GiB limit. Collection reduced usage from4,044,847KB to2,277,154KB; one subsequent Home preview raised allocation again to3,829,349KB, so no additional previews are scheduled in this batch. This is monitored recovery, not proof of a permanent leak fix.

Latest refinement: Shop shelves and labels now have reference-like vertical spacing and narrower separated name panels. Journal chart ticks and bars share a baseline and scale; native checked-task and pressed ginkgo details are restored. Large-text navigation reserves two lines for Nhật ký. Targeted previews confirmed the complete navigation label, aligned mixed-value chart (0–12) at 1.3 font scale, and a seven-day all-zero chart. These component checks do not establish whole-screen visual acceptance. Heap was measured at 2,979,414KB before this small sequential batch and 3,317,766KB after the first two renders; no atlas or full image payload was dumped.

Checkpoint 38457f4 was built and pushed. Full-shell Journal at 360×800 shows all normal-size metrics, chart, footer and navigation without clipping; remaining reference discrepancies include title sprout and botanical/flower accents. Full-shell Tasks exposed unnecessary normal-size title wrapping; compact side artwork/control allocation and 15sp titles corrected the sample titles to one line. Its 1.3-scale preview keeps wrapped titles, reward badges and completion controls separate; lower actions remain in the LazyColumn. These are layout findings, not complete behavioral or 90% visual acceptance. Heap recovery after a full render was verified from 3,804,157KB to 2,454,129KB, and later from 3,812,213KB to 2,447,579KB.

Nursery full-shell comparison exposed a collapsed basket background: fillMaxSize was being measured inside a vertically scrolling container. It now uses matchParentSize so the packet row determines the basket bounds. The large-text packet preview confirms a complete woven body behind readable labels/counts and the sow action. Empty Nursery now shows an unplanted seed rather than an apparently owned succulent. Build passed after these source changes. Full Nursery normal/large/empty rechecks and Tree Care comparisons remain pending.

Tree Care full-shell comparison identified an oversized dark top bar and wrapped Back sign. Back now dispatches the same AppIntent.Back through a feature callback and appears as a compact sign over the forest. Full-shell normal and 1.3-font previews confirm a single-line Back label, separate title/progress/care/decoration sections and scrollable lower content. The full-shell renderer first had a missing Kotlin class and then an old TreeInteractionScreen method signature; refreshing the feature and app-shell previews resolved it without changing JVM names or dependencies. Normal Home shell was also refreshed successfully. Remaining Care fidelity differences: title ribbon/flower accents, hanging lantern, tray edge/detail and cream decoration name tags. Build passed after the functional change; final checkpoint build follows import cleanup.

Further reference refinements: Care now uses a curved title ribbon with flower/sprout accents, a hanging lantern and live cream decoration labels. Its feature preview confirms separation of artwork, title and labels. Modal surfaces now share an arched upper outline with a matching inner border. Seed Detail restores a sprout divider, hint panel and circular close control; its normal preview shows all elements/actions without overlap. Add Task uses a sprouting seed on soil. Planting uses a curved title ribbon; its 1.3-font/no-stock preview shows the optional name field, explanation, cancel and disabled sow action without overlap. A missing border import caused one intermediate compile failure and was corrected; :app:assembleDebug passed after all source and preview changes. New large-text insufficient-funds Seed Detail fixture exists but has not yet been rendered. Add Task and other dialogs still need final checks after the shared modal shape change. Heap after the Planting render reached 4,120,660KB, so rendering stopped for collection; no permanent leak-fix claim.

Current verification: Seed Detail at 1.3 font scale/insufficient funds renders description, price/count, hint, warning and disabled purchase without overlap. Add Task daily at 1.3 after the shared arch keeps fixed actions visible and fields/helper text in the scroll body. Nursery full shell at 1.3 shows readable plaques and a scrollable basket below the initial viewport; empty full shell shows accurate zero counts and no planted specimen. Empty basket now has a live Ghé Tiệm shortcut through GardenIntent.OpenShop → GardenEffect.OpenShop → app navigation; source wiring was inspected and compilation passed. The updated feature-empty preview shows the new button. Detailed evidence and missing checks are recorded in docs/design-references/simple-mics-audit.md.

Home empty and 1.3 full shells are checked. Empty shows no planted specimen and disabled water/fertilizer. Large text exposed clipped Tiệm hạt signage; a smaller large-text shop icon plus bounded single-line label fixes it, verified in a constrained component and full shell. Shop 1.3 full shell has separate readable labels/prices and a scrolling footer. Reference comparison previews now specify160dpi, yielding360×800px instead of945×2100px without changing dp layout/font scale. Image output is smaller, but heap still rose substantially; monitoring remains required. Source inspection also found dailyBonus eligibility/balance/claim-marker operations were not atomic; they now share one transaction. Build passed and Studio inspection found warnings only, following the no-test constraint. See simple-mics-audit.md for exact remaining scope.

Journal now restores the top sprout, olive sprigs and weekly flower accent using live native components. Its normal feature, 1.3 full shell and empty full shell render without title/metric collisions; large weekly details scroll below the initial viewport. Empty Tasks shows Add Task with no false bonus, and zero-funds Shop retains detail browsing/shortcut with accurate zero wallet/stock. Compilation passed after the native art addition; Studio inspection of GardenSprig found formatting warnings only. Heap checks/collections bounded the sequential preview batch; lower image density is still not treated as a leak fix.

Dialog verification now shares actual runtime width/height bounds with previews, including correctly ordered width caps and a 700dp maximum height. The narrower large Seed Detail preview exposed partly hidden actions; its buttons and Planting's now stay fixed beneath scrolling content. Re-rendered 1.3/no-funds Seed Detail and 1.3/no-stock Planting show complete cancel/primary buttons. TODO Add Task at the shared width/height cap preserves the due field and fixed actions; lower reward fields remain scrollable. Build passed after all changes. Normal dialog variants, shell overlays and the remaining Care availability checks stay pending, explicitly recorded in the audit ledger.

Daily Add Task normal at shared runtime bounds fits all fields/actions. A full app Care fixture combines a long Vietnamese name, 1.3 text and zero water/fertilizer; it confirmed correct disabled actions/counts but exposed the ribbon's lower curve under the last name line. The curve is corrected; a focused ribbon preview and full app re-render show all three lines on cream. Core preview cache initially had an obsolete CHECKED_TASK enum and recovered after refreshing its defining artwork file, with no API workaround. Build passed after the geometry/fixture changes. Remaining normal dialogs and final overlay/reference comparisons remain listed in simple-mics-audit.md.

Dialog surface-layout verification is now complete across daily/TODO task forms, Seed Detail and Planting at normal/1.3 scales with shared runtime bounds. The normal Seed Detail purchase label wrapped unnecessarily; increasing its row weight to1.5 keeps the normal label on one line while the large variant remains fully visible. Form validation/six save values and purchase/plant intent wiring were inspected. Build passed after the weight change. App-shell overlays and remaining scene/details still require verification; the audit ledger now lists concrete missing lantern/cottage, sprig, shelf-edge and handle details instead of treating builds as visual acceptance.

Reference detail batch: new native cottage/hanging lantern scenery on Home, canopy lantern and edged shelf fronts in Shop, title/reward sprigs in Tasks, lateral plant sprigs in Seed Detail and side handles on Planting. No raster UI or baked text was introduced. Build passed; SimpleSceneDetails inspection has formatting warnings only. Normal Home/Shop full shells, large Tasks and normal Planting previews show the checked details clear of content. Seed Detail's new sprigs and normal Tasks reward accents remain for the final refreshed comparison. Sequential rendering and measured collection maintained the 4GiB heap constraint.

Latest composition audit: refreshed normal Nursery, Care and populated Journal full shells fit their basket/footer/metrics. Normal Tasks accents and Seed Detail sprigs are also checked. The actual-dialog diagnostic preview returned only the underlying Tasks image/hierarchy, with no dialog title. Three explicit preview compositions now reuse real content and bounds over the corresponding app shells with a 32% preview scrim; all three render with clear fields/actions and dimmed navigation. An initially missing garden theme around the task composition was corrected. These do not claim platform-window/keyboard/gesture verification. Build passed; runtime Dialog implementations remain unchanged. Formal reference acceptance and the remaining large/empty/source checks stay open in the ledger.

Pending: remaining large-text/empty-state and source checks, and complete visual acceptance. No numeric 90% score or full completion claim yet.

Previous goal turn: progress — authoritative source files were inspected, a new detailed plan written, simplified code/assets implemented and previews compared. No blocker status applies.

---
Historical ornate-design record follows; it is not the active target.

## Objective and acceptance

UPDATED USER REQUIREMENT: at least 90 percent visual similarity for each screen/dialog, with every illustrated component extracted from the supplied designs. This supersedes the earlier 80 percent target and any permission to use independently generated approximations as final art. Never use a complete UI screenshot as a background; text, fields and actions remain live Compose.

Tree-growth animation is explicitly deferred. Use static trees from the source designs. Home, Tree care and portrait now share GardenPlantPortrait; progress and care/decorate actions remain live. The development growth inspector has been removed from the active screen.

The user reattached all three sheets. Fifty-five source-guided extraction/inpainting assets are now integrated; provenance is recorded in source-derived-assets.md. These are generative edits of the supplied references, not verified lossless crops. Visual acceptance is still pending.

## Current continuation — 2026-11-08

- Build :app:assembleDebug passed after source artwork integration and Tasks/Stats changes.
- Tasks now uses source choice chips, progress vine/sprouts, completion controls and reward envelope, preserving completion, bonus, filtering and deletion actions.
- Stats uses source sprout chart caps and parchment section labels; values, dates and refresh remain live.
- Added mandarin consistently to shop, garden and storage catalogs at 60 coins; existing catalog entries remain.
- Corrected parchment nine-patch sizing and transparent margins. App content is constrained within Scaffold padding.
- Rendered Home, Tasks, Stats and navigation previews. Compiling MamXanhAppContent directly refreshes the shell; ReferenceScreenPreview can show stale dependencies until that compilation.
- Remaining visual issues: journal foliage overlaps some text; icon scale varies; Home labels/hero placement and remaining screen/dialog compositions need comparison. No 90 percent acceptance claim.
- Ruling: verify with builds and Compose Previews, without adding automated tests, following the approved design verification scope.
- Ruling: keep existing nonreference species available using neutral source seed packets; do not substitute a falsely labeled plant illustration.

Historical notes below are superseded by this continuation.

Mandatory visual review: compare every component, screen and dialog directly against the original source. Record each extracted asset's source sheet and region, preserve its material/shape/color, and check full app composition, typography, controls and spacing. No measured similarity score has been claimed. The full task is NOT complete.

The sections below contain historical implementation notes; any growing-rig work or older acceptance wording is superseded by this requirement.

## Current implemented state

- Shared serif GardenTypography is applied to runtime and preview themes.
- GardenBotanicalFrame draws independent nine-patch parchment/vine material behind live content. It supports compact contentPadding/cornerSize.
- GardenTrailBar uses separate painted teal tiles, live labels, selection semantics and gold selection outline. Icons are still old vector landmarks; they need painted replacements.
- GardenForestScene composes an open-center tree arch, separate stream sprite, gradient atmospheric light and distant forest layer. Old barn/windmill scenery was replaced in FarmPastoralBackdrop and all GardenSceneBackdrop variants.
- Home uses separate interactive painted treehouse and mushroom seed-stall sprites, central layered tree, live name/progress plaque and care controls. Navigation bar is now shown on Home too. Header and action icons still require fidelity work.
- Tasks now uses ONE journal page with compact rows, filters, completion controls, overflow delete, due-date metadata, daily reward and Add Task. Journal control art and item icons remain simplified.
- Stats now uses ONE journal page with large totals, sprouting green chart columns, refresh, weekly metrics and italic footer.
- Tree rig now has independent transparent soil, seed, bare bark, green foliage and pink blossom resources, five independently hinged canopy clusters, smooth overlapping seed/sprout/wood phases, breeze movement and preserved decoration controls. Small painted accessory sprites and species-specific pine/succulent rigs remain to implement. Assets were generated/reconstructed, not literal frame crops from the GIF.
- Tree screen frames compacted; growth inspector remains available through an expandable control.
- App shell's generic white top bar was replaced with a compact dark forest bar; a proper wooden title/back plaque remains pending.

## Saved artwork

core-ui/src/main/res/drawable-nodpi:
- botanical_parchment_panel.png
- garden_navigation_tile.png
- forest_arch.png
- forest_stream.png
- forest_treehouse.png
- forest_seed_stall.png
- forest_island.png (used beneath nursery trees)
- garden_journal_page.png (original retained)
- garden_journal_page_clean.png (active journal)
- nursery_pine.png, nursery_succulent.png, nursery_seed_basket.png
- nursery_painted_seed.png, nursery_painted_trunk.png, nursery_painted_blossoms.png, nursery_painted_leaves.png (shared copies of rig art)
- nursery_shelf.png, nursery_shop_frame.png
- garden_wood_sign.png, garden_leaf_button.png, garden_fertilizer_sack.png

tree/src/main/res/drawable-nodpi:
- tree_painted_soil.png
- tree_painted_seed.png
- tree_painted_trunk.png
- tree_painted_leaves.png
- tree_painted_blossoms.png

All generated through built-in imagegen. Project consumers use workspace copies; originals are retained under C:/Users/AP/.codex/generated_images/01a1b68d-8de0-7cb2-a083-432f68eaa9de.

## Active refinement

Clean journal asset integrated; Stats rendered without upper foliage collisions. Increased shared top padding from 32dp to 52dp after observing the title against the curled paper edge. Latest Stats render confirms the heading sits inside the page.

Nursery: replaced potted-card grid with staggered mossy islands, separate painted cherry/pine/succulent artwork, compact live name/growth/water plaques and a seed basket. All trees and seed species remain scroll-reachable. Planting dialog uses parchment, optional name, stock count and painted actions. It still needs a sprouting seed/soil composition and richer wood detail.

Shop: replaced pink tiles and tiny potted cards with separate mushroom shop frame, wooden shelves, plant art, fertilizer sack and live price labels. Four featured seeds and fertilizer come first; all other catalog seeds remain below. Selection opens a parchment detail popup; purchases still dispatch existing intents. Removed inaccurate local success copy claiming purchased seeds were planted. Existing ShopEffect feedback remains the source of purchase feedback.

Add Task: replaced white boxed sections with compact parchment form rows, three kind choices, category/repeat dropdowns, optional TODO due date and numeric reward. Preserved all six save values and blank-title disabling. Added painted action buttons; leaf heading has no fake click action.

Shared GardenPaintedControls now supplies GardenPaintedButton, GardenLeafPlaque and GardenWoodSign. Shared PaintedGardenPlant supplies nursery art without coupling garden/shop to tree feature state. Pine/succulent have dedicated sprites; other non-cherry species currently fall back to generic foliage and MUST receive species-correct artwork.

Ruling: retain existing domain catalog and prices rather than inventing the reference's mandarin species or changing economy values. Preserve all ten existing species. Cost: the fourth featured shop slot is ficus until the user changes the catalog.

Ruling: continue in the current authorized feature branch/workspace, preserving unsaved IDE content. Use preview/build verification and do not add automated tests, per approved global constraints.

## Verification evidence

- Latest :app:assembleDebug passed after the nursery/shop/dialog/control changes. A duplicate plaque function introduced by an overbroad replacement caused one intermediate build failure; removed exact duplicate and rebuilt successfully.
- New previews rendered: nursery normal/large text/empty; planting parchment; shop normal/large text; seed detail; Add Task DAILY normal/large text and TODO; Stats with cleaned journal and 52dp top padding. Title/label improvements visually confirmed.
- Compose Previews rendered: Tasks, Stats, Home, Tree screen, independent seed/mature/72-growth rig, navigation and parchment.
- At 72/120 the deterministic rig preview shows the painted pink flowering tree with its root attached to the soil.
- Preview renders sometimes return previous/current images together; compare the current result, and render the directly changed component to avoid stale dependent previews.
- No automated tests were added/run. Visual acceptance uses Compose Previews, as previously specified.

## Remaining high-priority scope

1. Replace old navigation landmark icons and Home/Tree care illustrations with painted icons (water can, pump, book, glass, boots). Fertilizer sack, leaf buttons and wood title artwork exist now; apply them consistently.
2. Species-specific art for monstera, ficus, snake, cactus, aloe, lavender and pilea. Generic-tree fallback is visibly wrong for these; replace it. Species-specific interactive rigs and decoration accessories remain pending.
3. Refine Tree scene scale and care shelf/decoration tray to reference. Home still needs wooden header and painted care controls.
4. Refine nursery proportions against reference 04 in the actual app shell: compact labels, first-viewport basket visibility, richer distant scenery. Current large-font layout scrolls as intended.
5. Refine shop interior: plain brown backing is too flat; canopy currently extends behind first product row. Tree products need burlap root wraps like reference 03. Preserve all catalog access.
6. Refine dialogs to reference 07/08/09: richer sprout icons, planted seed/soil, root trim, close affordance on seed popup, reference-like field/button borders.
7. Render all six screens plus three dialogs IN the app shell at phone size, larger text and empty/populated states. Confirm actions and Back/navigation preserved; compile. Full-shell visual acceptance is not yet established.
8. Explicitly compare each to its source reference; no screen has been formally accepted at 80 percent yet. Do not mark complete merely because compilation passes.

## Important implementation details

- User source images/GIF are visible as chat attachments, not project files. Do not ask for them again solely because studio_find_files returns none.
- Files are edited ONLY via Android Studio write/replace tools. Binary generated assets are copied with Studio shell Copy-Item to new resource paths.
- Shared component resources namespace is com.mamxanh.core.ui.R; tree rig resources namespace com.mamxanh.feature.tree.R.
- Changes are uncommitted; do not push automatically based on earlier completed commit requests.
