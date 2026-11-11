# Current Mics implementation — 2026-11-11

## Goal and authority
Implement the approved redesign from mics/01.png through 09.png. These nine mint/white annotated concepts supersede older ornate and composite artwork instructions. Preserve saved data, catalog prices, MVI boundaries, Koin/Ktor and persistence. Exact fidelity is the target; no measured 100% claim is made.

## Working instructions
- Reuse matching isolated artwork first. Build gradients, cards, text, forms and controls in Compose; never use a full mockup as UI.
- Fixed headers/wallet/navigation; lazy scrolling collections. Backgrounds extend without stretched scene images.
- Constraint-based dp/sp sizing, minimum 48dp targets, wrapping text, safe/IME insets once. Preserve drafts/filters/scroll through recreation.
- Inspect actual scroll boundaries separately from physical viewports. Sequential targeted previews only; no atlas or simultaneous build/render; retain the 4GiB IDE heap cap.
- Use isolated verification app for mutations; never reset user data. Build checkpoints, explicitly stage intended changes, and push the configured branch after verification.
- Delete only proven unused code/assets. Preserve source images and relevant provenance.
- Do not fix layoutlib cache failures by changing production API names. Refresh the defining component preview before its parent.

## Implemented
- Shared mint palette, opaque rounded surfaces, sans-serif typography, green pills, fields, segmented controls and white fixed navigation.
- Home: one featured tree, progress/today count, four care/resource tiles, task/reward cards and first-use state.
- Tasks: pinned header/filters/progress/Add, rounded lazy rows, completed filter, stable completed rows without duplicate sections, and reward states.
- Shop: pinned wallet/filters and lazy two-column catalog. Structured purchase outcomes show actual balance/stock changes. Repeated purchase submissions are guarded.
- Garden: lazy numbered plot cards, planted/empty/locked states, inventory, target-plot selector, transactional planting/unlock and local reveal.
- Care: fixed navigation, compact hero/dock, Care/Decoration/Information tabs, expanded scrollable panel, checked watering and shortage routes.
- Journal: summary/chart/weekly cards, fixed 0–3 axis, explicit upward marker for counts above 3, seven real count labels and scrolling footer.
- Task, seed and planting forms: mint styling, scrolling content and independent action rows. Task discard/validation/saver behavior is retained.
- Selecting any destination exits Tree Care. The nursery link opens Garden; system Back still returns to the source.
- Twelve orphaned Home/Shop component and preview files removed after global reference searches. Existing user doc/reference deletions remain preserved.

## Component provenance
Existing simple_* plant/tool/seed resources and supplied cherry-growth frames are reused. Their older generation/extraction records remain historical provenance, not evidence of literal crops from the new sheets.
MintSprout and MintEmptySprout are native vector recreations of the current sprout/soil/rays motif. Panels, badges, gradients, progress, navigation and controls are live native UI.
No complete demo screenshot is used as a runtime background.

## Defaults and interfaces
- Existing catalog prices and 50-coin plot unlock remain authoritative.
- No mature-plant products or prices are invented; unsupported Plants category explains the empty catalog.
- Shop adds ShopFilter, PurchaseOutcome, SetFilter/DismissOutcome and in-flight purchase state; feature state/intents remain the boundary.
- TaskFilter adds COMPLETED. Existing DAILY/ONCE values remain compatible.
- Shared persistence/schema/networking are unchanged.
- No new automated test infrastructure.

