package com.mamxanh.app.feature.garden.presentation

import com.mamxanh.app.presentation.ui.design.AppThemeEffects
import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppOpacity
import com.mamxanh.app.presentation.ui.design.AppTypography
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.localization.AppText
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.model.*

@Composable internal fun Encyclopedia(state:LivingGardenUiState,onOpen:(String,String)->Unit) {
    val records=state.snapshot.species
    val discovered=records.count{it.discoveredAt>0}
    GardenScreenIntro(AppText.text(TextKey.CopyMoiLoaiMotCauChuyen3baf0d))
    GardenPanel {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            GardenHeading(AppText.text(TextKey.CopyBoSuuTam539bd6),Modifier.weight(1f));Text("$discovered/${catalog.size} · ${discovered*100/catalog.size}%",color=Green,fontSize=AppTypography.Sp11,fontWeight=FontWeight.Bold)
        }
        PlotProgress(discovered.toFloat()/catalog.size)
        GardenCaption(AppText.text(TextKey.Copy0LoaiTrongDanhMucGiuLichSuKhiDonHoacBanCAc2aaf,catalog.size))
    }
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val columns=if(maxWidth<AppDimensions.Dp340 || LocalDensity.current.fontScale>AppThemeEffects.CollectionLargeFontScale)2 else 3
        Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
            catalog.chunked(columns).forEach{row->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
                row.forEach{s->
                    val r=records.firstOrNull{it.id==s.id} ?: SpeciesRecord(s.id)
                    Box(Modifier.weight(1f)) {
                        SmallCatalogTile(s.name,r.status,{onOpen("SPECIES",s.id)}) {
                            if(r.discoveredAt>0)GardenPlantPortrait(s.id,"NONE",it)
                            else Icon(Icons.Rounded.Spa,null,it.padding(AppDimensions.Dp5),tint=Green.copy(alpha=AppOpacity.Value_35))
                        }
                    }
                };repeat(columns-row.size){Spacer(Modifier.weight(1f))}
            }}
        }
    }
    GardenPanel {
        GardenHeading(AppText.text(TextKey.CopyDauMocSuuTam1b494b))
        state.snapshot.goals.forEachIndexed{index,goal->
            if(index>0)HorizontalDivider(color=Green.copy(alpha=AppOpacity.Value_08))
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
                val reward=gardenCosmetics.firstOrNull{it.id==goal.reward}
                if(reward!=null)CosmeticArtwork(reward,Modifier.size(AppDimensions.Dp22))
                else Icon(Icons.Rounded.WorkspacePremium,null,Modifier.size(AppDimensions.Dp22),tint=Green)
                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp3)) {
                    Text(goal.title,color=Deep,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp14,fontWeight=FontWeight.SemiBold)
                    GardenCaption(reward?.name ?: AppText.text(TextKey.CopyHuyHieuBoSuuTam521407))
                    PlotProgress(goal.progress.toFloat()/goal.target)
                }
                if(goal.unlockedAt>0)Icon(Icons.Rounded.CheckCircle,null,Modifier.size(AppDimensions.Dp15),tint=Green)
                else Text("${goal.progress.coerceAtMost(goal.target)}/${goal.target}",color=Muted,fontSize=AppTypography.Sp10)
            }
        }
    }
}

