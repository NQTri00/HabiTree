package com.mamxanh.app.feature.stats.presentation

import androidx.compose.ui.res.stringResource
import com.mamxanh.core.ui.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.mamxanh.app.presentation.ui.design.*

@Composable
fun StatisticsScreen(
    state: StatisticsScreenState,
    onNavigate: (StatisticsLink) -> Unit,
    onRefresh: () -> Unit,
) {
    BioIntro(stringResource(R.string.copy_nhat_ky_xanh_aa1337),stringResource(R.string.copy_nhung_dieu_da_lon_len_2c3892),stringResource(R.string.copy_moi_buoc_nho_deu_duoc_ghi_nhan_b0ce5b),{BioIconButton("settings",{onNavigate(StatisticsLink.Settings)})})
    BioMetrics(listOf(stringResource(R.string.copy_viec_hoan_thanh_e0966b) to state.total.toString(),stringResource(R.string.copy_cay_trong_vuon_6565bb) to state.treeCount.toString()))
    BioCard(Modifier.fillMaxWidth()){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Bt(stringResource(R.string.copy_7_ngay_vua_qua_66a783),Modifier.weight(1f),size=AppTypography.Size17,bold=true);BioIconButton("refresh",onRefresh)};StatisticsBars(state.dailyCompletions, state.dayLabels)}
    BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value18){BioMetrics(listOf(stringResource(R.string.copy_tuan_nay_cc1611) to state.week.toString(),stringResource(R.string.copy_hom_nay_e6747a) to state.today.toString(),stringResource(R.string.copy_chuoi_ngay_5ad2e6) to state.streak.toString()))}
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){listOf(Triple(StatisticsLink.Review,"calendar",stringResource(R.string.copy_nhin_lai_tuan_ff3f0c)),Triple(StatisticsLink.Achievements,"award",stringResource(R.string.copy_dau_moc_dffd66)),Triple(StatisticsLink.Timeline,"book",stringResource(R.string.copy_ky_niem_e3e113)),Triple(StatisticsLink.Focus,"clock",stringResource(R.string.copy_tap_trung_964002)),Triple(StatisticsLink.Settings,"settings",stringResource(R.string.copy_tuy_chon_598c1b)),Triple(StatisticsLink.More,"grid",stringResource(R.string.copy_hanh_trinh_566b59))).forEach{(r,i,t)->BioLink(t,i,{onNavigate(r)})}}
    Bt(stringResource(R.string.copy_luu_tren_thiet_bi_khong_can_tai_khoan_3ade82),Modifier.fillMaxWidth(),size=AppTypography.Size11,tint=bp.muted,align=TextAlign.Center)
}

@Composable
fun StatisticsBars(values: List<Int>, labels: List<String>) {
    val max = maxOf(4, values.maxOrNull() ?: 0)
    val barShape = RoundedCornerShape(
        topStart = AppDimensions.Dp7,
        topEnd = AppDimensions.Dp7,
        bottomStart = AppDimensions.Dp3,
        bottomEnd = AppDimensions.Dp3,
    )
    Row(
        Modifier.fillMaxWidth().heightIn(min = AppDimensions.Dp140),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp14),
        verticalAlignment = Alignment.Bottom,
    ) {
        values.forEachIndexed { index, value ->
            val label = labels[index]
            val summary = stringResource(R.string.copy_0_1_viec_hoan_thanh_0bc363, label, value)
            val height = maxOf(AppDimensions.Dp4, AppDimensions.Dp90 * (value.toFloat() / max))
            Column(
                Modifier.weight(1f).semantics(mergeDescendants = true) { contentDescription = summary },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp6),
            ) {
                Bt(value.toString(), size = AppTypography.Size9, tint = bp.muted)
                Box(
                    Modifier.widthIn(max = AppDimensions.Dp24).fillMaxWidth().height(height)
                        .background(if (index == values.lastIndex && value > 0) bp.deep else bp.accent, barShape),
                )
                Bt(label, size = AppTypography.Size9, tint = bp.muted)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun StatisticsScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        StatisticsScreen(StatisticsScreenState(9, 2, 6, 1, 3, listOf(0, 1, 0, 2, 0, 2, 1), listOf("M", "T", "W", "T", "F", "S", "S")), {}, {})
    }
}
