package com.mamxanh.app.feature.tree.presentation

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

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
import com.mamxanh.shared.model.Tree
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
            Row(Modifier.fillMaxWidth().padding(horizontal=AppDimensions.Dp16,vertical=AppDimensions.Dp8),verticalAlignment=Alignment.CenterVertically) {
                IconButton(onOpenGarden){Text("‹",color=Deep,fontSize=AppTypography.Sp26)}
                Text(AppText.text(TextKey.CopyCayCuaBan64f9d7),Modifier.weight(1f),color=Deep,fontSize=AppTypography.Sp17,fontWeight=FontWeight.SemiBold)
                MintCoinBadge(coins)
            }
            Column(Modifier.weight(1f).verticalScroll(scroll).padding(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp12),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp20)) {
                GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(AppDimensions.Dp18),cornerSize=AppDimensions.Dp28,skin=GardenPanelSkin.HINT) {
                    Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)) {
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){MintStatusPill(status.stateLabel);MintStatusPill("${status.stageIndex+1}/${status.stageCount}")}
                        Box(Modifier.fillMaxWidth().height(AppDimensions.Dp220),contentAlignment=Alignment.Center) {
                            Box(Modifier.size(AppDimensions.Dp220).background(AppColors.SwatchFFE1EACF,androidx.compose.foundation.shape.CircleShape))
                            GardenPlantPortrait(tree.speciesId,tree.decoration,sharedTreeModifier.fillMaxWidth().height(AppDimensions.Dp220),fitVisibleArtwork=true,artworkAlignment=Alignment.Center,withered=!status.alive)
                        }
                        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                            if(onPrevious!=null)IconButton(onPrevious){Text("‹",color=Green,fontSize=AppTypography.Sp24)}
                            Text(tree.name,Modifier.weight(1f),color=Deep,fontSize=AppTypography.Sp20,lineHeight=AppTypography.Sp26,fontWeight=FontWeight.Bold,textAlign=TextAlign.Center)
                            if(onNext!=null)IconButton(onNext){Text("›",color=Green,fontSize=AppTypography.Sp24)}
                        }
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text(AppText.text(TextKey.CopyTienDoE59478),color=Muted,fontSize=AppTypography.Sp12);Text("${status.percentage}%",color=Green,fontSize=AppTypography.Sp12)}
                        MintProgress(status.stageProgress,Modifier.fillMaxWidth())
                    }
                }
                TreeDetailTabs(tab,{index->tab=index;onIntent(TreeIntent.SetPanelExpanded(false))})
                TreeDetailSectionContent(tree,tab,inventory,state.isLoading,onIntent,onOpenDecorations)
            }
        }
        if(state.dispositionId==tree.id)MintDialog(
            if(state.selling)AppText.text(TextKey.CopyBanCay5210c7) else AppText.text(TextKey.CopyDonCayE3d410),
            {if(!state.isLoading)onIntent(TreeIntent.DismissDisposition)},
            if(state.selling)AppText.text(TextKey.CopyBanCay5210c7) else AppText.text(TextKey.CopyDonCayE3d410),
            {onIntent(TreeIntent.ConfirmDisposition)},cancelLabel=AppText.text(TextKey.CopyGiuLai3c3ba2),
            confirmEnabled=!state.isLoading,dismissEnabled=!state.isLoading,destructive=true) {
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)) {
                GardenPlantPortrait(tree.speciesId,tree.decoration,Modifier.size(AppDimensions.Dp32),withered=!status.alive)
                Text(tree.name,color=Deep,fontSize=AppTypography.Sp13,fontWeight=FontWeight.Bold)
            }
            Text(AppText.text(TextKey.CopyCaySeRoiOTrongVinhVienKhongHoanLaiHatHoac9f6b91),color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp17)
            if(state.selling) {
                val quote=state.quote
                MintMetricStrip(listOf(AppText.text(TextKey.CopyGiaBan358aea) to AppText.text(TextKey.Copy0Xu251e68,quote?.nominal ?: 0),AppText.text(TextKey.CopyNhanHomNay78a91a) to AppText.text(TextKey.Copy0Xu251e68,quote?.granted ?: 0)))
                if((quote?.granted ?: 0)<(quote?.nominal ?: 0))
                    Text(AppText.text(TextKey.CopyPhanVuotGioiHan0XuNgaySeMatKhongDeDanhCdcca8,Balance.DAILY_CAP),
                        color=MaterialTheme.colorScheme.error,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp17)
            } else MintStatusPill(AppText.text(TextKey.CopyKhongNhanXuHoacHatA3884a))
        }
    }
}

@Preview(name="Web tree comparison",widthDp=414,heightDp=1200,device="spec:width=414dp,height=1200dp,dpi=160")
@Composable
fun TreeWebDesignPreview(){MamXanhPreviewTheme{
    TreeInteractionScreen(Tree(1,"cherry",AppText.text(TextKey.CopyDaoHongXuanC2fd99),45,"2026-11-01","FLOWERS",status=TreeStatus(stageIndex=3,stageCount=9,stageProgress=.46f)),TreeUiState(),3,mapOf("fertilizer" to 2,"fertilizer_rich" to 1),{})
}}
