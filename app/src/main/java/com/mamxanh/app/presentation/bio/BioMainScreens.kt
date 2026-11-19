package com.mamxanh.app.presentation.bio

import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.app.feature.tree.presentation.BioTreeScreen
import com.mamxanh.app.feature.tree.presentation.TreeScreenAction
import com.mamxanh.app.feature.tree.presentation.TreeScreenFertilizer
import com.mamxanh.app.feature.tree.presentation.TreeScreenState
import com.mamxanh.app.feature.shop.presentation.BioShopScreen
import com.mamxanh.app.feature.shop.presentation.ShopTreeContext
import com.mamxanh.app.feature.inventory.presentation.BioInventoryScreen
import com.mamxanh.app.feature.garden.presentation.navigation.GardenRouteContent
import com.mamxanh.app.feature.garden.presentation.GardenAction
import com.mamxanh.app.feature.garden.presentation.GardenDisplayPlot
import com.mamxanh.app.feature.garden.presentation.GardenDisplayState
import com.mamxanh.app.feature.garden.presentation.GardenDisplayTree
import com.mamxanh.app.feature.home.presentation.BioHomeScreen
import com.mamxanh.app.feature.home.presentation.HomeScreenAction
import com.mamxanh.app.feature.home.presentation.HomeScreenState
import com.mamxanh.app.feature.tasks.presentation.TaskListAction
import com.mamxanh.app.feature.tasks.presentation.TaskListItem
import com.mamxanh.app.feature.tasks.presentation.TaskListRow
import com.mamxanh.app.feature.tasks.presentation.TaskListScreen
import com.mamxanh.app.feature.tasks.presentation.TaskListState

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppIcons
import com.mamxanh.app.presentation.ui.design.AppOpacity
import com.mamxanh.app.presentation.ui.design.AppRadii
import com.mamxanh.app.presentation.ui.design.AppSpacing
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import org.json.JSONObject

internal fun treeState(t:JSONObject)=when(t.s("state")){"dead"->AppText.text(TextKey.CopyDaKhoHeoA9c1b9);"mature"->AppText.text(TextKey.CopyTruongThanh8c14d7);else->AppText.text(TextKey.CopyDangLon40daab)}
internal fun taskReward(t:JSONObject)=if(t.s("verification")=="TIMER")when{t.i("minutes")>=45->20;t.i("minutes")>=20->15;else->10}else 8
internal fun growthCaption(t:JSONObject)=when(t.s("state")){"dead"->AppText.text(TextKey.CopyMamXanhDaDungLai6dabc6);"mature"->AppText.text(TextKey.CopyDaTruongThanh54f165);else->AppText.text(TextKey.CopyDangLonCungBan6913c2)}
internal fun accessoryIcon(id:String)=when(id){"tree_ribbon"->"gift";"tree_lantern"->"sun";else->"spark"}

