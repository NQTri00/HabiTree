# HabiTree: touch feedback, plant names and themed icon

## Changes

- The public name is **HabiTree** in both languages, including the header and Android app label. The application ID stays `com.mamxanh.app`, preserving update compatibility and saved data.
- Shared touch controls no longer paint a rectangular ripple/state-layer background. Presses use a small content-scale response without changing layout bounds. Raised card shadows remain; keyboard focus has a visible underline.
- Saved default species names now follow the selected language at the presentation boundary. Existing Vietnamese defaults display as English species names in English mode, and vice versa. Custom nicknames and database values are preserved.
- The English watering action is **Watered**. Adjacent Home actions share their measured height, preventing a longer label from making one button taller than Decorate/Trang trí.
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
- APK v2 signature verification passed. Packaged labels are HabiTree for default, English and Vietnamese configurations. The compiled manifest contains the enabled default launcher alias targeting the existing MainActivity.
- APK: **248,792,164 bytes**. SHA-256: `72b76eeedde9bf19e18a687fe459014789662243d0511adf9e088497b91b97f0`.

Live taps and launcher switching have not been tested on an authorized device: the available emulator is unauthorized. Launcher refresh timing can vary. Android's system themed-icon setting can replace application colors with wallpaper/system colors; the regular colored icon follows the main tree palette. See [Android adaptive icons](https://developer.android.com/develop/ui/compose/system/icon_design_adaptive), [activity aliases](https://developer.android.com/guide/topics/manifest/activity-alias-element) and [interaction indications](https://developer.android.com/develop/ui/compose/touch-input/user-interactions/handling-interactions).
