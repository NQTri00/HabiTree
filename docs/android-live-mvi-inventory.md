# Android live MVI inventory

## Entry and result paths

`MamXanhApp` in `presentation/ui/MamXanhApp.kt` passes the storage and external destination inputs to `BioNativeHost`. The host restores `BioController.Saver`, produces the `BioWorld` snapshot, and invokes `BioApplication`. The app registers typed feature destinations through `di/FeatureNavigation.kt`. Each `NavEntry` resolves its registered content and provides the shell adapter through a composition local. The `more` route falls back to the shell's `BioMore`.

Files with the Bio prefix are under `app/src/main/java/com/mamxanh/app/presentation/bio/`. `BioApplication` chooses the live bindings below for the installed app. Feature actions dispatch to the named ViewModels. The bindings pass state to the screen and translate events into shell navigation or feedback. The host retains the platform launchers and global theme, launcher-icon and alarm inputs. Route IDs and arguments remain part of the saved navigation stack. The table records source wiring and the behavior each route needs to preserve. The installed-app reports linked below verify the task draft and Focus confirmation flows on the same revision.

## Live routes

| ID | Owner | Live binding | State producer | Actions and restoration to verify |
| --- | --- | --- | --- | --- |
| home | Home | BioHomeTaskRoutes.liveHomeRouteContent | HomeViewModel, TaskListViewModel, TreeViewModel | Home-tree preference, next-task completion, care and rewards |
| onboard | Home | BioOnboarding.liveOnboardingRouteContent | OnboardingViewModel | SavedStateHandle draft and idempotent submit |
| tasks | Tasks | BioLiveTaskList.liveTaskListRouteContent | TaskListViewModel | Filter, completion, typed detail and Focus links |
| task | Tasks | BioLiveTaskDetail.liveTaskDetailRouteContent | TaskDetailViewModel | Task ID, edit, lifecycle and archive |
| task-new | Tasks | LiveTaskForm.bindLiveTaskForm | TaskFormViewModel | Draft recovery, validation, submit and dirty Back. The route supplies dirty and busy checks and a discard callback to the navigator. |
| focus | Tasks | LiveFocus.bindLiveFocus | FocusViewModel | Task argument, persisted timer, pause and end. The binding supplies the leave callback, and the modal receives the busy state and end action. |
| templates | Tasks | LiveTemplates.bindLiveTemplates | TemplatesViewModel | Selected templates and apply |
| garden | Garden | BioLiveGarden.liveGardenRouteContent | GardenViewModel | Filters, watering, unlock and planting entry |
| tree | Tree | BioLiveTree.liveTreeRouteContent | TreeViewModel | Tree ID, care, tabs and placement |
| shop | Shop | LiveShop.bindLiveShop | ShopViewModel | Tab, quantity and typed purchase results |
| inventory | Inventory | BioLiveInventory.liveInventoryRouteContent | InventoryViewModel | Inventory tab, owned items and item actions |
| stats | Statistics | BioLiveStatistics.liveStatisticsRouteContent | StatsViewModel | Summary, chart and feature links |
| review | Statistics | LiveJournal.bindLiveJournal | JournalViewModel | Weekly record, chart, reflection draft and save |
| rest | Tasks | BioLiveRest.bindLiveRest | RestViewModel | Date choice, calendar and protection purchase |
| insights | Tasks | BioLiveInsights.bindLiveInsights | InsightsViewModel | Task ID, calendar period and reminder edits |
| achievements | Statistics | BioLiveAchievements.liveAchievements | AchievementsViewModel | Achievement load and feedback |
| timeline | Statistics | LiveJournal.bindLiveJournal | JournalViewModel | Memory drafts and typed related destinations |
| settings | Settings | BioLiveSettings.liveSettingsRouteContent | SettingsViewModel | Reminder edits, locale and permission outcomes |
| data | Settings | BioApplication and BioNativeHost | BackupViewModel | Typed backup operation and Android launcher outcomes |
| encyclopedia | Catalog | LiveCatalog.bindLiveCatalog | CatalogViewModel | Collection load and species selection |
| species | Catalog | LiveCatalog.bindLiveCatalog | CatalogViewModel | Species ID and Shop navigation |
| more | App shell | BioMore | BioController | Menu destinations and Back |

