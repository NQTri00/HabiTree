package com.mamxanh.app.feature.garden.domain.model

data class Garden(
    val trees: List<Tree> = emptyList(),
    val inventory: Map<String, Int> = emptyMap(),
    val plots: List<GardenPlot> = emptyList(),
    val coins: Int = 0,
    val equippedCan: String = "can_basic",
    val waterAll: GardenWatering = GardenWatering(),
)

data class GardenPlot(
    val id: Int,
    val isUnlocked: Boolean,
    val treeId: Long? = null,
)

data class GardenWatering(
    val eligible: Int = 0,
    val active: Int = 0,
    val dead: Int = 0,
)

data class Tree(
    val id: Long,
    val speciesId: String,
    val name: String,
    val growth: Int,
    val planted: String,
    val decoration: String = "NONE",
    val wateredToday: Boolean = false,
    val status: GardenTreeCare = GardenTreeCare(),
)

enum class GardenTreeGrowth { Growing, Mature, Dead }

data class GardenTreeCare(
    val state: GardenTreeGrowth = GardenTreeGrowth.Growing,
    val stageIndex: Int = 0,
    val stageProgress: Float = 0f,
    val percentage: Int = 0,
    val waterActive: Boolean = false,
    val canWater: Boolean = true,
    val canFertilize: Boolean = false,
    val fertilizerId: String = "",
    val stateLabel: String = "",
    val waterLabel: String = "",
    val fertilizerLabel: String = "",
) {
    val alive: Boolean get() = state != GardenTreeGrowth.Dead
}
