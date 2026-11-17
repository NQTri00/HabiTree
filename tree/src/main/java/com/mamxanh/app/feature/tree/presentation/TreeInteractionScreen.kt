package com.mamxanh.app.feature.tree.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.feature.tree.domain.model.Tree
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreeInteractionScreen(
    tree:Tree,state:TreeUiState,fertilizerCount:Int,inventory:Map<String,Int>,
    onIntent:(TreeIntent)->Unit,modifier:Modifier=Modifier,sharedTreeModifier:Modifier=Modifier,
    coins:Int=0,onPrevious:(()->Unit)?=null,onNext:(()->Unit)?=null,
    onOpenGarden:()->Unit={onIntent(TreeIntent.Back)},onOpenDecorations:()->Unit={}
) {
    val status=tree.status
    var tab by rememberSaveable(tree.id){mutableIntStateOf(0)}
    val scroll=key(tree.id,tab){rememberScrollState()}
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=8.dp),verticalAlignment=Alignment.CenterVertically) {
                IconButton(onOpenGarden){Text("‹",color=Deep,fontSize=26.sp)}
                Text("Cây của bạn",Modifier.weight(1f),color=Deep,fontSize=17.sp,fontWeight=FontWeight.SemiBold)
                MintCoinBadge(coins)
            }
            Column(Modifier.weight(1f).verticalScroll(scroll).padding(horizontal=24.dp,vertical=12.dp),verticalArrangement=Arrangement.spacedBy(20.dp)) {
                GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(18.dp),cornerSize=28.dp,skin=GardenPanelSkin.HINT) {
                    Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){MintStatusPill(status.stateLabel);MintStatusPill("${status.stageIndex+1}/${status.stageCount}")}
                        Box(Modifier.fillMaxWidth().height(220.dp),contentAlignment=Alignment.Center) {
                            Box(Modifier.size(220.dp).background(androidx.compose.ui.graphics.Color(0xFFE1EACF),androidx.compose.foundation.shape.CircleShape))
                            GardenPlantPortrait(tree.speciesId,tree.decoration,sharedTreeModifier.fillMaxWidth().height(220.dp),fitVisibleArtwork=true,artworkAlignment=Alignment.Center,withered=!status.alive)
                        }
                        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                            if(onPrevious!=null)IconButton(onPrevious){Text("‹",color=Green,fontSize=24.sp)}
                            Text(tree.name,Modifier.weight(1f),color=Deep,fontSize=20.sp,lineHeight=26.sp,fontWeight=FontWeight.Bold,textAlign=TextAlign.Center)
                            if(onNext!=null)IconButton(onNext){Text("›",color=Green,fontSize=24.sp)}
                        }
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("Tiến độ",color=Muted,fontSize=12.sp);Text("${status.percentage}%",color=Green,fontSize=12.sp)}
                        MintProgress(status.stageProgress,Modifier.fillMaxWidth())
                    }
                }
                TreeDetailTabs(tab,{index->tab=index;onIntent(TreeIntent.SetPanelExpanded(false))})
                TreeDetailSectionContent(tree,tab,inventory,state.isLoading,onIntent,onOpenDecorations)
            }
        }
        if(state.dispositionId==tree.id)MintDialog(
            if(state.selling)"Bán cây" else "Dọn cây",
            {if(!state.isLoading)onIntent(TreeIntent.DismissDisposition)},
            if(state.selling)"Bán cây" else "Dọn cây",
            {onIntent(TreeIntent.ConfirmDisposition)},cancelLabel="Giữ lại",
            confirmEnabled=!state.isLoading,dismissEnabled=!state.isLoading,destructive=true) {
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                GardenPlantPortrait(tree.speciesId,tree.decoration,Modifier.size(32.dp),withered=!status.alive)
                Text(tree.name,color=Deep,fontSize=13.sp,fontWeight=FontWeight.Bold)
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

@Preview(name="Web tree comparison",widthDp=414,heightDp=1200,device="spec:width=414dp,height=1200dp,dpi=160")
@Composable
fun TreeWebDesignPreview(){MamXanhPreviewTheme{
    TreeInteractionScreen(Tree(1,"cherry","Đào hồng xuân",45,"2026-11-01","FLOWERS",status=TreeStatus(stageIndex=3,stageCount=9,stageProgress=.46f)),TreeUiState(),3,mapOf("fertilizer" to 2,"fertilizer_rich" to 1),{})
}}