## Executed evidence
- Baseline build initially failed on an extra GardenScreen closing brace; removed that single stray brace. Both APK builds then passed.
- Checkpoint e45b764 built and pushed to origin/y-tuong/quangtri. GitHub redirected the old remote to NQTri00/HabiTree.
- Sequential comparison renders reviewed Home, Shop, Garden full document, Tasks full document, populated Journal full document, Care, task form and planting form.
- Garden's six-plot fixture reaches complete inventory; reduced its audit height after locating the boundary.
- Shop runtime: overlapping captures covered all eleven species, fertilizer and nursery footer. Extra scroll retained footer label at y996..1036 on 360×640dp. Wallet/header/navigation stayed fixed.
- Task form: real keyboard leaves name input and Save/Cancel visible. Untouched form closes without mutation.
- Isolated com.mamxanh.app.verification.mint starts at 60 coins, zero trees and five unlocked plots.
- One succulent purchase: coins 60→35, stock 0→1; outcome panel shows both changes.
- Planting with keyboard open: Mint-QA assigned to plot01 at 0/120, purchased stock consumed. Success feedback names the tree/plot.
- One water action: growth 0→8 and water 542→492ml. Checked action is disabled afterward.
- Reinstall/configuration change retained 35 coins, Mint-QA at 8/120 and 492ml.
- Garden lower plots/inventory inspected in overlapping captures. At 35 coins the 50-coin unlock is disabled; cancellation returns to the same garden state.
- 320dp/1.3×: Home wraps without overlap; Journal seven zero days, weekly metrics and complete footer are reachable with fixed header/navigation.
- 320dp/2×: seed detail text scrolls and both purchase/cancel labels remain visible. Inspection prompted equal-height segment/care rows and compact detail layout.
- Device override restored to physical720×1280/density320 and font scale1.0.
- Populated chart refreshed directly from its source: 0–3 ticks, no sprout caps; 10/12 counts retain their values with ↑ markers.
- Care preview first hit layoutlib ListBuilder and stale TreeCareActions signature failures. Sync plus rendering the defining care component refreshed the parent successfully. No production API workaround.
- Studio heap samples stayed roughly 2.8–3.3GiB under the existing4GiB cap. No atlas or parallel renders; no permanent heap-fix claim.
- Latest captured dumpsys activity lastanr reported no ANR since boot.
- git diff --check passed.

## Additional verification — saved drafts and larger phones
- 393dp/1.3×: latest installed APK shows equal-height Home care tiles; Tasks and its form fit without overlap.
- Rotation with the keyboard open retained the task title. In landscape, the focused field and fixed Save/Cancel actions remain above the keyboard.
- 412dp/1.3×: Home and all task form fields/actions fit the portrait viewport.
- A confirmed background-process kill initially reset navigation and lost the draft. AppViewModel now saves navigation primitives; TasksViewModel saves filter, dialog visibility and draft values using SavedStateHandle.
- Retest: PID9875 disappeared; cold relaunch created PID9986. Tasks, open dialog and Mint-restored-draft title returned. No task was inserted.
- Explicit discard prompted confirmation; reopening showed an empty title and default fields.
- Both assembleDebug and assembleVerification passed after the fix (26 seconds, 250 tasks). The IDE build request had timed out; the successful result came from the Studio terminal using the configured JBR and offline cached dependencies.

## Home tree enlargement — approved follow-up
- The user's follow-up overrides the original Home proportions: reserve at least half the usable app-window height for the tree area (minimum240dp); permit scrolling instead of shrinking it.
- Compact Home-only header, 8dp summary/link padding, explicit smaller text leading, 24dp card icons and 6dp spacing. Wallet/navigation stay fixed.
- Four care controls moved into a 52dp right rail: 28dp artwork, minimum48dp targets, accessible action names, checked watering, disabled fertilizer and wrapping water volume.
- GardenPlantPortrait adds optional fitVisibleArtwork=false. Home opts in; other screens retain their default framing. Cherry generation emits one union alpha bound across all12 frames; no per-frame enlargement or runtime decoding of all stages.
- Decorations share the fitted artwork bounds rather than the oversized Home area. Growth mapping and saved state are unchanged.
- Runtime inspected at360dp/1×,320dp/2×,393dp/1.3×,412dp/1× and landscape. Complete lower cards and water label verified through scrolling on narrow/landscape windows.
- A sequential320dp/1.3× long-name preview confirmed wrapping. Layoutlib retained an old nested decoration render; final artwork positioning was verified in the installed APK.
- Final assembleDebug and assembleVerification passed (18 seconds). git diff --check passed. Installed main APK with-r;145 coins and saved tree/water persisted. Restored physical720×1280, font1× and automatic rotation.
- Half-height refers to the tree presentation area; visible artwork preserves its aspect ratio and growth scale, so its silhouette can occupy less height on tall narrow phones.

