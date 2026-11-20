package com.mamxanh.app.feature.garden.presentation

data class GardenDisplayTree(
    val id: String,
    val species: String,
    val name: String,
    val progress: Int,
    val stageLabel: String,
    val dead: Boolean,
    val watered: Boolean,
)
