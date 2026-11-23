package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavigator
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenBrowseState
import com.mamxanh.app.feature.garden.presentation.ui.garden.PlantingSelectionState
import com.mamxanh.app.feature.home.presentation.ui.home.HomeTreeState
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingSelectionState
import com.mamxanh.app.feature.settings.presentation.SettingsRequestState
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopSelectionState
import com.mamxanh.app.feature.tasks.presentation.ui.form.TaskDraftState
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskScreenSelection
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeDetailSelection
import org.json.JSONObject

@Stable internal class BioController(
    initialRoute: String = "home",
    initialParam: String = "",
    initialModal: String = "",
    var execute: (BioCommand) -> Unit = {
    },
) {
    private val destinations = mutableStateListOf(BioDestination(initialRoute, initialParam))
    private val navigator = HabiTreeNavigator(destinations)
    val backStack: List<BioDestination> get() = navigator.backStack
    val route: String get() = navigator.current.route.id
    var param: String
        get() = navigator.current.argument
        private set(value) {
            navigator.replaceCurrent(navigator.current.copy(argument = value))
        }
    var modal by mutableStateOf(initialModal)
    var modalId by mutableStateOf("")
    val homeTreeState = HomeTreeState()
    val treeDetail = TreeDetailSelection()
    var message by mutableStateOf("")
    var modalMessage by mutableStateOf("")
    var busy by mutableStateOf(false)
    var pendingAction by mutableStateOf<String?>(null)
        private set
    val shopSelection = ShopSelectionState()
    val plantingSelection = PlantingSelectionState()
    val gardenBrowse = GardenBrowseState()
    val onboarding = OnboardingSelectionState()
    val taskDraft = TaskDraftState()
    val taskSelection = TaskScreenSelection()
    val settingsRequests = SettingsRequestState()
    var pendingRoute by mutableStateOf("tasks")
    var pendingParam by mutableStateOf("")
    var pendingBack by mutableStateOf(false)
    var pendingRoot by mutableStateOf(false)
    var world: BioWorld? = null
    val tabs = mutableStateMapOf("insightDays" to "7")
    val fields = mutableStateMapOf<String, String>()

    private fun pauseFocus() {
        if (route == "focus" && world?.state?.o("focus")?.s("state") == "RUNNING")send("focus-pause")
    }

    private fun saved(): String =
        json(
            "route" to route,
            "param" to param,
            "modal" to modal,
            "modalId" to modalId,
            "homeTree" to homeTreeState.selectedId,
            "quantity" to shopSelection.quantity,
            "targetTree" to shopSelection.targetTree,
            "plot" to plantingSelection.plotKey,
            "step" to onboarding.step,
            "seed" to onboarding.seed,
            "category" to onboarding.category,
            "templates" to org.json.JSONArray(onboarding.selectedTemplates),
            "dirty" to taskDraft.isDirty,
            "pendingRoute" to pendingRoute,
            "pendingParam" to pendingParam,
            "pendingBack" to pendingBack,
            "pendingRoot" to pendingRoot,
            "tabs" to
                JSONObject(
                    tabs.toMap() +
                        mapOf(
                            "tree" to treeDetail.tab,
                            "shop" to shopSelection.shopTab,
                            "inventory" to shopSelection.inventoryTab,
                            TaskScreenSelection.FILTER_KEY to taskSelection.filter,
                            GardenBrowseState.SAVED_FILTER_KEY to gardenBrowse.filter,
                        ),
                ),
            "fields" to
                JSONObject(
                    fields.toMap() + taskDraft.savedValues() + taskSelection.savedFields() + settingsRequests.savedFields(),
                ),
            "history" to
                org.json.JSONArray().apply {
                    destinations
                        .dropLast(
                            1,
                        ).forEach { put(org.json.JSONArray(listOf(it.route.id, it.argument))) }
                },
        ).toString()

    companion object {
        val Saver =
            Saver<BioController, String>(save = { it.saved() }, restore = { raw ->
                runCatching {
                    val s = JSONObject(raw)
                    BioController(s.s("route", "home"), s.s("param"), s.s("modal").takeUnless { it == "confirm-import" }.orEmpty()).apply {
                        modalId = s.s("modalId")
                        homeTreeState.selectedId = s.s("homeTree", "0")
                        shopSelection.updateQuantity(s.i("quantity", 1))
                        shopSelection.targetTree =
                            s.s("targetTree")
                        plantingSelection.select(s.s("plot"))
                        onboarding.step = s.i("step")
                        onboarding.seed = s.s("seed", OnboardingSelectionState.DEFAULT_SEED)
                        onboarding.category =
                            s.s("category", OnboardingSelectionState.defaultCategory())
                        onboarding.restoreTemplates(s.a("templates").strings())
                        pendingRoute = s.s("pendingRoute", "tasks")
                        pendingParam = s.s("pendingParam")
                        pendingBack = s.b("pendingBack")
                        pendingRoot =
                            s.b("pendingRoot")
                        val savedTabs = s.o("tabs")
                        savedTabs.keys().forEach { setTab(it, savedTabs.s(it)) }
                        val savedFields = s.o("fields")
                        restoreFields(savedFields.keys().asSequence().associateWith { savedFields.s(it) }, s.b("dirty"))
                        val entries = s.a("history")
                        repeat(entries.length()) {
                            val entry = entries.getJSONArray(it)
                            destinations.add(it, BioDestination(entry.optString(0), entry.optString(1)))
                        }
                    }
                }.getOrElse { BioController() }
            })
    }

    fun setTab(
        key: String,
        value: String,
    ) {
        when (key) {
            TaskScreenSelection.FILTER_KEY -> taskSelection.filter = value
            GardenBrowseState.SAVED_FILTER_KEY -> gardenBrowse.filter = value
            "tree" -> treeDetail.tab = value
            "shop" -> shopSelection.shopTab = value
            "inventory" -> shopSelection.inventoryTab = value
            else -> tabs[key] = value
        }
    }

    fun restoreFields(
        values: Map<String, String>,
        dirty: Boolean = false,
    ) {
        fields.putAll(settingsRequests.restoreFields(taskSelection.restoreFields(taskDraft.restore(values, dirty))))
    }

    private fun clearFields() {
        taskDraft.clear()
        taskSelection.clearFields()
        settingsRequests.clear()
        fields.clear()
    }

    fun go(
        next: String,
        id: String = "",
        root: Boolean = false,
        treeTab: String? = null,
    ) {
        if (route == "task-new" &&
            taskDraft.isDirty &&
            next != "task-new"
        ) {
            pendingRoute = next
            pendingParam = id
            pendingBack = false
            pendingRoot = root
            show("discard")
            return
        }
        if (next != route)pauseFocus()
        if (next == "focus" && id.isNotBlank() && (route != "focus" || id != param))taskSelection.clearFocus()
        navigator.goTo(BioDestination(next, id), replaceStack = root)
        modal = ""
        message = ""
        if (next == "tree")treeDetail.open(treeTab)
        if (next == "task-new")clearFields()
    }

    fun back() {
        if (busy) return
        if (modal.isNotBlank()) {
            modal = ""
            return
        }
        if (route == "task-new" && taskDraft.isDirty) {
            val previous = navigator.previous
            pendingRoute = previous?.route?.id ?: "tasks"
            pendingParam = previous?.argument.orEmpty()
            pendingBack = true
            pendingRoot = false
            show("discard")
            return
        }
        pauseFocus()
        navigator.goBack(BioDestination(BioRoute.HOME))
    }

    fun discard() {
        clearFields()
        modal = ""
        if (pendingBack) {
            navigator.goBack(BioDestination(pendingRoute, pendingParam))
        } else {
            if (!pendingRoot) navigator.goBack(BioDestination(BioRoute.HOME))
            go(pendingRoute, pendingParam, root = pendingRoot)
        }
        pendingBack = false
        pendingRoot = false
    }

    fun show(
        type: String,
        id: String = "",
    ) {
        modal = type
        modalId = id
        modalMessage = ""
        shopSelection.updateQuantity(1)
        message = ""
    }

    fun send(
        action: String,
        values: Map<String, String> = emptyMap(),
    ) {
        if (!busy)execute(BioCommand(action, values))
    }

    fun result(result: BioResult) {
        busy = false
        pendingAction = null
        if (result.success) {
            modal = ""
            if (result.route.isNotBlank()) {
                if (route ==
                    "task-new"
                ) {
                    clearFields()
                }
                go(result.route, result.param, root = result.route in listOf("home", "tasks", "garden", "shop", "stats"))
            }
            if (result.message.isNotBlank()) {
                message =
                    result.message
            }
        } else if (modal.isNotBlank()) {
            modalMessage = result.message
        } else {
            message = result.message
        }
    }

    fun startCommand(action: String) {
        busy = true
        pendingAction = action
    }

    fun act(action: String) {
        val parts = action.split(':')
        val name = parts[0]
        val id = parts.getOrElse(1) { "" }
        val value = parts.getOrElse(2) { "" }
        when (name) {
            "go" -> go(id, value)
            "tab" -> setTab(id, value)
            "back" -> back()
            "bio-picker" -> show("tree-picker")
            "bio-pin" -> {
                homeTreeState.selectedId = id
                modal = ""
                send("pin", mapOf("id" to id))
            }
            "bio-tree-decor" -> go("tree", id, treeTab = "decor")
            "bio-accessory-shop" -> {
                shopSelection.shopTab = "accessories"
                shopSelection.targetTree = id.ifBlank { homeTreeState.selectedId }
                go("shop", shopSelection.targetTree)
            }
            "bio-seed-picker" -> {
                plantingSelection.select(id)
                show("seed-picker")
            }
            "bio-shop-seeds" -> {
                shopSelection.shopTab = "seeds"
                go("shop")
            }
            "shop-care" -> {
                shopSelection.shopTab = "care"
                shopSelection.targetTree = param
                go("shop", shopSelection.targetTree)
            }
            "purchase" -> show("purchase", id)
            "accessory" -> {
                shopSelection.targetTree = value.ifBlank { shopSelection.targetTree.ifBlank { homeTreeState.selectedId } }
                show("accessory", id)
            }
            "plant" -> {
                plantingSelection.select(value)
                show("plant", id)
            }
            "unlock",
            "sell",
            "remove",
            "archive",
            "water-all",
            "ticket",
            "adjust",
            "permission",
            "recover",
            "clear",
            "license",
            "focus-end",
            "fertilizer-help",
            ->
                show(
                    name,
                    id,
                )
            "tree-prev", "tree-next" -> {
                val trees = world?.trees.orEmpty()
                val i = trees.indexOfFirst { it.s("id") == param }.coerceAtLeast(0)
                if (trees.isNotEmpty()) {
                    param =
                        trees[(i + (if (name == "tree-next")1 else trees.size - 1)) % trees.size].s("id")
                }
            }
            "focus-task" -> go("focus", id)
            else -> send(action)
        }
    }
}
