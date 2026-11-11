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
            BoxWithConstraints(Modifier.weight(1f)) {
                val hero=minOf(mintTreeHeroHeight(tree.speciesId,maxWidth),maxHeight*.60f)
                Column(Modifier.fillMaxSize().padding(horizontal=10.dp)) {
                    GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(4.dp),cornerSize=18.dp) {
                        Column {
                            Row(verticalAlignment=Alignment.CenterVertically) {
                                TextButton(onClick=onOpenGarden,modifier=Modifier.width(48.dp)) {Text("‹",fontSize=22.sp,color=Deep)}
                                Text(tree.name,Modifier.weight(1f),color=Deep,fontWeight=FontWeight.Bold,fontSize=13.sp,textAlign=TextAlign.Center)
                                Text("$coins xu",color=Muted,fontSize=11.sp)
                                if(onPrevious!=null)IconButton(onClick=onPrevious){Text("‹",fontSize=20.sp,color=Deep)}
                                if(onNext!=null)IconButton(onClick=onNext){Text("›",fontSize=20.sp,color=Deep)}
                            }
                            Row(Modifier.fillMaxWidth().padding(horizontal=8.dp),verticalAlignment=Alignment.CenterVertically,
                                horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                                MintProgress(status.stageProgress,Modifier.weight(1f))
                                Text("${status.percentage}%",color=Green,fontSize=11.sp,fontWeight=FontWeight.Bold)
                            }
                        }
                    }
                    Box(Modifier.fillMaxWidth().height(hero),contentAlignment=Alignment.Center) {
                        GardenPlantPortrait(tree.speciesId,tree.decoration,sharedTreeModifier.fillMaxWidth()
                            .padding(horizontal=36.dp).height(hero),fitVisibleArtwork=true,
                            artworkAlignment=Alignment.Center,withered=!status.alive)
                        if(status.alive)Column(Modifier.align(Alignment.CenterEnd).width(48.dp)) {
                            MintCareIcon(GardenArt.WATERING_CAN,status.waterLabel,status.canWater && !state.isLoading,
                                {onIntent(TreeIntent.Water(tree.id))},checked=status.waterActive)
                            MintCareIcon(GardenArt.FERTILIZER,"Bón · $fertilizerCount túi",fertilizerCount>0 && status.canFertilize && !state.isLoading,
                                {tab=0;onIntent(TreeIntent.SetPanelExpanded(false))})
                        }
                    }
                    Column(Modifier.weight(1f).mintScrollEdges(scroll.canScrollBackward,scroll.canScrollForward)
                        .verticalScroll(scroll).padding(bottom=6.dp),verticalArrangement=Arrangement.spacedBy(4.dp)) {
                        Text("${status.stageLabel} · ${status.stateLabel} · ${status.cue.label}",color=Deep,fontSize=11.sp)
                        if(status.alive) {
                            Row(Modifier.fillMaxWidth()) {
                                listOf("Chăm sóc","Trang trí","Thông tin").forEachIndexed { index,label ->
                                    TextButton(onClick={tab=index;onIntent(TreeIntent.SetPanelExpanded(false))},modifier=Modifier.weight(1f),
                                        contentPadding=PaddingValues(4.dp)) {
                                        Text(label,color=if(tab==index)Green else Muted,fontWeight=if(tab==index)FontWeight.Bold else FontWeight.Normal,fontSize=12.sp)
                                    }
                                }
                            }
                            when(tab) {
                                0 -> {
                                    Text(status.waterLabel + if(status.fertilizerLabel.isNotBlank()) " · ${status.fertilizerLabel}" else "",color=Muted,fontSize=11.sp)
                                    if(status.state==TreeGrowthState.GROWING) FertilizerTier.entries.forEach { tier ->
                                        val count=inventory[tier.itemId] ?: 0
                                        OutlinedButton(onClick={onIntent(TreeIntent.Fertilize(tree.id,tier.itemId))},
                                            modifier=Modifier.fillMaxWidth().heightIn(min=48.dp),contentPadding=PaddingValues(horizontal=10.dp,vertical=4.dp),
                                            colors=ButtonDefaults.outlinedButtonColors(contentColor=Green),
                                            enabled=status.canFertilize && count>0 && !state.isLoading) {
                                            Text("${tier.label} · $count túi",Modifier.weight(1f),fontSize=12.sp)
                                            Text("${tier.rate/1000.0}×",fontSize=11.sp)
                                        }
                                    }
                                    if(!status.canFertilize && status.state==TreeGrowthState.GROWING)
                                        Text(if(!status.waterActive)"Hãy tưới cây trước." else "Cây chưa cần thêm phân bón.",color=Muted,fontSize=11.sp)
                                    TextButton(onClick={onIntent(TreeIntent.OpenFertilizerShop)}){Text("Ghé Tiệm hạt",color=Green,fontSize=12.sp)}
                                }
                                1 -> DecorationPicker(tree.decoration,{onIntent(TreeIntent.Decorate(tree.id,it))})
                                else -> {
                                    Text("Loài: ${catalog.first{it.id==tree.speciesId}.name}",color=Deep,fontSize=12.sp)
                                    Text("${status.stageCount} dáng · Ngày trồng: ${tree.planted}",color=Muted,fontSize=11.sp)
                                    Text("Tưới cây để tiếp tục phát triển. Chăm sóc đều đặn giúp cây khỏe mạnh.",color=Muted,fontSize=11.sp)
                                }
                            }
                            if(status.state==TreeGrowthState.MATURE) TextButton(
                                onClick={onIntent(TreeIntent.AskDisposition(tree.id,true))},enabled=!state.isLoading) {
                                Text("Bán cây trưởng thành",color=Green,fontSize=12.sp)
                            }
                        } else Text("Cây đã khô héo vĩnh viễn.",color=Muted,fontSize=12.sp)
                        TextButton(onClick={onIntent(TreeIntent.AskDisposition(tree.id,false))},enabled=!state.isLoading) {
                            Text("Dọn cây",color=Muted,fontSize=12.sp)
                        }
                    }
                }
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
