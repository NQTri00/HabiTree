# Android UI/UX audit

Audit started 17 November 2026. Judged all 127 existing Android Compose images, covering 22 routes, tabs, all onboarding steps, popups, full-scroll views, 320dp phones and short viewports. Original images are preserved in `native-bio-review/audit-before`. This audit judges Android independently; the web reference is unchanged.

## Findings recorded before fixes

| ID | Finding | Where it was visible / consequence |
|---|---|---|
| A01 | Close button has an oversized, heavy outline competing with the popup title. | Every popup; especially narrow purchase and short confirmations. |
| A02 | Overflow menu is almost invisible: three near-zero-length vector strokes. | Primary headers. |
| A03 | Dates use US month/day/year; empty fields show English `mm/dd/yyyy`. | Rest and date inputs; inconsistent with Vietnamese copy. |
| A04 | Time fields use AM/PM and an awkward empty mask. | Insights, quiet hours, task details. |
| A05 | Task save/cancel actions disappear below the fold, further away when details expand. | Task form and expanded form. |
| A06 | Template selection count and add action only exist after a very long list. | Templates full-scroll. |
| A07 | Focus presets have no selected state, and timer stays at 25:00 when duration changes. | Focus screen; source confirms the misleading preview value. |
| A08 | Start/pause/end focus controls require scrolling; bottom navigation competes with the focused workflow. | Focus viewport. |
| A09 | Task summary uses long copy and breaks awkwardly at 320dp. | Tasks narrow image. |
| A10 | Completed task action is a distorted oval; unfinished one looks like an unexplained empty circle. | Home and task rows. |
| A11 | Charts reserve excessive blank space and paint nonzero-looking marks for zero values. | Statistics and review. |
| A12 | Calendar has no date range, and its tiny single-line legend is difficult to decode. | Rest, seven/thirty-day insights. |
| A13 | Untranslated rarity labels interrupt otherwise Vietnamese UI. | Shop, species, seed purchase and onboarding. |
| A14 | Three-column encyclopedia makes names/statuses wrap into cramped fragments. | Encyclopedia full image. |
| A15 | Every unlocked stage displays the same mature plant, falsely implying distinct stage artwork. Locked labels are too faint. | Species stage grid. |
| A16 | Fertilizer actions are disabled without telling the user why. | Tree care; three identical disabled Bón buttons. |
| A17 | Short-height Home uses full-size artwork because its compact condition excludes heights below 500dp. | Home short viewport: only part of the tree is visible. |
| A18 | Backup actions collide visually with the next accordion divider; disabled safety recovery gives no reason. | Data screen. |
| A19 | First onboarding step shows a useless disabled Back button; task-choice step gives no live selection count/maximum guidance. | Onboarding steps 0 and 3. |
| A20 | Add/edit form uses the same ambiguous title, and title field starts completely blank without an example. | Task form. |
| A21 | Small section links and 44dp filters fall below the Android 48dp target; icon buttons, toggles and selected tabs lack meaningful accessibility state. | Shared controls; image and source review. |
| A22 | Task validation is at the top of a long form rather than beside the field; submit does not reveal the failing field. | Source-confirmed interaction issue. |
| A23 | ISO dates leak into review and species/tree facts. | Review eyebrow, species and tree full-scroll. |
| A24 | Inactive task filters still say “Việc hôm nay.” | Paused and archived task images. |
| A25 | Task-linked Focus exposes a duration override that the domain ignores; long task durations can incorrectly disable Start. | Source-confirmed while checking Focus. Free sessions support 5–120 minutes; a task uses its configured duration. |
| A26 | License retains hard line breaks/indentation, producing broken paragraphs and a split URL. | License popup at the top and bottom scroll positions. |
| A27 | At 150% text, the Home tab truncates, Home buttons split awkwardly, and stage-state labels become cramped. | Newly rendered large-text Home/species states. |
| A28 | Landscape Focus clips the timer, and Home's introductory spacing pushes the tree below the useful viewport. | Newly rendered 844×390 states and existing short-height images. |
| A29 | A stale tree link can display the first tree instead of the “tree has left” state. | Source-confirmed: tree lookup silently falls back to the first entry. |
| A30 | More highlights the Journal tab although the user opened a general destinations screen. | More viewport/full-scroll images. |
| A31 | In-flight operations disable controls without showing progress, especially inside purchase popups. | Source-confirmed; an additional busy-popup preview is included for visual verification. |
| A32 | Weekly review does not identify its chart as the trailing seven days, which can be confused with the calendar-week totals above it. | Review images: chart weekdays start midweek while the summary names the current week's Monday. Added an explicit “7 ngày gần nhất” chart heading. |

