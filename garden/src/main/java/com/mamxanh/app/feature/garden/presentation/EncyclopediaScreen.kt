package com.mamxanh.app.feature.garden.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class CollectionGoalDisplay(val title: String, val progress: Int, val target: Int, val reward: String)

@Composable
fun EncyclopediaScreen(
    discovered: Int,
    speciesCount: Int,
    goals: List<CollectionGoalDisplay>,
    item: @Composable (Int, Modifier) -> Unit,
) {
    Bt(AppText.text(TextKey.CopyMoiLoaiMotCauChuyen3baf0d),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1)
    val n = discovered
    BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value18){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Bt(AppText.text(TextKey.CopyBoSuuTam539bd6),semi=true);Bt("$n/${speciesCount} · ${n*100/speciesCount.coerceAtLeast(1)}%",semi=true)};BioProgress(n.toFloat()/speciesCount.coerceAtLeast(1));Bt(AppText.text(TextKey.CopyGiuLichSuKhiDonHoacBanCay42a565),size=AppTypography.Size12,tint=bp.muted)}
    BioGrid(speciesCount, 2, content = item)
    BioSection(AppText.text(TextKey.CopyDauMocSuuTam1b494b))
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
        goals.forEach{g->
            BioCard(Modifier.fillMaxWidth()){
                Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
                    Bt(g.title,Modifier.weight(1f),size=AppTypography.Size14,bold=true)
                    Bt("${g.progress}/${g.target}",size=AppTypography.Size12)
                    if(g.progress>=g.target){Spacer(Modifier.width(AppDimensions.Dp18));Bi("check")}
                }
                Bt(when(g.reward){"reward_sign"->AppText.text(TextKey.CopyBienNhaVuonD2e9b7);"reward_petals"->AppText.text(TextKey.CopyCanhHoa137700);else->AppText.text(TextKey.CopyHuyHieuBoSuuTam521407)},size=AppTypography.Size12,tint=bp.muted)
                Spacer(Modifier.height(AppDimensions.Dp8));BioProgress(g.progress.toFloat()/g.target)
            }
        }
    }
}

data class SpeciesTreeDisplay(val id: String, val name: String)

data class SpeciesDisplay(
    val id: String,
    val name: String,
    val description: String,
    val tier: String,
    val status: String,
    val discovered: Boolean,
    val highest: Int,
    val stages: Int,
    val planted: Int,
    val owned: Int,
    val living: Int,
    val firstPlanted: String,
    val firstMatured: String,
    val highestTree: Int?,
    val trees: List<SpeciesTreeDisplay>,
)

@Composable
fun SpeciesScreen(state: SpeciesDisplay, onTree: (String) -> Unit, onShop: () -> Unit) {
    BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value18,radius=AppRadii.Value26){Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally){BioTag(state.tier+" · "+state.status);if(state.discovered){Spacer(Modifier.height(AppDimensions.Dp2));BioPlant(state.id,Modifier.fillMaxWidth().height(AppDimensions.Dp220))}else Box(Modifier.fillMaxWidth().height(AppDimensions.Dp140),contentAlignment=Alignment.Center){Bi("leaf",size=AppIcons.Size36)};Bt(state.name,size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1,align=TextAlign.Center);Bt(state.description,size=AppTypography.Size12,tint=bp.muted,align=TextAlign.Center)}}
    BioSection(AppText.text(TextKey.CopyCacGiaiDoan01Bee217,state.highest,state.stages))
    BioGrid(state.stages,if(LocalDensity.current.fontScale>1.2f)2 else 3){i,m->val reached=i<state.highest;Column(m.background(if(reached)bp.soft else bp.surface,RoundedCornerShape(AppDimensions.Dp12)).padding(AppDimensions.Dp10),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp5)){Bt("${i+1}",size=AppTypography.Size22,mono=true,tint=if(reached)bp.deep else bp.muted);Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)){Bi(if(reached)"check"else "lock",size=AppIcons.Size12,tint=bp.muted);Bt(if(reached)AppText.text(TextKey.CopyDaDat1b9e88)else AppText.text(TextKey.CopyChuaDatF4aa8f),size=AppTypography.Size11,tint=bp.muted)}}}
    BioCard(Modifier.fillMaxWidth()){BioFacts(listOf(AppText.text(TextKey.CopyDaTrong42fa2b) to state.planted.toString(),AppText.text(TextKey.CopyTrongTui905deb) to state.owned.toString(),AppText.text(TextKey.CopyDangSongE3573d) to state.living.toString(),AppText.text(TextKey.CopyTrongDauTien2e1aed) to bioDisplayDate(state.firstPlanted),AppText.text(TextKey.CopyTruongThanhDauDd677a) to bioDisplayDate(state.firstMatured),AppText.text(TextKey.CopyCayCaoNhatDangSong7d778f) to (state.highestTree?.let { "$it/${state.stages}" } ?: AppText.text(TextKey.CopyChuaCoBc2b97))))}
    BioDetails(AppText.text(TextKey.CopyChamSocLoaiF520bc)){Bt(AppText.text(TextKey.CopyBinhTuoi8121824GioBonPhanKhiCayDangLonVCf24d9),size=AppTypography.Size12,tint=bp.muted)}
    state.trees.forEach { BioLink(it.name, "garden", { onTree(it.id) }) }
    BioButton(AppText.text(TextKey.CopyTimHatA56320),onShop,Modifier.fillMaxWidth(),"shop")
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun EncyclopediaScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        EncyclopediaScreen(0, 0, emptyList()) { _, _ -> }
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun SpeciesScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        SpeciesScreen(
            SpeciesDisplay("cherry", "Spring cherry", "", "Rare", "Undiscovered", false, 0, 9, 0, 0, 0, "", "", null, emptyList()),
            {},
            {},
        )
    }
}
