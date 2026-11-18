# English / Vietnamese resources

The latest branding, plant-name and themed launcher-icon work is documented in [POLISH-REPORT.md](POLISH-REPORT.md).

Edit `localization/strings/*.yaml` for application copy. The `vi` and `en` values share numbered placeholders, such as `{0}`. Pass user data as arguments; never translate user-written task titles, tree names or notes. `canonical` entries retain their original `storageValue` so existing categories and backups remain compatible. Use `AppText.category` only when displaying those IDs.

Install the pinned dependency from `tools/localization/requirements.txt` in your project Python environment. Keep each entry's `order` unique and preserve existing orders. `localization/android-outputs.json` defines the Android resource targets.

Run from the repository root:

```powershell
$env:PYTHONPYCACHEPREFIX='E:/Workspace/mamxanh-redesign/.tmp/python-cache'
python tools/localization/generate.py
python -m unittest discover -s tools/localization -p 'test_*.py'
```

Generation refuses missing English translations and mismatched placeholders. It produces typed common Kotlin copy and Android XML for the default, English and Vietnamese locales. `en_one` with `pluralArgument` handles the current count-sensitive English labels. Catalogs expose computed getters so they refresh after a language switch.

The Android language selector is in Settings. Vietnamese remains the migration default. The selected language is persisted, Android 13+ application locales are synchronized, and older Android activities recreate with the selected configuration. User-authored content is preserved.

Shared color, dimension, icon, radius, spacing, opacity and typography values live in `core-ui/.../ui/design`. Edit these definitions for design changes. Vector geometry, database/protocol identifiers, calculations and persisted ISO date formats are data rather than UI copy or spacing tokens.

The extraction/migration scripts document the migration; they are not part of normal resource generation. In particular, `design_tokens.py` intentionally refuses a second destructive extraction over existing token definitions.

Real Compose preview manifests are generated with `python tools/localization/preview_variants.py`. Run languages in separate render batches to isolate process locale state:

```powershell
& ./docs/bio-parity/headless.ps1 -Manifest ./docs/localization/preview-jobs-vi.json -Log ./docs/localization/render-vi.log
& ./docs/bio-parity/headless.ps1 -Manifest ./docs/localization/preview-jobs-en.json -Log ./docs/localization/render-en.log
```

Run heavy builds and renders sequentially and follow the memory limits in `AGENTS.md`. Existing `docs/bio-parity/evidence` captures are the historical pre-localization audit, not proof of this refactor.

KMP host tests are enabled using the [official Android test configuration](https://developer.android.com/kotlin/multiplatform/plugin#configure-tests). Validation and visual review results are recorded separately after execution.

## GitHub checkpoints

After the matching APK has been built and verified, commit the reviewed source and evidence report and push the authorized branch. Upload the APK through `tools/github_checkpoint.py`; it uses the existing Git credential-manager login, checks the pushed commit, and verifies uploaded asset size and the server-provided digest. It does not require GitHub CLI. The optional evidence ZIP contains a portable gallery of the original bilingual captures.