At 50ee00b the installed-app suite verified task draft recreation and discard, and Focus duration recreation, invalid input, cancellation and end confirmation. The full render contains 521 PNGs identical to 4ca235d. [Installed-app report](https://github.com/NQTri00/HabiTree/actions/runs/45781310293) [Compose report](https://github.com/NQTri00/HabiTree/actions/runs/45782676958)

## Preview entry points

The default manifest is `docs/bio-parity/preview-jobs.json`. The English and Vietnamese manifests are `docs/localization/preview-jobs-en.json` and `docs/localization/preview-jobs-vi.json`. Their source and preview function fields identify each standalone Compose entry. The fixtures call `BioReferenceSurface` with route and popup IDs. These fixtures exercise rendering from prepared snapshots. Live loading and command errors originate at the host and result paths above.

## Popups

`BioApplication` renders `BioModal` using `BioController.modal` and `modalId`. Unless noted below, these two fields restore the current popup and its argument. The live modal reads state from the feature bindings. The controller and reference snapshot supply fixture values for standalone previews. `BioModalFooter` invokes the confirmation callbacks supplied by the modal.

| ID | Owner | Trigger, popup state and result |
| --- | --- | --- |
| tree-picker | Home | Home emits `ChooseTree`; the Tree route can open the same picker through its Home binding. The modal lists dashboard trees and dispatches `HomeAction.SelectTree`. Home saves the preference and the active binding closes the modal on the selection event. |
| seed-picker | Garden | The shell carries the originating plot argument. The modal lists counts from the planting binding and opens planting for the chosen seed. |
| purchase | Shop | `LiveShop` supplies wallet, per-product quantity and the specific pending action. It dispatches buy, buy-and-plant, buy-and-equip or equip through `ShopViewModel`. |
| accessory | Tree, entered from Shop or Inventory | `AccessoryViewModel` reads ownership, wallet and living trees from `AccessoryRepository`, restores the selected target by SKU, and dispatches buy or equip. A direct purchase and placement commit in one database transaction. `BioLiveAccessory` closes on success and displays failures in the dialog. |
| plant | Garden | `PlantingViewModel` loads eligible plots, restores the seed, name and selected plot, and dispatches the planting command through Garden. |
| water-all | Garden | The Garden adapter opens confirmation. `BioModal` dispatches `GardenIntent.WaterAll` with an operation ID. |
| unlock | Garden | The Garden adapter passes the plot ID as `modalId`. `GardenViewModel` supplies eligibility and receives `GardenIntent.UnlockPlot`. |
| fertilizer-help | Tree | Tree care opens guidance. Footer navigates to Garden. |
| remove | Tree | The modal uses the Tree ViewModel's quote for the route's tree ID and dispatches `TreeAction.Dispose` with selling disabled. |
| sell | Tree | The modal uses the Tree ViewModel's selling quote and dispatches `TreeAction.Dispose` with selling enabled. |
| archive | Tasks | The task detail supplies its ID. The modal dispatches `TaskDetailAction.Archive` to the matching ViewModel. |
| discard | Tasks | BioController.go or back opens this for a dirty form. The footer invokes the route-supplied discard callback before following the saved pending navigation. |
| ticket | Tasks engagement | The Rest binding opens confirmation. The modal uses Rest's eligibility and dispatches `RestAction.BuyTicket`. |
| adjust | Tasks engagement | The Insights binding passes the task ID. The modal dispatches `InsightsAction.ApplyAdjustment` using that ID. |
| permission | Settings | The live Settings binding consumes the typed link event and opens the popup. Confirm dispatches SettingsAction.RequestPermission. The binding consumes the request event and invokes the Android host launcher. The fixture path opens permission-choice. |
| permission-choice | Settings | BioModalFooter opens the permission choice from the non-native path. |
| permission-settings | Settings | BioNativeHost opens this when permission requires system Settings. The host consumes the result of the system action. |
| clear | Settings | `BackupViewModel` emits the first clear prompt. The footer dispatches `BackupAction.ClearNext`. |
| clear-final | Settings | `BackupViewModel` emits the final prompt. Confirmation dispatches `BackupAction.Confirm(Clear)`; the host performs the Android storage operation. |
| recover | Settings | `BackupViewModel` emits recovery confirmation and validates the pending operation before the host invokes the recovery file adapter. |
| confirm-import | Settings | The host inspects the document and sends its summary to `BackupViewModel`. The ViewModel requests confirmation and dispatches restore to the host after the user chooses Replace. Document bytes stay in memory, and recreation requires choosing the document again. |
| license | Settings | BioForms.backupRouteContent opens the license content. Dismisses locally. |
| focus-end | Tasks engagement | The Focus action dock dispatches RequestEnd. The live binding consumes ConfirmEnd and opens the modal. The modal receives busy state and an end callback from the same binding, which dispatches FocusAction.End. |

## Per-entry preview index

Each cell identifies a representative runnable manifest method. The manifests also contain edge and adaptive variants.

| Type | ID | Default | English | Vietnamese |
| --- | --- | --- | --- | --- |
| route | home | [BioHomePreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioHomePreview.kt) | [BioHomePreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioHomePreviewEn.kt) | [BioHomePreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioHomePreviewVi.kt) |
| route | tasks | [BioTasksPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioTasksPreview.kt) | [BioTasksPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioTasksPreviewEn.kt) | [BioTasksPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioTasksPreviewVi.kt) |
| route | garden | [BioGardenPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioGardenPreview.kt) | [BioGardenPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioGardenPreviewEn.kt) | [BioGardenPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioGardenPreviewVi.kt) |
| route | shop | [BioShopPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioShopPreview.kt) | [BioShopPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioShopPreviewEn.kt) | [BioShopPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioShopPreviewVi.kt) |
| route | stats | [BioStatsPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioStatsPreview.kt) | [BioStatsPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioStatsPreviewEn.kt) | [BioStatsPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioStatsPreviewVi.kt) |
| route | task | [BioTaskPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioTaskPreview.kt) | [BioTaskPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioTaskPreviewEn.kt) | [BioTaskPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioTaskPreviewVi.kt) |
| route | task-new | [BioTaskNewPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioTaskNewPreview.kt) | [BioTaskNewPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioTaskNewPreviewEn.kt) | [BioTaskNewPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioTaskNewPreviewVi.kt) |
| route | inventory | [BioInventoryPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioInventoryPreview.kt) | [BioInventoryPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioInventoryPreviewEn.kt) | [BioInventoryPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioInventoryPreviewVi.kt) |
| route | tree | [BioTreePreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioTreePreview.kt) | [BioTreePreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioTreePreviewEn.kt) | [BioTreePreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioTreePreviewVi.kt) |
| route | focus | [BioFocusPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioFocusPreview.kt) | [BioFocusPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioFocusPreviewEn.kt) | [BioFocusPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioFocusPreviewVi.kt) |
| route | templates | [BioTemplatesPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioTemplatesPreview.kt) | [BioTemplatesPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioTemplatesPreviewEn.kt) | [BioTemplatesPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioTemplatesPreviewVi.kt) |
| route | review | [BioReviewPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioReviewPreview.kt) | [BioReviewPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioReviewPreviewEn.kt) | [BioReviewPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioReviewPreviewVi.kt) |
| route | rest | [BioRestPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioRestPreview.kt) | [BioRestPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioRestPreviewEn.kt) | [BioRestPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioRestPreviewVi.kt) |
| route | insights | [BioInsightsPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioInsightsPreview.kt) | [BioInsightsPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioInsightsPreviewEn.kt) | [BioInsightsPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioInsightsPreviewVi.kt) |
| route | achievements | [BioAchievementsPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioAchievementsPreview.kt) | [BioAchievementsPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioAchievementsPreviewEn.kt) | [BioAchievementsPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioAchievementsPreviewVi.kt) |
| route | timeline | [BioTimelinePreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioTimelinePreview.kt) | [BioTimelinePreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioTimelinePreviewEn.kt) | [BioTimelinePreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioTimelinePreviewVi.kt) |
| route | settings | [BioSettingsPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioSettingsPreview.kt) | [BioSettingsPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioSettingsPreviewEn.kt) | [BioSettingsPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioSettingsPreviewVi.kt) |
| route | data | [BioDataPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioDataPreview.kt) | [BioDataPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioDataPreviewEn.kt) | [BioDataPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioDataPreviewVi.kt) |
| route | encyclopedia | [BioEncyclopediaPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioEncyclopediaPreview.kt) | [BioEncyclopediaPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioEncyclopediaPreviewEn.kt) | [BioEncyclopediaPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioEncyclopediaPreviewVi.kt) |
| route | species | [BioSpeciesPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioSpeciesPreview.kt) | [BioSpeciesPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioSpeciesPreviewEn.kt) | [BioSpeciesPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioSpeciesPreviewVi.kt) |
| route | onboard | [BioOnboardPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioOnboardPreview.kt) | [BioOnboardPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioOnboardPreviewEn.kt) | [BioOnboardPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioOnboardPreviewVi.kt) |
| route | more | [BioMorePreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioMorePreview.kt) | [BioMorePreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioMorePreviewEn.kt) | [BioMorePreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioMorePreviewVi.kt) |
| popup | tree-picker | [BioPopupTreePickerPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupTreePickerPreview.kt) | [BioPopupTreePickerPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupTreePickerPreviewEn.kt) | [BioPopupTreePickerPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupTreePickerPreviewVi.kt) |
| popup | seed-picker | [BioPopupSeedPickerPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupSeedPickerPreview.kt) | [BioPopupSeedPickerPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupSeedPickerPreviewEn.kt) | [BioPopupSeedPickerPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupSeedPickerPreviewVi.kt) |
| popup | purchase | [BioPopupSeedBuyPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupSeedBuyPreview.kt) | [BioPopupSeedBuyPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupSeedBuyPreviewEn.kt) | [BioPopupSeedBuyPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupSeedBuyPreviewVi.kt) |
| popup | accessory | [BioPopupAccessoryPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupAccessoryPreview.kt) | [BioPopupAccessoryPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupAccessoryPreviewEn.kt) | [BioPopupAccessoryPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupAccessoryPreviewVi.kt) |
| popup | plant | [BioPopupPlantPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupPlantPreview.kt) | [BioPopupPlantPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupPlantPreviewEn.kt) | [BioPopupPlantPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupPlantPreviewVi.kt) |
| popup | water-all | [BioPopupWaterAllPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupWaterAllPreview.kt) | [BioPopupWaterAllPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupWaterAllPreviewEn.kt) | [BioPopupWaterAllPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupWaterAllPreviewVi.kt) |
| popup | unlock | [BioPopupUnlockPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupUnlockPreview.kt) | [BioPopupUnlockPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupUnlockPreviewEn.kt) | [BioPopupUnlockPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupUnlockPreviewVi.kt) |
| popup | fertilizer-help | [BioPopupFertilizerHelpPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupFertilizerHelpPreview.kt) | [BioPopupFertilizerHelpPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupFertilizerHelpPreviewEn.kt) | [BioPopupFertilizerHelpPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupFertilizerHelpPreviewVi.kt) |
| popup | remove | [BioPopupRemovePreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupRemovePreview.kt) | [BioPopupRemovePreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupRemovePreviewEn.kt) | [BioPopupRemovePreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupRemovePreviewVi.kt) |
| popup | sell | [BioPopupSellPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupSellPreview.kt) | [BioPopupSellPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupSellPreviewEn.kt) | [BioPopupSellPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupSellPreviewVi.kt) |
| popup | archive | [BioPopupArchivePreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupArchivePreview.kt) | [BioPopupArchivePreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupArchivePreviewEn.kt) | [BioPopupArchivePreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupArchivePreviewVi.kt) |
| popup | discard | [BioPopupDiscardPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupDiscardPreview.kt) | [BioPopupDiscardPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupDiscardPreviewEn.kt) | [BioPopupDiscardPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupDiscardPreviewVi.kt) |
| popup | ticket | [BioPopupTicketPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupTicketPreview.kt) | [BioPopupTicketPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupTicketPreviewEn.kt) | [BioPopupTicketPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupTicketPreviewVi.kt) |
| popup | adjust | [BioPopupAdjustPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupAdjustPreview.kt) | [BioPopupAdjustPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupAdjustPreviewEn.kt) | [BioPopupAdjustPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupAdjustPreviewVi.kt) |
| popup | permission | [BioPopupPermissionPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupPermissionPreview.kt) | [BioPopupPermissionPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupPermissionPreviewEn.kt) | [BioPopupPermissionPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupPermissionPreviewVi.kt) |
| popup | permission-choice | [BioPopupPermissionChoicePreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupPermissionChoicePreview.kt) | [BioPopupPermissionChoicePreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupPermissionChoicePreviewEn.kt) | [BioPopupPermissionChoicePreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupPermissionChoicePreviewVi.kt) |
| popup | permission-settings | [BioPopupPermissionSettingsPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupPermissionSettingsPreview.kt) | [BioPopupPermissionSettingsPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupPermissionSettingsPreviewEn.kt) | [BioPopupPermissionSettingsPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupPermissionSettingsPreviewVi.kt) |
| popup | clear | [BioPopupClearPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupClearPreview.kt) | [BioPopupClearPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupClearPreviewEn.kt) | [BioPopupClearPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupClearPreviewVi.kt) |
| popup | clear-final | [BioPopupClearFinalPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupClearFinalPreview.kt) | [BioPopupClearFinalPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupClearFinalPreviewEn.kt) | [BioPopupClearFinalPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupClearFinalPreviewVi.kt) |
| popup | recover | [BioPopupRecoverPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupRecoverPreview.kt) | [BioPopupRecoverPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupRecoverPreviewEn.kt) | [BioPopupRecoverPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupRecoverPreviewVi.kt) |
| popup | confirm-import | [BioImportConfirmPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioImportConfirmPreview.kt) | [BioImportConfirmPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioImportConfirmPreviewEn.kt) | [BioImportConfirmPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioImportConfirmPreviewVi.kt) |
| popup | license | [BioPopupLicensePreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupLicensePreview.kt) | [BioPopupLicensePreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupLicensePreviewEn.kt) | [BioPopupLicensePreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupLicensePreviewVi.kt) |
| popup | focus-end | [BioPopupFocusEndPreview](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/BioPopupFocusEndPreview.kt) | [BioPopupFocusEndPreviewEn](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupFocusEndPreviewEn.kt) | [BioPopupFocusEndPreviewVi](../app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization/BioPopupFocusEndPreviewVi.kt) |

## Current live binding audit

The table records the implementation path from the registered destination through the feature ViewModel to the shell event consumer. The earlier inventory records the pre-migration paths and preview fixtures. Runtime and rendered evidence remain pending.

The live shell keeps a typed tree index for theme, launcher icon, navigation, and reminder refresh. Growth-stage content, accessory ownership, and decoration no longer pass through a shell JSON tree projection.

A source scan across all nine features checked 291 Kotlin files for sibling-feature imports and domain imports of platform, shared-storage, or outer-layer packages. It found no violations. Gradle configuration and dependency resolution remain part of the integrated `verifyArchitecture` run.

Gradle 9.5.1 reached project configuration with the workspace-local writable cache. The first `ktlintCheck verifyArchitecture` attempt exited before task execution because its read-only dependency cache lacked the pinned ktlint plugin marker. The marker for 14.2.0 was found in the Android build runtime's separate cache. The retry using that cache was interrupted before task execution when available RAM fell below 1 GB. No formatter or architecture task result was produced.

A bounded standalone launch of the cached ktlint 1.5.0 CLI reached its entry point but failed because the assembled classpath lacked Clikt's JVM runtime. This probe produced no formatting evidence.

| Route | Live ViewModel and action path | Remaining shared data or host contract |
| --- | --- | --- |
| home | HomeViewModel loads the dashboard, resolves the selected tree against its own tree records and the saved preference, and supplies the displayed growth and care data. TaskListViewModel and TreeViewModel execute the two cross-feature mutations. | The Tree accessory catalog supplies the equipped decoration. The shell applies the shared theme preference. |
| onboard | OnboardingViewModel restores choices and submits through OnboardingRepository. The host refreshes and consumes Finished. | Static seed and template assets and the app-wide onboarding-complete flag. |
| tasks | TaskListViewModel restores the filter and loads TaskRepository. The route consumes navigation and feedback events. | Shared reward component. |
| task | TaskDetailViewModel loads by destination argument and handles completion, archive, and lifecycle changes. | Archive sheet dispatches to the same keyed model. |
| task-new | TaskFormViewModel loads defaults or the task, validates draft, and saves through TaskRepository. | Navigator checks dirty state and consumes Saved. |
| focus | FocusViewModel owns the controls and draft, and EngagementViewModel owns session reads, ticks, and recovery. | Host lifecycle and confirmation sheet. |
| templates | TemplatesViewModel restores selected template IDs and applies them through TaskRepository. | Static template assets. |
| garden | GardenViewModel owns plot display, filter, and mutations. LivingGardenViewModel provides the collection count. | Host opens Planting with an optional originating plot. |
| tree | TreeViewModel owns the tab and care, disposition, unequip, and adjacent-tree navigation. | Tree detail and hero load through TreeDetailRepository. The shell replaces the current tree argument on a browsing event. Accessory cards read typed ownership and equipment from the Tree accessory catalog. |
| shop | ShopViewModel owns category, quantity, pending checkout, and purchase outcomes. | The Shop wallet supplies balances, stock, equipped cans, and confirmation eligibility. Accessory ownership and equipment come from the Tree accessory catalog. Artwork comes from the static catalog. |
| inventory | InventoryViewModel owns category and quantities, equips watering cans, and emits planting and accessory intents. | Static catalog artwork and typed navigation context. |
| stats | StatsViewModel loads current statistics and emits links and refresh feedback. | Shared date labels. |
| review | JournalViewModel loads the review and chart through WeeklyReviewRepository and owns the draft and save. | Shared date labels. |
| rest | RestViewModel loads the calendar and ticket affordability and owns both date selections and ticket operations. | Shell opens and closes the confirmation in response to feature events. |
| insights | InsightsViewModel loads task and calendar records, saves reminders, and adjusts schedules. | Shell consumes the feature's destination and confirmation events. |
| achievements | AchievementsViewModel loads feature records. | Read-only screen and shell Back. |
| timeline | JournalViewModel loads memories and owns drafts, notes, and related links. | Host converts timestamp and typed entity links for display and navigation. |
| settings | SettingsViewModel loads saved preferences and owns draft and permission actions. | Host handles language and OS permission integration. |
| data | BackupViewModel owns operation and confirmation state and receives typed outcomes. | Host owns Android file operations and the inspected backup payload, and returns typed metadata to the feature. The feature restores the pending picker request until Android returns a document or cancellation. |
| encyclopedia | CatalogViewModel loads discovery and goal data and owns species navigation. | Static catalog assets. |
| species | CatalogViewModel loads collection records and typed tree cards for the active species, and emits tree and Shop links. | Static species descriptions and artwork come from the catalog assets. |
| more | Shell menu opens registered destinations. | Shell-only. |

| Popup | Live owner and confirmation consumer |
| --- | --- |
| tree-picker | HomeViewModel supplies and persists picker choices from both Home and Tree. The shell consumes the resulting selection event to update the shared theme and close the picker. |
| seed-picker | Garden owns eligible plot selection, and the shell opens Planting or Shop. PlantingViewModel loads seed counts and eligible plots through GardenRepository. |
| purchase | ShopViewModel owns quantity, checkout, pending labels, and planting plot availability. |
| accessory | AccessoryViewModel loads ownership and eligible living trees, persists the selected tree, and delegates purchase and equip to TreeRepository. |
| plant | PlantingViewModel loads plots, restores draft, and submits through GardenRepository. |
| water-all | GardenViewModel supplies the watering counts and executes bulk watering. |
| unlock | GardenViewModel supplies the price, balance, and confirmation eligibility and executes unlock. |
| fertilizer-help | Tree guidance opens Garden, with no mutation. |
| remove | TreeViewModel executes removal. |
| sell | TreeViewModel quotes and executes sale. |
| archive | TaskDetailViewModel executes archive. |
| discard | TaskFormViewModel clears the draft. The navigator consumes the saved destination. |
| ticket | RestViewModel submits the ticket purchase. |
| adjust | InsightsViewModel saves the schedule change. |
| permission | SettingsViewModel emits the request and receives the Android result. |
| permission-choice | Preview fallback. The live Android permission launcher owns the platform prompt. |
| permission-settings | SettingsViewModel emits the typed action that the host handles. |
| clear | BackupViewModel advances to final confirmation. |
| clear-final | BackupViewModel requests clear and consumes the host outcome. |
| recover | BackupViewModel requests recovery and consumes the host outcome. |
| confirm-import | BackupViewModel owns the summary and confirmation. The host owns the inspected payload. |
| license | BackupViewModel requests the read-only license sheet. |
| focus-end | FocusViewModel ends the active session through FocusSessionCommands. |
