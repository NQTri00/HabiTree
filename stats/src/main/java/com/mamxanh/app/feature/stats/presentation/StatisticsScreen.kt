package com.mamxanh.app.feature.stats.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class StatisticsScreenState(
    val total: Int,
    val treeCount: Int,
    val week: Int,
    val today: Int,
    val streak: Int,
    val dailyCompletions: List<Int>,
    val dayLabels: List<String>,
)

enum class StatisticsLink { Review, Achievements, Timeline, Focus, Settings, More }

@Composable
fun StatisticsScreen(
    state: StatisticsScreenState,
    onNavigate: (StatisticsLink) -> Unit,
    onRefresh: () -> Unit,
) {
    BioIntro(AppText.text(TextKey.CopyNhatKyXanhAa1337),AppText.text(TextKey.CopyNhungDieuDaLonLen2c3892),AppText.text(TextKey.CopyMoiBuocNhoDeuDuocGhiNhanB0ce5b),{BioIconButton("settings",{onNavigate(StatisticsLink.Settings)})})
    BioMetrics(listOf(AppText.text(TextKey.CopyViecHoanThanhE0966b) to state.total.toString(),AppText.text(TextKey.CopyCayTrongVuon6565bb) to state.treeCount.toString()))
    BioCard(Modifier.fillMaxWidth()){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Bt(AppText.text(TextKey.Copy7NgayVuaQua66a783),Modifier.weight(1f),size=AppTypography.Size17,bold=true);BioIconButton("refresh",onRefresh)};StatisticsBars(state.dailyCompletions, state.dayLabels)}
    BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value18){BioMetrics(listOf(AppText.text(TextKey.CopyTuanNayCc1611) to state.week.toString(),AppText.text(TextKey.CopyHomNayE6747a) to state.today.toString(),AppText.text(TextKey.CopyChuoiNgay5ad2e6) to state.streak.toString()))}
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){listOf(Triple(StatisticsLink.Review,"calendar",AppText.text(TextKey.CopyNhinLaiTuanFf3f0c)),Triple(StatisticsLink.Achievements,"award",AppText.text(TextKey.CopyDauMocDffd66)),Triple(StatisticsLink.Timeline,"book",AppText.text(TextKey.CopyKyNiemE3e113)),Triple(StatisticsLink.Focus,"clock",AppText.text(TextKey.CopyTapTrung964002)),Triple(StatisticsLink.Settings,"settings",AppText.text(TextKey.CopyTuyChon598c1b)),Triple(StatisticsLink.More,"grid",AppText.text(TextKey.CopyHanhTrinh566b59))).forEach{(r,i,t)->BioLink(t,i,{onNavigate(r)})}}
    Bt(AppText.text(TextKey.CopyLuuTrenThietBiKhongCanTaiKhoan3ade82),Modifier.fillMaxWidth(),size=AppTypography.Size11,tint=bp.muted,align=TextAlign.Center)
}

@Composable
fun StatisticsBars(values: List<Int>, labels: List<String>) {
    val max = maxOf(1, values.maxOrNull() ?: 0)
    Row(Modifier.fillMaxWidth().height(AppDimensions.Dp112),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10),verticalAlignment=Alignment.Bottom){values.forEachIndexed{i,value->Column(Modifier.weight(1f).semantics(mergeDescendants=true){contentDescription=AppText.text(TextKey.Copy01ViecHoanThanh0bc363,labels[i],value)},horizontalAlignment=Alignment.CenterHorizontally){Bt(value.toString(),size=AppTypography.Size12,tint=bp.muted);Spacer(Modifier.height(AppDimensions.Dp4));Box(Modifier.width(AppDimensions.Dp24).height((value.toFloat()/max*66).dp).background(if(i==6)bp.deep else bp.accent,RoundedCornerShape(topStart=AppDimensions.Dp6,topEnd=AppDimensions.Dp6)));HorizontalDivider(color=bp.line,thickness=AppDimensions.Dp1);Spacer(Modifier.height(AppDimensions.Dp5));Bt(labels[i],size=AppTypography.Size12,tint=bp.muted)}}}
}
