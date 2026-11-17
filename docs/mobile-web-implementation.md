# Approved web design → Android

Reference: E:/Workspace/mamxanh-redesign (current revision: raised cards, no Sắp xếp screen).

Preserve existing dirty documentation/assets and prior HomeScreen preview additions. Keep game storage, rewards, task scheduling, backups and permissions native. Work and temporary files stay on E: or the existing D: tool locations.

## Implementation gates
- [x] Shared palette, typography, surfaces, controls, headers and bottom navigation.
- [x] Home and tree layouts compared with the HTML.
- [x] Task, catalog, garden and journal layouts compared with the HTML.
- [x] Engagement, settings, data and living-garden details compared with the HTML.
- [x] Sắp xếp runtime entry points removed; old state resolves to the overview.
- [x] Build and relevant existing tests pass.
- [x] Render native previews and compare directly with current HTML screenshots; refine differences before delivery.

User requirement: always compare to the web design before stopping; similar visual result is a completion gate, independent of compilation.

## Verification — 2026-11-16

The persistent comparison rule is in the project's `AGENTS.md`.

Comparison gallery: `E:/Workspace/mamxanh-redesign/native-review/index.html`.
It contains 34 comparisons, including the application shell, screen content, tree-tab components and focus states. The web references come from the current `evidence/full-gallery/manifest.json`; the removed arrange screen is excluded. Android images come from Android Studio Compose preview renders of the actual composables. No components were extracted from screenshots, and emulator screenshots were not used for visual comparison.

The layouts were inspected against the web references and refined for the hero cards, two-column catalogs, raised surfaces, typography, task controls, timer, settings, species records, calendar, milestone cards and memory timeline. Preview fixtures differ in some counts, collection states and plant stages. Native permission/file-picker flows and modal dialogs remain native. Layoutlib omits separate dialog windows, so inspection mode places the same runtime dialog surface inside the preview window.

`native-review/verified-images.json` records image dimensions, timestamps, hashes and a nonblank image check. `native-review/final-render-log.jsonl` records renderer outcomes. These checks supplement the visual inspection; they do not assert pixel equality.

Validation:
- Final `:app:assembleDebug` succeeded offline with the Kotlin compiler running in process.
- Existing `:data:connectedDebugAndroidTest` completed: **127 tests passed**, zero failures/skips. These cover transactions, lifecycle, engagement, garden and backups. Test report: `data/build/reports/androidTests/connected/debug/index.html`.
- `git diff --check` passed.
- APK: `app/build/outputs/apk/debug/app-debug.apk`.

No storage schema, reward calculations, purchase calculations or scheduling engine changes were made. Legacy `ARRANGE` navigation resolves to the read-only garden overview. The home task's completion/timer control is separate from opening the task list. Existing destructive confirmations and backup restore flows remain in place.

The final preview renderer uses `D:/Program Files/AndroidCLI/studio-profile`; its temporary files use `E:/Workspace/mamxanh-redesign/.tmp`.