## Tree Detail enlargement — same treatment as Home
- Tree Detail now uses the same half-window tree area, fitted artwork,52dp side rail and compact header as Home. Shared MintTreeControls provides the sizing rule, header and48dp icon controls to both features.
- Nursery shortcut and name/growth panel use compact padding/text. Previous/next retain48dp targets. Care/Decoration/Information tabs and their expanded panels remain functional.
- Water/fertilizer intents and shortage behavior are preserved; pump/water icons use the existing water-supply navigation.
- Both APK variants built successfully (37 seconds). Installed main app with-r and inspected360dp detail: full tree, checked watering, disabled fertilizer, growth and all tabs visible. Information panel opened with real species/date/growth.
- At320dp/1.3× inspected the entire scroll boundary: care rail, water label, growth panel and all three wrapping tabs reachable. No game-data mutations were used for this verification.

## Centered tree and round controls — latest follow-up
- Home and Tree Detail now center the artwork horizontally and vertically in the half-window presentation area. Symmetric40dp artwork padding keeps the center independent of the right-hand control rail.
- Shared care controls use32dp circular surfaces and20dp artwork inside48dp touch targets. Water volume sits outside the circle; checked/disabled states and accessible names remain.
- GardenPlantPortrait adds optional artworkAlignment; these two screens use Center, while other callers retain BottomCenter.
- Both APK variants passed (21 seconds). Installed APK inspected on both Home and Tree Detail at360dp: centered tree, round controls, complete cards/tabs and saved145-coin wallet retained.

## Inventory screen — replaces seed-selection popup
- Added Android library module :inventory with InventoryUiState, InventorySeed and a full-screen InventoryScreen. The host supplies existing wallet/seed quantities; the module does not duplicate persistence or create new inventory stock.
- Two illustrated tabs: Vật phẩm (fertilizer, permanent watering can/pump) and Hạt giống (owned seeds with species artwork and quantities). Both use a lazy three-column grid, wrapping labels, separate saved scroll positions and a shop shortcut.
- Garden's bag button and footer open Inventory. Empty plot actions open Seeds directly with the target plot. Removed the old choose-seed AlertDialog and redundant inline SeedBasket/preview.
- Selecting a seed returns to the existing planting form with the exact target plot. Cancellation consumes nothing. Existing planting transactions and optional name form are unchanged.
- Inventory visibility, target plot and pending seed are SavedStateHandle navigation primitives. Tab/grid state use Compose savers; Back returns to Garden and bottom navigation still switches destinations.
- Both APK variants passed (final20 seconds,270 tasks); Gradle sync succeeded. Inspected Items and Seeds at360dp, and the three-column Items grid at320dp/1.3×; adjusted label/body leading after the narrow check.
- Runtime verified plot05→Inventory Seeds→succulent selection→planting target05; cancelled without planting. Stock remained1 and wallet115 coins.
- Confirmed process recreation: PID27313 disappeared; cold relaunch PID27578 restored Inventory with Seeds selected and the same quantities. Device size/font overrides restored. New screen left open on emulator.

## Shop and centered Home navigation — latest follow-up
- Shop now shares MintCatalogTabs with Inventory: illustrated Vật phẩm/Hạt giống tabs. ShopFilter is ITEMS/SEEDS; Seeds is the default. The previous All/Plants categories were UI-only state, so no persisted-data migration is needed.
- Seed catalog uses a lazy three-column grid with64dp artwork, wrapping names, existing prices and actual owned counts. Items shows the existing25-coin fertilizer offer; permanent tools are described as available rather than assigned invented prices. Existing purchase/detail/outcome behavior remains.
- Each shop category retains its own lazy-grid scroll state. Removed the duplicated tab implementation from Inventory.
- Navigation order is Tasks, Garden, Home, Shop, Journal. Home occupies the exact middle with a56dp circular surface,30dp icon and raised shadow; other icons are24dp. The bubble has measured layout/touch space and a separate label.
- Both APK variants passed (final18 seconds). Inspected all11 products and complete footer at360dp, both tabs at320dp/1.3×, restored category scroll, and the centered Home button navigating successfully. No purchases/game-data mutations were used.
- Restored physical720×1280/font1×. git diff --check is part of the checkpoint verification.

