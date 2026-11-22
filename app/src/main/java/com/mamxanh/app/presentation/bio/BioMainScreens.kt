package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import com.mamxanh.core.ui.R
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.feature.shop.presentation.navigation.ShopRouteContent
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopTreeContext
import com.mamxanh.app.feature.inventory.presentation.navigation.InventoryRouteContent
import com.mamxanh.app.feature.home.presentation.navigation.HomeRouteContent
import com.mamxanh.app.feature.home.presentation.ui.home.HomeScreenAction
import com.mamxanh.app.feature.home.presentation.ui.home.HomeReward
import com.mamxanh.app.feature.home.presentation.ui.home.HomeScreenState
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListAction
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListItem
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListRow
import com.mamxanh.app.feature.tasks.presentation.navigation.TaskListRouteContent
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListState
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppIcons
import com.mamxanh.app.core.components.design.AppOpacity
import com.mamxanh.app.core.components.design.AppRadii
import com.mamxanh.app.core.components.design.AppSpacing
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.localization.AppText
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import org.json.JSONObject

internal fun treeState(t:JSONObject)=when(t.s("state")){"dead"->AppText.text(TextKey.CopyDaKhoHeoA9c1b9);"mature"->AppText.text(TextKey.CopyTruongThanh8c14d7);else->AppText.text(TextKey.CopyDangLon40daab)}
internal fun taskReward(t:JSONObject)=if(t.s("verification")=="TIMER")when{t.i("minutes")>=45->20;t.i("minutes")>=20->15;else->10}else 8
internal fun growthCaption(t:JSONObject)=when(t.s("state")){"dead"->AppText.text(TextKey.CopyMamXanhDaDungLai6dabc6);"mature"->AppText.text(TextKey.CopyDaTruongThanh54f165);else->AppText.text(TextKey.CopyDangLonCungBan6913c2)}
internal fun accessoryIcon(id:String)=when(id){"tree_ribbon"->"gift";"tree_lantern"->"sun";else->"spark"}

@Composable
private fun BioReward(world: BioWorld, ui: BioController) {
    HomeReward(world.state.b("claimed"), world.data.o("phase").b("canClaim")) { ui.send("claim") }
}

@Composable internal fun BioProduct(world:BioWorld,ui:BioController,item:JSONObject,modifier:Modifier=Modifier,inventory:Boolean=false,target:String="",encyclopedia:Boolean=false){
    val id=item.s("id");val seed=world.species.any{it.s("id")==id};val accessory=item.s("category")=="TREE";val owned=(if(inventory)world.inventoryCount(id, accessory) else world.owned(id))>0;val equipped=world.trees.any{it.s("decor")==id};val known=world.record(id).b("discovered")||world.record(id).i("planted")>0||world.count(id)>0
    val action={if(encyclopedia)ui.go("species",id)else if(accessory)ui.act("accessory:$id:$target")else if(inventory){if(seed)ui.act("plant:$id:${ui.plantingSelection.plotKey}")else if(item.b("permanent"))ui.send("equip:$id")else ui.show("fertilizer-help")}else ui.show("purchase",id)}
    BioCard(modifier,padding=AppSpacing.Value12,onClick=action){
        val backdrop=Brush.linearGradient(if(seed&&(!encyclopedia||known))listOf(AppColors.SwatchFFFAFAF8,bp.soft)else listOf(bp.soft,bp.soft))
        Box(Modifier.fillMaxWidth().height(if(LocalConfiguration.current.screenWidthDp<AppThemeEffects.CompactWidth)AppDimensions.Dp95 else AppDimensions.Dp112).background(backdrop,bioShape(AppRadii.Value15)),contentAlignment=Alignment.Center){if(seed&&(!encyclopedia||known))BioPlant(id,Modifier.fillMaxSize())else Bi(if(seed)"leaf"else if(accessory)accessoryIcon(id)else if(item.b("permanent"))"water"else "fertilizer",size=if(accessory)AppIcons.Size32 else AppIcons.ProductArtworkSize)}
        Spacer(Modifier.height(AppDimensions.Dp10));Bt(item.s("name"),Modifier.heightIn(min=AppDimensions.Dp36),size=AppTypography.Size13,line=AppTypography.LineHeight18_85,semi=true);Spacer(Modifier.height(AppDimensions.Dp10))
        if(encyclopedia)Bt(world.record(id).s("status",stringResource(R.string.copy_chua_kham_pha_c0b0cb)),size=AppTypography.Size11,tint=bp.muted,line=AppTypography.LineHeight15_4)
        else if(accessory){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Bt(if(equipped)stringResource(R.string.copy_dang_dung_275ca2)else if(owned)stringResource(R.string.copy_da_so_huu_822625)else stringResource(R.string.copy_0_xu_251e68, item.i("price")),Modifier.weight(1f),size=AppTypography.Size13,tint=bp.deep);Box(Modifier.size(AppDimensions.Dp27).border(AppDimensions.Dp1,bp.line,RoundedCornerShape(AppDimensions.Dp8)),contentAlignment=Alignment.Center){Bi(if(equipped)"check"else if(owned)"next"else "plus",size=AppIcons.Size14)}}}
        else{Bt(if(inventory)if(item.b("permanent"))if(world.state.s("equipped")==id)stringResource(R.string.copy_dang_dung_275ca2)else stringResource(R.string.copy_trang_bi_9e8e12)else "×${world.inventoryCount(id, accessory)}"else if(item.b("permanent")&&world.count(id)>0)if(world.state.s("equipped")==id)stringResource(R.string.copy_dang_dung_275ca2)else stringResource(R.string.copy_da_so_huu_822625)else stringResource(R.string.copy_0_xu_251e68, item.i("price")),size=AppTypography.Size13,tint=bp.deep);if(seed&&!inventory)Bt(bioTier(item.s("tier")),size=AppTypography.Size12,line=AppTypography.LineHeight17,tint=bp.muted)}
    }
}
private fun catalogProducts(world: BioWorld, tab: String): List<JSONObject> =
    when (tab) {
        "care" -> world.products
        "accessories" -> world.accessories
        else -> world.species
    }

