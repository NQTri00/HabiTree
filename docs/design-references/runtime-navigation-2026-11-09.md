# Emulator navigation investigation — 2026-11-09

## Scope and status
Investigate startup and navigation hangs before changing implementation. Studio preview heap pressure and application ANRs are separate issues. Neither the runtime fix nor the outstanding visual/KMP/Koin/Ktor goals are complete.

## Reproduction environment
Small_Phone, emulator-5554, Android 17/API 37, 720×1280 pixels (360×640 dp). Cold boot used -no-snapshot-load -no-snapshot-save; existing application data was retained. Latest assembled debug APK installed successfully with adb install -r. Application com.mamxanh.app launched and displayed Home.

## Baseline evidence
After resetting gfxinfo, tapped the Shop tab at (501,1184), derived from the inspected UI hierarchy (Tiệm text bounds [432,1167][570,1201]). The subsequent hierarchy displayed Tiệm hạt and wallet 125 xu. Navigation completed.

Post-navigation gfxinfo: 31 rendered frames, 3 janky frames (9.68%), p50 19 ms, p90 34 ms, p95 150 ms, p99 350 ms; 2 slow UI-thread frames. Emulator timing is noisy and this single sample does not prove a specific source-code cause. dumpsys activity lastanr reported no ANR since boot.

Home hierarchy exposed overlapping layout on this shorter display: care tray [24,844][696,1060] overlaps the Tasks sign [29,768][360,891] and Shop sign [432,726][691,890]. Earlier 360×800 dp previews did not cover this viewport. This is an independently actionable layout defect; it is not evidence of an ANR.

Historical DropBox startup ANR includes a main-thread ART/DEX-loading stack (pread / ZIP extraction / DexFileLoader). Do not attribute that trace to bitmap composition without further evidence.

## Confirmed navigation ANR contributor and implementation
A later entry in the historical DropBox output (2026-11-09, input dispatch timeout 5002 ms) places the main thread in Bitmap.nativeGetPixels → ImageBitmap.readPixels → sourceArtworkBounds → GardenSourceImage → PaintedGardenPlant → ShopScreen. This is direct evidence of synchronous sprite inspection during the navigation ANR. The same session also lists timeouts in system applications, so this does not exclude emulator-wide contention.

GardenSourceImage and the potted PaintedGardenPlant path now obtain artwork using produceState with Dispatchers.IO. Decode and alpha-bound scanning happen on a worker; Canvas reserves its existing layout while artwork loads. Preview inspection retains a synchronous branch for layoutlib. Bounds remain cached; decoded bitmaps remain owned by their composition rather than an unbounded global cache.

:app:assembleDebug passed. Updated APK installed with data retained. Home → Shop completed and the Shop hierarchy was visible. Post-change sample: 39 frames, 2 janky (5.13%), p50 18 ms, p90 44 ms, p95 150 ms, p99 200 ms. No ANR since boot. Compared with baseline, the worst frame bucket improved, but p90 regressed and frame counts differ because artwork now arrives asynchronously. A single emulator sample does not establish a general performance guarantee. Further navigation and short-viewport verification remain open.

## Short-window and dependency-injection checkpoint
Home now reserves a minimum 680 dp scene height, scaled upward for font scale, inside a saveable scroll container. On the 360×640 dp emulator, the header/tree are separated at the top; scrolling reveals both destination signs above the complete care tray. Bottom app navigation remains fixed. This verifies that viewport only; large-text/recreation checks remain open.

Koin 4.1.1 now starts in MamXanhApplication. AppModule binds one GardenDatabase and GameStorage, six repository interfaces, and seven lifecycle ViewModels. MainActivity resolves ViewModels with koinViewModel; manual factories and Activity-owned storage construction are removed. Version 4.1.1 was chosen to fit the existing Kotlin 2.2.20 stack (its release uses Kotlin 2.1.21), rather than forcing a compiler upgrade. References: https://github.com/InsertKoinIO/koin/releases/tag/4.1.1 and https://lts.insert-koin.io/docs/4.1/reference/koin-android/start/.

assembleDebug passed after dependency resolution. Install retained existing data; Home showed 125 coins and the existing cherry; Shop opened and showed the same balance. No ANR since boot. Recreation and all-destination checks remain open, so Koin runtime acceptance is not yet complete.

