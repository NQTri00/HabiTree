# Android navigation

Use **Navigation 3 and Koin**. The app shell owns the root back stack, tab structure and external routing. Each feature registers its entries through its feature module. Destination keys belong to the feature and stay internal to its module.

```text
App Shell -> startKoin { modules(coreModules, featureNavigationModules) }
          -> HabiTreeFeatureModule.navigationRules()
          -> habiTreeNavigation<Key> { route content }
          -> HabiTreeNavigator updates the back stack
          -> Navigation 3 NavDisplay renders the matching entry
```

## Navigation keys

Use `@Serializable` data objects for destinations without arguments and `@Serializable` data classes for destinations with arguments. Implement Navigation 3's `NavKey`. Pass only primitives such as `String`, `Int` and `Boolean`. Use stable IDs and serialized scalars for arguments such as ISO dates. Resolve the destination's data using those IDs.

```kotlin
@Serializable
internal data object PlantListKey : NavKey

@Serializable
internal data class PlantDetailKey(
    val plantId: String,
    val plantName: String? = null,
) : NavKey

@Serializable
internal data class PlantCareKey(
    val plantId: String,
    val careType: String,
) : NavKey
```

## Feature registration

Expose one composed navigation module. Register data, domain and presentation bindings in the feature module. Inject the navigator in route content and pass callbacks to screens.

```kotlin
object CatalogFeatureModule : HabiTreeFeatureModule() {
    override val dataModule = catalogDataModule
    override val domainModule = catalogDomainModule
    override val presentationModule = catalogPresentationModule

    override fun Module.navigationRules() {
        habiTreeNavigation<PlantListKey> {
            val navigator = koinInject<HabiTreeNavigator>()
            PlantListRoute(
                onNavigateToDetail = { plantId -> navigator.goTo(PlantDetailKey(plantId)) },
            )
        }

        habiTreeNavigation<PlantDetailKey> { args ->
            val navigator = koinInject<HabiTreeNavigator>()
            PlantDetailScreen(
                plantId = args.plantId,
                onCareClick = { careType ->
                    navigator.goTo(PlantCareKey(args.plantId, careType))
                },
                onNavigateBack = { navigator.goBack() },
            )
        }

        habiTreeNavigation<PlantCareKey> { args ->
            val navigator = koinInject<HabiTreeNavigator>()
            PlantCareScreen(
                plantId = args.plantId,
                careType = args.careType,
                onNavigateBack = { navigator.goBack() },
            )
        }
    }
}

val catalogNavigationModule = CatalogFeatureModule.module
```

## App composition and stack operations

Include feature navigation modules in the app or its dedicated composition module. Keep feature imports out of other feature modules. Cross-feature routing belongs at the composition boundary and uses exposed entry contracts instead of internal keys from a sibling.

```kotlin
override fun Module.navigationRules() {
    includes(catalogNavigationModule, gardenNavigationModule, tasksNavigationModule)
    habiTreeNavigation<HomeKey> { HomeRoute() }
}
```

| Operation | Effect |
| --- | --- |
| `navigator.goTo(key)` | Push a typed destination. |
| `navigator.goBack()` | Pop the current destination and preserve a valid root. |
| `navigator.popBackTo<HomeKey>(inclusive = false)` | Pop to the destination and leave it on the stack. |
| `navigator.popBackTo<RootKey>(inclusive = true)` | Pop through the destination when the operation leaves a valid stack or installs a replacement root. |

Bind URL or notification entry points at the shell, validate their arguments and send them through the same typed navigation path. Restore the back stack and destination state through Navigation 3. One-time navigation effects are handled at the route boundary as described in [Android architecture](architecture.md).

- [ ] Destination keys are internal, serializable data classes or objects with primitive arguments.
- [ ] The feature extends the shared feature module and exposes its navigation registration.
- [ ] Route entries inject the navigator and screens receive callbacks.
- [ ] The shell composes all feature registrations and owns external routing.
- [ ] Forward, back, restore and cross-feature paths work through the shell.