## Gradient and shadow depth — latest follow-up
- Added shared MintDepth palettes and modifier: opaque white-to-mint cards, green primary actions, soft secondary surfaces, gold prices and pale-green hints. A consistent lit upper rim and green-tinted native elevation separate surfaces.
- Standard cards use6dp lift, primary buttons7dp, secondary buttons4dp, headers3dp, modal panels16dp and navigation12dp. Disabled buttons remove elevation. No layout dimensions, actions or saved game data changed.
- MintBackdrop now draws cached native vertical/radial gradients for soft daylight and a warm lower glow; no full-screen bitmap, blur layer, animation or preview atlas was introduced.
- Applied shared styling to panels, buttons, catalog tabs, segmented choices, dropdown fields, task rows, compact headers, circular care controls, navigation and the Home bubble. Text fields use a subtle inset gradient and retain red validation styling.
- Both APK variants passed (final16 seconds). Installed and inspected Home, Shop, Tasks and an untouched task form at360dp. Checked320dp/1.3× Shop cards/tabs, complete lower catalog/footer, button shadow and navigation labels. Closed the form without saving; restored physical resolution/font1×.
- This is a visual styling checkpoint; no claim of exhaustive performance profiling or exact concept-art identity.

## Angled gradient preference — latest follow-up
- Shared surface fills, highlighted rims, form fields and background base now use a45° upper-left→lower-right gradient. Shader endpoints are calculated from each surface's dimensions so the angle stays consistent on wide buttons, tall cards and resized windows.
- Existing palette, shadows, native radial background glows and layout sizes remain unchanged. ShaderBrush caches the shader by draw size; no bitmap rendering was added.
- Both APK variants passed (24 seconds). Installed APK inspected in Shop: diagonal cards, selected tab, header and navigation render correctly with clear labels.

## Scroll-edge softness — latest follow-up
- Verified IDE write access with a temporary file, then removed it. Added shared MintScrollEdges to Shop, Inventory, Garden, Tasks, Journal, Home and tree-detail scrolling content.
- A subtle16dp edge band uses2dp native blur on Android31+ only where more content exists; older Android versions and IDE previews use a light fade. Short lists add no effect. Headers, tabs, navigation and central content stay sharp.
- Reuses graphics display lists with small blurred edge strips; no bitmap snapshots or extra preview rendering. The effect does not change layout or touch targets.
- Debug and verification APK builds passed (48 seconds). Installed and inspected Shop at top, middle and bottom, including the complete footer and the short Items tab at360dp. No purchases or game-data mutations were performed. git diff --check passed.

## Phase 1 gameplay implementation — 2026-11-11
- See [execution plan and acceptance evidence](../superpowers/plans/2026-11-11-phase-1-execution.md). Added additive schema v4, immutable task occurrences, capped and audited rewards, persistent timers, daily snapshots/streaks, atomic quantity purchases and care history.
- Existing mint styling, centered tree, five primary destinations and secondary Inventory are retained. New Home details follow the featured tree; long Journal histories use lazy rows.
- Task form supports creation/editing, schedules/custom weekdays, Click/Timer, optional overnight windows, due dates, descriptions and local reminders. Pause/archive retain history. New profile gets one starter seed and no uncapped coin gift; existing wealth is retained.
- Shared storage revision streams refresh all module snapshots on mutations and foreground entry. Android-only notification scheduling remains outside shared rules.
- 35 SQLite tests and Android/common/iOS-target compilation passed. iOS runtime and Phase 2 growth/death/can upgrades remain outside this Phase 1 checkpoint.

## Simplified tasks and Journal — latest user preference
- Removed task filter tabs, section labels, detail/edit/pause-task links and long-press management actions from the task screen. Click tasks retain direct completion.
- Timer tasks use inline elapsed time and play/pause controls. Reaching the required active duration submits completion through the existing atomic transaction; no separate completion checkbox is used for an unfinished Timer.
- Removed visible completion, wallet and care event logs and the detailed daily-record panel from Journal. The summary/chart remain; persisted records continue supporting correct rewards and statistics.
- Debug and verification APK builds passed (21 seconds). Device verification covers the compact list, Timer controls and Journal's complete scroll range.

