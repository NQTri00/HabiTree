---
description: Add Vietnamese and English copy and generate Android resources
---

# Add localization

## 1. Find the owner and key

Search `localization/strings/common.yaml` and the owning feature YAML. Search Android resources for the relevant alias. Reuse a key only when the wording has the same meaning and arguments. Preserve resource IDs referenced by existing call sites.

## 2. Update the HabiTree catalog

Edit the existing key for a wording change. For a new key, add Vietnamese and English, a feature-prefixed Android alias, and named parameters and plural forms where needed. Declare platform-specific wording in the catalog when it applies. Keep persisted identifiers stable and pass user-authored text as arguments.

## 3. Validate and generate

Run the HabiTree generator's catalog validation and Android export for the owning feature. Use its mapping to select the output module and files for `values/` and `values-en/`. If the target generator is pending, bring migration of the affected copy and the generator into scope before changing managed entries.

Review the generated diff. Use `stringResource` or `pluralStringResource` with the generated ID and check placeholder order. Commit the YAML source and generated resources together. If an entry is shared by multiple platforms, check the generated outputs for each platform.

- [ ] The key belongs to common or the affected feature.
- [ ] Vietnamese and English contain matching arguments and appropriate plural forms.
- [ ] Validation passes and generated output matches the Android mapping.
- [ ] The rendered screen and its accessibility wording use the intended translation.
- [ ] Stored identifiers, existing aliases and user-authored content are preserved.
