# Phase 4 execution — Living Garden

## Goal
Deliver the offline customizable garden and botanical collection from phase-4-living-garden-detailed-concept.md, preserving existing player assets, tree identities and plot assignments.

## Decisions and instructions
- Economy v2 section 13 overrides stale concept numbers: 6/9/12 forms, universal 1.40× transitions and existing income limits. Preserve existing unlocked plots; future plots use Balance.plot.
- Four finite six-plot sections; accommodate larger legacy gardens without moving trees. Phones use a scrollable two-column scene with accessible tap-to-place rather than tiny pan/zoom targets. Long press enters arrange mode.
- Shared schema 7 validates ownership, slots, footprints, current layout version and receipts. Draft Undo/Cancel remains local; Save commits one atomic layout. Buying never auto-places. Moving/returning never charges; permanent purchases cannot charge twice.
- Garden owns editor, encyclopedia and appearance UI. Shop/Inventory link through app callbacks; preserve five navigation tabs and feature isolation. Unknown future layout records remain intact with fallback presentation.
- Static tree images remain temporary. No seasons, payments, network or growth animation. Native cosmetic art and existing resources avoid additional bitmap decoding. Ambient motion defaults off and stops with backgrounding, Reduced Motion or Battery Saver. Optional audio/export are not shipped.
- Render complete previews sequentially with explicit bounds, never an atlas. Mutating QA uses isolated .verification.phasefour and .verification.mint packages. Do not reset or grant fixture assets to the main profile; preserve unrelated staged deletions and old-plan edits.

## Completed implementation
1. Added versioned cosmetic catalog, stable sections/layout, historical species records, appearance settings and unique collection rewards.
2. Added transactional purchases/expansion/placement, grandfathered legacy accessory copies, preserved disposition milestones and linked garden moments into Phase 3 memories. Themes and rewards never alter growth or create income.
3. Added compact Studio, editor with valid-slot feedback/Undo/Cancel/Save, cosmetic catalog/detail/owned views, encyclopedia/stage gallery/records, nickname/themes/particle settings and Shop/Inventory/tree-detail navigation.
4. Added 24 real-SQLite Phase 4 regressions and retained all previous gameplay suites.
5. Built and tested; inspected complete previews and isolated runtime flows. Fixed issues found during inspection before the final build.
6. Prepared scoped publication and a data-preserving main-profile install; Git history records the resulting verified checkpoint.

## Verification evidence
- Initial combined suite: 102 passing tests, both APK builds and shared metadata/iOS ARM64 compilation (shell-32, 1m5s). Added old disposition and multiple legacy accessory cases: 104 passing (shell-37, 1m25s).
- Final shared rules: 105 passing tests, both APK builds and shared metadata/iOS ARM64 compilation (shell-39, 1m15s, exit 0). Includes receipt collision, finite expansion, cancellation, overlap/off-grid/locked rejection, ownership/permanence, history, restart, unknown records and cosmetic invariance.
- Final UI state/art refresh build passed in 53s (shell-41). Final text cleanup APK builds passed in 47s (shell-46). No business rules changed after the 105-test pass.
- Sequential full-content previews covered the complete six-plot section, all 11 species and five collection goals, every species-gallery/footer row at 320dp/font1.3, and every appearance control/footer. Explicit dp/dpi bounds avoid preview truncation. Existing plant/ribbon/lantern art is reused; ground/border/stone/sign drawings use Canvas.
- Fresh isolated profile skipped onboarding and opened Studio/editor. Fixed no-selection highlights and linked empty plots to seed selection.
- Legacy Mint QA migrated with its tree, original plot, ribbon and 35 coins intact. Removed the ribbon in a draft, exercised Undo, verified Cancel restored the committed layout, then saved its return to Inventory. A force-stop/restart retained the removal. Selected the still-owned ribbon, previewed it on the compatible tree and saved successfully; balance stayed 35 throughout.
- Actual encyclopedia reported 3/11 discoveries (27%) from the QA profile's tree and owned seeds. Its succulent gallery unlocked only stage 1 of 6. Two slow scrolls inspected the full gallery, real planted date, no invented maturity record, owned-tree link and final Shop action.
- Shop discovery records only seed IDs actually visible in the seed grid, rather than marking the entire catalog seen on entry. Derived viewport state avoids recomposition for every scroll pixel; discovery writes are queued independently of purchase/layout controls.
- Draft artwork now reflects draft placement rather than the saved tree's accessory. Editor entry refreshes before creating a draft; hidden screens avoid per-tick collection reloads. Visible garden screens refresh every 15s. Particles are bounded to 4/8 and only render for the active visible scene.
- Final discovery integration APK builds passed (shell-48/49). The final full-section preview showed the updated native lantern and clean footer without clipping. Latest debug APK installed using adb install -r; main Home retained its 63 coins, mature cherry, 48/50 income and 6/9 task plan. No data reset or main-profile purchases, completions, care actions or layout edits were used for QA.
- Write access was verified through successful IDE edits; Git publication includes only the Phase 4 files, leaving unrelated staged deletions and old-plan edits intact.

## Acceptance and limits
Criteria 1–10 and 13 have shared transactional tests; Undo/Cancel/Save/restart and gallery behavior also have emulator evidence. Criterion 11 has suppression-rule tests and explicit conditional rendering. Criterion 12 uses finite sections, lazy visible-cell rendering, reused resources, bounded particles and IO-dispatched storage; physical 8GB-device/GPU performance was not measured. Native iOS simulator runtime is unavailable on Windows and is not claimed. Optional audio and screenshot export are excluded; tree models/animations remain deferred as requested.
