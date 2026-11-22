package com.mamxanh.app.feature.tasks.presentation.ui.focus

import com.mamxanh.shared.localization.AppLanguage
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

fun focusSessionTitle(
    taskId: Long,
    savedTitle: String,
    language: AppLanguage = AppText.language,
): String = if (taskId == 0L) AppText.forLanguage(TextKey.CopyTapTrungTuDo7ed36c, language) else savedTitle
