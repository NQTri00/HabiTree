# Native UI visual review

Historical web-parity report, recorded before the subsequent Android-only audit. See [ANDROID-AUDIT.md](ANDROID-AUDIT.md) and its before/after gallery for the latest Android changes and verification. The original web reference is unchanged; current native captures include usability improvements made independently of it.

The native implementation covers the same 22 navigation routes as the approved phone design. At this report's original checkpoint, 127 real Compose preview captures were available: 126 paired with approved web images, plus one Android backup-import confirmation that has no web counterpart. 100% pixel identity was not established; the remaining differences at that checkpoint are listed below.

[Open the original-size comparison gallery](http://127.0.0.1:4173/native-bio-review/). Each pair includes an opacity slider for inspecting alignment. Original approved reference images were preserved. Native images were rendered from the application's real composables, without screenshot backgrounds, cropped components, stitching, or alignment corrections.

## What the images show

The light tree-dependent palette, asymmetric raised cards, plant artwork, navigation hierarchy, forms and phone-contained overlays are implemented. The active navigation has no arrangement screen, and Home has no garden-corner section or decorative tree orbits. The same Be Vietnam Pro font files and original plant assets are used on both sides.

The review covered 82 viewport states and 44 full-scroll states. These include all 22 routes, 10 tab variants, all five onboarding stages, 25 reference popup scenarios, expanded sections, alternate tree palettes, popup scroll positions and narrow/short phone layouts. Earlier complete passes were inspected as paired contact sheets; final affected screens were inspected again after corrections. The final native regression check found 124 of the 126 paired captures byte-identical to the previous native iteration. Only the two Tree decoration captures changed after its heading correction. This is an iteration-regression result, not a native-versus-web similarity percentage.

| Screen | Visual judgment and corrections |
| --- | --- |
| Home | Hero, tree, actions and task card closely align. Pink, lavender and orange palettes inspected. Full-scroll review caught the third metric wrapping; its horizontal inset now matches the design. Android mono glyphs remain different. |
| Tasks | Summary, task rows, reward, filters and paused/archived empty states align closely. Added the missing summary gap at narrow widths. The narrow reward badge still wraps differently. |
| Garden | Numbered plant plots, empty plots, progress and bottom navigation match the intended hierarchy. Corrected card height and care-label spacing. Locked-plot styling has a small residual difference. |
| Shop | Seeds, care and accessories tabs inspected through full scroll. Removed the unintended target-tree card from ordinary entry; intentional tree-specific entry retains context. Direct accessory purchase/equip is wired. |
| Statistics | Metrics, seven-day chart and destination cards follow the design. Small text/row-rounding differences remain. |
| Task details | Facts, expanded history, completion/edit actions and archive popup inspected. Layout is close; native text metrics cause small differences. |
| Task form | Main fields, optional details, time controls, reward and save/cancel row inspected, including the full expanded form. Corrected accordion spacing. Native picker glyphs differ from browser controls. |
| Inventory | All three tabs, quantities, equipment states and Shop entry inspected. Accessory buying does not require entering Inventory first. |
| Tree | Care, decoration and information states inspected. Reduced artwork correctly for narrow and short screens. Corrected decoration-heading height, leading cleanup icon and product-grid position. Minor lower-section spacing differences remain. |
| Focus | Timer circle, presets and fields match the structure. The timer's Android monospace font visibly differs from the web's Windows Consolas fallback. |
| Templates | Full task-template list and selection controls inspected. Small accumulated row-height differences remain. |
| Weekly review | Metrics, chart, expanded facts, reflection and save action inspected. Geometry is close. |
| Rest | Calendar colors, legend, date fields and protection controls inspected. Use-ticket now has primary styling and stays disabled until a date is selected. Native control rendering differs slightly. |
| Insights | Habit links, seven/thirty-day views, expanded details and reminder controls inspected. Structure and grouping match. |
| Achievements | All ten cards inspected through full scroll. States and progress are consistent with the design; small vertical drift remains. |
| Timeline | Left rule, milestone dots, note fields and related actions inspected. Removed the enclosing rectangle. The second entry has a small remaining vertical offset. |
| Settings | Toggle panel, expanded quiet hours, save/permission actions and navigation cards inspected. Accordion gap corrected. |
| Data | Backup, privacy, recovery, clear and diagnostics sections inspected, including expanded sections and confirmations. |
| Encyclopedia | Full-scroll review exposed cumulative grid spacing. Corrected three-column gaps, unnecessary status-label minimum heights, goal-row alignment and goal spacing. Reward names are readable Vietnamese rather than internal identifiers. |
| Species | Hero, stages, collection facts, expanded care and Find-seed action inspected. Corrected lock fading and primary action styling. Some lower-section spacing remains different. |
| Onboarding | All five stages inspected, including the entire task list. Footer now flows when content fits and stays accessible on shorter screens. Skip action and Vietnamese captions render correctly. |
| More | All nine destinations inspected. Grouped link spacing and hierarchy match closely. |

## Popup review

The 25 reference popup scenarios were visually inspected: tree/seed selection; seed, tool and fertilizer purchase; owned tool; accessory; planting; water-all; plot unlock; fertilizer help; tree removal/sale; task archive; dirty-form discard; protection ticket; schedule adjustment; permission introduction/choice/settings; clear/last confirmation; safe recovery; license; and ending focus. Purchase, planting and license scroll states were also inspected. All custom overlays and actions stay inside the phone root.

[Native import confirmation](http://127.0.0.1:4173/native-bio-review/popup-import-confirm.png) is an additional confirmation state for Android file import, not an additional navigation route. Its first render exposed a blank creation-date row. The host now supplies the inspected backup's creation time; the preview shows representative counts/date, and the format-version row was removed. The corrected image was visually inspected. It has no approved web screenshot, so it is displayed separately from the 126 comparison pairs.

## Remaining visual differences

- Windows Consolas and Android monospace have different glyph shapes, especially the timer's zero. No proprietary system font was copied into the app.
- Native vector anti-aliasing, shadows and rounding differ slightly from browser rendering.
- Small spacing or line-wrapping differences remain in the narrow task badge, Timeline, Species and license text. Some popup total heights differ slightly.
- Android system permission, document and date/time dialogs use platform UI. They are separate from the custom phone-contained overlays.

These differences prevent an honest claim of 100% pixel identity.

## Build and practical limits

The debug APK was assembled from the final UI sources. APK Signature Scheme v2 verification passed. Artifact: E:/Workspace/thuc-tap-sinh/app/build/outputs/apk/debug/app-debug.apk. Build and signature logs are in this report's directory.

Fresh image exports used the official Android CLI Compose preview renderer. Android Studio GUI export was attempted but its launcher stalled on storage access; final GUI exports were not obtained. Isolated emulator attempts also stalled before ADB connected, so native tapping, rotation and complete transaction flows remain unverified. No emulator screenshot was substituted for a Compose preview.

All configured task output, cache and temporary destinations are on E: or under D:/Program Files. The source and design remain in E:/Workspace. Existing JDK files were copied and a pinned Gradle dependency cache was populated under D:/Program Files/AndroidCLI to reduce slow E: reads. Existing E: runtime/cache originals and user AVDs were preserved. Verification used one Gradle worker and a 1.5 GiB heap cap, without increasing heap limits. Task-owned Studio/emulator workloads were closed; the local reference server remains available for reviewing the gallery.

## Consolas request and current status

The user chose to preserve the web font and asked to download its package for Android. The original reference used the Consolas already installed in Windows through CSS fallback; no portable Consolas package was downloaded. That was a reference setup mistake, not an asset missing from the user. The user confirmed they do not have a package.

Microsoft lists no standalone download and says Windows font rights do not include app redistribution. The official vendor offers a separate App license. No purchase was authorized or made, and no proprietary font binaries were downloaded or bundled. The web remains unchanged. Sources and import instructions are in CONSOLAS.md.

Android now has a resource-font hookup for licensed regular/bold files. It preserves the existing fallback until both real font resources are present, then removes the fallback tracking adjustment. An initial preview test caught Layoutlib assigning identifiers to missing fonts and substituting a sans face. The loader now validates actual TrueType/OpenType data. The final full pass produced 127 captures, all byte-identical to the validated native baseline (font-regression.json: 127 identical, zero changed or missing). This proves the optional hookup did not change the fallback images; it does not prove web parity or actual Consolas rendering. Actual Consolas rendering remains unverified until the package is available.

The final APK was rebuilt successfully on 17 November 2026 at 19:33 local time after this safeguard, and its v2 signature was verified again. The APK is 247,853,787 bytes. No device interaction verification was added by this build.
