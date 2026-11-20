package com.mamxanh.app.feature.home.presentation.ui.onboarding

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Stable
class OnboardingSelectionState {
    var step by mutableIntStateOf(0)
    var seed by mutableStateOf(DefaultSeed)
    var category by mutableStateOf(defaultCategory())
    private val templates = mutableStateListOf("study25", "water")
    val selectedTemplates: List<String> get() = templates.toList()

    fun previousStep() {
        step = (step - 1).coerceAtLeast(0)
    }

    fun advance(): Boolean {
        if (step >= 4) return false
        step++
        return true
    }

    fun toggleTemplate(id: String): Boolean {
        if (templates.remove(id)) return true
        if (templates.size >= 5) return false
        templates.add(id)
        return true
    }

    fun restoreTemplates(values: List<String>) {
        templates.clear()
        templates.addAll(values)
    }

    companion object {
        const val DefaultSeed = "succulent"
        fun defaultCategory() = AppText.canonical(TextKey.CopyHocTapA51516)
    }
}
