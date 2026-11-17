package com.mamxanh.app.feature.tree.presentation

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.feature.tree.presentation.components.DecorationPicker
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.model.*

@Composable
internal fun TreeDetailTabs(selected: Int, onSelect: (Int)->Unit) {
    Row(Modifier.fillMaxWidth().selectableGroup()
        .background(Pale,RoundedCornerShape(AppDimensions.Dp15)).padding(AppDimensions.Dp4),
        horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp3)) {
        listOf(AppText.text(TextKey.CopyChamSoc7b7df6),AppText.text(TextKey.CopyTrangTriD48231),AppText.text(TextKey.CopyThongTin4aa16c)).forEachIndexed { index,label ->
            Box(Modifier.weight(1f).heightIn(min=AppDimensions.Dp48)
                .selectable(selected==index,role=Role.Tab,onClick={onSelect(index)}),
                contentAlignment=Alignment.Center) {
                Surface(Modifier.fillMaxWidth(),shape=RoundedCornerShape(AppDimensions.Dp9),
                    color=if(selected==index)AppColors.White else AppColors.Transparent,
                    shadowElevation=if(selected==index)AppDimensions.Dp2 else AppDimensions.Dp0) {
                    Column(Modifier.padding(vertical=AppDimensions.Dp6),horizontalAlignment=Alignment.CenterHorizontally,
                        verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)) {
                        Text(label,color=if(selected==index)Green else Muted,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19,
                            fontWeight=if(selected==index)FontWeight.Bold else FontWeight.Medium,textAlign=TextAlign.Center)
                        Box(Modifier.width(AppDimensions.Dp16).height(AppDimensions.Dp2)
                            .background(if(selected==index)Green else AppColors.Transparent,RoundedCornerShape(50)))
                    }
                }
            }
        }
    }
}

