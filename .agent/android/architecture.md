# Android Architecture

This is the required Android target architecture. The plant catalog examples illustrate the layer contracts. Use [modularization](modularization.md) for module layout and [the dependency graph](dependency-graph.md) for allowed Gradle edges.

## 1. Technology Stack

| Technology                             | Purpose                                      |
| -------------------------------------- | -------------------------------------------- |
| **Kotlin**                             | Primary language — single source of truth    |
| **Jetpack Compose**                    | 100% Compose UI — declarative, reactive      |
| **MVI (Model-View-Intent)**            | Presentation pattern: State / Event / Action |
| **Koin**                               | Dependency injection framework               |
| **Ktor Client**                        | HTTP networking (multi-platform ready)       |
| **kotlinx.serialization**              | JSON serialization                           |
| **Kotlin Coroutines + Flow**           | Async operations and reactive streams        |
| **DataStore**                          | Local key-value storage                      |
| **Coil 3**                             | Image loading                                |
| **Macrobenchmark & Baseline Profiles** | Performance optimization                     |

## 2. Presentation Pattern: MVI

Use **MVI (Model-View-Intent)** with `BaseViewModel`.

### Core Concepts

- **State** — Immutable data class holding the complete UI state (`isLoading`, `plants`, `error`). The UI observes and re-renders.
- **Action** — User interactions sent from the UI to the ViewModel (`onSearchClick`, `onItemClick`).
- **Event** — One-time side effects sent from ViewModel to UI (`ShowToast`, `Navigate`). Handled inside `LaunchedEffect`.

### Data Flow

```
User taps button
       │
       ▼
   Screen ──────── sends ──────── Action (sealed class)
                                    │
                                    ▼
                               ViewModel.handleAction()
                                    │
                                    ▼
                               UseCase.execute(params)         ← suspend function
                                    │
                                    ▼
                               Repository.getData(params)      ← Domain interface
                                    │
                                    ▼
                               RepositoryImpl (Data layer)     ← Implements interface
                                    │
                                    ▼
                               DataSource → Ktor API call
                                    │
                                    ▼
                               NetworkResult<DTO>
                                    │
                                    ▼
                               DataMapper.toDomain(dto)        ← DTO → Entity
                                    │
                                    ▼
                               RepositoryResult<Entity>        ← flows back up
                                    │
                                    ▼
                               ViewModel: updateState { copy(plants = ...) }
                                    │
                                    ▼
                               Screen auto re-renders          ← Compose reactive
```

## 3. Layer Mapping

### ① Domain Layer — Android Components

| Component                | Directory            | Responsibility                                                     |
| ------------------------ | -------------------- | ------------------------------------------------------------------ |
| **Model**                | `domain/model/`      | Pure Kotlin data class, immutable (`val` only), no Android deps    |
| **Repository Interface** | `domain/repository/` | Interface defining data operations, returns `RepositoryResult<T>`  |
| **UseCase**              | `domain/usecase/`    | Extends `RepositoryUseCase<Params, Result>`, single responsibility |

**Domain rules**

- Use pure Kotlin APIs and keep Android and AndroidX imports outside domain.
- All fields use `val` (immutable)
- Represent monetary values with `Money` and `Currency` from `core.domain`. Game counters such as HabiTree coins retain their integer semantics.
- UseCases return `RepositoryResult<T>` via suspend functions

### ② Data Layer — Android Components

| Component          | Directory          | Responsibility                                                              |
| ------------------ | ------------------ | --------------------------------------------------------------------------- |
| **DTO**            | `data/model/`      | `@Serializable` data class, all fields nullable with defaults               |
| **DataMapper**     | `data/mapper/`     | Implements `DataMapper<Domain, Data>` interface                             |
| **RepositoryImpl** | `data/repository/` | Implements Domain Repository, converts `NetworkResult` → `RepositoryResult` |
| **DataSource**     | `data/source/`     | Remote (Ktor API) and local (DataStore/Room) data sources                   |

**Data mapping extensions**

```kotlin
data.price.toMoneyOrZero(currency)  // Returns Money(0) if null
data.name.orEmpty()                  // Returns "" if null
mapper.toDomainOrNull(dto)           // Returns null if dto is null
mapper.toDomainList(dtoList)         // Maps list safely
```

