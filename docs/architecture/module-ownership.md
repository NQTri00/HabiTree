# Module ownership and Kotlin Multiplatform migration

## Target layout

```text
app/                  # Android launcher and navigation composition only
shared/               # Shared infrastructure only: persistence, network, DI, cross-feature models
  src/commonMain/     # Shared, platform-independent abstractions
  src/androidMain/    # Android SQLite and platform implementations
  src/iosMain/        # iOS database and native implementations
core-ui/              # Genuinely reusable design primitives (currently Android-only)
home/                 # Home feature owns its own domain, data and presentation
tasks/                # Tasks feature owns its own domain, data and presentation
garden/               # Garden feature owns its own domain, data and presentation
shop/                 # Shop feature owns its own domain, data and presentation
tree/                 # Tree feature owns its own domain, data and presentation
stats/                # Statistics feature owns its own domain, data and presentation
```

For each feature, the desired layout is:

```text
feature/
  src/commonMain/kotlin/.../domain/       # Feature contracts, models, use cases
  src/commonMain/kotlin/.../data/         # Platform-independent feature repository implementation
  src/androidMain/kotlin/.../presentation/ # Existing Android Compose screens/ViewModels until UI is migrated
  src/iosMain/kotlin/.../                 # Only iOS-specific implementations when needed
```

**Do not mechanically rename `src/main` to `src/androidMain` while a feature uses `com.android.library`.** Source sets must first be activated with the Kotlin Multiplatform + Android KMP Gradle plugins; Android resources, dependencies, generated code, and runtime tests must be migrated together.

## Ownership rules

1. **Feature isolation:** a feature must not depend on another feature Gradle module. Expose navigation destinations and actions via callbacks at the app composition root.
2. **Local-first:** domain models, use cases, repository interfaces and their implementations are owned by the feature that changes them. Prefer `internal` visibility for implementation classes and UI components.
3. **Shared is infrastructure, not a feature bucket:** only cross-feature domain values that represent genuine app-wide identity/state, database interfaces and storage, networking, and dependency wiring belong in shared. Keep shared public APIs narrow.
4. **Platform placement:** pure Kotlin in `commonMain`; Android APIs and Android-only UI in `androidMain`; native iOS APIs in `iosMain`. UI should remain Android-only until Compose Multiplatform dependency/target migration is complete.
5. **Data flow:** cross-feature updates flow through shared persistent state and refreshed state holders, or explicitly defined app-level events. Never import another feature's presentation components.
6. **Composition:** bind each feature's repository/ViewModel in its owning module when possible. The app composes module declarations without knowing concrete repositories.

## Current migration status and debt

The current branch already has `:shared` as a Kotlin Multiplatform library with `commonMain`, `androidMain` and `iosMain`. It also has six Android feature modules with small `typealias` forwarding classes to implementations inside `:shared`, plus `:data` forwarding shared dependencies. This means **business logic is currently centralized in shared**, not feature-owned yet. Moving those implementations into per-feature KMP modules requires splitting shared Gradle dependencies, updating Android aliases/imports, migrating iOS wiring, verifying SQL dependencies and preserving existing on-device data.

This commit removes the two unused feature-to-feature Gradle dependencies `:home → :tree` and `:garden → :shop`. It does not yet convert Android feature modules or move their source files: doing so without coordinated Gradle and code changes would create a broken Android build.

## Verification

On Windows/Android run:

```sh
./gradlew :app:assembleDebug :app:assembleVerification :shared:compileCommonMainKotlinMetadata
```

On macOS additionally validate iOS compilation/linkage:

```sh
./gradlew :shared:compileKotlinIosArm64 :shared:linkDebugFrameworkIosSimulatorArm64
```

Do not treat Android compilation or metadata compilation as proof that the native iOS framework builds.