@Composable internal fun SpeciesDetail(state:LivingGardenUiState,onTree:(Long)->Unit,onShop:()->Unit) {
    val s=catalog.firstOrNull{it.id==state.selected} ?: run{GardenCaption(AppText.text(TextKey.CopyLoaiChuaDuocHoTro9107c4));return}
    val r=state.snapshot.species.firstOrNull{it.id==s.id} ?: SpeciesRecord(s.id)
    GardenBotanicalFrame(Modifier.fillMaxWidth(),skin=GardenPanelSkin.HINT,contentPadding=PaddingValues(AppDimensions.Dp18),cornerSize=AppDimensions.Dp28) {
        Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)) {
            MintStatusPill("${s.tier} · ${r.status}")
            if(r.discoveredAt>0)GardenPlantPortrait(s.id,"NONE",Modifier.fillMaxWidth().height(AppDimensions.Dp220))
            else Box(Modifier.fillMaxWidth().height(AppDimensions.Dp220),contentAlignment=Alignment.Center){Icon(Icons.Rounded.Spa,null,Modifier.size(AppDimensions.Dp72),tint=Green)}
            Text(s.name,color=Deep,fontSize=AppTypography.Sp26,lineHeight=AppTypography.Sp34,fontWeight=FontWeight.Bold)
            GardenCaption(s.description)
        }
    }
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
        Row(Modifier.fillMaxWidth()){GardenHeading(AppText.text(TextKey.CopyCacGiaiDoanD4547f),Modifier.weight(1f));GardenCaption("${(r.highestStage+1).coerceAtLeast(0)}/${s.formCount}")}
        val columns=3
        s.profile.stageThresholds.indices.toList().chunked(columns).forEach{row->
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp5)) {
                row.forEach{index->
                    Column(Modifier.weight(1f).background(if(index<=r.highestStage)AppColors.SwatchFFEDF5E9 else AppColors.SwatchFFF0F4EF,RoundedCornerShape(AppDimensions.Dp9))
                        .padding(vertical=AppDimensions.Dp4),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp2)) {
                        if(index<=r.highestStage)GardenPlantPortrait(s.id,"NONE",Modifier.size(AppDimensions.Dp64))
                        else Box(Modifier.size(AppDimensions.Dp64),contentAlignment=Alignment.Center){Icon(Icons.Rounded.Lock,null,Modifier.size(AppDimensions.Dp14),tint=Muted.copy(alpha=AppOpacity.Value_55))}
                        Text((index+1).toString(),color=Muted,fontSize=AppTypography.Sp9,lineHeight=AppTypography.Sp12)
                    }
                };repeat(columns-row.size){Spacer(Modifier.weight(1f))}
            }
        }
    }
    GardenPanel {
        listOf(AppText.text(TextKey.CopyDaTrong42fa2b) to r.planted.toString(),AppText.text(TextKey.CopyTrongTui905deb) to r.ownedSeeds.toString(),AppText.text(TextKey.CopyDangSongE3573d) to r.living.toString(),AppText.text(TextKey.CopyTrongDauTien2e1aed) to dateLabel(r.firstPlanted),AppText.text(TextKey.CopyTruongThanhDauDd677a) to dateLabel(r.firstMatured),AppText.text(TextKey.CopyCayCaoNhatDangSong7d778f) to if(r.tallestStage>=0)"${r.tallestStage+1}/${s.formCount}" else AppText.text(TextKey.CopyChuaGhiNhanF7654d)).forEachIndexed{index,(label,value)->
            if(index>0)HorizontalDivider(color=AppColors.SwatchFFE4E7DC)
            Row(Modifier.fillMaxWidth().padding(vertical=AppDimensions.Dp8),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){Text(label,Modifier.weight(1f),color=Muted,fontSize=AppTypography.Sp12);Text(value,color=Deep,fontSize=AppTypography.Sp12)}
        }
    }
    GardenExpandableSection(AppText.text(TextKey.CopyChamSocLoaiF520bc)){GardenCaption(AppText.text(TextKey.CopyBinhTuoi8121824GioPhanBonDungKhiCayDangDe84c6))}
    state.garden.trees.filter{it.speciesId==s.id}.forEach{tree->GardenNavigationCard(tree.name,{onTree(tree.id)})}
    GardenPaintedButton(AppText.text(TextKey.CopyTimHatA56320),onShop,Modifier.fillMaxWidth())

}