## Changes implemented for verification

- Popups: removed the 3dp oversized close outline; kept a named 48dp close target and clearer title spacing. Reflowed license text without changing its words.
- Shared controls: visible filled overflow dots; proper button/tab/switch semantics; selected tabs and chips expose state; filters and section actions have 48dp minimum targets; finite-line text uses ellipsis rather than clipping; fact columns wrap within equal widths.
- Tasks: compact summary copy; contextual inactive-filter headings; circular 48dp completion controls with a check glyph and descriptive labels. Add/edit have distinct titles, a title example, fixed Save/Cancel dock, inline validation and bring-into-view requests for invalid fields.
- Focus: fixed Start/Pause/End dock; no competing tab bar during focus setup; duration presets show selection and update the timer; clearer free-session limits; task-linked duration follows its task and is presented read-only. New task links reset stale Focus selection.
- Templates/onboarding: persistent add action with live count; larger template text/checkboxes; first onboarding step has one useful forward action; task-choice count and five-item limit are explicit.
- Data/care: added space before backup privacy disclosure, explained missing safety backups and disabled fertilizer actions, and labelled out-of-stock fertilizer “Hết.” Fertilizer enabling also respects the domain's canFertilize flag.
- Calendars/charts: Vietnamese day/month/year and 24-hour fields, visible date ranges/weekdays and readable two-line legends. Smaller chart area, bars scaled to actual data and zero values drawn at the baseline. Review and factual ISO dates display locally.
- Catalog/species: Vietnamese rarity labels, two-column encyclopedia for readable names, numbered achieved/locked stage markers instead of repeated plant art, readable locked-state labels.
- Compact layout: reduced hero artwork at short heights, including landscape; removed the erroneous lower-height exclusion in Home.
- Large text: Home actions stack, navigation allows two-line labels in a taller bar, and stage markers use two columns. Font size is not reduced to conceal overflow.
- Context/recovery: stale tree IDs now reach the missing-tree screen; More no longer falsely selects Journal. In-flight work shows progress in the screen and active popup.

## Visual review by screen

| Screen | Judgment from Android images |
|---|---|
| Home | Cherry, lavender and orange palettes retain the bright raised-card style. Smaller hero leaves room for the next task; larger text has readable full-width actions. The short/landscape tree is visible. |
| Tasks | Shorter summary stays compact at 320dp. Completion targets are round and clearly different from the filled completed state. Paused/archived headings identify the current list and omit today's summary. |
| Garden | Plot numbers, tree names, care states, empty plots and expansion card remain aligned. Filters keep readable labels and horizontally scroll at narrow widths. |
| Shop | Seeds, care and accessory cards retain even column widths. Rarity is Vietnamese; owned/equipped states and prices are visually distinct. |
| Statistics | Zero days sit on a baseline; the actual nonzero bar is legible. Removed empty chart height without compressing its weekday labels. |
| Task details | Fact columns remain bounded and readable; expanded history, edit, completion and archive areas reviewed. |
| Task form | Distinct add/edit titles, useful title example and fixed Save/Cancel actions. Title, duration and custom-day error states were separately rendered and inspected. |
| Inventory | Seed quantities, watering-can ownership/equip states and accessory state reviewed. Direct Shop access remains visible. |
| Tree | Care, decoration and information tabs reviewed. Fertilizer explains why it cannot be applied; empty stock says “Hết.” Missing-tree state no longer shows a different plant. |
| Focus | 25-minute, 45-minute, invalid-duration and task-linked states inspected. Preset and timer agree; sticky action remains visible. Landscape timer fits. |
| Templates | Larger descriptions and checkboxes are readable. Selected rows and fixed “Thêm 2 việc” action were inspected independently of the empty selection. |
| Weekly review | Localized week date, compact chart and explicit trailing-seven-day timeframe. Expanded facts, reflection field and lower Rest link reviewed. |
| Rest | Date range, weekdays and two-line legend explain the rolling calendar. Date fields use day/month/year. Taller capture includes the protection-ticket action. |
| Insights | List and seven/thirty-day views inspected. Calendars and 24-hour reminders are readable; full captures include the lower global-settings link. |
| Achievements | All ten cards inspected through the complete list; progress and awarded/locked treatment remain consistent. |
| Timeline | Dots/rule, entry text, editable notes and save actions reviewed; no enclosing rectangle or competing card outline was added. |
| Settings | Toggle panel, expanded quiet hours and lower links reviewed. 22:00/07:00 replaces AM/PM. |
| Data | Backup/privacy separation is clear; missing safety backup has an explanation. Expanded capture includes diagnostics export. Destructive confirmations stay distinct. |
| Encyclopedia | Two columns keep complete names/statuses readable. Tall capture shows all eleven species and the three collection goals. |
| Species | Stage markers communicate achieved/locked state without pretending repeated artwork is different. Larger text uses two columns; bottom state keeps the Find-seed action readable. |
| Onboarding | All five stages reviewed. First step has a useful forward action; task selection shows its count, enforces five maximum, and has reachable continuation. Full list includes Skip at the end. |
| More | All destinations reviewed through full scroll; no unrelated Journal selection is implied. |