@Composable internal fun BioHero(world:BioWorld,ui:BioController,t:JSONObject,home:Boolean=false,short:Boolean=false){
    val p=bp;val narrow=LocalConfiguration.current.screenWidthDp<359;val shape=bioShape(AppRadii.Value26);val dead=t.s("state")=="dead"
    Column(Modifier.fillMaxWidth().shadow(AppDimensions.Dp5,shape,ambientColor=p.deep.copy(alpha=AppOpacity.Value_35),spotColor=p.deep.copy(alpha=AppOpacity.Value_30)).background(remember(p){bioHeroBrush(p)},shape).border(AppDimensions.Dp1,AppColors.White.copy(alpha=AppOpacity.Value_75),shape).clip(shape).drawBehind {
        val step=AppDimensions.Dp24.toPx();var x=0f;while(x<size.width){drawLine(AppColors.White.copy(alpha=AppOpacity.Value_08),Offset(x,0f),Offset(x,size.height),1f);x+=step};var y=0f;while(y<size.height){drawLine(AppColors.White.copy(alpha=AppOpacity.Value_08),Offset(0f,y),Offset(size.width,y),1f);y+=step}
        val right=size.width-AppDimensions.Dp12.toPx();val top=AppDimensions.Dp12.toPx();val len=AppDimensions.Dp18.toPx();drawLine(p.deep.copy(alpha=AppOpacity.Value_45),Offset(right-len,top),Offset(right,top),AppDimensions.Dp1.toPx());drawLine(p.deep.copy(alpha=AppOpacity.Value_45),Offset(right,top),Offset(right,top+len),AppDimensions.Dp1.toPx())
    }.padding(start=if(narrow)AppDimensions.Dp15 else AppDimensions.Dp19,end=if(narrow)AppDimensions.Dp15 else AppDimensions.Dp19,top=AppDimensions.Dp15,bottom=AppDimensions.Dp19)){
        Row(Modifier.fillMaxWidth().height(AppDimensions.Dp48),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){
            BioTag(treeState(t),dot=true)
            if(home)BioTextAction(AppText.text(TextKey.CopyDoiCayFada9c),{ui.show("tree-picker")},"next")
            else BioTextAction(if(ui.homeTree==t.s("id"))AppText.text(TextKey.CopyTrenTrangChuE7c2f5) else AppText.text(TextKey.CopyDatOTrangChuD8241c),{ui.act("bio-pin:"+t.s("id"))})
        }
        Spacer(Modifier.height(AppDimensions.Dp2))
        val landscape=LocalConfiguration.current.screenHeightDp<500
        val artHeight=if(landscape)100 else if(home){if(short)130 else 150}else if(short)160 else if(narrow)190 else 220
        Box(Modifier.fillMaxWidth().height(artHeight.dp).then(if(home)Modifier.clickable{ui.go("tree",t.s("id"))}else Modifier),contentAlignment=Alignment.Center){
            BioPlant(t.s("species"),Modifier.height((artHeight-if(home)0 else 12).dp).fillMaxWidth(),dead)
            val accessory=t.s("decor").takeIf{it.isNotBlank()&&world.owned(it)>0}
            if(accessory!=null&&!dead)Box(Modifier.align(Alignment.TopEnd).padding(top=AppDimensions.Dp55,end=AppDimensions.Dp14).size(AppDimensions.Dp32).background(AppColors.White.copy(alpha=AppOpacity.Value_6),RoundedCornerShape(50)).border(AppDimensions.Dp1,p.accent,RoundedCornerShape(50)),contentAlignment=Alignment.Center){Bi(accessoryIcon(accessory),size=AppIcons.Size23)}
            if(!home&&world.trees.size>1){BioIconButton("back",{ui.act("tree-prev")},Modifier.align(Alignment.CenterStart).offset(x=AppDimensions.DpMinus6),boxed=true,iconSize=AppIcons.Size18,tint=p.deep,label=AppText.text(TextKey.CopyCayTruoc6648b1));BioIconButton("next",{ui.act("tree-next")},Modifier.align(Alignment.CenterEnd).offset(x=AppDimensions.Dp6),boxed=true,iconSize=AppIcons.Size18,tint=p.deep,label=AppText.text(TextKey.CopyCayTiepTheo242116))}
        }
        Spacer(Modifier.height(AppDimensions.Dp10))
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)){
            Column(Modifier.weight(1f)){Bt(t.s("name"),size=AppTypography.Size21,line=AppTypography.LineHeight28_35,bold=true,spacing=AppTypography.TrackingMinus_7);Bt(AppText.text(TextKey.Copy01GiaiDoan869247,t.i("stage",1),world.species(t.s("species")).i("stages",6)),size=AppTypography.Size12,tint=p.muted,line=AppTypography.LineHeight21_7)}
            BioTag(if(dead)AppText.text(TextKey.CopyKhoHeoBf546a) else if(t.b("watered"))AppText.text(TextKey.CopyDuNuocC17aa8) else AppText.text(TextKey.CopyCanTuoiA0957f),"water")
        }
        Spacer(Modifier.height(AppDimensions.Dp12));BioProgress(t.i("progress")/100f,white=true)
        Spacer(Modifier.height(AppDimensions.Dp6));Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Bt(growthCaption(t),size=AppTypography.Size10,tint=p.muted,line=AppTypography.LineHeight18_6);Bt("${t.i("progress")}%",size=AppTypography.Size11,tint=p.deep,mono=true,line=AppTypography.LineHeight18_6)}
        if(home){Spacer(Modifier.height(AppDimensions.Dp13));if(LocalDensity.current.fontScale>1.2f)Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
            BioButton(if(t.b("watered"))AppText.text(TextKey.CopyDaTuoiDe2042) else AppText.text(TextKey.CopyTuoiCay0990f6),{ui.send("water:"+t.s("id"))},Modifier.fillMaxWidth(),"water",enabled=!t.b("watered")&&!dead&&!ui.busy)
            BioButton(AppText.text(TextKey.CopyTrangTriD48231),{ui.act("bio-tree-decor:"+t.s("id"))},Modifier.fillMaxWidth(),"spark",secondary=true,enabled=!dead&&!ui.busy)
        }else Row(Modifier.height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
            BioButton(if(t.b("watered"))AppText.text(TextKey.CopyDaTuoiDe2042) else AppText.text(TextKey.CopyTuoiCay0990f6),{ui.send("water:"+t.s("id"))},Modifier.weight(1f).fillMaxHeight(),"water",enabled=!t.b("watered")&&!dead&&!ui.busy,labelSize=if(narrow)12 else 14)
            BioButton(AppText.text(TextKey.CopyTrangTriD48231),{ui.act("bio-tree-decor:"+t.s("id"))},Modifier.weight(1f).fillMaxHeight(),"spark",secondary=true,enabled=!dead&&!ui.busy,labelSize=if(narrow)12 else 14)
        }}
    }
}

