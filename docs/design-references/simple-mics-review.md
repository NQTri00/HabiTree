# Simple mics redesign review — 2026-11-09

Fresh read-only review found no Critical issues and four Important issues:
1. Home fixed-position plaque allows unlimited name lines; long names overlap destination/care controls. Fix: one visible line with ellipsis, full semantic text; verify long-name preview at fontScale1.3.
2. Fixed 78x108 seed slots center art over bottom-aligned labels; wrapped labels overlap art and can hide stock. Fix: measured vertical art/text regions and separate count; allow basket to grow.
3. Seven legacy species render as fertilizer sacks. Fix: SimpleSpeciesPlant provides recognizable monstera, ficus, snake, cactus, aloe, lavender and pilea in the new simple style; integrate into PaintedGardenPlant fallback.
4. Cherry suppresses FLOWERS overlay, so two decoration selections look identical. Fix: render the overlay for cherry too.

Catalog IDs/prices agree across storage, garden and shop. Real intents remain connected for purchases, planting, water/fertilizer/refill, decoration, task completion/deletion/bonus and navigation. Dialog content is scrollable.

Review scope: static code and three mics sheets only. Builds, runtime behavior and visual similarity were not independently certified. Root must verify fixes with build and Compose Preview, per the approved verification scope.

Review fixes are now applied, including measured seed slots, bounded Home names, species-specific fallback art and cherry flower overlay. :app:assembleDebug passed after these changes. The large-text TODO form preview confirmed stacked labels and full-width fields; its body remains scrollable above fixed actions. Remaining visual checks are tracked in the implementation plan. Studio now runs with a persisted 4GiB heap, and inspections/previews have recovered; see heap-recovery-2026-11-09.md. No 90% similarity score or full completion claim.
