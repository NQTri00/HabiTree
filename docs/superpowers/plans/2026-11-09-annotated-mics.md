# Annotated Mics implementation plan

Authority: mics/01-home.png through 09-planting.png, inspected individually on 2026-11-09. These annotated state sheets extend the three simple composite references. User requests 100% similarity, superseding the earlier 90% target. No acceptance percentage is established yet.

Execute inline using executing-plans. Existing authorization: no questions, preserve data/economy, no new automated test infrastructure; verify builds, real dialogs and emulator interactions. Existing MVI, Koin/Ktor and Android/iOS shared persistence remain in scope.

## Rulings and dependencies
- Annotated sheets specify behavior as well as artwork. Listing files does not count as reviewing them.
- Static mockup prices do not override the existing catalog. Plot-unlock price X is explicitly undecided. Ruling: use GardenPlotPolicy.UNLOCK_PRICE = 50 as the named implementation default, with five initial unlocked plots. This is an implementation choice, not a value supplied by the reference; changing it later changes unlock affordability. Existing catalog prices remain untouched.
- Growth sheet is proposed artwork; static plants remain the existing instruction until the GIF is actually inspected. IDE reader cannot serialize tree growth.gif; inspect frames through an available image-capable path before designing animation.
- Garden slots require shared persisted schema migration, repository models, Garden MVI, planting selection and statistics checks. Implement data semantics before connecting slot buttons.
- A build proves compilation; native CI already proves both iOS framework links at 30d65ee. Neither proves visual acceptance or iOS runtime.

## 1. Add Task modes and dismissal
Files: tasks/.../dialog/AddTaskDialog.kt; tasks contract/ViewModel only if save feedback needs changes.
- [ ] Close X, Cancel, outside dismissal and Back use one dirty-form guard.
- [ ] Clean form closes directly; modified/incomplete draft opens “Bỏ thay đổi?” with “Tiếp tục sửa” preserving every field and “Bỏ thay đổi” closing the form.
- [ ] Title blank shows “Thêm tên việc”; reward outside 1..100 shows an inline error. Never silently replace/clamp invalid input at Save.
- [ ] DAILY/HABIT show repeat; TODO shows optional due date.
- [ ] Form body scrolls with keyboard; Save/Cancel stay visible. Draft and confirmation survive recreation.
- [ ] Save valid form once, parent list remains positioned; feedback matches success state.
Verification: normal and 1.3 scale, blank/invalid/valid, Back and both confirmation choices, keyboard open, rotate, build.

## 2. Tasks extended document
Files: TasksScreen.kt and existing task MVI.
- [ ] Fixed Add action and destination bar; journal rows scroll.
- [ ] Completion stays in the same row position, displays check and +reward feedback; inspect existing sorting before changing.
- [ ] Completed section/count, empty-filter illustration + “Xem tất cả”, claimed daily reward disabled.
- [ ] Preserve filters and list position after add/delete/complete/recreation.
Verification: more than six rows, complete a middle row, scroll bottom, empty filter, eligible/claimed reward.

## 3. Shop and Seed Detail
Files: ShopScreen.kt, seed detail surface and Shop MVI.
- [x] Shelves/products scroll together; wallet and destination bar fixed.
  Evidence: :app:assembleDebug passed (14s), installed on emulator-5554. Real scroll moves cherry label from y880 to y417 while wallet and navigation stay visible. Opening cherry detail and closing with Back returns the label to identical [126,417][286,455] bounds. No balance/inventory mutation occurred. Selection now stores catalog id with rememberSaveable; runtime recreation and purchase-return checks remain open. Insufficient detail message receives the actual missing coin amount, pending zero-funds runtime check.
- [ ] All existing catalog entries reachable; reference eight-plant ordering is visual guidance, preserve actual prices.
- [ ] Seed detail X and actions fixed while long description scrolls; backdrop dimmed.
- [ ] Success adds exactly one seed, debits once, returns to identical shelf offset; failure returns there too.
- [ ] Insufficient balance disables purchase and shows missing amount.
Verification: mid-list item, repeat purchase, zero balance, long description, close/back, recreation.

## 4. Persisted Garden plots
Files: shared common models/sql/schema/repos, Android/iOS adapters as necessary, Garden contract/ViewModel/screen, planting surface.
- [ ] Add stable plot identity and tree-to-plot association; migrate existing trees without loss.
- [ ] Initially five unlocked plots, including explicit empty plots; numbered labels. Existing gardens with more trees retain all trees.
- [ ] Fixed nursery header/counts and nav; entire landscape (tree/soil/path/labels) scrolls together.
- [ ] Empty plot opens planting for that plot; locked plot opens unlock confirmation, current balance/price, disabled insufficient state.
- [ ] Unlock transaction debits once and reveals empty plot in place; never auto-plants; cancel preserves scroll.
- [ ] Plant consumes one seed only on successful assignment, returns Garden at same offset with local reveal. No available plot consumes nothing and offers locked-plot route; empty seed bag offers Shop.
Verification: fresh five plots, populated migration, unlock cancel/success/insufficient, no-slot planting, restart persistence and both platform compilation.

