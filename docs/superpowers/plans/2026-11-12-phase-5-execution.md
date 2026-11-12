# Phase 5 execution

Goal: reliable Android upgrades, portable offline backups and enforceable KMP/release ownership, preserving all Phase 1–4 rules and the compact mint UI.

## Work sequence and completion criteria
1. Audit module ownership and schema history; record dependencies and migration evidence. Shared owns rules/SQL, data owns Android adapters, app owns file pickers/navigation. No new feature-to-feature dependency.
2. Implement versioned, bounded portable snapshots with SHA-256, exact nullable cell preservation, all persistent tables and sequence values. Reject unknown schema/tables/columns, malformed lengths and invalid references. Replacement is transactional and repeatable.
3. Add compact Data/About screen from Settings: SAF export/import, validated preview, explicit replace confirmation, automatic private safety backup, restore/recovery, double-confirmed clear, diagnostics without personal text, privacy/version/licenses guidance. No storage permission. Rebuild observable UI/reminders after replacement.
4. Add real SQLite tests for round trip, repeated restore, corruption, malformed archive, rollback, references, sequence identity and historical upgrades. Add common rule checks where feasible and CI integrity/dependency/test/report gates.
5. Verify Android debug/verification, metadata/native compilation, all SQLite tests, complete targeted previews, restore navigation and narrow/large-text UI. Measure reference-emulator launch/memory and document limits; no universal performance claim.
6. Document portable format, migration ownership, privacy/store copy, signing/version policy, forward-fix rollback and release runbook. Push only owned work after successful checks.

## Constraints
Keep static tree images; animations/transitions remain deferred. Keep existing player data and unrelated staged deletions. No payment, account, server, iOS UI or season feature. Plain backups contain private history and are explicitly unencrypted. Destructive restore/clear require in-app confirmation. Never claim Play publication, signing custody, hosted CI or physical-device performance checks passed without evidence.

## Verification log
Implemented the planned Android/data-portability work and local verification. No gameplay package shuffle or new schema version was required.

- Architecture gate passed; shared owns the bounded format/SHA-256/SQL replacement, data owns Android files/SQLite adapter, app owns the compact SAF workflow. Existing feature boundaries are preserved.
- All 34 tables and nullable cells are exported. Shape/type, date/state, reference, ownership and slot-overlap validation run before live writes; replace-all is transactional, repeatable and creates an AtomicFile safety copy.
- Settings links to Data/About: export, validated preview, explicit replacement, safety recovery, double-confirmed clear, sanitized diagnostics, privacy, version and bundled Apache license. Replacement discards stale garden drafts and resets navigation. Shipping locale stays Vietnamese; English resources are prepared but filtered until legacy copy is translated.
- Combined checkpoint: 124 SQLite tests passed. Final Phase 5 suite: 22 passed, 0 errors/failures (21 initial cases plus state validation); together with the existing 105 gameplay cases, 127 distinct cases are covered. Android file export/preview/safety restore/recovery/clear and 1,000-occurrence scale cases passed. Four common codec tests compile for iOS ARM64; native execution is not claimed on Windows.
- Debug and isolated verification APKs, common metadata, iOS ARM64 main/test compilation, architecture and Android lint passed. Runtime/navigation/locale rebuild passed in shell-103; final direct-reminder rebuilding, resource labels, both APKs and all 22 focused tests passed in shell-118. Lint retains nonfatal existing/project warnings; there is no claim of zero warnings.
- Full sequential previews checked normal Data and the complete 320dp/130% Vietnamese replacement document. Real SAF export, preview/cancel/restore, success counts, safety copy and Back-to-Settings/Home passed in .verification.phasefive. Offline + denied notifications + narrow/large text remained usable through the final diagnostic action. Device size, font and connectivity were restored.
- Reference measurements: repeat cold Activity launch median 1907 ms (first-install outliers are recorded); scale completion transaction 1–4 ms; 20 checked tree/Home cycles in one process with eventual reclamation to below starting PSS. See phase-5-reference-benchmark.md for raw values and limitations.
- Main package upgraded in place to build 2; five existing trees and 63 coins remained intact. No main-profile purchase, completion, care, restore or clear was performed.
- preReleaseBuild intentionally failed without external signing credentials, verifying the guard. Version 0.2.0 / code 2 retains the stable application ID. Keystores and personal backups are ignored by Git.
- CI definitions now enforce architecture/whitespace, assemble/lint/metadata, emulator SQLite tests and report/APK publication; native CI also compiles common tests. Hosted execution must be reported separately from local success.
- Store/privacy copy, module/migration checklist, format, signing inputs and forward-fix runbook are documented in phase-5-data-and-release.md and privacy.md.

## Release-owner gates
Actual production key custody, signed Play upload/contact declarations, physical low/midrange performance and TalkBack/keyboard usability remain publication checks, not fabricated passing results. No iOS UI, payment, season or tree animation work was added. Unrelated user deletions/old-plan edits remain outside Phase 5 commits.

