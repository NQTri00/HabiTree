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

## Next verification
Collect additional navigation/runtime evidence, fix the constrained-height Home layout, and compare equivalent navigation before/after any image pipeline change. Preserve application data. Finish old plan acceptance and implement the requested KMP/Koin/Ktor stack without marking partial work complete.