@Composable internal fun BioReward(world:BioWorld,ui:BioController){BioCard(Modifier.fillMaxWidth(),AppColors.RewardSurface,padding=AppSpacing.Value14,radius=AppRadii.Value18){Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){Bi("gift",tint=bp.ink);Column(Modifier.weight(1f)){Bt(AppText.text(TextKey.CopyThuongHomNayC015c6),size=AppTypography.Size12,semi=true);Bt(AppText.text(TextKey.Copy8Xu3a7892),size=AppTypography.Size11)};BioButton(if(world.state.b("claimed"))AppText.text(TextKey.CopyDaNhan1d62ad) else AppText.text(TextKey.CopyNhan8518f2),{ui.send("claim")},enabled=world.data.o("phase").b("canClaim")&&!world.state.b("claimed"))}}}

@Composable internal fun BioProduct(world:BioWorld,ui:BioController,item:JSONObject,modifier:Modifier=Modifier,inventory:Boolean=false,target:String="",encyclopedia:Boolean=false){
    val id=item.s("id");val seed=world.species.any{it.s("id")==id};val accessory=item.s("category")=="TREE";val owned=(if(inventory)world.inventoryCount(id, accessory) else world.owned(id))>0;val equipped=world.trees.any{it.s("decor")==id};val known=world.record(id).b("discovered")||world.record(id).i("planted")>0||world.count(id)>0
    val action={if(encyclopedia)ui.go("species",id)else if(accessory)ui.act("accessory:$id:$target")else if(inventory){if(seed)ui.act("plant:$id:${ui.plot}")else if(item.b("permanent"))ui.send("equip:$id")else ui.show("fertilizer-help")}else ui.show("purchase",id)}
    BioCard(modifier,padding=AppSpacing.Value12,onClick=action){
        val backdrop=Brush.linearGradient(if(seed&&(!encyclopedia||known))listOf(AppColors.SwatchFFFAFAF8,bp.soft)else listOf(bp.soft,bp.soft))
        Box(Modifier.fillMaxWidth().height(if(LocalConfiguration.current.screenWidthDp<359)AppDimensions.Dp95 else AppDimensions.Dp112).background(backdrop,bioShape(AppRadii.Value15)),contentAlignment=Alignment.Center){if(seed&&(!encyclopedia||known))BioPlant(id,Modifier.fillMaxSize())else Bi(if(seed)"leaf"else if(accessory)accessoryIcon(id)else if(item.b("permanent"))"water"else "fertilizer",size=if(accessory)32 else 43)}
        Spacer(Modifier.height(AppDimensions.Dp10));Bt(item.s("name"),Modifier.heightIn(min=AppDimensions.Dp36),size=AppTypography.Size13,line=AppTypography.LineHeight18_85,semi=true);Spacer(Modifier.height(AppDimensions.Dp10))
        if(encyclopedia)Bt(world.record(id).s("status",AppText.text(TextKey.CopyChuaKhamPhaC0b0cb)),size=AppTypography.Size11,tint=bp.muted,line=AppTypography.LineHeight15_4)
        else if(accessory){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Bt(if(equipped)AppText.text(TextKey.CopyDangDung275ca2)else if(owned)AppText.text(TextKey.CopyDaSoHuu822625)else AppText.text(TextKey.Copy0Xu251e68,item.i("price")),Modifier.weight(1f),size=AppTypography.Size13,tint=bp.deep);Box(Modifier.size(AppDimensions.Dp27).border(AppDimensions.Dp1,bp.line,RoundedCornerShape(AppDimensions.Dp8)),contentAlignment=Alignment.Center){Bi(if(equipped)"check"else if(owned)"next"else "plus",size=AppIcons.Size14)}}}
        else{Bt(if(inventory)if(item.b("permanent"))if(world.state.s("equipped")==id)AppText.text(TextKey.CopyDangDung275ca2)else AppText.text(TextKey.CopyTrangBi9e8e12)else "×${world.inventoryCount(id, accessory)}"else if(item.b("permanent")&&world.count(id)>0)if(world.state.s("equipped")==id)AppText.text(TextKey.CopyDangDung275ca2)else AppText.text(TextKey.CopyDaSoHuu822625)else AppText.text(TextKey.Copy0Xu251e68,item.i("price")),size=AppTypography.Size13,tint=bp.deep);if(seed&&!inventory)Bt(bioTier(item.s("tier")),size=AppTypography.Size12,line=AppTypography.LineHeight17,tint=bp.muted)}
    }
}
@Composable
internal fun BioCatalog(world: BioWorld, ui: BioController, inventory: Boolean) {
    val family = if (inventory) "inventory" else "shop"
    val tab = ui.tabs[family] ?: "seeds"
    val products = when (tab) {
        "care" -> world.products
        "accessories" -> world.accessories
        else -> world.species
    }
    val items = products.filter {
        val id = it.s("id")
        !inventory || world.inventoryCount(id, accessory = tab == "accessories") > 0
    }
    val item: @Composable (Int, Modifier) -> Unit = { index, modifier ->
        BioProduct(world, ui, items[index], modifier, inventory, ui.targetTree.ifBlank { ui.homeTree })
    }
    if (inventory) {
        BioInventoryScreen(
            tab = tab,
            itemCount = items.size,
            onTab = { ui.tabs[family] = it },
            onShop = { ui.go("shop") },
            onBuy = {
                if (tab == "accessories") ui.tabs["shop"] = "accessories"
                ui.go("shop")
            },
            item = item,
        )
    } else {
        val selected = ui.param.takeIf { it.isNotBlank() }?.let(world::tree)
        BioShopScreen(
            tab = tab,
            shoppingTree = selected?.let { ShopTreeContext(it.s("id"), it.s("name"), it.s("species")) },
            itemCount = items.size,
            onTab = { ui.tabs[family] = it },
            onInventory = { ui.go("inventory") },
            onTree = { ui.go("tree", it) },
            onShop = { ui.go("shop") },
            item = item,
        )
    }
}