Popups reviewed: tree/seed picker, seed/tool/fertilizer purchase, owned tool, accessory purchase/use, planting, water-all, plot unlock, fertilizer help, removal/sale, archive/discard, protection ticket, schedule adjustment, notification introduction/choice/settings, clear/final-clear, safety recovery/import, license and ending Focus. Purchase, planting and license bottom positions were included. The extra busy-purchase image verifies visible progress and disabled actions. The source implementation keeps all custom popup content within the app root.

The six taller full-page captures are genuine larger Compose canvases, not stitched screenshots. Normal 375×812 and 320×640 images remain separate from those captures; true 844×390 landscape and 150% text cases are explicitly named in the gallery. This evidence covers the listed fixtures, not every possible combination of live user data.

## Verification status

All 32 findings above have implementation changes. Final visual pass: **144 real Compose captures**, with zero missing, stale or incorrectly sized outputs (android-audit-verification.json). The final batch is `batch-20261117-200928`. Original Android images are preserved for comparison: 117 of the original states changed, 10 remained byte-identical, and 17 additional audit states were added. Those counts describe changed images, not a quality or similarity score.

All initial images and the complete first corrected pass were visually inspected. Subsequent affected states were inspected at original size, including larger text, landscape, validation errors, complete lower sections, missing tree and busy purchase. The final change touched only the four Review captures; all four were visually inspected again and the other 140 captures were byte-identical to the preceding validated batch (android-audit-final-regression.json).

[Browse the committed before/after images](evidence/README.md), or download the evidence folder and open [the portable gallery](evidence/index.html). The [local gallery](http://127.0.0.1:4173/native-bio-review/android-audit.html) also remains available. Its images are fresh real composables, not screenshot-backed layouts. No web design source or approved reference image was changed in this audit. The app retains its 22 navigation routes.

The final debug APK rebuilt successfully in 1m 38s after the final source change. APK Signature Scheme v2 verification passed. Artifact: [app-debug.apk](../../app/build/outputs/apk/debug/app-debug.apk), 247,880,822 bytes; build log `assemble-audit.log`, signature log `apk-audit-signature.log`. Final source/image freshness was checked again after the build. No task-owned build JVM or preview renderer remained running when checked; the local gallery server remains available.

Images establish appearance, not successful live interactions. `adb devices -l` reports emulator-5580 as unauthorized, so taps, keyboard behavior, rotation, purchases and screen-reader announcements remain unverified on a device. Source-level semantics/validation changes are implemented, and their visible static states were inspected; this is not a claim that runtime accessibility testing passed.

The browser contact-sheet script initially could inherit C:'s Windows temp directory despite pinning the browser child's environment. Its parent TEMP/TMP are now explicitly pinned to E:, too. No temporary Playwright directories remained in C:'s temp directory when checked. Project/output files are on E:; build tools/cache use the existing permitted D:/Program Files location. No tools were installed for this audit. Heavy builds and renders ran sequentially with the existing heap cap.
