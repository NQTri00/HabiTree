package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavigator
import com.mamxanh.app.feature.home.presentation.ui.home.HomeTreeState
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopPendingAction
import org.json.JSONObject

@Stable internal class BioController(
    initialRoute: String = "home",
    initialParam: String = "",
    initialModal: String = "",
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
    val preview by lazy(LazyThreadSafetyMode.NONE) { BioPreviewState() }
    var pendingTreeTab by mutableStateOf<String?>(null)
    var pendingShopTab by mutableStateOf<String?>(null)
    var message by mutableStateOf("")
    var modalMessage by mutableStateOf("")
    var busy by mutableStateOf(false)
    var previewPurchase by mutableStateOf<ShopPendingAction?>(null)
    var preferredPlantingPlot by mutableStateOf<Int?>(null)
    var accessoryTargetArgument by mutableStateOf("")
        private set

    fun browseTree(
        from: String,
        to: String,
    ) {
        if (route == "tree" && param == from) param = to
    }

    fun openSeedPicker(plot: Int?) {
        preferredPlantingPlot = plot
        if (world?.native != true) preview.plantingSelection.select(plot?.toString().orEmpty())
        show("seed-picker")
    }

    fun openAccessory(
        sku: String,
        target: String,
    ) {
        if (world?.native == true) {
            accessoryTargetArgument = target.ifBlank { accessoryTargetArgument.ifBlank { homeTreeState.selectedId } }
        } else {
            preview.shopSelection.targetTree = target.ifBlank { preview.shopSelection.targetTree.ifBlank { homeTreeState.selectedId } }
        }
        show("accessory", sku)
    }

    fun resetShopContext() {
        preferredPlantingPlot = null
        accessoryTargetArgument = ""
        if (world?.native != true) {
            preview.shopSelection.targetTree = ""
            preview.plantingSelection.clear()
        }
    }

    var plantingBusy: (() -> Boolean)? = null
    var shopBuying: (() -> Boolean)? = null
    var dismissModal: (() -> Unit)? = null
    var selectLanguage: (String) -> Unit = {}
    var requestNotificationPermission: () -> Unit = {}
    var openNotificationSettings: () -> Unit = {}
    var taskFormNavigation: TaskFormNavigation? = null
    private val taskFormDirty: Boolean
        get() = if (world?.native == true) taskFormNavigation?.isDirty?.invoke() == true else preview.taskDraft.isDirty
    private val taskFormBusy: Boolean get() = taskFormNavigation?.isBusy?.invoke() == true
    var pendingRoute by mutableStateOf("tasks")
    var pendingParam by mutableStateOf("")
    var pendingBack by mutableStateOf(false)
    var pendingRoot by mutableStateOf(false)
    private var pendingDiscardTreeTab: String? = null
    var world: BioWorld? = null

    var onLeaveFocus: (() -> Unit)? = null

    private fun pauseFocus() {
        if (route == "focus") onLeaveFocus?.invoke()
    }

    private fun saved(): String =
        json(
            "route" to route,
            "param" to param,
            "modal" to modal,
            "modalId" to modalId,
            "treeTabRequest" to pendingTreeTab.orEmpty(),
            "shopTabRequest" to pendingShopTab.orEmpty(),
            "homeTree" to homeTreeState.selectedId,
            "targetTree" to accessoryTargetArgument,
            "plot" to preferredPlantingPlot?.toString().orEmpty(),
            "pendingRoute" to pendingRoute,
            "pendingParam" to pendingParam,
            "pendingBack" to pendingBack,
            "pendingRoot" to pendingRoot,
            "pendingDiscardTreeTab" to pendingDiscardTreeTab.orEmpty(),
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
                        accessoryTargetArgument = s.s("targetTree")
                        preferredPlantingPlot = s.s("plot").toIntOrNull()
                        pendingRoute = s.s("pendingRoute", "tasks")
                        pendingParam = s.s("pendingParam")
                        pendingBack = s.b("pendingBack")
                        pendingRoot =
                            s.b("pendingRoot")
                        pendingDiscardTreeTab = s.s("pendingDiscardTreeTab").ifBlank { null }
                        val savedTabs = s.o("tabs")
                        pendingTreeTab = s.s("treeTabRequest", savedTabs.s("tree")).ifBlank { null }
                        pendingShopTab = s.s("shopTabRequest", savedTabs.s("shop")).ifBlank { null }
                        val entries = s.a("history")
                        repeat(entries.length()) {
                            val entry = entries.getJSONArray(it)
                            destinations.add(it, BioDestination(entry.optString(0), entry.optString(1)))
                        }
                    }
                }.getOrElse { BioController() }
            })
    }

    fun openAccessoryShop(tree: String = "") {
        openTreeShop("accessories", tree.ifBlank { homeTreeState.selectedId })
    }

    fun openCareShop(tree: String) {
        openTreeShop("care", tree)
    }

    private fun openTreeShop(
        tab: String,
        tree: String,
    ) {
        requestShopTab(tab)
        if (world?.native == true) accessoryTargetArgument = tree else preview.shopSelection.targetTree = tree
        go("shop", tree)
    }

    fun requestShopTab(tab: String) {
        if (world?.native == true) pendingShopTab = tab else preview.shopSelection.shopTab = tab
    }

    fun go(
        next: String,
        id: String = "",
        root: Boolean = false,
        treeTab: String? = null,
    ) {
        if (route == "task-new" && taskFormBusy) return
        if (route == "task-new" &&
            taskFormDirty &&
            (next != route || id != param)
        ) {
            pendingRoute = next
            pendingParam = id
            pendingBack = false
            pendingRoot = root
            pendingDiscardTreeTab = treeTab
            show("discard")
            return
        }
        if (next != route)pauseFocus()
        if (world?.native != true && next == "focus" && id.isNotBlank() && (route != "focus" || id != param)) {
            preview.taskSelection.clearFocus()
        }
        navigator.goTo(BioDestination(next, id), replaceStack = root)
        modal = ""
        message = ""
        if (next == "tree") {
            if (world?.native == true) pendingTreeTab = treeTab ?: "care" else preview.treeDetail.open(treeTab)
        }
        if (world?.native != true && next == "task-new") preview.clearFields()
    }

    fun back() {
        if (busy || (route == "task-new" && taskFormBusy) || plantingBusy?.invoke() == true || shopBuying?.invoke() == true) return
        if (modal.isNotBlank()) {
            val dismiss = dismissModal
            if (dismiss != null) dismiss() else modal = ""
            return
        }
        if (route == "task-new" && taskFormDirty) {
            val previous = navigator.previous
            pendingRoute = previous?.route?.id ?: "tasks"
            pendingParam = previous?.argument.orEmpty()
            pendingBack = true
            pendingRoot = false
            pendingDiscardTreeTab = null
            show("discard")
            return
        }
        pauseFocus()
        navigator.goBack(BioDestination(BioRoute.HOME))
    }

    fun discard() {
        if (world?.native == true) {
            taskFormNavigation?.discard?.invoke()
        } else {
            preview.clearFields()
        }
        modal = ""
        if (pendingBack) {
            navigator.goBack(BioDestination(pendingRoute, pendingParam))
        } else {
            if (!pendingRoot) navigator.goBack(BioDestination(BioRoute.HOME))
            go(pendingRoute, pendingParam, root = pendingRoot, treeTab = pendingDiscardTreeTab)
        }
        pendingBack = false
        pendingRoot = false
        pendingDiscardTreeTab = null
    }

    fun show(
        type: String,
        id: String = "",
    ) {
        modal = type
        modalId = id
        modalMessage = ""
        if (world?.native != true) preview.shopSelection.updateQuantity(1)
        message = ""
    }

    fun result(result: BioResult) {
        busy = false
        previewPurchase = null
        if (result.success) {
            modal = ""
            if (result.route.isNotBlank()) {
                if (world?.native != true && route == "task-new") {
                    preview.clearFields()
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

    fun selectPreviewTree(id: String) {
        if (world?.native == true) return
        homeTreeState.selectedId = id
        modal = ""
    }

    fun browsePreviewTree(forward: Boolean) {
        if (world?.native == true) return
        val ids = world?.treeIds.orEmpty()
        val current = ids.indexOf(param).coerceAtLeast(0)
        if (ids.isNotEmpty()) {
            param = ids[(current + (if (forward) 1 else ids.size - 1)) % ids.size]
        }
    }
}
