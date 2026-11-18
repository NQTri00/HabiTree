package com.mamxanh.app.feature.tree.presentation.navigation

import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import kotlinx.serialization.Serializable

@Serializable
internal data class TreeKey(override val argument: String) : HabiTreeNavKey {
    override val routeId: String get() = "tree"
}

val treeFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("tree") { TreeKey(it) },
)
