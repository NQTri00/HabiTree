package com.mamxanh.app.feature.tree.domain.repository

data class AccessoryTree(
    val id: String,
    val name: String,
    val decoration: String,
    val living: Boolean,
    val speciesId: String = "",
)

data class AccessorySnapshot(
    val quantities: Map<String, Int> = emptyMap(),
    val trees: List<AccessoryTree> = emptyList(),
    val coins: Int = 0,
) {
    fun owns(sku: String): Boolean = (quantities[sku] ?: 0) > 0

    fun equipped(sku: String): Boolean = trees.any { it.decoration == sku }
}

fun interface AccessoryRepository {
    fun load(): AccessorySnapshot
}