## Shop quantity and item-card polish — latest follow-up
- Quantity selection now uses a shared-style mint pill with48dp circular minus/plus controls, a centered count, angled gradients and soft shadows. The confirmation centers the artwork/name, wraps price/stock badges and groups the total/balance in a compact panel.
- Purchase/cancel actions stay below the scrollable content. Insufficient funds show the missing amount and earning estimate rather than a negative projected balance.
- Removed the oversized wrapped action button inside the three-column Items card. Tapping the card opens the same quantity confirmation as seeds.
- Debug and verification APK builds passed (25 seconds). Inspected360dp and320dp/font1.3×, changed quantity1→2, checked the70-coin total and disabled purchase, and scrolled the complete confirmation. No purchase was made.

## Phase 2 living trees — latest implementation

- Synced GitHub plans through78892d5. The authoritative universal1.4× schedule now defines6/9/12 forms, Common/Rare/Legendary prices200/900/4,500 and14/44/127 distinct care-day floors. Fractional growth and precise elapsed floors remain separate. Exclusive is unavailable; no billing.
- Added shared interval simulation, additive schema v5, fair legacy8h grace, durable cans/equipping, free Water All, three timed fertilizer tiers, hidden neglect/death, capped mature sales and zero-refund removal. Transactions preserve history and prevent duplicate effects. Task rewards no longer add instant tree growth.
- Home and detail show stage/state, countdown and qualitative care; detail includes fertilizer selection, active-effort estimate, mature sale/removal confirmations and last-watered information. Garden adds lifecycle filters and Water All counts. Shop and Inventory use owned/equipped can state and tiered items, with independent tab scroll positions.
- Removed reservoir/refill APIs and runtime ml UI. Existing static artwork is temporary: growth artwork, planting reveal and tree transitions are deferred by the user. No new preview atlas or bitmap scene background.
- 60 real-SQLite tests passed, including migration, file-backed restart, concurrency, DST, clock/cap boundaries and long-term simulations. Android debug/verification APKs, common metadata and iOS ARM64 Kotlin compilation passed. Final UI-only APK build passed in34s.
- Runtime checked360dp,320dp/font1.3,412dp and640×360dp/font1.3 landscape; complete Home, Garden, care and Shop scroll ranges were inspected. Short dialogs retain close/cancel/purchase controls. Catalogs use three columns normally, two on narrow large-text phones, and more on wide windows.
- Installed over the main app without resetting data. Main profile retained50 coins,4 trees,6 unlocked plots,1 fertilizer and1 succulent seed. Legacy mature trees remained mature; partial succulent progress maps to stage3/6 with5/14 care days. Basic can is granted/equipped and grace starts on update. No main-profile purchase, task completion, care or disposition was used for QA.
- See [Phase2 execution plan and evidence](../superpowers/plans/2026-11-11-phase-2-execution.md). Earlier sections describe historical checkpoints; this section supersedes their old catalog, water-volume and growth rules.

## Compact cards, percentages and pinned trees — latest preference

- Shop cards now contain artwork, name and price/ownership only. Removed reserved two-line names and secondary descriptions; name/price spacing is2dp. Inventory names and quantities use the same compact spacing and56dp artwork.
- Home and tree detail keep the static tree and percentage outside the scrolling information area. Compact care tabs are inline; they no longer open a sheet over the tree. The wide succulent scene trims unused canvas space without reducing its visible image size.
- Growth is shown as a stage percentage. Removed watering/fertilizer countdowns, next-stage time estimates, visible care-day/maturity requirements and Shop growth-duration promises. Internal simulation/deadlines and maturity constraints are unchanged.
- Replaced planting, purchase, Water All, task/reward and global success dialogs with a small mint feedback bar that dismisses after3.5s. Financial/destructive confirmations remain. Success does not require an OK tap.
- Debug and isolated verification APK builds passed (final29s). QA used com.mamxanh.app.verification.compact: planted its starter, watered it, completed five Click tasks and bought one fertilizer (wallet40→5). Captured nonmodal task/purchase feedback and verified it expires without blocking navigation.
- Inspected compact Shop/Inventory cards, Home's complete scroll range and tree detail care footer at360dp; the tree stays in place while lower content scrolls. Checked320dp/font1.3, including inline fertilizer controls and two-column catalogs. No preview atlas or mass rendering used.
- Latest preference supersedes earlier evidence describing visible countdowns, care-day counts and modal success outcomes.