@Composable internal fun AppearanceContent(state:LivingGardenUiState,reducedMotion:Boolean,onReducedMotion:(Boolean)->Unit,
    onAct:(LivingGardenAction)->Unit,onMemories:()->Unit) {
    val initial=state.snapshot.appearance
    var nickname by rememberSaveable{mutableStateOf(initial.nickname)}
    var theme by rememberSaveable{mutableStateOf(initial.theme)}
    var density by rememberSaveable{mutableStateOf(initial.density)}
    var battery by rememberSaveable{mutableStateOf(initial.batterySaver)}
    GardenCaption(AppText.text(TextKey.CopyTu07eb90b,dateLabel(initial.createdAt)))
    GardenTextField(nickname,{nickname=it.take(40)},AppText.text(TextKey.CopyTenVuon1b0761))
    MintMetricStrip(listOf(AppText.text(TextKey.CopyLoaiCayDd9342) to state.garden.trees.map{it.speciesId}.distinct().size.toString(),AppText.text(TextKey.CopyTruongThanh8c14d7) to state.garden.trees.count{it.status.state==TreeGrowthState.MATURE}.toString(),AppText.text(TextKey.CopyTrangTriD48231) to state.snapshot.placements.size.toString()))
    GardenChoice(AppText.text(TextKey.CopyChuDe59500d),theme,(listOf("MEADOW" to AppText.text(TextKey.CopyDongCo09da3d))+gardenCosmetics.filter{it.category=="THEME"}.map{it.id to it.name}),{theme=it}){it=="MEADOW"||(state.snapshot.owned[it]?:0)>0}
    GardenChoice(AppText.text(TextKey.CopyHatBayA6343b),density,listOf("OFF" to AppText.text(TextKey.CopyTat328606),"LOW" to AppText.text(TextKey.CopyItA3681b),"NORMAL" to AppText.text(TextKey.CopyVua8af1c0)),{density=it})
    GardenPanel {
        SettingToggle(AppText.text(TextKey.CopyTietKiemPinB1018c),battery,{battery=it},Icons.Rounded.BatterySaver)
        HorizontalDivider(color=AppColors.SwatchFFE4E7DC)
        SettingToggle(AppText.text(TextKey.CopyGiamChuyenDongFf3295),reducedMotion,onReducedMotion,Icons.Rounded.MotionPhotosOff)
    }
    GardenCaption(AppText.text(TextKey.CopyGiamChuyenDongHoacTietKiemPinTatHatBayAmT91e597))
    GardenPaintedButton(AppText.text(TextKey.CopyLuuDienMao105663),{onAct(LivingGardenAction.Appearance(initial.copy(nickname=nickname,theme=theme,density=density,batterySaver=battery,muted=true)))},Modifier.fillMaxWidth(),enabled=!state.busy&&nickname.isNotBlank())
    GardenNavigationCard(AppText.text(TextKey.CopyKyNiemE3e113),onMemories,art=GardenArt.JOURNAL)
}

@Composable private fun GardenChoice(label:String,value:String,options:List<Pair<String,String>>,onChange:(String)->Unit,enabled:(String)->Boolean={true}) {
    var expanded by remember{mutableStateOf(false)}
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)) {
        GardenCaption(label)
        Box {
            GardenPaintedButton((options.firstOrNull{it.first==value}?.second?:value)+"  ⌄",{expanded=true},Modifier.fillMaxWidth(),primary=false)
            DropdownMenu(expanded,{expanded=false}){options.forEach{(id,name)->DropdownMenuItem(text={Text(name)},onClick={onChange(id);expanded=false},enabled=enabled(id))}}
        }
    }
}
@Composable private fun SettingToggle(label:String,checked:Boolean,onChecked:(Boolean)->Unit,icon:androidx.compose.ui.graphics.vector.ImageVector) {
    Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp48).clickable{onChecked(!checked)},verticalAlignment=Alignment.CenterVertically,
        horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
        Icon(icon,null,Modifier.size(AppDimensions.Dp16),tint=Green)
        Text(label,Modifier.weight(1f),color=Deep,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19)
        Switch(checked,onChecked,Modifier.graphicsLayer(scaleX=.75f,scaleY=.75f),
            colors=SwitchDefaults.colors(checkedTrackColor=Green,uncheckedTrackColor=AppColors.SwatchFFDCE9DF,uncheckedBorderColor=AppColors.SwatchFFB7CFC0))
    }
}
@Composable internal fun ExpansionContent(state:LivingGardenUiState,onAct:(LivingGardenAction)->Unit) {
    val section=state.snapshot.sections.firstOrNull{it.id==state.selected.toIntOrNull()} ?: return
    Column(Modifier.fillMaxWidth().padding(vertical=AppDimensions.Dp24),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)) {
        Icon(Icons.Rounded.GridView,null,Modifier.size(AppDimensions.Dp44),tint=Green)
        Text(AppText.text(TextKey.CopyKhuVuon0C58827,section.id),color=Deep,fontSize=AppTypography.Sp26,lineHeight=AppTypography.Sp34,fontWeight=FontWeight.Bold)
        GardenCaption(AppText.text(TextKey.CopyThem6OGiuNguyenCayHienCoF944b2))
    }
    section.plots.chunked(3).forEach{row->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){row.forEach{id->
        GardenBotanicalFrame(Modifier.weight(1f),contentPadding=PaddingValues(AppDimensions.Dp18),cornerSize=AppDimensions.Dp18){Text(id.toString(),Modifier.fillMaxWidth(),textAlign=TextAlign.Center,color=Deep,fontSize=AppTypography.Sp14)}
    }}}
    MintMetricStrip(listOf(AppText.text(TextKey.CopyGiaMoKhuD7a20a) to AppText.text(TextKey.Copy0Xu251e68,section.price),AppText.text(TextKey.CopyViHienTai127da5) to AppText.text(TextKey.Copy0Xu251e68,state.snapshot.coins)))
    GardenPaintedButton(AppText.text(TextKey.CopyMoKhu0XuD27450,section.price),{onAct(LivingGardenAction.Expand(section.id,newKey()))},Modifier.fillMaxWidth(),enabled=!state.busy&&!section.unlocked&&state.snapshot.coins>=section.price)
    if(state.snapshot.coins<section.price)GardenCaption(AppText.text(TextKey.CopyCanThem0Xu8837ee,section.price-state.snapshot.coins))
}

