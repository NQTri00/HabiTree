package com.mamxanh.app.feature.tree.domain.repository

data class TreeDetailRecord(
    val id: Long,
    val speciesId: String,
    val name: String,
    val growth: String,
    val stage: Int,
    val progress: Int,
    val watered: Boolean,
    val canFertilize: Boolean,
    val planted: String,
    val decoration: String,
    val accessoryOwned: Boolean,
    val quantities: Map<String, Int>,
    val treeIds: List<Long>,
)

fun interface TreeDetailRepository {
    fun load(id: Long): TreeDetailRecord?
}
