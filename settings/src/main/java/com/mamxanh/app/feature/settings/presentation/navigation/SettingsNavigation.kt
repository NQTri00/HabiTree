package com.mamxanh.app.feature.settings.presentation.navigation

import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import kotlinx.serialization.Serializable

@Serializable
internal data object SettingsKey : HabiTreeNavKey {
    override val routeId: String get() = "settings"
}

@Serializable
internal data object BackupKey : HabiTreeNavKey {
    override val routeId: String get() = "data"
}

val settingsFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("settings") { argument -> SettingsKey.takeIf { argument.isEmpty() } },
    HabiTreeFeatureDestination("data") { argument -> BackupKey.takeIf { argument.isEmpty() } },
)
