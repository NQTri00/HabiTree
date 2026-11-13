# HabiTree — Mobile-first web UI concept

This is a **self-contained web redesign preview** based on the `y-tuong/quangtri` branch. It intentionally uses a **portrait mobile layout on both desktop and phone**. It does not replace the Kotlin Multiplatform app or its data models.

## Open it

From the repository root run `python -m http.server 8080`, then visit **http://localhost:8080/web/**. No npm dependencies, backend, build step, remote fonts, or CDN are required.

The preview reuses original assets from `core-ui/src/main/res/drawable-nodpi/` using relative paths. Keep the repository directories together.

## Visual direction

- Cream, forest green, sage, muted gold and soft pink.
- A centered **mobile-sized app surface** up to 430px wide even on desktop.
- Large original tree art; compact supporting text, progress, and clear care indicators.
- One dominant action per view and five main destinations, with a raised Home tab.
- Consistent cards and controls across Tasks, Garden, Shop, Tree, Inventory, and Journal.
- Labeled controls, visible keyboard focus, minimum touch targets, motion preference, non-blocking toasts, and simple empty states.

## Screens

| Feature | Web preview |
| --- | --- |
| Home | Tree hero, water action, progress, daily tasks and streak |
| Tasks | Compact task cards, add-task sheet, click completion, inline sample timer |
| Garden | Five plots, water all, tree detail, planting |
| Tree | Progress, water and fertilizer care |
| Shop | Eleven genuine species, tier prices, quantity picker, sample supplies |
| Inventory | Owned seeds and consumables |
| Journal | Streak, summary, seven-day sample chart, timeline |

## Sources and decisions

- `docs/economy-and-growth-balancing.md`: price and stage model (6 / 9 / 12 forms), daily earnings cap.
- `docs/design-references/current-mics-implementation.md`: latest compact task and journal guidance, percentage labels, centered art, minimal copy, toast feedback.
- `docs/architecture/module-ownership.md`: preserve module ownership; web demo isolated from KMP modules.
- `shared/src/commonMain/kotlin/com/mamxanh/shared/model/TreeLifecycle.kt` and `GameModels.kt`: actual species, prices, and stages.
- `mics/`: existing screen mockups, composition direction, and referenced illustrations.

Original proposal text suggesting seasons, HOLD verification, or unlimited tree height is superseded by later specifications and not reused.

## Prototype limits

This is a **visual and interaction concept with sample local state**, NOT an integrated product build:

- Local sample state persists under localStorage key `habitree-design-demo-v1`. Settings → `Đặt lại` resets it.
- Shop purchases, task rewards and watering in this demo do not use KMP SQL transactions or production validation.
- The inline timer is a sample. It does not guarantee elapsed-time recovery, scheduling windows, notifications, or clock-change protection.
- Growth, permanent tree death, sale/removal, fertilizer cooldowns, and plant-stage time calculation are deliberately **not simulated** here.
- The shop displays all eleven real seed species but only two sample care cards. A production implementation needs the actual complete care and cosmetic catalogs.
- The seven-day Journal bars are illustrative.
- No existing Kotlin/Compose UI is modified. The web concept can be evaluated before any native redesign.

## Manual review

1. Visit all five tabs with the bottom navigation; keep the view portrait-shaped on desktop.
2. Add and complete a Click task; verify small reward feedback.
3. Start and pause a timer in a task row.
4. Buy a seed, open Garden, and plant it in a free plot.
5. Water a tree and use Water All; verify the state changes.
6. Inspect 320px and 390px viewport widths, keyboard focus, and reduced-motion setting.
7. Refresh to test local state persistence; reset it through Settings.

For a production web port, adapt the KMP contracts. Do not create a competing JavaScript source of truth for economy or lifecycle rules.