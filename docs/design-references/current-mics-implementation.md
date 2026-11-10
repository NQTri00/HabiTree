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

## Acceptance still open
Exact plant/icon silhouettes and illustration style differ from the new sheets; reused/generated art is not lossless source extraction. Do not describe this checkpoint as 100% visually identical.
The full 393/412dp, landscape/IME, long-name, process-death, reward-claim, transaction-race and every annotated outcome matrix has not been re-executed for this redesign. Existing historical evidence does not automatically close those checks.
Native frameworks were not rebuilt because shared/native code did not change. Native runtime is not claimed.
