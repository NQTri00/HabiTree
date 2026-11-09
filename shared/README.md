# Shared Mầm Xanh module

`shared` is a Kotlin Multiplatform library targeting Android, iOS arm64 and iOS simulator arm64. The existing Android app consumes its models, catalog, feature repository contracts/use cases and six offline repository implementations through the `data` module. Android package aliases preserve the current screen/MVI imports; the implementations live in `commonMain`.

## Persistence boundary

`GameDatabase` is the blocking platform persistence contract. `GameStorage` and feature repositories are common code. Android's existing `GardenDatabase` implements the contract using the same `mam_xanh.db` file, schema version 2 and transaction bodies. Call repositories from worker dispatchers, as the existing Android ViewModels do.

Migration is not finished: transaction/business rules still live in the Android SQLite implementation, and an iOS persistence adapter/bootstrap remains to be implemented. Declaring native targets does not mean an iOS app has been built or verified. Local yyyy-MM-dd date keys have platform implementations; iOS date behavior still requires native verification.

## Dependency injection and networking

`sharedModule(NetworkConfig(...))` binds shared storage, repositories, Ktor `HttpClient`, and `RemoteService`. Supply a platform `GameDatabase` binding alongside this module. Android adds its SQLite binding and lifecycle ViewModels in `appModule`.

Ktor uses the Android engine on Android and Darwin on iOS. The client is lazy, has a 15-second request/socket timeout and a 10-second connect timeout, and closes with its Koin definition. Darwin does not support the common connect-timeout setting, so the request timeout provides its overall bound. Cancellation and request errors propagate to the caller. No local game data is uploaded automatically.

No backend contract has been supplied. `RemoteService.get(pathSegments, query)` is a configurable transport boundary, not a fabricated game API. With no URL, explicit requests fail with `No backend URL is configured.` and offline app features continue operating. When a real backend is available, build Android with `-Pmamxanh.apiBaseUrl=https://your-service.example/api/` or pass `NetworkConfig(baseUrl = ...)` to the shared module on another platform. The URL is public app configuration, not a secret. Endpoint-specific DTOs, authentication and repository integration require that backend's actual contract.

## Verification

On Windows:

```
./gradlew :app:assembleDebug :shared:compileCommonMainKotlinMetadata
```

On macOS with Xcode compatible with Kotlin 2.4.21 (documented version 26.4), additionally run:

```
./gradlew :shared:compileKotlinIosArm64 :shared:linkDebugFrameworkIosSimulatorArm64
```

The static framework is named `MamXanhShared`. Native compilation/linkage and iOS runtime are not verified by a successful Windows Android/metadata build. See `docs/superpowers/plans/2026-11-09-runtime-kmp.md` for the unfinished migration and acceptance work.

Primary references: [KMP compatibility](https://kotlinlang.org/docs/multiplatform/multiplatform-compatibility-guide.html), [Android KMP plugin](https://developer.android.com/kotlin/multiplatform/plugin), [Ktor engines](https://ktor.io/docs/client-engines.html), [Ktor timeout support](https://ktor.io/docs/client-timeout.html).
