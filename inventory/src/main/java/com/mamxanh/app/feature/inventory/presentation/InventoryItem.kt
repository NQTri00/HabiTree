package com.mamxanh.app.feature.inventory.presentation

data class InventoryItem(val id: String, val name: String, val count: Int, val isCan: Boolean, val equipped: Boolean = false)