@Composable internal fun ColumnScope.BioMore(ui:BioController){BioIntro(AppText.text(TextKey.CopyThemMotChut006e8b),AppText.text(TextKey.CopyTheoNhipCuaBan5a2ada));Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){listOf(Triple("focus","clock",AppText.text(TextKey.CopyTapTrung964002)),Triple("templates","plus",AppText.text(TextKey.CopyViecMau535dc1)),Triple("review","calendar",AppText.text(TextKey.CopyNhinLaiTuanFf3f0c)),Triple("achievements","award",AppText.text(TextKey.CopyDauMocDffd66)),Triple("timeline","book",AppText.text(TextKey.CopyKyNiemE3e113)),Triple("insights","chart",AppText.text(TextKey.CopyNhipThoiQuen1b8324)),Triple("encyclopedia","book",AppText.text(TextKey.CopyBachThaoF2ed64)),Triple("settings","settings",AppText.text(TextKey.CopyTuyChonNhacViecA44d85)),Triple("data","download",AppText.text(TextKey.CopyDuLieuUngDung9843b7))).forEach{(r,i,t)->BioLink(t,i,{ui.go(r)})}}}


@Composable
internal fun BioHome(world: BioWorld, ui: BioController, short: Boolean) {
    val tree = world.tree(ui.homeTree)
    val next = world.tasks.firstOrNull {
        !it.b("done") && !it.b("finished") && it.s("lifecycle", "ACTIVE") == "ACTIVE" &&
            it.b("scheduledToday", true)
    }
    val phase = world.data.o("phase")
    BioHomeScreen(
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

@Composable
internal fun BioTasks(world: BioWorld, ui: BioController) {
    val filter = ui.tabs["taskFilter"] ?: "today"
    val lifecycle = when (filter) {
        "paused" -> "PAUSED"
        "archived" -> "ARCHIVED"
        else -> "ACTIVE"
    }
    val phase = world.data.o("phase")
    TaskListScreen(
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
                is TaskListAction.Filter -> ui.tabs["taskFilter"] = action.value
                is TaskListAction.Open -> ui.go("task", action.id)
                is TaskListAction.Complete -> ui.completeTask(action.id, action.timer)
            }
        },
        reward = { BioReward(world, ui) },
    )
}