internal fun inventoryRouteContent(world: BioWorld, ui: BioController): InventoryRouteContent {
    val tab = ui.shopSelection.inventoryTab
    val items = catalogProducts(world, tab).filter {
        world.inventoryCount(it.s("id"), accessory = tab == "accessories") > 0
    }
    val openShop = {
        ui.shopSelection.shopTab = tab
        ui.go("shop")
    }
    return InventoryRouteContent(
        tab = tab,
        itemCount = items.size,
        onTab = { ui.shopSelection.inventoryTab = it },
        onShop = openShop,
        onBuy = openShop,
        item = { index, modifier ->
            BioProduct(world, ui, items[index], modifier, inventory = true, target = ui.shopSelection.targetTree.ifBlank { ui.homeTreeState.selectedId })
        },
    )
}

internal fun shopRouteContent(world: BioWorld, ui: BioController, argument: String): ShopRouteContent {
    val tab = ui.shopSelection.shopTab
    val items = catalogProducts(world, tab)
    val selected = argument.takeIf { it.isNotBlank() }?.let(world::tree)
    return ShopRouteContent(
        tab = tab,
        shoppingTree = selected?.let { ShopTreeContext(it.s("id"), it.s("name"), it.s("species")) },
        itemCount = items.size,
        onTab = { ui.shopSelection.shopTab = it },
        onInventory = { ui.go("inventory") },
        onTree = { ui.go("tree", it) },
        onShop = { ui.go("shop") },
        item = { index, modifier ->
            BioProduct(world, ui, items[index], modifier, target = ui.shopSelection.targetTree.ifBlank { ui.homeTreeState.selectedId })
        },
    )
}

@Composable internal fun ColumnScope.BioMore(ui:BioController){BioIntro(stringResource(R.string.copy_them_mot_chut_006e8b),stringResource(R.string.copy_theo_nhip_cua_ban_5a2ada));Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){listOf(Triple("focus","clock",stringResource(R.string.copy_tap_trung_964002)),Triple("templates","plus",stringResource(R.string.copy_viec_mau_535dc1)),Triple("review","calendar",stringResource(R.string.copy_nhin_lai_tuan_ff3f0c)),Triple("achievements","award",stringResource(R.string.copy_dau_moc_dffd66)),Triple("timeline","book",stringResource(R.string.copy_ky_niem_e3e113)),Triple("insights","chart",stringResource(R.string.copy_nhip_thoi_quen_1b8324)),Triple("encyclopedia","book",stringResource(R.string.copy_bach_thao_f2ed64)),Triple("settings","settings",stringResource(R.string.copy_tuy_chon_nhac_viec_a44d85)),Triple("data","download",stringResource(R.string.copy_du_lieu_ung_dung_9843b7))).forEach{(r,i,t)->BioLink(t,i,{ui.go(r)})}}}


