# Simple mics verification ledger

Scope remains all six screens and three dialogs in the 2026-11-09 plan, including normal, large-text, empty/loading and live-action behavior. A build or a single component preview does not establish 90% visual similarity. No numeric acceptance score is claimed.

## Evidence and remaining verification

| Surface | Current evidence | Remaining work |
|---|---|---|
| Home | Full shell normal, empty and 1.3; corrected shop-label clipping confirmed in constrained component and full shell | Final scenery/sign fidelity against simple-home-tasks-shop; remaining action source checks |
| Tasks | Full shell normal/1.3 after row compaction and empty full shell; add action visible with no tasks | Final reference accents; inspect filter, completion, delete and bonus wiring |
| Shop | Normal, 1.3 and zero-funds full shell; readable labels/prices, zero stock, nursery shortcut | Scroll access to all species and fertilizer; final reference detail comparison |
| Nursery | Full shell normal after basket fix, 1.3 populated and empty; normal basket and sow action fit above navigation | Large-text empty and final reference acceptance |
| Tree Care | Latest normal full shell fits progress/actions/decorations/footer; 1.3 long name/zero-supplies case checked | Final reference acceptance |
| Journal | Latest normal, 1.3 and empty full shells; totals/chart/weekly grid/footer separate | Final reference acceptance |
| Add Task | Daily/TODO normal/1.3 surfaces and themed composition over Tasks shell; form source checked | Final reference acceptance; platform-window limitation below |
| Seed Detail | Normal/1.3 surfaces and composition over Shop shell; sprigs, labels and actions separate | Final reference acceptance; platform-window limitation below |
| Planting | Normal/1.3 surfaces and composition over Nursery shell; handles, name/stock/actions separate | Final reference acceptance; platform-window limitation below |

## Current continuation findings

- Large Seed Detail wraps the description and purchase label without overlap. The no-funds message and disabled purchase button remain visible.
- Large Add Task keeps its action row visible. Lower helper text is in a separate scrollable body, not hidden beneath the action row. Chip labels wrap at 1.3 scale.
- Large Nursery separates names/progress/watering status. The basket extends below the initial viewport and is in a vertical ScrollView; navigation remains separate. No claim that a screenshot proves an executed scroll interaction.
- Empty Nursery has accurate zero counts, a seed illustration and a fully drawn basket. A new explicit shop shortcut dispatches GardenIntent.OpenShop, produces GardenEffect.OpenShop and is mapped by the app shell to AppDestination.SHOP. This follows the existing effect-collection architecture.

Latest Journal accents use native Canvas sprigs and existing plant icons, with no baked text. Normal feature and 1.3 full-shell previews show no title/metric collisions. Large weekly details remain in the scrolling list. Empty full-shell Journal has zero totals, seven zero values and a valid 0–3 axis; footer and navigation are visible. Empty Tasks retains Add Task. Zero-funds Shop retains product detail browsing and the nursery shortcut; source affordability disables fertilizer purchase.

Form source inspection confirms trimmed title, all six save values, blank-title disabling, numeric reward filtering/clamping to1–100, category/frequency choices and optional TODO due date. Seed purchase emits BuySeed with the selected id and affordability disables purchase. Planting emits the selected species/name, requires positive stock and persistence retains the blank-name species default. This is source verification, not executed input interaction.

## Composition previews and platform-window limit

ReferenceTaskDialogPreview sets the real adding-task state, but the renderer returns only the underlying Tasks image/hierarchy: one image, with the task screen title and no dialog title. It does not capture the separate Android Dialog window. This case remains as a diagnostic fixture. Three explicit composition previews now reuse the existing dialog surface/bounds over ReferenceWorld and a preview-only 32% scrim. They render the intended app-shell placement and dimmed navigation. The Add Task composition explicitly applies MamXanhPreviewTheme; an initially missing wrapper was corrected and re-rendered. These are composition checks, not runtime dialog-window, keyboard or gesture evidence. Actual Dialog implementation and production navigation are unchanged.

## Dialog constraint correction

Runtime and previews now share each dialog's width/max-height Modifier. Maximum width is applied before fillMaxWidth, so it can constrain wide windows; phone proportions remain 94% (Add Task) and 92% (Seed Detail/Planting), with a 700dp height cap. This uncovered a genuine Seed Detail issue hidden by the previously wider preview: actions could start partly below the viewport at 1.3 scale. Seed Detail and Planting now keep their actions outside a weighted scrolling body, matching Add Task. Re-rendered large/no-funds and large/no-stock cases show complete action buttons. No actual system Dialog dimming, keyboard or interactive scrolling is claimed from these surface previews.

## Wrapped ribbon and unavailable care

The full app fixture combines a three-line Vietnamese custom name, 1.3 text scale, 0ml water and zero fertilizer. It exposed the ribbon's lower curve rising underneath the last text line. The lower curve now bows below the text. A focused 288dp ribbon preview and the full app fixture confirm all name lines sit on cream, with separate flower/leaf accents. Water/fertilizer actions are dimmed and disabled; water reads Cần nước and supply counts remain zero. Lower decorations/footer are in the screen's scroll container. Refreshing GardenArtworkPreview resolved an obsolete CHECKED_TASK enum in the core preview cache; no app API workaround was added.

## Remaining concrete reference differences

- Home's hanging lantern/cottage are now native illustrations, checked in the normal full shell. Destination signs still use simplified tray silhouettes and need the final comparison.
- Shop's hanging lantern and softly edged shelf fronts are added and checked in the normal full shell without label/product collisions.
- Tasks' title/reward sprigs are now checked in the normal app-root image and themed composition; the large title is also clear of text.
- Seed Detail's lateral sprigs and Planting's handles are checked in their full compositions without label/field collisions.
- Normal full shells for Nursery, Care and populated Journal and all three dialog compositions are now checked. Formal reference acceptance, remaining source checks and the explicitly listed large/empty cases are still required.

## Action/concurrency inspection

Purchases and task completion already use database transactions. Daily bonus claiming had separate eligibility, balance and claim-marker writes, allowing concurrent calls to pass the same check. These operations now share one transaction, with finally cleanup on success, early return and exception. Reward amount, scheduling eligibility, repository contract and schema are preserved. App compilation passes; Studio inspections report warnings only. This is source/build verification, not a concurrency test run.

## Memory discipline

Renders are sequential, one image returned per call, with measured heap recovery before further allocation when pressure is high. Current Studio max remains 4GiB. ReferenceScreenPreviews now explicitly use 160 dpi with the same 360×800 dp geometry and font scales. Images are 360×800 instead of 945×2100, reducing output pixels about 85%. Observed heap still rose from 2,838,514KB to 4,068,787KB for a render, so no heap-saving claim follows from the smaller image. Sequential rendering and heap checks remain necessary. Live heap after collection in this continuation was about 2.6GiB; transient allocation still approached the limit. This is controlled recovery, not proof of a permanent memory-leak fix.

## Completion status

Incomplete. Preserve every unchecked requirement in the implementation plan. Finish the missing comparisons and source/action checks, correct discrepancies, build, and only then assess the requested 90% similarity across the entire scope.
