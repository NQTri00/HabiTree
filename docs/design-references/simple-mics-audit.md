# Simple mics verification ledger

Scope remains all six screens and three dialogs in the 2026-11-09 plan, including normal, large-text, empty/loading and live-action behavior. A build or a single component preview does not establish 90% visual similarity. No numeric acceptance score is claimed.

## Evidence and remaining verification

| Surface | Current evidence | Remaining work |
|---|---|---|
| Home | Full shell normal, empty and 1.3; corrected shop-label clipping confirmed in constrained component and full shell | Final scenery/sign fidelity against simple-home-tasks-shop; remaining action source checks |
| Tasks | Full shell normal and 1.3 after row compaction; titles/rewards/controls separate | Empty full shell and final reference accents; inspect filter, completion, delete and bonus wiring |
| Shop | Normal and 1.3 full shell after shelf/label changes; names/prices separate, footer scrolls below initial viewport | Zero-funds full shell; scroll access to all species and fertilizer |
| Nursery | Full shell normal before basket sizing fix; full shell 1.3 after fix; full shell empty checked | Final normal full shell after fix; large-text empty and sow/buy navigation checks |
| Tree Care | Full shell normal/1.3 after Back relocation; feature render after ribbon/lantern/label changes | Final full shell with latest accents; unavailable water/fertilizer and long custom name |
| Journal | Full shell normal after chart correction; earlier large full shell; mixed-value and zero chart components | Final large/empty full shell; title/foliage/flower accents |
| Add Task | Daily normal before shared arch; daily 1.3 after arch, fields scroll above fixed actions | Final daily normal and TODO normal/large with latest shape; blank-title and reward/due/category/frequency source checks |
| Seed Detail | Normal and 1.3 insufficient-funds render after arch/divider/hint/close changes | Confirm actual Dialog width/height constraints and final shell overlay composition |
| Planting | 1.3 zero-stock render after arch/ribbon; disabled sow and visible cancel/field | Normal in actual Dialog bounds and final shell overlay; optional-name and stock source checks |

## Current continuation findings

- Large Seed Detail wraps the description and purchase label without overlap. The no-funds message and disabled purchase button remain visible.
- Large Add Task keeps its action row visible. Lower helper text is in a separate scrollable body, not hidden beneath the action row. Chip labels wrap at 1.3 scale.
- Large Nursery separates names/progress/watering status. The basket extends below the initial viewport and is in a vertical ScrollView; navigation remains separate. No claim that a screenshot proves an executed scroll interaction.
- Empty Nursery has accurate zero counts, a seed illustration and a fully drawn basket. A new explicit shop shortcut dispatches GardenIntent.OpenShop, produces GardenEffect.OpenShop and is mapped by the app shell to AppDestination.SHOP. This follows the existing effect-collection architecture.

## Action/concurrency inspection

Purchases and task completion already use database transactions. Daily bonus claiming had separate eligibility, balance and claim-marker writes, allowing concurrent calls to pass the same check. These operations now share one transaction, with finally cleanup on success, early return and exception. Reward amount, scheduling eligibility, repository contract and schema are preserved. App compilation passes; Studio inspections report warnings only. This is source/build verification, not a concurrency test run.

## Memory discipline

Renders are sequential, one image returned per call, with measured heap recovery before further allocation when pressure is high. Current Studio max remains 4GiB. ReferenceScreenPreviews now explicitly use 160 dpi with the same 360×800 dp geometry and font scales. Images are 360×800 instead of 945×2100, reducing output pixels about 85%. Observed heap still rose from 2,838,514KB to 4,068,787KB for a render, so no heap-saving claim follows from the smaller image. Sequential rendering and heap checks remain necessary. Live heap after collection in this continuation was about 2.6GiB; transient allocation still approached the limit. This is controlled recovery, not proof of a permanent memory-leak fix.

## Completion status

Incomplete. Preserve every unchecked requirement in the implementation plan. Finish the missing comparisons and source/action checks, correct discrepancies, build, and only then assess the requested 90% similarity across the entire scope.
