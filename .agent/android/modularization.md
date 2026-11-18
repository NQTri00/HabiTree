# Android Modularization

## 1. Multi-Repo Gradle Module System

Use a **multi-repository modular system**. Each feature or core library has an independent repository. A feature repository contains separate Gradle domain, data and presentation libraries. The app consumes those libraries through its composition root.

### Project structure

```
/
├── :app                                       ← 🏠 App Shell (Android Application)
│
├── habitree.android.core.config              ← ⚙️ Core: App configuration
├── habitree.android.core.domain              ← 🧠 Core: Pure business logic
├── habitree.android.core.data                ← 📦 Core: Data layer abstractions
├── habitree.android.core.presentation        ← 🖼️ Core: Base ViewModel, MVI, Screen
├── habitree.android.core.components          ← 🎨 Core: Design system (Compose UI)
├── habitree.android.core.network             ← 🌐 Core: Ktor HTTP client
├── habitree.android.core.storage             ← 💾 Core: Local storage (DataStore)
├── habitree.android.core.utils               ← 🛠️ Core: Utilities, formatters
│
├── habitree.android.feature.home
├── habitree.android.feature.tasks
├── habitree.android.feature.garden
├── habitree.android.feature.catalog
├── habitree.android.feature.tree
├── habitree.android.feature.shop
├── habitree.android.feature.inventory
└── habitree.android.feature.stats
```

## 2. Feature Module Internal Structure

Decompose every feature repository into these layer modules.

```
habitree.android.feature.catalog/
├── catalog-domain/                              ← Pure Kotlin module
│   └── src/main/kotlin/com/mamxanh/app/feature/catalog/domain/
│       ├── model/                             ← Domain models
│       ├── repository/                        ← Repository interfaces
│       └── usecase/                           ← Business logic
│
├── catalog-data/                                ← Android Library module
│   └── src/main/kotlin/com/mamxanh/app/feature/catalog/data/
│       ├── model/                             ← DTOs (@Serializable)
│       ├── mapper/                            ← DataMappers
│       ├── repository/                        ← Repository implementations
│       └── source/                            ← Remote/Local data sources
│
└── catalog-presentation/                        ← Android Library module
    └── src/main/kotlin/com/mamxanh/app/feature/catalog/presentation/
        ├── ui/
        │   └── <screen>/
        │       ├── <Screen>ViewModel.kt       ← ~100 lines
        │       ├── <Screen>State.kt
        │       ├── <Screen>Event.kt
        │       ├── <Screen>Action.kt
        │       ├── <Screen>Screen.kt          ← ~150 lines
        │       └── component/                 ← Sub-components
        ├── mapper/                            ← UiMappers
        ├── navigation/                        ← NavKeys + FeatureModule
        └── di/                                ← Koin modules
```

## 3. Core Module Inventory

| Module              | Type            | Dependencies      | Responsibility                                                                                                    |
| ------------------- | --------------- | ----------------- | ----------------------------------------------------------------------------------------------------------------- |
| `core.config`       | Pure Kotlin     | None              | API base URLs, feature flags, environment config (`HabiTreeConfigProvider`)                                      |
| `core.domain`       | Pure Kotlin     | None              | Base contracts (`DomainModel`, `BaseRepository`), `RepositoryResult`, common models (`Money`, `Currency`, `User`) |
| `core.data`         | Android Library | `core.domain`     | `DataMapper` interface, network operation handlers, shared DTOs                                                   |
| `core.presentation` | Android Library | `core.components` | `BaseViewModel`, `HabiTreeScreen`, `UiMapper`, navigation system, error handling UI                              |
| `core.components`   | Android Library | None              | Design system: Theme, Colors, Typography, `HabiTreeTokens`, atomic components (`HabiTreeButton`, etc.)          |
| `core.network`      | Android Library | Ktor 3.x          | Ktor `HttpClient`, auth interceptor, `NetworkResult` wrapper, SSL pinning                                         |
| `core.storage`      | Android Library | DataStore         | Local storage, encrypted preferences, file management                                                             |
| `core.utils`        | Android Library | None              | Date/time formatting, currency formatting, Kotlin extensions, validators                                          |

## 4. App Shell Responsibilities

### `:app` Module

| Component                | Responsibility                                                |
| ------------------------ | ------------------------------------------------------------- |
| **Koin Setup**           | `startKoin { }` — loads all core + feature modules            |
| **Navigation Host**      | Root Navigation 3 `NavDisplay` with tab structure                             |
| **Feature Registration** | Includes all `featureNavigationModule` instances              |
| **Configuration**        | Initializes `HabiTreeConfigProvider` with environment config |
| **Deep Linking**         | URL schemes and notification intents → typed feature routing                                 |

## 5. Package Naming Convention

```
com.mamxanh.app.feature.[feature].[layer]/
├── di/           # Koin modules
├── model/        # Models (Domain/Data)
├── mapper/       # Mappers
├── repository/   # Repository (interface/impl)
├── usecase/      # UseCases (Domain only)
├── source/       # DataSources (Data only)
├── ui/           # Screens (Presentation only)
│   └── [screen]/
│       ├── component/
│       └── ...
└── navigation/   # Nav keys (Presentation only)
```

## HabiTree feature boundaries

Use the same shape for HabiTree feature repositories such as home, tasks, garden, tree, shop, inventory and statistics. Place Android app packages under the project namespace, for example `com.mamxanh.app.feature.garden.domain`. Register each feature through the app shell. Keep interactions between inventory, garden and shop at that boundary or behind genuinely shared core contracts. See [dependency direction](dependency-graph.md).
