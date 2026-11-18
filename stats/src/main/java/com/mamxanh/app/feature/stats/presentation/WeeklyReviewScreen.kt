package com.mamxanh.app.feature.stats.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class WeeklyReviewDisplay(
    val startDisplay: String,
    val reflection: String,
    val completed: Int,
    val scheduled: Int,
    val focusMinutes: Int,
    val previousRate: Int?,
    val earned: Int,
    val spent: Int,
    val careDays: Int,
    val milestones: Int,
    val consistent: String,
    val improve: String,
    val tickets: Int,
    val dailyCompletions: List<Int>,
    val dayLabels: List<String>,
)

@Composable
fun WeeklyReviewScreen(
    review: WeeklyReviewDisplay?,
    onTasks: () -> Unit,
    onSaveNote: (String) -> Unit,
    onRest: () -> Unit,
) {
    if(review == null){BioEmpty(AppText.text(TextKey.CopyChuaCoTongKet002b13),AppText.text(TextKey.CopySoLieuSeXuatHienKhiCoHoatDong43a1f5),"calendar",AppText.text(TextKey.CopyXemViec463e95),onTasks);return}
    var note by rememberSaveable{mutableStateOf(review.reflection)}
    Bt(AppText.text(TextKey.CopyTuanTuA3719d)+bioDisplayDate(review.startDisplay),size=AppTypography.Size11,mono=true,tint=bp.muted,spacing=AppTypography.Tracking1_2);Bt(AppText.text(TextKey.CopyTuanCuaBanC82d43),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1)
    BioMetrics(listOf(AppText.text(TextKey.CopyHoanThanh5eb36f) to "${review.completed}/${review.scheduled}",AppText.text(TextKey.CopyTyLeE86c12) to if(review.scheduled>0)"${review.completed*100/review.scheduled}%"else "—",AppText.text(TextKey.CopyTapTrung964002) to "${review.focusMinutes}′"))
    BioCard(Modifier.fillMaxWidth()){Bt(AppText.text(TextKey.Copy7NgayGanNhat442aa3),size=AppTypography.Size13,semi=true);Spacer(Modifier.height(AppDimensions.Dp8));StatisticsBars(review.dailyCompletions,review.dayLabels);Spacer(Modifier.height(AppDimensions.Dp14));Bt(if(review.previousRate != null)AppText.text(TextKey.CopyTuanTruoc0SoLieuDenHomNay87b0c8,review.previousRate) else AppText.text(TextKey.CopyChuaDuDuLieuSoSanh24d4ce),size=AppTypography.Size12,tint=bp.muted)}
    BioDetails(AppText.text(TextKey.CopyNhungDieuDaLam9f52b2)){BioFacts(listOf(AppText.text(TextKey.CopyXuNhanChi548b91) to "${review.earned} / ${review.spent}",AppText.text(TextKey.CopyChamCayFff511) to AppText.text(TextKey.Copy0Ngay45fa48,review.careDays),AppText.text(TextKey.CopyMocTruongThanhB6cf90) to review.milestones.toString(),AppText.text(TextKey.CopyDeuDanNhat2e2e31) to AppText.category(review.consistent),AppText.text(TextKey.CopyCoTheDieuChinhC73bca) to AppText.category(review.improve)))}
    BioField(AppText.text(TextKey.CopyMotDieuMuonNho455aad),note,{note=it.take(2000)},multiline=true,placeholder=AppText.text(TextKey.CopyTuanNayMinhE6de5c))
    BioButton(AppText.text(TextKey.CopyLuuSuyNgam18cdd3),{ onSaveNote(note) },Modifier.fillMaxWidth())
    BioLink(AppText.text(TextKey.CopyLichNghiBaoVe8eda57),"moon",onRest,AppText.text(TextKey.Copy0VeBaoVe8bf912,review.tickets))
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun WeeklyReviewScreenPreview() {
    WeeklyReviewScreen(null, {}, {}, {})
}