**Layer rules**

- All DTO fields nullable with defaults for safe JSON parsing
- Always use `BigDecimal` for money-related fields
- Mappers implement `DataMapper<Domain, Data>` bidirectional interface

### ③ Presentation Layer — Android Components

| Component     | Directory                  | Responsibility                                |
| ------------- | -------------------------- | --------------------------------------------- |
| **State**     | `ui/<screen>/<Screen>State.kt` | Immutable data class implementing `UiState`   |
| **Event**     | `ui/<screen>/<Screen>Event.kt` | Sealed class implementing `UiEvent`           |
| **Action**    | `ui/<screen>/<Screen>Action.kt` | Sealed class implementing `UiAction`          |
| **ViewModel** | `ui/<screen>/<Screen>ViewModel.kt` | Extends `BaseViewModel<State, Event, Action>` |
| **Screen**    | `ui/<screen>/<Screen>Screen.kt`    | Stateless `@Composable`, renders state only   |
| **UiMapper**  | `mapper/`                  | Formats Domain data for display               |
| **UiModel**   | `ui/<screen>/`             | Display-ready data models                     |

**Layer rules**

- ViewModel extends `BaseViewModel<State, Event, Action>` with `initialState`
- Keep the screen stateless. Resolve the ViewModel and navigator at the route boundary and pass state and callbacks to screen content.
- Collect events in a route-level `LaunchedEffect` scoped to the ViewModel and active lifecycle.
- No business logic in `@Composable` functions
- Monetary display uses `MoneyUiModel` and `CurrencyUiModel` from `core-presentation`

## 4. Code Examples

### Domain Layer

```kotlin
// ── Model ──
data class Plant(
    val id: String,
    val name: String,
    val price: Money,
    val rating: Double,
)

// ── Repository Interface ──
interface PlantRepository {
    suspend fun getPlants(): RepositoryResult<List<Plant>>
    suspend fun getPlantById(id: String): RepositoryResult<Plant>
}

// ── UseCase ──
class GetPlantsUseCase(
    private val repository: PlantRepository,
) : RepositoryUseCase<Unit, List<Plant>>() {
    override suspend fun execute(parameters: Unit): RepositoryResult<List<Plant>> {
        return repository.getPlants()
    }
}
```

### Data Layer

The example assumes an exact-decimal contextual serializer is installed and the money type exposes an amount and currency code. The illustrative `RatingDto` exposes a nullable numeric value. Configure both against the actual API contract. Use nullable-field helpers only where the domain allows the fallback.

```kotlin
// ── DTO ──
@Serializable
data class PlantDto(
    val id: String? = null,
    val name: String? = null,
    @Contextual val price: BigDecimal? = null,
    val currency: String? = null,
    val rating: RatingDto? = null,
)

// ── DataMapper ──
class PlantDataMapper(private val moneyMapper: MoneyDataMapper) : DataMapper<Plant, PlantDto> {
    override fun toDomain(data: PlantDto): Plant {
        return Plant(
            id = data.id.orEmpty(),
            name = data.name.orEmpty(),
            price = moneyMapper.toDomain(data.price, data.currency),
            rating = data.rating?.value ?: 0.0,
        )
    }
    override fun toData(domain: Plant): PlantDto = PlantDto(
        id = domain.id,
        name = domain.name,
        price = domain.price.amount,
        currency = domain.price.currency.code,
        rating = RatingDto(value = domain.rating),
    )
}

// ── Repository Implementation ──
class PlantRepositoryImpl(
    private val remoteDataSource: PlantRemoteDataSource,
    private val mapper: PlantDataMapper,
) : PlantRepository {
    override suspend fun getPlants(): RepositoryResult<List<Plant>> {
        return when (val result = remoteDataSource.getPlants()) {
            is NetworkResult.Success -> RepositoryResult.Success(mapper.toDomainList(result.data))
            is NetworkResult.Error -> RepositoryResult.Error(result.exception)
        }
    }

    override suspend fun getPlantById(id: String): RepositoryResult<Plant> =
        when (val result = remoteDataSource.getPlantById(id)) {
            is NetworkResult.Success -> RepositoryResult.Success(mapper.toDomain(result.data))
            is NetworkResult.Error -> RepositoryResult.Error(result.exception)
        }
}

// ── Remote Data Source ──
class PlantRemoteDataSource(private val apiClient: HttpClient) {
    suspend fun getPlants(): NetworkResult<List<PlantDto>> {
        return apiClient.safeGet("/api/plants")
    }

    suspend fun getPlantById(id: String): NetworkResult<PlantDto> =
        apiClient.safeGet("/api/plants/${id.encodeURLPathPart()}")
}
```

