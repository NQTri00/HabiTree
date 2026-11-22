package com.mamxanh.app.feature.tree.presentation.ui.detail

data class TreeScreenFertilizer(
    val id: String,
    val name: String,
    val rate: String,
    val hours: Int,
    val quantity: Int,
)

data class TreeScreenState(
    val growth: String,
    val watered: Boolean,
    val fertilizerNoteAllowed: Boolean,
    val canFertilize: Boolean,
    val busy: Boolean,
    val hasDecoration: Boolean,
    val decorationName: String,
    val speciesName: String,
    val stage: Int,
    val stages: Int,
    val progress: Int,
    val planted: String,
    val description: String,
    val fertilizers: List<TreeScreenFertilizer>,
) {
    val canWater: Boolean get() = !watered && growth != "dead"

    fun canApplyFertilizer(fertilizer: TreeScreenFertilizer): Boolean =
        canFertilize && fertilizer.quantity > 0 && growth == "growing" && !busy
}
