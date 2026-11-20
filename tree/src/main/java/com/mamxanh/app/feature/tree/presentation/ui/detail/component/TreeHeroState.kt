package com.mamxanh.app.feature.tree.presentation.ui.detail.component

data class TreeHeroState(
    val species: String,
    val name: String,
    val stageLabel: String,
    val growthCaption: String,
    val stage: Int,
    val stages: Int,
    val progress: Int,
    val home: Boolean,
    val compact: Boolean,
    val dead: Boolean,
    val watered: Boolean,
    val busy: Boolean,
    val selectedOnHome: Boolean,
    val canBrowseTrees: Boolean,
    val accessoryIcon: String?,
)
