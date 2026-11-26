package com.mamxanh.app.presentation.bio

import com.mamxanh.shared.model.FertilizerTier
import com.mamxanh.shared.model.WateringCanTier
import com.mamxanh.shared.model.catalog
import org.json.JSONObject

internal data class BioPurchaseDetails(
    val name: String,
    val price: Int,
    val description: String = "",
    val tier: String = "",
    val stages: Int = 0,
    val hours: Int = 0,
    val rate: String = "",
)

internal data class BioPurchaseProduct(
    val details: BioPurchaseDetails,
    val seed: Boolean = false,
    val permanent: Boolean = false,
)

internal fun livePurchaseProduct(id: String): BioPurchaseProduct? {
    val species = catalog.firstOrNull { it.id == id }
    val can = WateringCanTier.entries.firstOrNull { it.itemId == id }
    val fertilizer = FertilizerTier.fromId(id)
    return when {
        species != null ->
            BioPurchaseProduct(
                BioPurchaseDetails(species.name, species.price, species.description, species.tier, species.formCount),
                seed = true,
            )
        can != null ->
            BioPurchaseProduct(BioPurchaseDetails(can.label, can.price, hours = can.hours), permanent = true)
        fertilizer != null ->
            BioPurchaseProduct(
                BioPurchaseDetails(
                    fertilizer.label,
                    fertilizer.price,
                    hours = fertilizer.hours,
                    rate = (fertilizer.rate / 1000.0).toString(),
                ),
            )
        else -> null
    }
}

internal fun JSONObject.referencePurchaseDetails() =
    BioPurchaseDetails(
        s("name"),
        i("price"),
        s("description"),
        s("tier"),
        i("stages"),
        i("hours"),
        s("rate"),
    )