2026-11-09 plot checkpoint: shared schema v3 adds garden_plots with unique tree association, assigns existing trees ordered by id, preserves at least five unlocked plots and adds one locked successor. Common Garden exposes plots/balance; planting selects an empty unlocked plot transactionally and unlock uses named 50-coin policy transactionally. Android Garden MVI connects selected-plot planting, unlock and failure feedback. Header/navigation are separate from plot scrolling. Both APKs plus metadata passed (51s, then 29s after removing duplicate plaques); diff check passed. QA app v2→v3 launch succeeded, retained three trees, shows five unlocked plots, empty 04/05 and locked 06. Zero-coin unlock confirmation shows disabled Mở ô. Final plaque layout is now installed in QA. Selected plot04→one succulent seed→blank-name planting produced a fourth tree at 0/120 in plot04. Plot03 bounds remained at y411 before/after planting, proving retained scroll offset. Force-stop/restart retained four trees/five unlocked plots and plot04 assignment; opening empty plot05 then showed Bạn chưa có hạt with Shop route. Successful unlock/no-space/fresh-schema and macOS linking remain unverified; landscape artwork and themed confirmations still differ from the reference. Do not mark the plot task complete from this partial evidence.

## 5. Care journey
Files: tree screen/parts/contract/ViewModel and app destination effects.
- [ ] Tree + soil enlarge together on local entry, fixed root position; Back restores Garden offset.
- [ ] Compact dock with water/fertilizer and stock; “Trang trí & thông tin” expands a scrollable panel over fixed dimmed scenery.
  Progress: MVI SetPanelExpanded intent/state and modal panel implemented. Mutations preserve expansion via state.copy; Back collapses before leaving. :app:assembleDebug passed (23s), updated APK installed. On emulator the panel opens with dimmed scene, care dock, decoration choices and actual species/date/growth. Follow-up: replaced the fixed 280dp hero with remaining-height sizing in a constrained scene. Built and installed; emulator 360×640 confirms back/title/tree/progress/care/supplies/expansion all fit without scrolling. Large text retains a scrollable minimum scene; opening the panel resets that scene to its top. Panel height now capped at 75% to expose the title; capped-panel runtime and full reference styling remain to verify.
- [ ] Expanded panel keeps care dock reachable, decoration choices and tree species/planted date/growth.
- [ ] Watered today checked; low water links Home pump; no fertilizer links Shop.
Verification: compact/expanded/back, normal/large/short, water 50ml debit +growth, fertilizer, decoration persistence, unavailable routes.

## 6. Journal document
Files: StatsScreen.kt and chart.
- [x] Fixed header/nav; notebook sections scroll. Weekly grid and local-storage footer at bottom.
  Evidence: :app:assembleDebug passed (17s), installed APK on emulator-5554. Header bounds remain [147,203][602,261] before and after a real upward swipe; weekly section and Số liệu được lưu trên thiết bị footer become visible while the selected Journal navigation remains fixed. Zero-history footer now uses the annotated encouragement text. Remaining visual/large-text/refresh acceptance is not implied.
- [ ] Refresh retains screen and chart, updates data without navigation reset.
- [ ] True zero history, large numbers/wrapped labels without changing chart scale.
Verification: zero/populated, long values, refresh and same scroll, 1.3 scale.

## 7. Home stable hub
Files: HomeScreen.kt and shared scene/sign components.
- [ ] One featured tree only; no infinite canvas or additional garden trees.
- [ ] Match arched wood title/sprout, wallet, curved heading, hero/name and today-task count plaque; destination signposts and compact dock.
- [ ] Empty ground + “Gieo mầm đầu tiên”; daily-clear popup with current reward.
- [ ] Short crossfade on destination signs, fixed nav, reachable controls at short height.
Verification: populated/empty/daily-clear, return from Garden/Shop/Tasks, short and normal reference viewport. Current hero geometry edit is built but remains visually unverified.

## 8. Complete visual audit
- [ ] Inspect GIF frames and record frame/root behavior.
- [ ] Capture each of six screens and three dialogs at comparable reference geometry and state.
- [ ] Compare composition, silhouette, exact artwork, palette, typography, spacing, layering and all annotated interaction states. Record discrepancies per surface; do not invent a similarity score.
- [ ] Correct every observed difference within live-data/accessibility constraints, rerender changed surfaces sequentially.
- [ ] Full Android build, iOS links for shared changes, manual navigation/IME/recreation/persistence checks; no ANR evidence.
- [ ] Fresh final review, evidence ledger, authorized commit/push. Completion requires all above, not a passing build.

Progress: all nine PNG sheets visually inspected. GIF reader limitation recorded. Sections 1–8 remain incomplete.

2026-11-09 checkpoint: Add Task now has a close control, unified dirty dismissal guard and inline title/reward validation without silent Save clamping. Tasks Add action is outside the scrolling list; empty nonmatching filters offer Xem tất cả. :app:assembleDebug passed in 20s; updated APK installed on emulator-5554. Runtime confirms Add action visible at initial short viewport, blank Save retains form and shows Thêm tên việc, changing kind then Back opens Bỏ thay đổi?, and Tiếp tục sửa returns to the existing form with validation state retained. Full invalid-reward, IME/recreation and visual comparison matrix remains open. No task was saved during this check.
