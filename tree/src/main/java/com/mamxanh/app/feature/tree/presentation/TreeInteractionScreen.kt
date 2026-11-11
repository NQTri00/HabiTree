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
                    GardenBotanicalFrame(Modifier.weight(1f).fillMaxWidth(),contentPadding=PaddingValues(10.dp),cornerSize=20.dp) {
                    Column(Modifier.fillMaxSize().mintScrollEdges(scroll.canScrollBackward,scroll.canScrollForward)
                        .verticalScroll(scroll).padding(bottom=4.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                        MintTreeStatus("${status.stageIndex+1}/${status.stageCount}",status.stateLabel,status.cue.label)
                        if(status.alive) {
                            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(4.dp)) {
                                listOf("Chăm sóc","Trang trí","Thông tin").forEachIndexed { index,label ->
                                    Surface(Modifier.weight(1f),shape=androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                        color=if(tab==index)Green else androidx.compose.ui.graphics.Color(0xFFF0F6F2)) {
                                        TextButton(onClick={tab=index;onIntent(TreeIntent.SetPanelExpanded(false))},
                                            contentPadding=PaddingValues(4.dp)) {
                                            Text(label,color=if(tab==index)androidx.compose.ui.graphics.Color.White else Muted,
                                                fontWeight=FontWeight.SemiBold,fontSize=11.sp)
                                        }
                                    }
                                }
                            }
                            when(tab) {
                                0 -> {
                                    MintStatusPill(status.waterLabel)
                                    if(status.fertilizerLabel.isNotBlank())Text(status.fertilizerLabel,color=Muted,fontSize=11.sp)
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
                                    if(status.state==TreeGrowthState.GROWING)MintDialogAction("Chọn vật phẩm",{onIntent(TreeIntent.OpenFertilizerShop)},Modifier.fillMaxWidth(),primary=false)
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
        }
        if(state.dispositionId==tree.id)MintDialog(
            if(state.selling)"Bán cây" else "Dọn cây",
            {if(!state.isLoading)onIntent(TreeIntent.DismissDisposition)},
            if(state.selling)"Bán cây" else "Dọn cây",
            {onIntent(TreeIntent.ConfirmDisposition)},cancelLabel="Giữ lại",
            confirmEnabled=!state.isLoading,dismissEnabled=!state.isLoading,destructive=true) {
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                GardenPlantPortrait(tree.speciesId,tree.decoration,Modifier.size(64.dp),withered=!status.alive)
                Text(tree.name,color=Deep,fontSize=16.sp,fontWeight=FontWeight.Bold)
            }
            Text("Cây sẽ rời ô trồng vĩnh viễn. Không hoàn lại hạt hoặc tiến trình.",color=Muted,fontSize=12.sp,lineHeight=17.sp)
            if(state.selling) {
                val quote=state.quote
                MintMetricStrip(listOf("Giá bán" to "${quote?.nominal ?: 0} xu","Nhận hôm nay" to "${quote?.granted ?: 0} xu"))
                if((quote?.granted ?: 0)<(quote?.nominal ?: 0))
                    Text("Phần vượt giới hạn ${Balance.DAILY_CAP} xu/ngày sẽ mất, không để dành.",
                        color=MaterialTheme.colorScheme.error,fontSize=12.sp,lineHeight=17.sp)
            } else MintStatusPill("Không nhận xu hoặc hạt")
        }
    }
}
