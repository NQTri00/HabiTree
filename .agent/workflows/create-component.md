---
description: Create a reusable Compose UI component with the shared Android design system
---

# Create a component

Use this workflow for a reusable card, button, input group or similar Compose component.

## 1. Search and choose the owner

Search existing components and approved Figma assets before creating new ones. Search the shared design-system library and the current feature presentation. A component reused by two or more modules belongs in `core.components`. Put feature-only composables in `<feature>-presentation/ui/<screen>/component/`. Shared components must stay independent of feature models and dependencies.

## 2. Define a stateless API and model

Pass immutable display data and callbacks. Apply the caller's `Modifier` to the root. Use an app-specific prefix for public design-system components, such as `HabiTreePlantCard`. Use semantic roles and localized accessibility descriptions where relevant.

```kotlin
data class HabiTreePlantCardModel(
    val title: String,
    val subtitle: String,
)

@Composable
fun HabiTreePlantCard(
    model: HabiTreePlantCardModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalColorScheme.current
    val typography = LocalTypography.current

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(HabiTreeTokens.Radius.Component.card),
        elevation = CardDefaults.cardElevation(
            defaultElevation = HabiTreeTokens.Elevation.Component.card,
        ),
    ) {
        Column(modifier = Modifier.padding(HabiTreeTokens.Spacing.lg)) {
            Text(model.title, style = typography.titleMedium, color = colors.textPrimary)
            Text(model.subtitle, style = typography.bodySmall, color = colors.textSecondary)
        }
    }
}
```

Use the owning theme and design tokens for padding, size, color, typography, radius and elevation. Use `HabiTreeTokens.Spacing` for spacing and `HabiTreeTokens.Sizing` for component sizes. Put a fixed feature-only value in the owning token set, such as `GardenTokens.Plot.tileSize`.

## 3. Add same-file previews

Render the actual component inside the theme. Include light, dark and representative UI states.

```kotlin
@Preview(showBackground = true)
@Composable
private fun HabiTreePlantCardPreview() {
    HabiTreeTheme {
        HabiTreePlantCard(
            model = HabiTreePlantCardModel("Sample plant", "Description"),
            onClick = {},
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun HabiTreePlantCardDarkPreview() {
    HabiTreeTheme {
        HabiTreePlantCard(
            model = HabiTreePlantCardModel(
                title = "Sample plant with a longer title",
                subtitle = "Description that can wrap onto a second line",
            ),
            onClick = {},
        )
    }
}
```

Use semantic colors in all rendering paths, including previews and shimmer. Sample strings and dimensions are allowed in previews and skeleton placeholders.

## 4. Localize and verify

Add new production wording to the shared feature YAML, export Android resources and reference the generated ID. See [Add localization](add-localization.md).

- [ ] Existing components and tokens were reviewed.
- [ ] The component is in the owning module and the public name follows the design system.
- [ ] UI state is passed through a model and interactions use callbacks.
- [ ] Color, typography, spacing, sizing, radius and elevation come from the theme or tokens.
- [ ] Same-file light and dark previews cover meaningful states.
- [ ] Production wording is localized and the actual UI implementation stays within the 300-line limit.
- [ ] `./gradlew :<module>:ktlintFormat` and `./gradlew assembleDebug` pass.
