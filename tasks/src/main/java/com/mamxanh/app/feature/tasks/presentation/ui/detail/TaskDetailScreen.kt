package com.mamxanh.app.feature.tasks.presentation.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable
fun TaskDetailScreen(
    t: TaskDetailDisplay?,
    onTasks: () -> Unit,
    onComplete: () -> Unit,
    onEdit: () -> Unit,
    onInsights: () -> Unit,
    onLifecycle: (String) -> Unit,
    onArchive: () -> Unit,
) {
    if (t ==
        null
    ) {
        BioEmpty(
            stringResource(R.string.copy_viec_khong_con_o_day_7f288d),
            icon = "tasks",
            action = stringResource(R.string.copy_ve_danh_sach_5c7bca),
            onClick = onTasks,
        )
        return
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
        Column(Modifier.weight(1f)) {
            Bt(
                AppText.category(t.category),
                size = AppTypography.SIZE_10,
                tint = bp.muted,
                mono = true,
                spacing = AppTypography.TRACKING_1_8,
            )
            Spacer(Modifier.height(AppDimensions.Dp7))
            Bt(
                t.title,
                size = AppTypography.SIZE_27,
                line = AppTypography.LINE_HEIGHT_35_1,
                bold = true,
                spacing = AppTypography.TRACKING_MINUS_1,
            )
        }
        BioTag(AppText.text(TextKey.Copy0Xu7c2391, t.reward))
    }
    val lifecycle = t.lifecycle
    val done = t.done
    val timer = t.timer
    BioCard(Modifier.fillMaxWidth()) {
        BioFacts(
            listOf(
                stringResource(R.string.copy_loai_3bfbf9) to
                    when (t.kind) {
                        "HABIT" -> stringResource(R.string.copy_thoi_quen_e6e6e8)
                        "TODO" -> stringResource(R.string.copy_mot_lan_ca8e62)
                        else -> stringResource(R.string.copy_hang_ngay_545b54)
                    },
                stringResource(R.string.copy_lich_be87b3) to
                    when (t.frequency) {
                        "WEEKDAYS" -> stringResource(R.string.copy_ngay_thuong_ef45e7)
                        "WEEKENDS" -> stringResource(R.string.copy_cuoi_tuan_a0fb27)
                        "CUSTOM" ->
                            AppText.text(TextKey.CopyNgay96007e) +
                                t.days
                        ;"ONCE" -> stringResource(R.string.copy_mot_lan_ca8e62)
                        else -> stringResource(R.string.copy_moi_ngay_8270cb)
                    },
                stringResource(R.string.copy_xac_nhan_1503b0) to
                    if (timer) {
                        stringResource(
                            R.string.copy_0_phut_2596d2,
                            t.minutes,
                        )
                    } else {
                        stringResource(R.string.copy_cham_de_hoan_thanh_856927)
                    },
                stringResource(R.string.copy_trang_thai_cb31de) to
                    if (done) {
                        stringResource(R.string.copy_da_xong_565e93)
                    } else if (lifecycle ==
                        "PAUSED"
                    ) {
                        stringResource(R.string.tab_paused)
                    } else if (lifecycle ==
                        "ARCHIVED"
                    ) {
                        stringResource(R.string.copy_luu_tru_7e7c5f)
                    } else {
                        stringResource(R.string.copy_dang_hoat_dong_767d67)
                    },
            ),
        )
        ;if (t.description.isNotBlank())Bt(t.description, size = AppTypography.SIZE_12, tint = bp.muted)
    }
    BioDetails(stringResource(R.string.copy_lich_lich_su_7153b3)) {
        BioFacts(
            listOf(
                stringResource(R.string.copy_khung_gio_78354f) to
                    if (t.start.isBlank())stringResource(R.string.copy_ca_ngay_891abb)else t.start + "–" + t.end,
                stringResource(R.string.copy_han_chot_8a5e27) to t.due,
                stringResource(R.string.copy_chuoi_ngay_5ad2e6) to t.streak.toString(),
                stringResource(R.string.copy_lan_cuoi_adde92) to
                    t.lastDone.ifBlank {
                        if (done) {
                            stringResource(
                                R.string.copy_hom_nay_e6747a,
                            )
                        } else {
                            stringResource(R.string.copy_chua_ghi_nhan_f7654d)
                        }
                    },
            ),
        )
        ;Bt(stringResource(R.string.copy_7_ngay_gan_day_486803), size = AppTypography.SIZE_12)
        if (t.history.isEmpty()) {
            Bt(stringResource(R.string.copy_chua_co_lan_hoan_thanh_5b7c8b), size = AppTypography.SIZE_12, tint = bp.muted)
        } else {
            BioFacts(
                t.history.map {
                    it.date to
                        stringResource(R.string.copy_0_1_xu_8c9c4c, it.granted, it.nominal)
                },
            )
        }
    }
    BioButton(
        if (timer) {
            stringResource(R.string.copy_tap_trung_964002)
        } else if (done) {
            stringResource(R.string.copy_da_hoan_thanh_14eefb)
        } else {
            stringResource(R.string.copy_hoan_thanh_5eb36f)
        },
        onComplete,
        Modifier.fillMaxWidth(),
        if (timer)"clock"else "check",
        enabled =
            !done && lifecycle == "ACTIVE",
    )
    BioButton(stringResource(R.string.copy_sua_viec_55ba70), onEdit, Modifier.fillMaxWidth(), "edit", true)
    BioLink(stringResource(R.string.copy_nhip_thoi_quen_1b8324), "chart", onInsights)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        BioTextAction(
            if (lifecycle ==
                "ACTIVE"
            ) {
                stringResource(R.string.copy_tam_dung_676358)
            } else {
                stringResource(R.string.copy_tiep_tuc_87a7ae)
            },
            {
                onLifecycle(
                    if (lifecycle ==
                        "ACTIVE"
                    ) {
                        "PAUSED"
                    } else {
                        "ACTIVE"
                    },
                )
            },
        )
        ;BioTextAction(stringResource(R.string.action_archive), onArchive)
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun TaskDetailScreenPreview() {
    TaskDetailScreen(null, {}, {}, {}, {}, {}, {})
}
