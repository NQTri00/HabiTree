package com.mamxanh.app.feature.catalog.presentation

import androidx.compose.ui.res.stringResource
import com.mamxanh.core.ui.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.feature.catalog.domain.CollectionMilestone
import com.mamxanh.app.presentation.ui.design.*

data class CollectionGoalDisplay(val title: String, val progress: Int, val target: Int, val reward: String)

fun List<CollectionMilestone>.toEncyclopediaGoals(): List<CollectionGoalDisplay> =
    filter { it.id != "expansion" && it.id != "section" }
        .map { CollectionGoalDisplay(it.title, it.progress, it.target, it.reward) }

@Composable
fun EncyclopediaScreen(
    discovered: Int,
    speciesCount: Int,
    goals: List<CollectionGoalDisplay>,
    item: @Composable (Int, Modifier) -> Unit,
) {
    Bt(stringResource(R.string.copy_moi_loai_mot_cau_chuyen_3baf0d),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1)
    val n = discovered
    BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value18){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Bt(stringResource(R.string.copy_bo_suu_tam_539bd6),semi=true);Bt("$n/${speciesCount} · ${n*100/speciesCount.coerceAtLeast(1)}%",semi=true)};BioProgress(n.toFloat()/speciesCount.coerceAtLeast(1));Bt(stringResource(R.string.copy_giu_lich_su_khi_don_hoac_ban_cay_42a565),size=AppTypography.Size12,tint=bp.muted)}
    val columns = if (LocalConfiguration.current.screenWidthDp >= AppThemeEffects.ThreeColumnCatalogWidth && LocalDensity.current.fontScale <= AppThemeEffects.LargeFontScale) 3 else 2
    BioGrid(speciesCount, columns, content = item)
    BioSection(stringResource(R.string.copy_dau_moc_suu_tam_1b494b))
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
        goals.forEach{g->
            BioCard(Modifier.fillMaxWidth()){
                Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
                    Bt(g.title,Modifier.weight(1f),size=AppTypography.Size14,bold=true)
                    Bt("${g.progress}/${g.target}",size=AppTypography.Size12)
                    if(g.progress>=g.target){Spacer(Modifier.width(AppDimensions.Dp18));Bi("check")}
                }
                Bt(when(g.reward){"reward_sign"->stringResource(R.string.copy_bien_nha_vuon_d2e9b7);"reward_petals"->stringResource(R.string.copy_canh_hoa_137700);else->stringResource(R.string.copy_huy_hieu_bo_suu_tam_521407)},size=AppTypography.Size12,tint=bp.muted)
                Spacer(Modifier.height(AppDimensions.Dp8));BioProgress(g.progress.toFloat()/g.target)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun EncyclopediaScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        EncyclopediaScreen(0, 0, emptyList()) { _, _ -> }
    }
}