internal fun homeRouteContent(world: BioWorld, ui: BioController, short: Boolean): HomeRouteContent {
    val tree = world.tree(ui.homeTreeState.selectedId)
    val next = world.tasks.firstOrNull {
        !it.b("done") && !it.b("finished") && it.s("lifecycle", "ACTIVE") == "ACTIVE" &&
            it.b("scheduledToday", true)
    }
    val phase = world.data.o("phase")
    return HomeRouteContent(
        state = HomeScreenState(
            date = world.data.s("date").split('-').takeLast(2).reversed().joinToString("-"),
            completed = phase.i("completed"),
            required = phase.i("required"),
            earned = world.state.i("earned"),
            streak = phase.i("streak"),
        ),
        onAction = { action ->
            when (action) {
                HomeScreenAction.PickSeed -> ui.show("seed-picker")
                HomeScreenAction.ViewTasks -> ui.go("tasks")
                HomeScreenAction.AddTask -> ui.go("task-new")
                HomeScreenAction.BrowseSeeds -> ui.act("bio-seed-picker")
                HomeScreenAction.BrowseAccessories -> ui.act("bio-accessory-shop")
                HomeScreenAction.Review -> ui.go("review")
            }
        },
        tree = if (tree != null) { { BioHero(world, ui, tree, true, short) } } else null,
        nextTask = if (next != null) { { BioTaskRow(next, ui) } } else null,
        reward = if (phase.b("canClaim") && !world.state.b("claimed")) {
            { BioReward(world, ui) }
        } else null,
    )
}


private fun JSONObject.toTaskListItem() = TaskListItem(
    id = s("id"),
    title = s("title"),
    category = s("category"),
    minutes = i("minutes"),
    reward = taskReward(this),
    done = b("done") || b("finished"),
    timer = s("verification") == "TIMER",
    active = s("lifecycle", "ACTIVE") == "ACTIVE",
)

private fun BioController.completeTask(id: String, timer: Boolean) {
    if (timer) go("focus", id) else send("complete:$id")
}

@Composable
internal fun BioTaskRow(task: JSONObject, ui: BioController) {
    val item = task.toTaskListItem()
    TaskListRow(item, ui.busy, { ui.go("task", item.id) }, { ui.completeTask(item.id, item.timer) })
}

internal fun taskListRouteContent(world: BioWorld, ui: BioController): TaskListRouteContent {
    val filter = ui.taskSelection.filter
    val lifecycle = when (filter) {
        "paused" -> "PAUSED"
        "archived" -> "ARCHIVED"
        else -> "ACTIVE"
    }
    val phase = world.data.o("phase")
    return TaskListRouteContent(
        state = TaskListState(
            filter = filter,
            items = world.tasks.filter { it.s("lifecycle", "ACTIVE") == lifecycle }.map { it.toTaskListItem() },
            completed = phase.i("completed"),
            required = phase.i("required"),
            earned = world.state.i("earned"),
            busy = ui.busy,
        ),
        onAction = { action ->
            when (action) {
                TaskListAction.Add -> ui.go("task-new")
                TaskListAction.Focus -> ui.go("focus")
                TaskListAction.Templates -> ui.go("templates")
                TaskListAction.Insights -> ui.go("insights")
                is TaskListAction.Filter -> ui.taskSelection.filter = action.value
                is TaskListAction.Open -> ui.go("task", action.id)
                is TaskListAction.Complete -> ui.completeTask(action.id, action.timer)
            }
        },
        reward = { BioReward(world, ui) },
    )
}

@Preview(name = "Shop products", widthDp = 335, showBackground = true)
@Composable
private fun BioProductPreview() {
    val context = LocalContext.current
    val world = remember(context) { BioWorld.reference(context) }
    val ui = remember { BioController() }
    BioTheme(world, "1") {
        Column {
            BioReward(world, ui)
            BioProduct(world, ui, world.products.first())
            BioTaskRow(world.tasks.first(), ui)
        }
    }
}

@Preview(name = "More actions", widthDp = 335, heightDp = 720)
@Composable
private fun BioMoreComponentPreview() {
    BioReferenceSurface("more")
}
