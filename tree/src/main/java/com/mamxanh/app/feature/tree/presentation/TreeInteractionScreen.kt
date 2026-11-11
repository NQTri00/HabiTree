package com.mamxanh.app.feature.tree.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.feature.tree.domain.model.Tree
import com.mamxanh.app.feature.tree.presentation.components.DecorationPicker
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreeInteractionScreen(
    tree:Tree,state:TreeUiState,fertilizerCount:Int,inventory:Map<String,Int>,
    onIntent:(TreeIntent)->Unit,modifier:Modifier=Modifier,sharedTreeModifier:Modifier=Modifier,
    coins:Int=0,onPrevious:(()->Unit)?=null,onNext:(()->Unit)?=null,
    onOpenGarden:()->Unit={onIntent(TreeIntent.Back)}
) {
    val scroll=rememberScrollState()
    val status=tree.status
    var tab by rememberSaveable(tree.id){mutableIntStateOf(0)}
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintCompactHeader(coins)
            Column(Modifier.weight(1f).mintScrollEdges(scroll.canScrollBackward,scroll.canScrollForward)
                .verticalScroll(scroll).padding(horizontal=12.dp,vertical=8.dp),
                horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
                GardenPaintedButton("‹ Vườn ươm",onOpenGarden,Modifier.fillMaxWidth(),primary=false)
                val hero=mintTreeHeroHeight()
                Box(Modifier.fillMaxWidth().heightIn(min=hero),contentAlignment=Alignment.Center) {
                    GardenPlantPortrait(tree.speciesId,tree.decoration,sharedTreeModifier.fillMaxWidth()
                        .padding(horizontal=40.dp).height(hero),fitVisibleArtwork=true,
                        artworkAlignment=Alignment.Center,withered=!status.alive)
                    if(status.alive)Column(Modifier.align(Alignment.CenterEnd).width(48.dp)) {
                        MintCareIcon(GardenArt.WATERING_CAN,status.waterLabel,status.canWater && !state.isLoading,
                            {onIntent(TreeIntent.Water(tree.id))},checked=status.waterActive)
                        MintCareIcon(GardenArt.FERTILIZER,"Bón · $fertilizerCount túi",
                            status.canFertilize && fertilizerCount>0 && !state.isLoading,
                            {tab=0;onIntent(TreeIntent.SetPanelExpanded(true))})
                    }
                }
                GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(8.dp)) {
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        IconButton(onClick={onPrevious?.invoke()},enabled=onPrevious!=null){Text("‹",fontSize=24.sp,color=Deep)}
                        Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(4.dp)) {
                            Text(tree.name,color=Deep,fontWeight=FontWeight.Bold,fontSize=17.sp,textAlign=TextAlign.Center)
                            Text("${status.stageLabel} · ${status.stateLabel}",color=Green,fontSize=12.sp)
                            MintProgress(status.stageProgress,Modifier.fillMaxWidth())
                        }
                        IconButton(onClick={onNext?.invoke()},enabled=onNext!=null){Text("›",fontSize=24.sp,color=Deep)}
                    }
                }
                Text("${status.waterLabel} · ${status.cue.label}",color=Deep,fontSize=13.sp)
                Text("Ngày chăm sóc ${status.growthDays}/${status.requiredDays}",color=Muted,fontSize=12.sp)
                if(status.nextStageActiveMillis>0) Text("Dáng tiếp theo: khoảng ${remainingTime(status.nextStageActiveMillis,0)} tưới hiệu quả",
                    color=Muted,fontSize=12.sp,textAlign=TextAlign.Center)
                if(status.fertilizerLabel.isNotBlank())Text(status.fertilizerLabel,color=Green,fontSize=12.sp)
                if(status.alive) {
                    MintSegments(listOf("Chăm sóc","Trang trí","Thông tin"),tab,{tab=it;onIntent(TreeIntent.SetPanelExpanded(true))})
                    if(status.state==TreeGrowthState.MATURE)GardenPaintedButton("Bán cây trưởng thành",
                        {onIntent(TreeIntent.AskDisposition(tree.id,true))},Modifier.fillMaxWidth(),enabled=!state.isLoading)
                } else Text("Cây đã khô héo vĩnh viễn. Dọn cây để dùng lại ô trồng.",color=Muted,textAlign=TextAlign.Center)
                GardenPaintedButton("Dọn cây",{onIntent(TreeIntent.AskDisposition(tree.id,false))},
                    Modifier.fillMaxWidth(),primary=false,enabled=!state.isLoading)
            }
        }
        if(state.isPanelExpanded && status.alive)ModalBottomSheet(
            onDismissRequest={onIntent(TreeIntent.SetPanelExpanded(false))},
            sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true),containerColor=androidx.compose.ui.graphics.Color(0xFFF1FAF5)) {
            Column(Modifier.fillMaxWidth().fillMaxHeight(.75f).navigationBarsPadding().padding(horizontal=16.dp),
                verticalArrangement=Arrangement.spacedBy(12.dp)) {
                MintSegments(listOf("Chăm sóc","Trang trí","Thông tin"),tab,{tab=it})
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    when(tab) {
                        0 -> {
                            Text(status.waterLabel,color=Deep,fontWeight=FontWeight.Bold)
                            GardenPaintedButton("Tưới cây",{onIntent(TreeIntent.Water(tree.id))},Modifier.fillMaxWidth(),
                                enabled=status.canWater && !state.isLoading)
                            Text("Phân chỉ tăng tốc khi cây được tưới; không cộng dồn. Mỗi cây dùng một lần trong 24 giờ.",color=Muted)
                            FertilizerTier.entries.forEach { tier ->
                                val count=inventory[tier.itemId] ?: 0
                                GardenPaintedButton("${tier.label} · $count túi · ${tier.rate/1000.0}×",
                                    {onIntent(TreeIntent.Fertilize(tree.id,tier.itemId))},Modifier.fillMaxWidth(),primary=false,
                                    enabled=status.canFertilize && count>0 && !state.isLoading)
                            }
                            if(!status.canFertilize)Text(when {
                                status.state==TreeGrowthState.MATURE -> "Cây đã trưởng thành, không cần phân bón."
                                !status.waterActive -> "Hãy tưới cây trước."
                                status.fertilizerEndsAt>status.sampledAt -> "Phân bón đang có tác dụng."
                                else -> "Có thể bón lại sau ${remainingTime(status.fertilizerAvailableAt,status.sampledAt)}."
                            },color=Muted)
                            GardenPaintedButton("Ghé Tiệm hạt",{onIntent(TreeIntent.OpenFertilizerShop)},Modifier.fillMaxWidth(),primary=false)
                        }
                        1 -> DecorationPicker(tree.decoration,{onIntent(TreeIntent.Decorate(tree.id,it))})
                        else -> {
                            val profile=lifecycleProfile(tree.speciesId)
                            Text("Loài: ${catalog.first{it.id==tree.speciesId}.name}",color=Deep)
                            Text("${profile.stageCount} giai đoạn · Ít nhất ${profile.minimumAgeDays} ngày",color=Deep)
                            Text("Ngày trồng: ${tree.planted}",color=Deep)
                            Text("Ngày chăm sóc: ${status.growthDays}/${status.requiredDays}",color=Deep)
                            Text("Lần tưới gần nhất: " + if(status.lastWateredAt>0)
                                java.text.DateFormat.getDateTimeInstance().format(java.util.Date(status.lastWateredAt)) else "Chưa tưới",color=Deep)
                            Text("Mỗi ngày tối đa một ngày tăng trưởng. Cây ngừng lớn khi khô và có thể khô héo nếu bị bỏ quên lâu.",color=Muted)
                        }
                    }
                }
                GardenPaintedButton("Đóng",{onIntent(TreeIntent.SetPanelExpanded(false))},Modifier.fillMaxWidth(),primary=false)
                Spacer(Modifier.height(8.dp))
            }
        }
        if(state.dispositionId==tree.id)AlertDialog(
            onDismissRequest={if(!state.isLoading)onIntent(TreeIntent.DismissDisposition)},
            title={Text(if(state.selling)"Bán ${tree.name}?" else "Dọn ${tree.name}?")},
            text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Text("Cây sẽ rời ô trồng vĩnh viễn. Không hoàn lại hạt hoặc tiến trình.")
                if(state.selling) {
                    val quote=state.quote
                    Text("Giá ${quote?.nominal ?: 0} xu · Nhận hôm nay ${quote?.granted ?: 0} xu.")
                    if((quote?.granted ?: 0)<(quote?.nominal ?: 0))
                        Text("Giới hạn ${Balance.DAILY_CAP} xu/ngày: phần còn lại mất vĩnh viễn, không để dành.",color=MaterialTheme.colorScheme.error)
                } else Text("Không nhận xu khi dọn cây.")
            }},
            confirmButton={TextButton(onClick={onIntent(TreeIntent.ConfirmDisposition)},enabled=!state.isLoading){Text(if(state.selling)"Bán cây" else "Dọn cây")}},
            dismissButton={TextButton(onClick={onIntent(TreeIntent.DismissDisposition)},enabled=!state.isLoading){Text("Giữ lại")}})
    }
}
