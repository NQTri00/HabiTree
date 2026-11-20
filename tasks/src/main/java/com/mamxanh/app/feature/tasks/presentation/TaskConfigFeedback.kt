package com.mamxanh.app.feature.tasks.presentation

import com.mamxanh.app.feature.tasks.domain.model.TaskConfigError
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

internal fun TaskConfigError.message(): String = AppText.text(
    when (this) {
        TaskConfigError.Title -> TextKey.CopyTenViecCanTu1Den100KyTuEe458c
        TaskConfigError.Description -> TextKey.CopyMoTaToiDa500KyTu1b6c1c
        TaskConfigError.Kind -> TextKey.CopyLoaiViecKhongHopLe591535
        TaskConfigError.Verification -> TextKey.CopyChonClickHoacTimer4ca463
        TaskConfigError.Duration -> TextKey.CopyThoiGianTu5Den240Phut2e8442
        TaskConfigError.Frequency -> TextKey.CopyLichKhongHopLeE44ee4
        TaskConfigError.Days -> TextKey.CopyChonItNhatMotNgay87d34b
        TaskConfigError.Date -> TextKey.CopyHanChotTheoYyyyMmDdEd8713
        TaskConfigError.TimePair -> TextKey.CopyNhapCaGioBatDauVaKetThucB4c5b3
        TaskConfigError.TimeRange -> TextKey.CopyGioKhongHopLeB9fc9c
        TaskConfigError.ZeroDuration -> TextKey.CopyKhungGioPhaiCoDoDaiLonHon09d5e85
    },
)