## KMP/Ktor checkpoint
Kotlin 2.4.21, Android KMP plugin 8.13.2 and Ktor 3.6.1 are configured. Android and iOS arm64/simulator framework targets exist. Common code owns persisted models/catalog, feature domain models/contracts/use cases, all six offline repositories, GameStorage/GameDatabase contract, Koin shared graph and Ktor transport. Android SQLite implements the common contract with unchanged file/schema and transaction bodies. iOS date and Darwin factories exist; native persistence/bootstrap and shared transaction rules remain open.

Android assembleDebug and common metadata compilation passed. Installed APK retained 125 coins and the existing garden. Inspected Home → Tasks (3/7 today) → Nursery (3 trees, 1 seed) → Shop (125 coins) → Journal (13 completed, 3 trees). Each destination rendered and lastanr still reported none since boot. Latest application logcat had no fatal exception in the captured entries, but recorded a slow first frame around 1121 ms / 64 skipped frames. This is not evidence of full startup performance recovery. No HTTP request or iOS build/runtime is claimed.

## Shared SQL persistence and isolated QA
GameSchema and SqlGameDatabase now own schema, reads and game transactions in commonMain. Android's small adapter retains SQLiteOpenHelper and mam_xanh.db v2; native SQLite uses SQLDelight 2.4.0 behind the same connection interface. IosGarden exposes an isolated Koin graph with lazy native storage/client and close callbacks. IDE inspections of native database/date/bootstrap sources reported warnings only after successful sync. Native compilation/linkage/runtime remain unverified on Windows.

assembleDebug, assembleVerification and common metadata compilation passed. The verification app has a separate application ID/database and label. Installing the normal APK preserved the original 125 coins and cherry. QA fresh schema started at 60 coins; blank-name cherry planting consumed one seed and created 0/120 growth. Watering produced 8 growth, 492 ml from 542 ml, and disabled repeat watering. Succulent purchase cost 25 (60→35); fertilizer cost 25 (35→10), stock became 1 and another purchase was disabled. Applying fertilizer produced 33 growth and disabled the action at zero stock. Completing the 5-coin journal habit produced 15 coins and 43 growth. Adding QA KMP through the real form produced seven tasks. After force-stop/relaunch, QA retained 15 coins, 43 growth and 1/7 completion. No ANR since boot. These are executed Android checks, not a concurrency stress test or iOS result.

All three real dialogs were opened. Planting and Seed Detail fit the short screen with reachable actions. Blank Add Task disabled saving; entered title persisted when hiding the keyboard and saved successfully. A real defect remains: the keyboard overlays Add Task's lower fields/actions. This is now a reproduced IME issue to fix using the edge-to-edge guidance. The migrated statistics code also deliberately preserves the legacy week-anchored streak pending a separate correctness fix.

## Keyboard, recreation and streak correction
Add Task and Planting now use full-window dialogs with decorFitsSystemWindows=false and a safeDrawingPadding container, which accounts for keyboard/system insets once. Their fixed action rows stay outside scrolling fields. MainActivity explicitly enables edge-to-edge with light system-bar icons on the dark garden background; manifest uses adjustResize. Add Task saves its six draft fields through a listSaver. Planting saves the selected species ID and optional name.

Both APK builds and common metadata passed. At 360×640 dp, Add Task's save label moved from the keyboard-covered y978–1018 range to y566–606 above the keyboard beginning around y682. At 1.3 text scale, the complete wrapped save label was at y514–614; tapping it while the keyboard remained open saved QA IME successfully. That draft survived landscape/portrait rotation and the text-scale change. Planting's QA Plant input and actions remained above the keyboard; species/count/name survived rotation, and cancelling retained one seed. The input surface can be partly clipped at the scroll boundary while editing, but its text is visible and the body scrolls; no claim of full visual acceptance follows. Original emulator settings were restored: font_scale=1.0, user_rotation=0, accelerometer_rotation=1.

A pre-fix QA Journal showed one completion today but 0 consecutive days. The shared SQL streak now starts at today, or yesterday if today has no entry, rather than weekStart. With the same saved data after reinstall, it showed 1 consecutive day; total/today completions remained 1 and coins remained 15. Later completing QA IME refreshed 2/8 and buying one 25-coin seed reduced the QA wallet to zero. No ANR was recorded during these checks. Two additional QA trees were observed before these later actions; their origin was not established, so no claim of an exclusively controlled QA session is made.

## Next verification
Verify native persistence/bootstrap on macOS, configurable Ktor request execution/lifecycle, remaining startup work, and the rest of the back/recreation/large-text matrix. Finish previous visual acceptance against the simple-mics references. Preserve application data and keep all incomplete requirements visible.