/** Content has natural height: runtime scrolls it; previews show every row. */
@Composable
internal fun TreeDetailSectionContent(tree: Tree, selected: Int, inventory: Map<String,Int>,
    busy: Boolean, onIntent: (TreeIntent)->Unit, onOpenDecorations:()->Unit={}) {
    val status=tree.status
    Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
        when(selected) {
            0 -> {
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
                    GardenArtwork(GardenArt.WATERING_CAN,Modifier.size(AppDimensions.Dp24))
                    Column(Modifier.weight(1f)) {
                        Text(status.waterLabel,color=Deep,fontSize=AppTypography.Sp15,lineHeight=AppTypography.Sp21,fontWeight=FontWeight.SemiBold)
                        Text(status.cue.label,color=Muted,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19)
                    }
                    GardenPaintedButton(if(status.waterActive)AppText.text(TextKey.CopyDaTuoiDe2042) else AppText.text(TextKey.CopyTuoi617613),{onIntent(TreeIntent.Water(tree.id))},enabled=status.canWater&&!busy)
                }
                if(status.fertilizerLabel.isNotBlank())MintStatusPill(status.fertilizerLabel)
                if(status.state==TreeGrowthState.GROWING) {
                    HorizontalDivider(color=AppColors.SwatchFFE2ECE5)
                    Text(AppText.text(TextKey.CopyDinhDuong3c8978),color=Deep,fontSize=AppTypography.Sp15,lineHeight=AppTypography.Sp21,fontWeight=FontWeight.Bold)
                    Text(if(!status.waterActive)AppText.text(TextKey.CopyTuoiCayTruocKhiBonPhanCfd0cb) else AppText.text(TextKey.CopyChonMotLoaiPhanTrongTui8bb0e2),
                        color=Muted,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19)
                    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)) {
                        FertilizerTier.entries.forEach { tier ->
                            val count=inventory[tier.itemId] ?: 0
                            val available=status.canFertilize && count>0 && !busy
                            Surface(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp48)
                                .clickable(enabled=available,role=Role.Button,onClick={
                                    onIntent(TreeIntent.Fertilize(tree.id,tier.itemId))
                                }),shape=RoundedCornerShape(AppDimensions.Dp18),color=AppColors.White,shadowElevation=AppDimensions.Dp4,
                                border=BorderStroke(AppDimensions.Dp1,AppColors.SwatchFFE2ECE5)) {
                                Row(Modifier.padding(horizontal=AppDimensions.Dp14,vertical=AppDimensions.Dp14),verticalAlignment=Alignment.CenterVertically,
                                    horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
                                    GardenArtwork(GardenArt.FERTILIZER,Modifier.size(AppDimensions.Dp24))
                                    Column(Modifier.weight(1f)) {
                                        Text(tier.label,color=Deep,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19,fontWeight=FontWeight.SemiBold)
                                        Text(AppText.text(TextKey.CopyTrongTui079a061,count),color=Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16)
                                    }
                                    Text("${tier.rate/1000.0}×",color=Green,fontSize=AppTypography.Sp11,fontWeight=FontWeight.Bold)
                                    Surface(shape=RoundedCornerShape(AppDimensions.Dp8),color=if(available)Green else AppColors.SwatchFFE1EBE5) {
                                        Text(if(available)AppText.text(TextKey.CopyBon4fd741) else if(count==0)AppText.text(TextKey.CopyHetA89ea0) else AppText.text(TextKey.CopyCho8de7e9),
                                            Modifier.padding(horizontal=AppDimensions.Dp8,vertical=AppDimensions.Dp4),
                                            color=if(available)AppColors.White else Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16)
                                    }
                                }
                            }
                        }
                    }
                    TextButton(onClick={onIntent(TreeIntent.OpenFertilizerShop)},enabled=!busy,
                        contentPadding=PaddingValues(horizontal=AppDimensions.Dp4)) {
                        Text(AppText.text(TextKey.CopyBoSungVatPham0a35b0),color=Green,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19)
                    }
                } else Text(if(status.alive)AppText.text(TextKey.CopyCayDaTruongThanhTiepTucTuoiDeGiuCayKhoeMa06b5ad)
                    else AppText.text(TextKey.CopyCayDaKhoHeoVaKhongTheHoiPhucF189fa),color=Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16)
            }
            1 -> {
                Text(AppText.text(TextKey.CopyDiemToChoCay027c8d),color=Deep,fontSize=AppTypography.Sp15,lineHeight=AppTypography.Sp21,fontWeight=FontWeight.Bold)
                Text(AppText.text(TextKey.CopyXemCacPhuKienTrongTuiDo6602d1),color=Muted,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19)
                if(status.alive)GardenPaintedButton(AppText.text(TextKey.CopyTrangTriCuaBanDa2761),onOpenDecorations,Modifier.fillMaxWidth(),primary=false,enabled=!busy)
                else Text(AppText.text(TextKey.CopyKhongTheTrangTriCayDaKhoHeo8dfe04),color=Muted,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19)
            }
            else -> {
                Text(AppText.text(TextKey.CopyHoSoCay3c00b9),color=Deep,fontSize=AppTypography.Sp15,lineHeight=AppTypography.Sp21,fontWeight=FontWeight.Bold)
                val species=catalog.first { it.id==tree.speciesId }
                Column(Modifier.fillMaxWidth().mintDepth(RoundedCornerShape(AppDimensions.Dp22),MintDepth.card,AppDimensions.Dp6)
                    .padding(AppDimensions.Dp18)) {
                    listOf(AppText.text(TextKey.CopyLoaiD0aa6d) to species.name,AppText.text(TextKey.CopyGiaiDoanAa70a1) to "${status.stageIndex+1} / ${status.stageCount}",
                        AppText.text(TextKey.CopyPhatTrien2d33cb) to "${status.percentage}%",AppText.text(TextKey.CopyNgayTrong2eb870) to tree.planted).forEachIndexed { index,(label,value) ->
                        if(index>0)HorizontalDivider(color=AppColors.SwatchFFE2ECE5)
                        Row(Modifier.fillMaxWidth().padding(vertical=AppDimensions.Dp8),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
                            Text(label,Modifier.weight(.4f),color=Muted,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19)
                            Text(value,Modifier.weight(.6f),color=Deep,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19,
                                fontWeight=FontWeight.Medium,textAlign=TextAlign.End)
                        }
                    }
                }
                Text(species.description,color=Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16)
            }
        }
        HorizontalDivider(color=AppColors.SwatchFFE2ECE5)
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
            if(status.state==TreeGrowthState.MATURE)TextButton(onClick={onIntent(TreeIntent.AskDisposition(tree.id,true))},
                enabled=!busy,contentPadding=PaddingValues(horizontal=AppDimensions.Dp4)) {
                Text(AppText.text(TextKey.CopyBanCay5210c7),color=Green,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19)
            } else Spacer(Modifier.weight(1f))
            TextButton(onClick={onIntent(TreeIntent.AskDisposition(tree.id,false))},enabled=!busy,
                contentPadding=PaddingValues(horizontal=AppDimensions.Dp4)) {
                Text(AppText.text(TextKey.CopyDonCayE3d410),color=Muted,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19)
            }
        }
    }
}

private fun sectionPreviewTree()=Tree(1,"cherry",AppText.text(TextKey.CopyDaoHongXuanC2fd99),45,"2026-11-01","FLOWERS",
    status=TreeStatus(stageIndex=3,stageCount=9,stageProgress=.46f,wateredUntil=100,sampledAt=1,cue=CareCue.HEALTHY))

@Composable
private fun SectionDocument(selected: Int) {
    MamXanhPreviewTheme {
                Column(Modifier.fillMaxWidth().background(Cream).padding(AppDimensions.Dp24),
            verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
            TreeDetailTabs(selected,{})
            TreeDetailSectionContent(sectionPreviewTree(),selected,mapOf("fertilizer" to 2,"fertilizer_rich" to 1),false,{})
        }
    }
}

@Preview(name="Care · complete content",widthDp=360)
@Composable fun TreeCareFullContentPreview(){SectionDocument(0)}
@Preview(name="Decoration · complete content",widthDp=360)
@Composable fun TreeDecorationFullContentPreview(){SectionDocument(1)}
@Preview(name="Information · complete content",widthDp=360)
@Composable fun TreeInformationFullContentPreview(){SectionDocument(2)}
@Preview(name="Care · narrow, large text, complete content",widthDp=320,fontScale=1.3f)
@Composable fun TreeCareNarrowContentPreview(){SectionDocument(0)}