### Presentation Layer

```kotlin
// PlantListState.kt
data class PlantListState(
    val isLoading: Boolean = false,
    val plants: List<PlantUiModel> = emptyList(),
    val error: ErrorState? = null,
) : UiState

// PlantListEvent.kt
sealed class PlantListEvent : UiEvent {
    data class NavigateToDetail(val plantId: String) : PlantListEvent()
    data class ShowToast(val message: String) : PlantListEvent()
}

// PlantListAction.kt
sealed class PlantListAction : UiAction {
    object LoadPlants : PlantListAction()
    data class PlantClicked(val plantId: String) : PlantListAction()
}

// ── ViewModel ──
class PlantListViewModel(
    private val getPlantsUseCase: GetPlantsUseCase,
    private val mapper: PlantUiMapper,
) : BaseViewModel<PlantListState, PlantListEvent, PlantListAction>(
    initialState = PlantListState()
) {
    override fun onInitialize() { loadPlants() }

    override fun handleAction(action: PlantListAction) {
        when (action) {
            PlantListAction.LoadPlants -> loadPlants()
            is PlantListAction.PlantClicked -> {
                emitEvent(PlantListEvent.NavigateToDetail(action.plantId))
            }
        }
    }

    private fun loadPlants() {
        launchWithLoading {
            when (val result = getPlantsUseCase()) {
                is RepositoryResult.Success -> updateState { copy(plants = mapper.toUiList(result.data)) }
                is RepositoryResult.Error -> updateState { copy(error = ErrorState(result.exception)) }
            }
        }
    }
}

// ── Route wrapper ──
@Composable
fun PlantListRoute(
    viewModel: PlantListViewModel = koinViewModel(),
    onNavigateToDetail: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    val navigateToDetail by rememberUpdatedState(onNavigateToDetail)
    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                when (event) {
                    is PlantListEvent.NavigateToDetail -> navigateToDetail(event.plantId)
                    is PlantListEvent.ShowToast -> { /* show toast */ }
                }
            }
        }
    }

    PlantListScreen(
        state = state,
        onPlantClick = { id -> viewModel.onAction(PlantListAction.PlantClicked(id)) },
    )
}
```

## 5. Key Patterns Summary

| Pattern                    | Where Used           | Description                                              |
| -------------------------- | -------------------- | -------------------------------------------------------- |
| **Clean Architecture**     | All feature modules  | Data and presentation depend on domain            |
| **MVI**                    | Presentation layer   | State / Event / Action via `BaseViewModel`               |
| **Koin DI**                | All modules          | Module-per-layer with `factory`, `single`, `viewModelOf` |
| **Navigation 3**           | Cross-module nav     | Type-safe `@Serializable` NavKeys + `HabiTreeNavigator` |
| **HabiTreeFeatureModule** | Feature registration | Combines data/domain/presentation modules + nav rules    |
| **RepositoryResult**       | Domain ↔ Data        | Sealed class for Success/Error result handling           |
| **DataMapper**             | Data layer           | Bidirectional DTO ↔ Entity mapping                       |
| **UiMapper**               | Presentation layer   | Domain → UiModel transformation                          |
| **Coroutines + Flow**      | All async operations | `suspend` functions + `StateFlow` for reactive UI        |

## Migration invariants

Move existing gameplay behind the target layer interfaces before replacing its entry points. Preserve offline operation, saved data and stable IDs. Keep writes such as rewards, purchases and inventory changes transactional and safe to retry. Use worker dispatchers for blocking storage operations. Verify each migrated route through the app shell and its normal back stack.
