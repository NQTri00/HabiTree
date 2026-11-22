package com.mamxanh.shared.model

import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

/** Versioned cosmetic catalog: footprints occupy a distinct slot, never a tree/path cell. */
data class GardenCosmetic(
    val id: String,
    val name: String,
    val category: String,
    val price: Int,
    val icon: String,
    val permanent: Boolean = true,
    val slot: String = category,
    val art: String = "",
    val tier: String = "Common",
    val footprint: String = AppText.text(TextKey.Copy1O64163f),
)

val gardenCosmetics get() =
    listOf(
        GardenCosmetic("ground_grass", AppText.text(TextKey.CopyThamCoEc9609), "GROUND", 100, "🌱"),
        GardenCosmetic("ground_moss", AppText.text(TextKey.CopyReuMem819dc9), "GROUND", 250, "🍃"),
        GardenCosmetic(
            "border_wood",
            AppText.text(TextKey.CopyHangRaoGoB61b64),
            "BORDER",
            150,
            "🪵",
            footprint = AppText.text(TextKey.Copy1CanhFa7688),
        ),
        GardenCosmetic("accent_lantern", AppText.text(TextKey.CopyDenVuonBc5252), "ACCENT", 300, "🏮", false, art = "LANTERNS"),
        GardenCosmetic("accent_stone", AppText.text(TextKey.CopyDaBinhYenE9debc), "ACCENT", 250, "🪨"),
        GardenCosmetic("tree_ribbon", AppText.text(TextKey.CopyNoDiuDangE01645), "TREE", 150, "🎀", art = "RIBBON"),
        GardenCosmetic("tree_flowers", AppText.text(TextKey.CopyVongHoaD3c932), "TREE", 200, "🌼", art = "FLOWERS"),
        GardenCosmetic("tree_lantern", AppText.text(TextKey.CopyDenNhoE15515), "TREE", 300, "🏮", art = "LANTERNS"),
        GardenCosmetic(
            "ambient_lights",
            AppText.text(TextKey.CopyAnhDomDom8a6bd6),
            "AMBIENT",
            700,
            "✨",
            footprint = AppText.text(TextKey.CopyToanVuonC190a5),
        ),
        GardenCosmetic(
            "theme_stone",
            AppText.text(TextKey.CopySanDaFa7bef),
            "THEME",
            1500,
            "🏛",
            slot = "THEME",
            footprint = AppText.text(TextKey.CopyToanVuonC190a5),
        ),
        GardenCosmetic(
            "theme_night",
            AppText.text(TextKey.CopyVuonDem093442),
            "THEME",
            1500,
            "🌙",
            slot = "THEME",
            footprint = AppText.text(TextKey.CopyToanVuonC190a5),
        ),
        GardenCosmetic("reward_sign", AppText.text(TextKey.CopyBienNhaVuonD2e9b7), "ACCENT", 0, "🪧", tier = "Reward"),
        GardenCosmetic(
            "reward_petals",
            AppText.text(TextKey.CopyCanhHoa137700),
            "AMBIENT",
            0,
            "🌸",
            tier = "Reward",
            footprint = AppText.text(TextKey.CopyToanVuonC190a5),
        ),
        GardenCosmetic(
            "reward_path",
            AppText.text(TextKey.CopyLoiDa0b51ec),
            "BORDER",
            0,
            "🪨",
            tier = "Reward",
            footprint = AppText.text(TextKey.Copy1CanhFa7688),
        ),
        GardenCosmetic("reward_plaque", AppText.text(TextKey.CopyBangSuuTamA4cfac), "ACCENT", 0, "🏅", tier = "Reward"),
    )

data class GardenPlacement(
    val id: String,
    val sku: String,
    val plotId: Int = 0,
    val treeId: Long = 0,
)

data class GardenSection(
    val id: Int,
    val plots: List<Int>,
    val unlocked: Boolean,
    val price: Int,
)

data class GardenAppearance(
    val nickname: String = AppText.text(TextKey.CopyVuonBinhYenA8fe13),
    val createdAt: Long = 0,
    val theme: String = "MEADOW",
    val density: String = "OFF",
    val batterySaver: Boolean = false,
    val muted: Boolean = true,
) {
    fun particlesEnabled(reducedMotion: Boolean) = !reducedMotion && !batterySaver && density != "OFF"
}

data class SpeciesRecord(
    val id: String,
    val discoveredAt: Long = 0,
    val highestStage: Int = -1,
    val planted: Int = 0,
    val firstPlanted: Long = 0,
    val firstMatured: Long = 0,
    val ownedSeeds: Int = 0,
    val living: Int = 0,
    val tallestStage: Int = -1,
    val flourished: Boolean = false,
) {
    val status get() =
        when {
            flourished -> AppText.text(TextKey.CopyNoRoA8f65c)
            firstMatured > 0 -> AppText.text(TextKey.CopyTruongThanh8c14d7)
            planted >
                0 -> AppText.text(TextKey.CopyDaTrong42fa2b)
            ownedSeeds > 0 -> AppText.text(TextKey.CopyCoHat4034e7)
            discoveredAt > 0 -> AppText.text(TextKey.CopyDaKhamPha3f2f11)
            else -> AppText.text(TextKey.CopyChuaKhamPhaC0b0cb)
        }
}

data class CollectionGoal(
    val id: String,
    val title: String,
    val progress: Int,
    val target: Int,
    val reward: String,
    val unlockedAt: Long = 0,
)

data class LivingGardenSnapshot(
    val version: Long = 0,
    val placements: List<GardenPlacement> = emptyList(),
    val appearance: GardenAppearance = GardenAppearance(),
    val sections: List<GardenSection> = emptyList(),
    val species: List<SpeciesRecord> = emptyList(),
    val goals: List<CollectionGoal> = emptyList(),
    val owned: Map<String, Int> = emptyMap(),
    val coins: Int = 0,
    val earningRate: Double = 40.0,
    val estimatedRate: Boolean = true,
)

sealed interface LivingGardenAction {
    data class Layout(
        val expectedVersion: Long,
        val placements: List<GardenPlacement>,
        val key: String,
    ) : LivingGardenAction

    data class Buy(
        val sku: String,
        val key: String,
    ) : LivingGardenAction

    data class Expand(
        val section: Int,
        val key: String,
    ) : LivingGardenAction

    data class Appearance(
        val value: GardenAppearance,
    ) : LivingGardenAction

    data class Discover(
        val ids: List<String>,
    ) : LivingGardenAction
}
