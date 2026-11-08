# Dependency-Free MVI Refactor Design

## Intent

Rework MamXanh's existing multi-module Android app so user actions flow through explicit intents, feature state is exposed as one immutable UI state per feature, and one-off outcomes use effects. This makes state ownership and behavior easier to follow while preserving the current features and visual UI.

## Goals

- Apply one MVI pattern consistently to Home, Tasks, Garden, Shop, Stats, and Tree.
- Give each feature a single immutable presentation state, typed user intents, and one-off effects for transient outcomes.
- Keep business rules in existing domain operations and persistence in existing repositories.
- Move navigation, add-task dialog visibility, and app-level messages out of composable-local coordination and into explicit app state.
- Retain the current modular Gradle structure and existing Compose screens/design.

## Non-goals

- No new MVI framework or dependency.
- No redesign of screens, feature behavior, data schema, repository contracts, or use-case/domain layer.
- No conversion of repositories from synchronous calls unless needed to safely implement the agreed state handling.
- No migration of navigation libraries or DI frameworks.

## Constraints

- Continue using Kotlin, Jetpack Compose, ViewModel, StateFlow, and the existing manual ViewModel factories.
- Keep feature presentation code inside its feature module; app-shell state remains in :app.
- Use immutable state snapshots and typed intent/effect definitions, with ViewModels as the state transition boundary.
- Preserve cross-feature data refresh after task, shop, and tree mutations.

## Approaches

1. **Dependency-free MVI (selected):** define state, intent, and effect types in each feature and process intents in its ViewModel; add explicit app-shell state and intents for navigation and global UI. This fits the modules and avoids a new runtime dependency.
2. **MVI framework:** use a framework such as Orbit for containers and side effects. This adds a dependency and imposes a framework-specific style across all modules.

## Design

Each feature ViewModel exposes a read-only StateFlow of a feature UiState and a dispatch(intent) entry point. Each UiState groups the screen's current data and presentation flags (including loading/error state where relevant). Existing individual data flows and action methods are replaced by state plus typed intents. Domain operations remain the source of business decisions; the ViewModel executes them on the existing IO boundary and emits updated state.

One-off outcomes, such as task/shop feedback, use a buffered or shared effect stream collected by the Compose host with lifecycle-aware collection. Effects cover transient user feedback and navigation requests only; durable data belongs in UiState. Screens receive UiState and an intent callback instead of parallel data/callback parameters. Pure presentation components remain unchanged.

The :app shell receives an AppViewModel (or equivalent app-level state holder) that owns destination/back stack, selected tree, add-task dialog visibility, and global feedback presentation. It dispatches app intents and collects feature effects. Cross-feature refresh coordination currently wired through callbacks in MainActivity and LaunchedEffects in MamXanhApp will be centralized in this shell boundary while feature ViewModels continue to own their own state.

## Scope and migration order

1. Add shared MVI presentation primitives only if the modules can depend on them without creating dependency cycles; otherwise keep small state/intent/effect types local to each module.
2. Migrate Tasks and Shop first because they already have action and message flows, then Garden and Tree, then Home and Stats.
3. Convert feature Composables to consume their feature UiState and dispatch intents.
4. Introduce app-shell state/intents/effects, migrate MamXanhApp and MainActivity coordination, and remove direct state mutation and refresh orchestration from Composables.
5. Check every module for remaining direct ViewModel action APIs and split state flows; compile the app and inspect resulting diagnostics.

## Acceptance criteria

- All six feature modules expose one immutable UiState flow per feature and accept typed intents through one dispatch boundary.
- One-off feedback/navigation is represented by effects rather than durable string state or composable side-effect copying.
- Navigation, selected-tree state, add-task visibility, and global feedback are owned by the app shell state holder.
- Compose screens render from state and report user actions as intents.
- Existing repositories, business rules, and visible behavior remain intact.
- The application compiles after the migration.
