# Shared Mầm Xanh module

`shared` is a Kotlin Multiplatform library targeting Android, iOS arm64 and iOS simulator arm64. The existing Android app consumes its models, catalog, feature repository contracts/use cases and six offline repository implementations through the `data` module. Android package aliases preserve the current screen/MVI imports; the implementations live in `commonMain`.

## Persistence boundary

`GameDatabase` is the blocking persistence contract. `GameSchema` and `SqlGameDatabase` contain the shared schema, queries, game rules and transaction boundaries behind `GameSqlConnection`. Android retains its SQLiteOpenHelper, the same `mam_xanh.db` file and schema version 2. The iOS adapter uses SQLDelight 2.4.0's native SQLite driver without a code-generation plugin. Both platforms run the same game SQL and rules. Call repositories from worker dispatchers, as the existing Android ViewModels do.

On iOS, construct `IosGarden()` once for the owning session. It exposes home/tasks/garden/shop/statistics/tree repositories and configurable remote transport through an isolated Koin graph. Native storage and HTTP resources are lazy and close with `IosGarden.close()`; finish repository work before closing. Blocking repository operations must run off the UI thread. Local yyyy-MM-dd date keys and the locale's first weekday have platform implementations.

Android/common builds and Studio inspections of the iOS sources pass (style/unused-public-API warnings only). Native framework compilation, linkage and iOS runtime remain unverified on this Windows host. This is an implemented native adapter, not a claim of a verified iOS app. The legacy week-anchored statistics streak was preserved during the persistence move and remains a follow-up correctness issue.

## Dependency injection and networking

`sharedModule(NetworkConfig(...))` binds shared storage, repositories, Ktor `HttpClient`, and `RemoteService`. Supply a platform `GameDatabase` binding alongside this module. Android adds its SQLite binding and lifecycle ViewModels in `appModule`.

Ktor uses the Android engine on Android and Darwin on iOS. The client is lazy, has a 15-second request/socket timeout and a 10-second connect timeout, and closes with its Koin definition. Darwin does not support the common connect-timeout setting, so the request timeout provides its overall bound. Cancellation and request errors propagate to the caller. No local game data is uploaded automatically.

No backend contract has been supplied. `RemoteService.get(pathSegments, query)` is a configurable transport boundary, not a fabricated game API. With no URL, explicit requests fail with `No backend URL is configured.` and offline app features continue operating. When a real backend is available, build Android with `-Pmamxanh.apiBaseUrl=https://your-service.example/api/` or pass `NetworkConfig(baseUrl = ...)` to the shared module on another platform. The URL is public app configuration, not a secret. Endpoint-specific DTOs, authentication and repository integration require that backend's actual contract.

## Verification

On Windows:

```
./gradlew :app:assembleDebug :app:assembleVerification :shared:compileCommonMainKotlinMetadata
```

The verification APK uses `com.mamxanh.app.verification` and the label `Mầm Xanh · QA`. It has its own database, so manual economy/input checks do not alter the normal app's saved garden. Its dependency fallback uses debug libraries; Preview annotations are an explicit app compile dependency.

On macOS with Xcode compatible with Kotlin 2.4.21 (documented version 26.4), additionally run:

```
./gradlew :shared:compileKotlinIosArm64 :shared:linkDebugFrameworkIosSimulatorArm64
```

The static framework is named `MamXanhShared`. Native compilation/linkage and iOS runtime are not verified by a successful Windows Android/metadata build. See `docs/superpowers/plans/2026-11-09-runtime-kmp.md` for the unfinished migration and acceptance work.

Primary references: [KMP compatibility](https://kotlinlang.org/docs/multiplatform/multiplatform-compatibility-guide.html), [Android KMP plugin](https://developer.android.com/kotlin/multiplatform/plugin), [Ktor engines](https://ktor.io/docs/client-engines.html), [Ktor timeout support](https://ktor.io/docs/client-timeout.html).
