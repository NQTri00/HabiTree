package com.mamxanh.app.feature.tasks.presentation.ui.engagement

import com.mamxanh.app.core.presentation.UiAction
import com.mamxanh.shared.model.EngagementAction

data class EngagementUiAction(
    val mutation: EngagementAction,
) : UiAction