internal fun gardenRouteContent(world: BioWorld, ui: BioController): GardenRouteContent {
    val open = world.state.i("unlocked", 5)
    val filter = ui.tabs["gardenFilter"] ?: "all"
    fun thirsty(tree: JSONObject) = !tree.b("watered") && tree.s("state") != "dead"
    val trees = world.trees
    val byPlot = trees.groupBy { it.i("plot") }
    val plots = buildList {
        for (plot in 1..open) {
            val tree = byPlot[plot]?.firstOrNull()
            if (filter != "all" && (tree == null ||
                    (filter != tree.s("state") && !(filter == "thirsty" && thirsty(tree))))) continue
            val displayTree = tree?.let {
                GardenDisplayTree(
                    id = it.s("id"),
                    species = it.s("species"),
                    name = it.s("name"),
                    progress = it.i("progress"),
                    stageLabel = treeState(it),
                    dead = it.s("state") == "dead",
                    watered = it.b("watered"),
                )
            }
            add(GardenDisplayPlot(plot, displayTree))
        }
        if (filter == "all" && open < 24) {
            add(GardenDisplayPlot(open + 1, price = 400 + (open - 5).coerceAtLeast(0) * 200))
        }
    }
    return GardenRouteContent(
        state = GardenDisplayState(filter, trees.size, world.emptyPlots().size, world.species.size, trees.any(::thirsty), plots),
        onAction = { action ->
            when (action) {
                GardenAction.Inventory -> ui.go("inventory")
                GardenAction.WaterAll -> ui.show("water-all")
                GardenAction.PickSeed -> ui.show("seed-picker")
                GardenAction.Encyclopedia -> ui.go("encyclopedia")
                is GardenAction.Filter -> ui.tabs["gardenFilter"] = action.value
                is GardenAction.Plot -> {
                    val plot = action.value
                    val tree = plot.tree
                    when {
                        tree != null -> ui.go("tree", tree.id)
                        plot.price != null -> ui.show("unlock", plot.number.toString())
                        else -> {
                            ui.plot = plot.number.toString()
                            ui.show("seed-picker")
                        }
                    }
                }
            }
        },
    )
}


@Composable
internal fun BioTree(world: BioWorld, ui: BioController, compact: Boolean = false) {
    val tree = world.tree(ui.param)
    val id = tree?.s("id").orEmpty()
    val speciesId = tree?.s("species").orEmpty()
    val state = tree?.let {
        val species = world.species(speciesId)
        TreeScreenState(
            growth = it.s("state"),
            watered = it.b("watered"),
            fertilizerNoteAllowed = it.b("canFertilize", true),
            canFertilize = it.b("canFertilize", it.b("watered")),
            busy = ui.busy,
            hasDecoration = it.s("decor").isNotBlank(),
            decorationName = world.product(it.s("decor")).s("name").lowercase(),
            speciesName = species.s("name"),
            stage = it.i("stage"),
            stages = species.i("stages"),
            progress = it.i("progress"),
            planted = it.s("planted"),
            description = species.s("description"),
            fertilizers = world.products.filter { product -> !product.b("permanent") }.map { product ->
                val productId = product.s("id")
                TreeScreenFertilizer(productId, product.s("name"), product.s("rate"), product.i("hours"), world.count(productId))
            },
        )
    }
    BioTreeScreen(
        state = state,
        tab = ui.tabs["tree"] ?: "care",
        onTab = { ui.tabs["tree"] = it },
        onAction = { action ->
            when (action) {
                TreeScreenAction.Encyclopedia -> ui.go("encyclopedia")
                TreeScreenAction.Water -> ui.send("water:$id")
                is TreeScreenAction.Fertilize -> ui.send("fertilize:$id:${action.id}")
                TreeScreenAction.BuyFertilizer -> ui.act("shop-care")
                TreeScreenAction.Unequip -> ui.send("unequip:$id")
                TreeScreenAction.BuyAccessory -> ui.act("bio-accessory-shop:$id")
                TreeScreenAction.Species -> ui.go("species", speciesId)
                TreeScreenAction.Sell -> ui.show("sell", id)
                TreeScreenAction.Remove -> ui.show("remove", id)
            }
        },
        hero = { if (tree != null) BioHero(world, ui, tree, short = compact) },
        accessories = {
            val products = world.accessories
            BioGrid(products.size) { index, modifier -> BioProduct(world, ui, products[index], modifier, target = id) }
        },
    )
}
