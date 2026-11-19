# Android architecture migration

The [migration checklist](android-migration-plan.md) records the active Android implementation and verification requirements in the [.agent guide](../.agent/README.md).

## Latest published checkpoint

At `f47e3e2`, Android Shop owns a JVM domain contract and maps existing purchase transactions at the data boundary. Inventory owns its data binding. Focus owns its persisted session commands. A clean app export also resolved the independently built `core-domain` and `core-utils` artifacts from their pinned local Maven coordinates and passed its app unit tests.

The release includes the matching APK, 432 Compose preview PNGs, per-image checksums, and a hierarchy of all 1,448 tracked paths. The preview inventory covers 22 routes and 23 popups. The source report also identifies Kotlin symbol occurrences, generated-resource inputs, and linked tests. [Shop domain release](https://github.com/NQTri00/HabiTree/releases/tag/android-shop-domain-f47e3e2c).

## Earlier checkpoints

The initial checkpoint applied the guide to the Android module graph, localization source, and navigation host.

Home, tasks, garden, shop, tree, stats and inventory each have domain, data and presentation Gradle projects. The Android shell includes the feature ViewModel bindings through Koin. Inventory's domain is a pure JVM project. Its offline adapter handles stock reads, watering-can equip and seed planting through existing storage operations. Other Android feature domain projects still expose types from the Android variant of the shared module. The shared KMP module continues to provide game rules and models.

The running Compose app uses Navigation 3's `NavDisplay` and a stack of serializable destination keys. The same stack serves normal opens, Back, tab changes and notification intents. The shared `HabiTreeNavigator` owns stack operations. The controller retains the existing saved-state representation and dirty-form handling. Home, Tasks, Garden, Shop, Inventory, Tree and Statistics declare their typed destination keys and register their factory bindings through Koin. The shell still composes the entries and owns external routing and the saved back stack. The Statistics, Home, Tasks, Garden, Shop, Inventory and Tree presentation modules render their live screens from immutable display state and typed callbacks. Home uses content slots for its tree, next task and reward cards, and the Tasks module owns the task row shared with Home. The shell adapts its existing snapshot and dispatches cross-feature links. The shared Bio controls, touch indication, typography and date formatting live in `:core-ui`.

All nine Android ViewModels derive from the shared MVI base in `:core-presentation`. It owns the read-only state stream, typed action dispatch and buffered one-time events. The app still runs its screen controller and storage adapter in the shell, while task and engagement ViewModels handle the session timers.

The canonical copy now lives in `localization/strings/` as common and feature YAML. The [Android output mapping](../localization/android-outputs.json) preserves existing Android resource names, and the generator emits the same strings and Kotlin key order. See the [localization workflow](localization/README.md) for regeneration.

For the first architecture checkpoint (`911533d`), `verifyArchitecture`, `:app:assembleDebug`, all 11 shared host tests and all 15 localization and resource checks passed. All 19 fresh Compose captures match the preceding verified Android checkpoint byte for byte. On Android 15, the installed APK matched the signed artifact's SHA-256. Launch, opening More, switching to Garden, Back and routing a tree notification intent passed. The release archive includes all 19 Compose captures and four emulator screenshots.

The [full target architecture](../.agent/android/architecture.md) extends beyond this checkpoint. Independent feature repositories, the full core module inventory, feature-owned entry content, full screen-level MVI wiring and legacy preview placement still need migration. The Android and iOS Ktor engines and shared transport live in the cross-platform `:core-network` library. The existing backend remains optional and has no production endpoint or certificate material configured. The retired reference shell and its previews are available in the debug source set.

Android persistence and its instrumentation tests are exposed as `:core-storage` from the existing `data/` directory. The seven migrated feature screens have colocated Compose previews. Achievements, Timeline and Weekly Review render in Statistics. Rest scheduling and task insights render in Tasks. These screens use the shared text and date fields while preserving the existing note, scheduling and protection actions.

The Garden feature owns the encyclopedia and species screens. Tasks owns the task detail, form, focus, and template screens. Their typed presentation callbacks preserve the existing route and action IDs.

The Settings feature owns language and reminder preferences, the backup screen, and the destination keys for both. The app still supplies system permission and file actions.

Home owns the onboarding screen and its destination key. The shell still coordinates first launch and hands the user's selections to the existing onboarding action.
