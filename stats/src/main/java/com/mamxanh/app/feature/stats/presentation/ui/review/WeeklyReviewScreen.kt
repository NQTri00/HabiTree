package com.mamxanh.app.feature.stats.presentation.ui.review
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatisticsBars
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable
fun WeeklyReviewScreen(
    review: WeeklyReviewDisplay?,
    onTasks: () -> Unit,
    onSaveNote: (String) -> Unit,
    onRest: () -> Unit,
    draft: String? = null,
    onEdit: ((String) -> Unit)? = null,
    busy: Boolean = false,
) {
    if (review ==
        null
    ) {
        BioEmpty(
            stringResource(R.string.copy_chua_co_tong_ket_002b13),
            stringResource(R.string.copy_so_lieu_se_xuat_hien_khi_co_hoat_dong_43a1f5),
            "calendar",
            stringResource(R.string.copy_xem_viec_463e95),
            onTasks,
        )
        return
    }
    var previewNote by rememberSaveable { mutableStateOf(review.reflection) }
    val note = draft ?: previewNote
    Bt(
        AppText.text(TextKey.CopyTuanTuA3719d) + bioDisplayDate(review.startDisplay),
        size = AppTypography.SIZE_11,
        mono = true,
        tint = bp.muted,
        spacing = AppTypography.TRACKING_1_2,
    )
    Bt(
        stringResource(R.string.copy_tuan_cua_ban_c82d43),
        size = AppTypography.SIZE_27,
        bold = true,
        spacing = AppTypography.TRACKING_MINUS_1,
    )
    BioMetrics(
        listOf(
            stringResource(R.string.copy_hoan_thanh_5eb36f) to "${review.completed}/${review.scheduled}",
            stringResource(R.string.copy_ty_le_e86c12) to
                if (review.scheduled >
                    0
                ) {
                    "${review.completed * 100 / review.scheduled}%"
                } else {
                    "—"
                },
            stringResource(R.string.copy_tap_trung_964002) to "${review.focusMinutes}′",
        ),
    )
    BioCard(Modifier.fillMaxWidth()) {
        StatisticsBars(review.dailyCompletions, review.dayLabels)
        Spacer(Modifier.height(AppDimensions.Dp14))
        Bt(
            if (review.previousRate != null) {
                stringResource(R.string.copy_tuan_truoc_0_so_lieu_den_hom_nay_87b0c8, review.previousRate)
            } else {
                stringResource(R.string.copy_chua_du_du_lieu_so_sanh_24d4ce)
            },
            size = AppTypography.SIZE_12,
            tint = bp.muted,
        )
    }
    BioDetails(stringResource(R.string.copy_nhung_dieu_da_lam_9f52b2)) {
        BioFacts(
            listOf(
                stringResource(R.string.copy_xu_nhan_chi_548b91) to "${review.earned} / ${review.spent}",
                stringResource(R.string.copy_cham_cay_fff511) to stringResource(R.string.copy_0_ngay_45fa48, review.careDays),
                stringResource(R.string.copy_moc_truong_thanh_b6cf90) to review.milestones.toString(),
                stringResource(R.string.copy_deu_dan_nhat_2e2e31) to AppText.category(review.consistent),
                stringResource(R.string.copy_co_the_dieu_chinh_c73bca) to AppText.category(review.improve),
            ),
        )
    }
    BioField(stringResource(R.string.copy_mot_dieu_muon_nho_455aad), note, {
        if (onEdit == null) previewNote = it.take(2000) else onEdit(it)
    }, multiline = true, placeholder = stringResource(R.string.copy_tuan_nay_minh_e6de5c))
    BioButton(stringResource(R.string.copy_luu_suy_ngam_18cdd3), { onSaveNote(note) }, Modifier.fillMaxWidth(), enabled = !busy)
    BioLink(
        stringResource(R.string.copy_lich_nghi_bao_ve_8eda57),
        "moon",
        onRest,
        stringResource(R.string.copy_0_ve_bao_ve_8bf912, review.tickets),
    )
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun WeeklyReviewScreenPreview() {
    WeeklyReviewScreen(null, {}, {}, {})
}
