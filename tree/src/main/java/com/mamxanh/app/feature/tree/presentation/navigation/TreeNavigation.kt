package com.mamxanh.app.feature.tree.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import com.mamxanh.app.feature.tree.presentation.ui.detail.BioTreeScreen
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeScreenAction
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeScreenState
import kotlinx.serialization.Serializable

@Serializable
internal data class TreeKey(
    override val argument: String,
) : HabiTreeNavKey {
    override val routeId: String get() = "tree"
}

class TreeRouteContent(
    val state: TreeScreenState?,
    val tab: String,
    val onTab: (String) -> Unit,
    val onAction: (TreeScreenAction) -> Unit,
    val hero: @Composable () -> Unit,
    val accessories: @Composable () -> Unit,
)

val LocalTreeRouteContent =
    compositionLocalOf<(String) -> TreeRouteContent> {
        error("Tree route requires its state and actions")
    }

val treeFeatureNavigation =
    HabiTreeFeatureModule(
        HabiTreeFeatureDestination("tree", content = {
            val content = LocalTreeRouteContent.current(it.argument)
            BioTreeScreen(content.state, content.tab, content.onTab, content.onAction, content.hero, content.accessories)
        }) { TreeKey(it) },
    )
