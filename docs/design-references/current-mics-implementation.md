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

## Acceptance still open
Exact plant/icon silhouettes and illustration style differ from the new sheets; reused/generated art is not lossless source extraction. Do not describe this checkpoint as 100% visually identical.
The full 393/412dp, landscape/IME, long-name, process-death, reward-claim, transaction-race and every annotated outcome matrix has not been re-executed for this redesign. Existing historical evidence does not automatically close those checks.
Native frameworks were not rebuilt because shared/native code did not change. Native runtime is not claimed.
