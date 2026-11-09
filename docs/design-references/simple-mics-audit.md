# Simple mics verification ledger

Scope remains all six screens and three dialogs in the 2026-11-09 plan, including normal, large-text, empty/loading and live-action behavior. A build or a single component preview does not establish 90% visual similarity. No numeric acceptance score is claimed.

## Evidence and remaining verification

| Surface | Current evidence | Remaining work |
|---|---|---|
| Home | Full shell normal, empty and 1.3; corrected shop-label clipping confirmed in constrained component and full shell | Final scenery/sign fidelity against simple-home-tasks-shop; remaining action source checks |
| Tasks | Full shell normal/1.3 after row compaction and empty full shell; add action visible with no tasks | Final reference accents; inspect filter, completion, delete and bonus wiring |
| Shop | Normal, 1.3 and zero-funds full shell; readable labels/prices, zero stock, nursery shortcut | Scroll access to all species and fertilizer; final reference detail comparison |
| Nursery | Full shell normal before basket sizing fix; full shell 1.3 after fix; full shell empty checked | Final normal full shell after fix; large-text empty and sow/buy navigation checks |
| Tree Care | Full shell 1.3 with long custom name and zero supplies after ribbon correction; disabled actions and accurate counts | Final populated normal full shell with latest accents and reference acceptance |
| Journal | Normal feature plus 1.3 and empty full shell after top sprout, olive sprigs and weekly flower accent; chart/totals remain separate | Final populated normal full-shell composition and reference acceptance |
| Add Task | Daily/TODO normal and 1.3 at shared runtime bounds; fixed actions, scrolling fields; six save values and validation inspected | Final app-shell overlay/reference composition |
| Seed Detail | Normal and 1.3/no-funds at shared bounds; normal purchase label corrected to one line, large actions fully visible | Final app-shell overlay/reference composition |
| Planting | Normal and 1.3/zero-stock at shared bounds; name/stock/action source wiring inspected | Final app-shell overlay/reference composition |

## Current continuation findings

- Large Seed Detail wraps the description and purchase label without overlap. The no-funds message and disabled purchase button remain visible.
- Large Add Task keeps its action row visible. Lower helper text is in a separate scrollable body, not hidden beneath the action row. Chip labels wrap at 1.3 scale.
- Large Nursery separates names/progress/watering status. The basket extends below the initial viewport and is in a vertical ScrollView; navigation remains separate. No claim that a screenshot proves an executed scroll interaction.
- Empty Nursery has accurate zero counts, a seed illustration and a fully drawn basket. A new explicit shop shortcut dispatches GardenIntent.OpenShop, produces GardenEffect.OpenShop and is mapped by the app shell to AppDestination.SHOP. This follows the existing effect-collection architecture.

Latest Journal accents use native Canvas sprigs and existing plant icons, with no baked text. Normal feature and 1.3 full-shell previews show no title/metric collisions. Large weekly details remain in the scrolling list. Empty full-shell Journal has zero totals, seven zero values and a valid 0–3 axis; footer and navigation are visible. Empty Tasks retains Add Task. Zero-funds Shop retains product detail browsing and the nursery shortcut; source affordability disables fertilizer purchase.

Form source inspection confirms trimmed title, all six save values, blank-title disabling, numeric reward filtering/clamping to1–100, category/frequency choices and optional TODO due date. Seed purchase emits BuySeed with the selected id and affordability disables purchase. Planting emits the selected species/name, requires positive stock and persistence retains the blank-name species default. This is source verification, not executed input interaction.

## Dialog constraint correction

Runtime and previews now share each dialog's width/max-height Modifier. Maximum width is applied before fillMaxWidth, so it can constrain wide windows; phone proportions remain 94% (Add Task) and 92% (Seed Detail/Planting), with a 700dp height cap. This uncovered a genuine Seed Detail issue hidden by the previously wider preview: actions could start partly below the viewport at 1.3 scale. Seed Detail and Planting now keep their actions outside a weighted scrolling body, matching Add Task. Re-rendered large/no-funds and large/no-stock cases show complete action buttons. No actual system Dialog dimming, keyboard or interactive scrolling is claimed from these surface previews.

## Wrapped ribbon and unavailable care

The full app fixture combines a three-line Vietnamese custom name, 1.3 text scale, 0ml water and zero fertilizer. It exposed the ribbon's lower curve rising underneath the last text line. The lower curve now bows below the text. A focused 288dp ribbon preview and the full app fixture confirm all name lines sit on cream, with separate flower/leaf accents. Water/fertilizer actions are dimmed and disabled; water reads Cần nước and supply counts remain zero. Lower decorations/footer are in the screen's scroll container. Refreshing GardenArtworkPreview resolved an obsolete CHECKED_TASK enum in the core preview cache; no app API workaround was added.

## Remaining concrete reference differences

- Home's hanging lantern/cottage are now native illustrations, checked in the normal full shell. Destination signs still use simplified tray silhouettes and need the final comparison.
- Shop's hanging lantern and softly edged shelf fronts are added and checked in the normal full shell without label/product collisions.
- Tasks' title leaf and daily-reward sprigs are implemented. Large-text title comparison shows no collision; normal reward-accent comparison remains pending.
- Seed Detail's lateral plant sprigs are implemented and compiled; final refreshed detail comparison remains pending. Planting's side handles are added and checked in its normal surface preview.
- Final normal full-shell comparisons for Nursery, Care and populated Journal, plus dialog overlay composition, remain required. These differences are not waived by the completed surface-layout checks.

## Action/concurrency inspection

Purchases and task completion already use database transactions. Daily bonus claiming had separate eligibility, balance and claim-marker writes, allowing concurrent calls to pass the same check. These operations now share one transaction, with finally cleanup on success, early return and exception. Reward amount, scheduling eligibility, repository contract and schema are preserved. App compilation passes; Studio inspections report warnings only. This is source/build verification, not a concurrency test run.

## Memory discipline

Renders are sequential, one image returned per call, with measured heap recovery before further allocation when pressure is high. Current Studio max remains 4GiB. ReferenceScreenPreviews now explicitly use 160 dpi with the same 360×800 dp geometry and font scales. Images are 360×800 instead of 945×2100, reducing output pixels about 85%. Observed heap still rose from 2,838,514KB to 4,068,787KB for a render, so no heap-saving claim follows from the smaller image. Sequential rendering and heap checks remain necessary. Live heap after collection in this continuation was about 2.6GiB; transient allocation still approached the limit. This is controlled recovery, not proof of a permanent memory-leak fix.

## Completion status

Incomplete. Preserve every unchecked requirement in the implementation plan. Finish the missing comparisons and source/action checks, correct discrepancies, build, and only then assess the requested 90% similarity across the entire scope.
