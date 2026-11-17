# English/Vietnamese and design-token refactor

The previous Android UI/audit baseline is committed and pushed as 92a0b16 on y-tuong/quangtri in NQTri00/HabiTree (GitHub redirected the former thuc-tap-sinh remote).

## Required outcome

- A saved English/Vietnamese setting, retaining Vietnamese for existing installations until explicitly changed; Android locale metadata and system dialogs follow the selected language.
- All application-owned UI copy, accessibility descriptions, confirmations, notifications, validation, catalog labels and domain feedback have complete English/Vietnamese resources. User-written titles, names and notes retain their original content.
- Storage identifiers, category compatibility, protocol values and database schemas remain stable. Localization must not rewrite persisted user content or translate SQL.
- UI colors, spacing, sizing and typography use shared tokens; values are defined once in their resource/token source rather than repeated in screen code.
- Repeatable inventory/generation and regression checks prevent missing translations and new inline UI literals. Kotlin interpolation is parsed, with arguments kept as code and positional placeholders checked between languages.
- Build and verify English/Vietnamese real Compose captures across routes/popups and narrow/large-text layouts. Report runtime limits honestly.

## Approach

Inventory existing production Kotlin separately from previews/tests. Extract application copy to a typed cross-platform resource catalog, keeping stable stored category values explicit. Android's locale selection synchronizes the shared catalog and Android resource configuration. Preserve existing Android resource translations and remove the Vietnamese-only packaging restriction.

Keep resource data separate from domain/presentation source. Generated typed accessors are reproducible from the catalog. Dates and numbers are formatted at presentation boundaries; persisted date keys keep their machine format. Consolidate tokens in core-ui and migrate all affected modules without changing numeric layout values during extraction.

No changes to the approved web source. All task files/temp remain on E: or the existing D:/Program Files tools/cache paths. Heavy work runs sequentially under the existing heap cap.

## Progress

Implemented saved Android language selection, 1,098 bilingual catalog entries and matching generated Android XML, explicit localization boundaries for app-owned catalog/history data, stable stored categories, computed catalog labels, and shared design tokens. Thirteen Python resource/token checks and ten shared Kotlin tests have passed. Debug assembly succeeded.

Both languages have been exported across all 144 preview states. All 24 bilingual contact sheets were reviewed. Corrected purchase labels without Vietnamese diacritics, action/status wording, More navigation title, species fallback text, and wrapping task actions. English task actions were checked at full-size 320dp. Final date labels and picker fields share a locale-aware formatter; fresh bilingual date views and the matching APK have been verified.

Final local verification is complete: 288 corrected Compose exports, 13 resource/token checks, 10 shared Kotlin tests, matching debug assembly and v2 APK signature verification. The report records the final artifact checksum and image comparison.

The user explicitly requested periodic GitHub code and APK checkpoints. GitHub API authentication through Git's existing credential manager is verified. The checkpoint workflow pushes the reviewed commit on the authorized branch and uploads the matching verified APK plus portable image-review ZIP as release assets. See `tools/github_checkpoint.py`; never print or persist credentials.
