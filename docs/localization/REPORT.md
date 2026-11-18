# English / Vietnamese implementation and visual review

Historical checkpoint `39d1e83`. See [the HabiTree polish follow-up](POLISH-REPORT.md) for the subsequent branding, icon, plant-name and touch-feedback changes.

Status: implementation, final bilingual image review, build and APK signature verification completed.

## Result

Settings now includes a saved English / Tiếng Việt selector. Vietnamese remains the default for existing installations. Android application locales and the shared copy catalog follow the selection. Navigation and draft state use the existing saved-state controller during activity recreation. User-written task titles, tree names, notes and reflections retain their content.

There are 1,098 bilingual copy entries in `shared/src/commonMain/resources/i18n/messages.json`. Generated Android resources have matching keys in `core-ui/src/main/res/values/app_copy.xml`, `values-vi/app_copy.xml` and `values-en/app_copy.xml`. Shared Kotlin code uses generated typed keys from the same source. This covers screen labels, accessibility descriptions, validation, confirmations, reminders, catalog text and domain feedback.

Colors, dimensions, spacing, radii, typography, icon sizes, opacity and tree-theme effects are centralized under `core-ui/.../ui/design`. Existing numeric values were retained. Drawing geometry, vector assets, calculations, SQL, storage/protocol identifiers and persisted ISO date keys remain data. They are not translated or rewritten as UI copy.

## Image review

The review uses original exports of real Compose functions from the official Android CLI renderer. No emulator images, screenshot backgrounds or extracted component crops are used. Both languages cover the same 22 navigation routes and 144 preview states each: tabs, every onboarding step, purchase/confirmation/permission popups, expanded and full-scroll content, 320dp widths, short viewports, landscape and large text.

All 24 bilingual contact sheets were visually inspected, with full-size checks of important and suspect views and fresh checks after corrections. The final Vietnamese comparison has **118 of 144 images pixel-identical** to the preceding Android audit. The other 26 reflect the language card or locale-aware calendar/date labels, including species and tree dates. Home and its artwork retain the preceding Android layout and colors. A direct comparison with the unchanged web Home reference was also reviewed; this refactor retains the native baseline rather than claiming new pixel equality with the web renderer.

Actual image findings and corrections:

| Finding | Image evidence | Correction |
|---|---|---|
| Language selector was above the Settings heading. | Settings, expanded Settings and permission-popup backgrounds | Moved the card below the heading and introduction. |
| English purchase popups still displayed `Mua` and `Mua & gieo`. | Seed purchase, fertilizer purchase, narrow and large-text purchase popups | Added reusable bilingual resources for unaccented Vietnamese; expanded the regression scan. |
| `Archived` appeared on an action button; the paused tab used `Pause`. | Task details, archive confirmation and task lists | Separated action labels from status/tab labels. |
| More-screen title read `Add`. | More and More full-scroll | Added a context-specific navigation title. The add-action label stays independent. |
| English species records retained a Vietnamese missing-date value. | Species full-scroll and large-text species bottom | Localized app-owned record fallback fields at their presentation boundary. |
| English custom-weekday form used a singular date-picker label. | Task-days validation state | Added a dedicated `Choose days` label. |
| `Task templates` wrapped and made adjacent task actions different heights. | English task lists, including 320dp | Shortened the shared English label to `Templates`; shortened the template heading to `Choose your tasks`. |
| English date inputs and calendar headings used conflicting numeric date orders. | Rest, insight calendars and species/tree dates | Shared the selected locale's date formatter between fields and date labels. Persisted ISO values stay unchanged. |
| Previous history could expose `SOLD` / `REMOVED` codes or retain an old language. | History data review; covered by shared runtime tests | Translate legacy app-generated event labels at display time while preserving captured user names. |

English text may wrap differently from Vietnamese; the same layouts, artwork, fonts and design values are retained. Example tree names and task titles that remain Vietnamese in English captures represent preserved user content. The original Apache license text remains in its original language.

The complete side-by-side gallery is available locally at `http://127.0.0.1:4173/localization-review/`. The portable release ZIP opens through `index.html` and includes all 288 original PNGs. `captures-en.json`, `captures-vi.json`, preview manifests and `baseline-comparison.json` identify the complete evidence set.

## Verification

- Resource/token checks: 13 passing. These check bilingual coverage, placeholder compatibility, stable stored category IDs, XML coverage, parser handling, token references and prevention of new inline copy/color/unit literals.
- Shared Kotlin tests: 10 passing, including six localization cases and four backup codec cases. Localization covers language changes, catalog refresh, category compatibility, plural forms, literal preservation in user arguments and legacy event translation.
- Final Android debug assembly succeeded after the last source changes; all 288 corrected preview exports completed and their dimensions were checked.
- APK signature verification passed with APK Signature Scheme v2. The matching APK is **248,759,652 bytes**. SHA-256: `be442601a0a295860cb686c7318e184b981320465665cc2c9549c273b073f3ab`.
- Existing framework deprecation and Kotlin expect/actual warnings remain.

Live language selection, process restart and Android system picker interactions have not been verified on an authorized device. Compose exports verify both language configurations visually; they do not substitute for live device interaction tests. iOS tests cannot run on this Windows host.

The approved web source is unchanged. Historical pre-localization evidence in `docs/bio-parity/evidence` is retained separately.
