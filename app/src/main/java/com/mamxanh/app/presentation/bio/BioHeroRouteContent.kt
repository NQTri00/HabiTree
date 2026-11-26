package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.feature.tree.presentation.ui.detail.component.TreeHero
import com.mamxanh.app.feature.tree.presentation.ui.detail.component.TreeHeroAction
import com.mamxanh.app.feature.tree.presentation.ui.detail.component.TreeHeroState
import org.json.JSONObject

@Composable
internal fun BioHero(
    world: BioWorld,
    ui: BioController,
    tree: JSONObject,
    home: Boolean = false,
    short: Boolean = false,
    water: (() -> Unit)? = null,
    dispatch: ((TreeHeroAction) -> Unit)? = null,
) {
    val treeId = tree.s("id")
    val species = tree.s("species")
    TreeHero(
        TreeHeroState(
            species = species,
            name = tree.s("name"),
            stageLabel = treeState(tree),
            growthCaption = growthCaption(tree),
            stage = tree.i("stage", 1),
            stages = world.species(species).i("stages", 6),
            progress = tree.i("progress"),
            home = home,
            compact = short,
            dead = tree.s("state") == "dead",
            watered = tree.b("watered"),
            busy = ui.busy,
            selectedOnHome = ui.homeTreeState.selectedId == treeId,
            canBrowseTrees = world.trees.size > 1,
            accessoryIcon = tree.s("decor").takeIf { it.isNotBlank() && world.owned(it) > 0 }?.let(::accessoryIcon),
        ),
    ) { action ->
        if (dispatch != null) {
            dispatch(action)
            return@TreeHero
        }
        when (action) {
            TreeHeroAction.ChooseTree -> ui.show("tree-picker")
            TreeHeroAction.PinTree -> ui.selectPreviewTree(treeId)
            TreeHeroAction.OpenTree -> ui.go("tree", treeId)
            TreeHeroAction.PreviousTree -> ui.browsePreviewTree(forward = false)
            TreeHeroAction.NextTree -> ui.browsePreviewTree(forward = true)
            TreeHeroAction.Water -> if (water != null) water() else Unit
            TreeHeroAction.Decorate -> ui.go("tree", treeId, treeTab = "decor")
        }
    }
}

@Preview(name = "Selected home tree", widthDp = 335, showBackground = true)
@Composable
private fun BioHeroPreview() {
    val context = LocalContext.current
    val world = remember(context) { BioWorld.reference(context) }
    val tree = world.trees.first()
    BioTheme(world, tree.s("id")) {
        BioHero(world, remember { BioController() }, tree, home = true)
    }
}