@Preview(name="Compact appearance · owner refresh",widthDp=360,heightDp=620,device="spec:width=360dp,height=620dp,dpi=160")
@Composable fun CompactAppearanceOwnerPreview() {
    MamXanhPreviewTheme {ProvideTextStyle(TextStyle(fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19)) {
        Column(Modifier.fillMaxWidth().padding(AppDimensions.Dp10),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
            AppearanceContent(LivingGardenUiState(loading=false),false,{}, {}, {})
        }
    }}
}

private fun webComparisonGardenState()=LivingGardenUiState(loading=false,route="STUDIO",selected="accent_lantern",
 snapshot=LivingGardenSnapshot(coins=620,owned=mapOf("accent_lantern" to 1),species=listOf(SpeciesRecord("cherry",discoveredAt=1,highestStage=3,planted=1,firstPlanted=1793494800000,ownedSeeds=2,living=1,tallestStage=3),SpeciesRecord("pine",discoveredAt=1,highestStage=2,planted=1,living=1),SpeciesRecord("succulent",discoveredAt=1,highestStage=5,planted=1,firstMatured=1)),sections=(1..4).map{GardenSection(it,((it-1)*6+1..it*6).toList(),it==1,5400)}),
 garden=Garden(trees=listOf(Tree(1,"cherry",AppText.text(TextKey.CopyDaoHongXuanC2fd99),45,"2026-11-01",status=TreeStatus(stageIndex=3,stageCount=9,stageProgress=.46f)),Tree(2,"pine",AppText.text(TextKey.CopyThongNho84a4db),24,"2026-11-04")),plots=(1..24).map{GardenPlot(it,it<=5,if(it<=2)it.toLong()else null)}))
@Preview(widthDp=414,heightDp=1000,device="spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun StudioWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState(),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}
@Preview(widthDp=414,heightDp=1000,device="spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun CosmeticWebDesignPreview(){MamXanhPreviewTheme{MintBackdrop{Column(Modifier.padding(AppDimensions.Dp24),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp18)){CosmeticDetail(webComparisonGardenState(),{_,_->},{})}}}}


@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun DecorShopWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState().copy(route="SHOP",selected=""),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun DecorInventoryWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState().copy(route="INVENTORY",selected=""),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun EncyclopediaWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState().copy(route="ENCYCLOPEDIA",selected=""),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun SpeciesWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState().copy(route="SPECIES",selected="cherry"),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun AppearanceWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState().copy(route="APPEARANCE",selected=""),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}

@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun ExpansionWebDesignPreview(){MamXanhPreviewTheme{LivingGardenScreen(webComparisonGardenState().copy(route="EXPANSION",selected="2"),{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},false,{},true)}}
