package com.mamxanh.app.feature.settings.presentation.navigation

import androidx.compose.runtime.compositionLocalOf
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupDisplay
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupScreen
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupScreenAction
import com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsDisplay
import com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsLink
import com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
internal data object SettingsKey : HabiTreeNavKey {
    override val routeId: String get() = "settings"
}

@Serializable
internal data object BackupKey : HabiTreeNavKey {
    override val routeId: String get() = "data"
}

class SettingsRouteContent(
    val state: SettingsDisplay,
    val onLanguage: (String) -> Unit,
    val onToggle: (String, Boolean) -> Unit,
    val onSave: (String, String) -> Unit,
    val onLink: (SettingsLink) -> Unit,
)

class BackupRouteContent(
    val state: BackupDisplay,
    val onAction: (BackupScreenAction) -> Unit,
)

val LocalSettingsRouteContent = compositionLocalOf<() -> SettingsRouteContent> {
    error("Settings route requires its state and actions")
}

val LocalBackupRouteContent = compositionLocalOf<() -> BackupRouteContent> {
    error("Backup route requires its state and actions")
}

val settingsFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("settings", content = {
        val content = LocalSettingsRouteContent.current()
        SettingsScreen(content.state, content.onLanguage, content.onToggle, content.onSave, content.onLink)
    }) { argument -> SettingsKey.takeIf { argument.isEmpty() } },
    HabiTreeFeatureDestination("data", content = {
        val content = LocalBackupRouteContent.current()
        BackupScreen(content.state, content.onAction)
    }) { argument -> BackupKey.takeIf { argument.isEmpty() } },
)
