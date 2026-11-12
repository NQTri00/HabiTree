# Phase 4 compact UI refinement — 2026-11-12

## Result
Redesigned the Phase 4 Studio/editor, decoration catalog and owned view, item details, expansion confirmation, encyclopedia, species records and appearance controls. Use compact mint surfaces, green selection pills, consistent icons, angled gradients and lighter shadows. Removed redundant shortcut rows by moving their actions into existing Garden/Shop/Inventory headers.

## Layout changes
- Phone Studio uses three columns, with two columns for narrow or enlarged-text windows. Equal plot cards are approximately118dp high; artwork is40dp, percentage stays centered in its bar, and decoration indicators share the status row. All six plots fit the normal phone viewport.
- Move/return controls live in one selected-object toolbar. Back cancels, Undo and Save stay in the header. Plot cards no longer grow two full button rows for each decoration.
- Decoration/species tiles use fixed artwork/name/value slots and72dp normal-text card height. Grouped appearance panels put themes and particle choices into rows. Settings and detail pages use compact facts and restrained primary actions.
- Native fence/sign/ground strokes scale with their bounds; the stone-path reward has its own stepping-stone icon. Theme/ambient icons use scalable vectors instead of oversized emoji glyphs.
- Interactive controls retain48dp touch areas while their visible surfaces are smaller. Names and progress-label slots scale with text size. Main Home/tree-care hero dimensions are preserved.

## Full-content preview evidence
Measurements are approximate fixture content extents at dpi160, not guarantees for every profile or text scale.

| Document | Before | After |
|---|---:|---:|
| Six-plot Studio |753dp|414dp|
| Appearance controls |969dp|528dp|
| Species detail,320dp/font1.3 |1012dp|612dp|
| Encyclopedia and all five milestones |1235dp|691dp|

All15 decoration tiles fit through488dp in the final normal-text catalog preview. Rendered complete Studio, arrange mode, appearance, encyclopedia, species detail, decoration detail, catalog and expansion confirmation sequentially. Refreshed the defining component preview when layoutlib reused old classes; no atlas or simultaneous builds/renders. A targeted24dp artwork preview verified that fence strokes no longer merge.

## Verification
- Debug and isolated verification APK builds passed, including final source/art cleanup (shell-67,48s).
- Isolated Mint QA: Studio/editor show all six plots; select ribbon, return it in the draft, Undo, then Cancel. Artwork, ownership and the saved layout remain correct; balance stays35 coins.
- Real320dp phone window with130% text: Shop header shortcuts fit; decoration tiles adapt to two columns; two slow scrolls reach the final plaque and value. Restored physical720×1280 and font scale1.0 afterward.
- Final full-content previews include all catalog entries, five collection goals, gallery states, record fields, settings and confirmation actions.
- This refinement changes presentation and UI selection, with no schema/economy/repository changes. Verification uses builds, complete previews and runtime gestures; the earlier105-rule-test pass belongs to the Phase 4 implementation checkpoint.
