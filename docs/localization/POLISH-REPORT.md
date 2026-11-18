# HabiTree: touch feedback, plant names and themed icon

## Changes

- The public name is **HabiTree** in both languages, including the header and Android app label. The application ID stays `com.mamxanh.app`, preserving update compatibility and saved data.
- Shared touch controls no longer paint a rectangular ripple/state-layer background. Presses use a small content-scale response without changing layout bounds. Raised card shadows remain; keyboard focus has a visible underline.
- Saved default species names now follow the selected language at the presentation boundary. Existing Vietnamese defaults display as English species names in English mode, and vice versa. Custom nicknames and database values are preserved.
- The English watering action reads **Water**, then **Watered**. Adjacent Home actions share their measured height, preventing a longer label from making one button taller than Decorate/Trang trí.
- The new HabiTree mark combines leaves with circuit-style roots. Eleven light launcher palettes come from the same tree palettes used by the app. The header uses the matching mark and theme color.
- Changing the main tree updates the launcher alias to its species palette. Android 13+ updates component states atomically; older versions enable the replacement before disabling the previous alias. Updates run off the UI thread and request that Android keep the application running.

## Visual review

Nineteen original Compose exports were checked: 11 English and eight Vietnamese. They cover Home, 320dp Home, lavender/watered states, full-scroll content, tree detail, a purchase popup, large text, a controlled press/focus component sheet, and all 11 icon colors under circular and rounded-square masks.

The reviewed images show:

- HabiTree fits the header at normal, narrow and large-text sizes.
- Default names such as Spring cherry and Purple lavender are English in English mode; the Vietnamese equivalents appear in Vietnamese mode.
- Watered and Decorate have equal heights; Đã tưới and Trang trí also align.
- Held-press controls have no rectangular overlay; normal shadows and keyboard focus remain visible.
- All icon palettes are light, with contrasting tree marks. The design supports both representative launcher masks without clipping the artwork.

The gallery opens at `http://127.0.0.1:4173/habitree-polish-review/`. The portable release ZIP contains `index.html` and all 19 untouched PNG exports. The press sheet uses deterministic interaction states of the real controls; it is not a recording of a physical device tap. The icon sheets show native vector layers under representative masks, not screenshots of a device launcher.

The preceding web reference was checked for the retained Home layout and raised surfaces. Branding, the icon and localized default names differ intentionally according to the new instructions. The previous 288-image localization report is historical evidence for checkpoint `39d1e83`; this follow-up uses its own capture manifests and metadata.

## Verification

- Debug APK assembly passed.
- Fifteen resource, design-token and launcher-resource checks passed. These include palette consistency and exactly one enabled launcher alias by default.
- Eleven shared Kotlin tests passed, including preservation of custom nicknames and translation of saved default plant names in both directions.
- APK v2 signature verification passed. Packaged labels are HabiTree for default, English and Vietnamese configurations. The compiled manifest contains the default launcher alias targeting a short-lived entry activity that hands off to MainActivity.
- APK size, SHA-256 and capture hashes are recorded in the accompanying verification JSON.

The Android 15 runtime review exposed a task identity issue when changing launcher aliases and a SQLite lock when reminders opened a second connection. The launcher entry now transfers task identity to MainActivity. Reminder and UI callers share a database helper, and the Android shell avoids refreshing unused screens and polling idle task timers.

On the runtime build, switching from cherry to lavender and then mandarin kept MainActivity open. The launcher showed one orange HabiTree icon. Both languages showed single-line watered actions and equal adjacent button heights. An actual held press produced no rectangular overlay. A custom nickname remained visible after changing languages, and notification links opened the correct tree, focus or insights screen. The focus timer counted down and paused. Temporary CPU contention during system boot affected earlier emulator attempts.

Android themed icons may use wallpaper colors. The app supplies the tree palette for the regular colored icon. See [Android adaptive icons](https://developer.android.com/develop/ui/compose/system/icon_design_adaptive).
