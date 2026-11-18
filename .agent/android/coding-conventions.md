# Android Coding Conventions

## 1. UI File Size Limit

> **Maximum 300 lines of actual UI implementation code per file**

This limit applies only to real UI implementation code. Do not count:

- Package declaration and imports
- Compose preview functions and preview-only sample data

If the actual UI implementation code exceeds 300 lines:

- Extract UI sub-components to `component/` subfolder
- Split logic into separate classes
- Use extension files for related functions

## 2. Naming Conventions

### Kotlin Naming

| Element   | Case            | Example              |
| --------- | --------------- | -------------------- |
| Class     | PascalCase      | `PlantListViewModel` |
| Interface | PascalCase      | `PlantRepository`    |
| Function  | camelCase       | `loadPlants()`       |
| Variable  | camelCase       | `isLoading`          |
| Constant  | SCREAMING_SNAKE | `MAX_ITEMS`          |
| Extension | camelCase       | `String.toMoney()`   |

### Class Suffixes

| Layer        | Type       | Suffix           | Example                 |
| ------------ | ---------- | ---------------- | ----------------------- |
| Domain       | Model      | (none)           | `Plant`, `GardenPlot`      |
| Domain       | UseCase    | `UseCase`        | `GetPlantsUseCase`      |
| Domain       | Repository | `Repository`     | `PlantRepository`       |
| Data         | DTO        | `Dto`            | `PlantDto`              |
| Data         | Mapper     | `DataMapper`     | `PlantDataMapper`       |
| Data         | Repo Impl  | `RepositoryImpl` | `PlantRepositoryImpl`   |
| Data         | Source     | `DataSource`     | `PlantRemoteDataSource` |
| Presentation | State      | `State`          | `PlantListState`        |
| Presentation | Event      | `Event`          | `PlantListEvent`        |
| Presentation | Action     | `Action`         | `PlantListAction`       |
| Presentation | ViewModel  | `ViewModel`      | `PlantListViewModel`    |
| Presentation | Screen     | `Screen`         | `PlantListScreen`       |
| Presentation | Mapper     | `UiMapper`       | `PlantUiMapper`         |
| Presentation | UiModel    | `UiModel`        | `PlantUiModel`          |
| Presentation | Preview    | `Preview`        | `SeedCardPreview`  |
| Navigation   | Key        | `Key`            | `PlantDetailKey`        |

### UseCase Naming

Format: `[Action][Entity]UseCase`

| Action | Usage             | Example                     |
| ------ | ----------------- | --------------------------- |
| Get    | Fetch single/list | `GetPlantsUseCase`          |
| Search | Query with params | `SearchPlantsUseCase`       |
| Create | Create new record | `CreateGardenPlotUseCase`      |
| Update | Modify existing   | `UpdateProfileUseCase`      |
| Delete | Remove record     | `DeleteCartItemUseCase`     |
| Clear  | Clear all         | `ClearSearchHistoryUseCase` |

## 3. Resource Naming

**Prefix by module to avoid conflicts:**

| Module  | Prefix     | Example             |
| ------- | ---------- | ------------------- |
| Core UI | `core_ui_` | `core_ui_ic_back`   |
| Plant   | `plant_`   | `plant_img_leaf`  |
| Garden  | `garden_`  | `garden_ic_water`   |
| Account | `account_` | `account_ic_avatar` |

**Resource types:**

| Type     | Format                   | Example                  |
| -------- | ------------------------ | ------------------------ |
| Drawable | `[module]_[type]_[name]` | `plant_ic_seed`          |
| String   | `[module]_[name]`        | `plant_title_search`     |
| Color    | `[module]_color_[name]`  | `core_ui_color_primary`  |
| Dimen    | `[module]_[name]`        | `core_ui_padding_medium` |

## 4. UI & Design System Rules

### Typography

Use `LocalTypography.current` at UI call sites. Keep typography definitions in the design system.

### Colors

Use `LocalColorScheme.current` or the feature's defined color scheme at UI call sites. This applies to regular UI, previews, and shimmer. Raw palette values belong only in the color scheme definition, where they are mapped to semantic colors.

### Dimensions

For production UI, do not hardcode dimensions (`16.dp`, `20.sp`, etc.). Choose tokens by their semantic role.

1. Search `HabiTreeTokens.Spacing` for padding/gaps and `HabiTreeTokens.Sizing` for component sizes. Use the matching role (spacing around text, button height, icon size, card size). Use `LocalTypography` for text styling and check radius/elevation tokens when relevant.
2. If the value is specific to a feature or shared garden component and no token with the same meaning exists, add a clearly named token to that module's token set, such as `GardenTokens` or `InventoryTokens`. Add to `HabiTreeTokens` only when the value is genuinely shared across features. Do not borrow an unrelated token merely because its number matches.
3. A dimension parameter derived from available width, density, or another token may be calculated at the call site; keep fixed design values in tokens.

`@Preview` sample layouts and shimmer/skeleton placeholders may use literal dimensions and strings. Use the appropriate color scheme in previews and skeletons. User-facing text outside these exceptions follows [Android localization](localization.md).

```kotlin
// ❌ FORBIDDEN
Text("Hello", style = TextStyle(fontSize = 16.sp))
Box(modifier = Modifier.background(Color.Blue))
Modifier.padding(16.dp) // production UI

// ✅ REQUIRED
Text(stringResource(R.string.plant_title_search), style = LocalTypography.current.bodyMedium)
Box(modifier = Modifier.background(LocalColorScheme.current.primary))
Modifier.padding(HabiTreeTokens.Spacing.lg)
Modifier.height(HabiTreeTokens.Sizing.Button.heightMedium)
```

## 5. Compose Previews

- Previews MUST be used for all UI components and screens.
- **Location:** Preview functions MUST always be placed in the **SAME FILE** as the Composable UI component/screen (never in a separate preview file).
- **Naming:** `[ContentViewName]Preview` (e.g., `SeedCardPreview`, `PlantListContentPreview`).
- Show multiple variants (default, loading, error, edge cases).

## 6. MVI Contract Separation

- `State`, `Action`, and `Event` classes MUST be separated into **individual files** (`[Screen]State.kt`, `[Screen]Action.kt`, `[Screen]Event.kt`).

## 7. Figma Integration

- Always use the configured Figma integration to inspect Figma designs and extract layout, tokens, and assets.

## 8. File Organization

Within a Kotlin file, order elements:

1. Package declaration
2. Imports
3. Top-level constants
4. Main class/interface
5. Extension functions
6. Preview functions (Compose)

## 9. Build Verification

Run these checks before committing Android implementation changes.

```bash
# Format code
./gradlew :[module-name]:ktlintFormat

# Build
./gradlew assembleDebug
```
