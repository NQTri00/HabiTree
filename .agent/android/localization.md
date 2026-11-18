# Android localization

## Source of truth

The target architecture uses a HabiTree-owned YAML catalog for application copy. Organize it as `localization/strings/common.yaml` and `localization/strings/<feature>.yaml`. Maintain the catalog schema and Android output mapping alongside the generator. Define each output's module, resource file, alias and locale in the mapping.

Search the common catalog, the owning feature catalog and Android resources before creating a key. Reuse a matching entry owned by common or the feature. Keep the key stable for wording-only changes. Use a separate key if meaning or arguments differ. Leave user-authored names, notes and technical identifiers outside the translation catalog.

## Android exports and usage

Generate Vietnamese resources under `values/` and English under `values-en/`. Resolve the destination module and resource file using the catalog mapping. Keep generated resources under generator ownership and commit them with the source catalog.

```kotlin
Text(stringResource(R.string.catalog_search_plant_hint))
```

Supply named placeholders and plural forms in both languages where needed. Use the generated Android ID and pass arguments in the exported order.

```kotlin
Text(pluralStringResource(R.plurals.catalog_search_result_count, count, count))
```

Localize labels, error messages and accessibility text. Literal sample copy is permitted in previews and non-content skeleton placeholders. See [Add localization](../workflows/add-localization.md) for implementation and review steps.