## Status panels, uniform catalog and dialog redesign — follow-up

- IDE write access verified by successful source and documentation edits.
- Added shared status badges, metric strips and catalog cards. Catalog name slots bottom-align up to two lines so prices remain close and card heights match.
- Home groups stage/care badges and three daily metrics in one panel; tree detail groups status, selected tabs and care content in a single surface.
- Reduced Journal headings, summary numbers and body labels. Replaced runtime native confirmations and the previous planting/purchase/task forms with the compact shared dialog shell.
- Debug and verification APK builds passed (39s). Runtime checks confirmed the pine and mandarin cards share top/bottom edges and price baselines. Checked grouped Home/tree panels, reduced Journal summary/chart/weekly text, and the new purchase popup at360dp and320dp/font1.3.
- Task form Save/Cancel remain visible with the keyboard open; an untouched form was cancelled without adding a task. Main-profile purchase and care actions were not used for QA.
- Final debug/verification builds passed (44s), including the single compact Journal header and matching metric tile heights. Fresh com.mamxanh.app.verification.dialogs profile verified the planting form, planting callback, Water All count tiles and confirmation callback: one eligible tree became watered and the eligible count became zero. No extra preview renders were used.

## Compact sizing across the app — 2026-11-12

- Applied the Garden sizing preference to Home, Tasks, Shop, Inventory, Tree Detail and Journal. Large secondary artwork is approximately halved; medium headings, badges, padding and spacing are reduced while existing small text stays readable.
- Shared catalog artwork is 28dp, with unchanged equal-height two-line name slots and aligned prices. Shop/Inventory retain three columns on standard phones and the existing larger-text fallback.
- Compact headers, metric strips, forms, confirmation dialogs, decoration choices and quantity selectors use smaller visual elements. Primary buttons draw a content-sized pill inside a minimum48dp action area.
- Bottom navigation is shorter; Home remains in the raised central bubble. Main Home/Tree Detail artwork remains large and centered under the earlier explicit tree-size requirement.
- Garden retains fixed filters/water control, small planting labels, centered overall progress percentages and no duplicate inventory footer.
- Initial parallel build failed on native memory allocation. One-worker builds with a1536MiB heap and in-process Kotlin compilation passed, including the final compact visual-button pass (2m4s). No Compose previews were rendered. The emulator activity service also became unavailable; a non-wiping restart recovered Android and saved data.
- Installed APK checked at360dp: seven task rows are visible, complete task/reward list remains reachable, and the task form keeps Save/Cancel above the keyboard. An untouched form was cancelled without saving. Journal summary/chart/weekly/footer fit together; Inventory items/seeds states, all eleven Shop seed cards and compact purchase controls were inspected.
- Final text-leading build passed (1m21s); installed with saved data preserved and restored density320/font1.0. Build daemons stopped after verification to release memory.
- At320dp/font1.3, Shop uses the existing two-column fallback. Purchase description, totals, quantity and both actions fit; Home and Tree Detail retain the pinned centered tree and scrollable lower content. No purchase, task completion, watering, sale or planting was confirmed on the main profile.

## Acceptance still open
Exact plant/icon silhouettes and illustration style differ from the new sheets; reused/generated art is not lossless source extraction. Do not describe this checkpoint as 100% visually identical.
The full 393/412dp, landscape/IME, long-name, process-death, reward-claim, transaction-race and every annotated outcome matrix has not been re-executed for this redesign. Existing historical evidence does not automatically close those checks.
Shared metadata and iOS ARM64 Kotlin compilation passed for Phase1. Native framework runtime is not claimed on this Windows host.
