# HabiTree Android agent guide

This directory defines the **required target architecture** for HabiTree (Mầm Xanh). Build new Android work to these contracts and migrate existing code toward them. Use the HabiTree namespace for project implementations.

| Topic | Guide |
| --- | --- |
| Kotlin, Compose, Clean Architecture, MVI, and layer examples | [Android architecture](android/architecture.md) |
| Multi-repository Gradle layout and module responsibilities | [Android modularization](android/modularization.md) |
| Feature isolation, dependency direction and Koin scopes | [Dependency graph and Koin](android/dependency-graph.md) |
| Navigation 3, internal serializable keys and feature registration | [Android navigation](android/navigation.md) |
| Ktor 3, DTOs, remote data sources and result mapping | [Android networking](android/networking.md) |
| Kotlin, resources, MVI contracts, design tokens and Figma | [Android coding conventions](android/coding-conventions.md) |
| Canonical YAML, locale mapping and generated Android resources | [Android localization](android/localization.md) |
| Step-by-step development tasks | [Add localization](workflows/add-localization.md) · [Create a component](workflows/create-component.md) |

## Applying the architecture

1. Choose the feature owner and identify the required domain, data and presentation boundaries.
2. Define an immutable domain model, a repository interface and focused use cases. Implement data sources and mappers in the data layer. Implement the MVI state, actions and events in presentation.
3. Register layer bindings and Navigation 3 entries through the feature module. Compose features in the shell and keep feature-to-feature Gradle edges out of the graph.
4. Use the shared design system and localization workflow for UI. Verify the affected modules, navigation flow and existing saved-data compatibility.

Existing directory layouts, route controllers, contracts and copy systems are migration inputs. Use these guides when deciding the destination structure and interfaces. Follow [AGENTS.md](../AGENTS.md) for workspace and verification procedures.

For the current Android migration, apply the [stop condition in AGENTS.md](../AGENTS.md#stop-condition-for-the-current-android-architecture-migration) after each verified checkpoint. The scope excludes iOS.
