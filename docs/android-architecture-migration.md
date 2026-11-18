# Android architecture migration

This checkpoint applies the project's [.agent guide](../.agent/README.md) to the Android module graph, localization source and navigation host.

Home, tasks, garden, shop, tree and stats each have domain, data and presentation Gradle projects. The presentation and data projects depend on their own domain project. The Android shell includes the feature ViewModel bindings through Koin. Inventory currently has presentation code only. The shared KMP module continues to provide game rules and models to both Android and iOS. The Android domain projects contain Kotlin type aliases and have no platform imports. They use the Android variant of the shared module.

The running Compose app uses Navigation 3's `NavDisplay` and a stack of serializable destination keys. The same stack serves normal opens, Back, tab changes and notification intents. The controller retains the existing saved-state representation and dirty-form handling. The current screen implementations and route registrations still live in the app shell.

The canonical copy now lives in `localization/strings/` as common and feature YAML. The [Android output mapping](../localization/android-outputs.json) preserves existing Android resource names, and the generator emits the same strings and Kotlin key order. See the [localization workflow](localization/README.md) for regeneration.

`verifyArchitecture`, `:app:assembleDebug`, all 11 shared host tests and all 15 localization and resource checks passed. All 19 fresh Compose captures match the preceding verified Android checkpoint byte for byte. On Android 15, the installed APK matched the signed artifact's SHA-256. Launch, opening More, switching to Garden, Back and routing a tree notification intent passed. The release archive includes all 19 Compose captures and four emulator screenshots.

The [full target architecture](../.agent/android/architecture.md) extends beyond this checkpoint. Independent feature repositories, the full core module inventory, feature-owned destination registrations, the shared MVI base and legacy preview placement still need migration. The existing backend is optional and has no production endpoint or certificate material configured.
