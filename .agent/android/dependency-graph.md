# Android Dependency Graph

## 1. Dependency Direction

```
┌─────────────────────────────┐
│       :app (App Shell)       │  ← Imports ALL feature + core modules
│       startKoin { }          │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────────────────────────────────┐
│              Feature Modules                             │
│  Feature A ──▶ its own layers and approved core APIs     │
│  Feature A ✕──▶ Feature B                                │
└──────────────────────────────────────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────────────────────┐
│              Core Modules                                │
│  core.domain     ← Pure Kotlin, no deps                  │
│  core.config     ← Pure Kotlin, no deps                  │
│  core.components ← Android Library, no core deps         │
│  core.data       ← Depends on core.domain                │
│  core.presentation ← Depends on core.components          │
│  core.network    ← Depends on Ktor                       │
│  core.storage    ← Depends on DataStore                  │
│  core.utils      ← Minimal deps                          │
└──────────────────────────────────────────────────────────┘
```

Within one feature, data and presentation depend on its pure Kotlin domain. The feature assembly layer combines registrations for the three layers. Domain must not depend on data, presentation or Android. Keep cross-feature routing and composition at the app shell. See [module layout](modularization.md).

## 2. Koin Dependency Injection

### Module Structure (per feature)

```kotlin
// Domain Module — factory scope
val catalogDomainModule = module {
    factory { GetPlantsUseCase(get()) }
    factory { GetPlantByIdUseCase(get()) }
}

// Data Module — mappers + repositories
val catalogDataModule = module {
    factory { PlantDataMapper(get()) }
    single { PlantRemoteDataSource(get()) }
    single<PlantRepository> { PlantRepositoryImpl(get(), get()) }
}

// Presentation Module — viewmodels + mappers
val catalogPresentationModule = module {
    factory { PlantUiMapper() }
    viewModelOf(::PlantListViewModel)
    viewModelOf(::PlantDetailViewModel)
}
```

### Scope Guidelines

| Type       | Scope         | Reason                            |
| ---------- | ------------- | --------------------------------- |
| UseCase    | `factory`     | Stateless, new instance each time |
| Mapper     | `factory`     | Stateless, lightweight            |
| Repository | `single`      | Shared state, caching             |
| DataSource | `single`      | Connection management             |
| ViewModel  | `viewModelOf` | Lifecycle-aware                   |

### Interface Binding

```kotlin
single<PlantRepository> { PlantRepositoryImpl(get(), get()) }
// or
single { PlantRepositoryImpl(get(), get()) } bind PlantRepository::class
```

### Feature Module Integration

```kotlin
object CatalogFeatureModule : HabiTreeFeatureModule() {
    override val dataModule = catalogDataModule
    override val domainModule = catalogDomainModule
    override val presentationModule = catalogPresentationModule
    override fun Module.navigationRules() { ... }
}
```

### App Initialization

```kotlin
startKoin {
    modules(
        coreNetworkModule,
        coreStorageModule,
        mainNavigationModule,  // includes all feature modules
    )
}
```

## 3. Core Module Key Components

### core.config

- `HabiTreeConfigProvider` — singleton for API URLs, feature flags
- `HabiTreeConfig` — data class with serialization support
- Pure Kotlin — can be shared across platforms

### core.domain

- `Money`, `Currency` — monetary value types
- `RepositoryResult<T>` — sealed class (Success/Error)
- `RepositoryUseCase<Params, Result>` — base UseCase class
- `DomainModel`, `BaseRepository` — base contracts

### core.data

- `DataMapper<Domain, Data>` — bidirectional mapping interface
- `NetworkResult` → `RepositoryResult` conversion helpers
- Safe mapping extensions (`.orEmpty()`, `.toMoneyOrZero()`)

### core.presentation

- `BaseViewModel<State, Event, Action>` — MVI state management
- `HabiTreeScreen` — base screen composable with error handling
- `UiMapper` — domain → UI model transformation interface
- `MoneyUiModel`, `CurrencyUiModel` — display-ready money types
- Navigation system — `NavKey`, transitions, `HabiTreeNavigator`

### core.components

- Theme: `HabiTreeTheme`, `LocalColorScheme`, `LocalTypography`
- Tokens: `HabiTreeTokens` (Spacing, Radius, Elevation)
- Atomic: `HabiTreeButton`, `HabiTreeCheckbox`, `HabiTreeText`
- Molecules: Cards, Input Groups, Dialogs

### core.network

- Ktor 3.x `HttpClient` with kotlinx.serialization
- Auth interceptor (Bearer token, 401 refresh)
- `NetworkResult` wrapper
- SSL pinning configuration

### core.storage

- DataStore Preferences (async, safe replacement for SharedPreferences)
- Encrypted preferences for sensitive data
- File management helpers

### core.utils

- Date/time formatting (Vietnam + international)
- Currency/number formatting
- Kotlin extensions (String, List, Map)
- Validation (email, phone, passport)

## Dependency review

- [ ] Each feature consumes only its own layers and approved core APIs.
- [ ] Core modules have no dependency on app or feature modules.
- [ ] Pure Kotlin domain has no Android or networking imports.
- [ ] App shell binds implementations and composes feature navigation.
- [ ] Use cases and mappers use factories, repositories and data sources use singletons, and ViewModels use lifecycle-aware Koin definitions.
