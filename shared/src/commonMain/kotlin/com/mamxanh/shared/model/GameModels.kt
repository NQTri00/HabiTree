package com.mamxanh.shared.model

import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.time.currentDateKey

data class Task(
    val id: Long,
    val title: String,
    val kind: String,
    val category: String,
    val reward: Int,
    val frequency: String,
    val due: String,
    val completed: String,
    val streak: Int,
    val lastDone: String,
    val config: TaskConfig = TaskConfig(title, kind, category, frequency = frequency, due = due),
    val lifecycle: String = "ACTIVE",
    val scheduledToday: Boolean = true,
    val available: Boolean = true,
    val availability: String = "",
    val history: List<TaskOccurrence> = emptyList(),
    val requiredToday: Boolean = false,
    val occurrenceDone: Boolean = false,
) {
    val doneToday: Boolean get() = completed == currentDateKey() || occurrenceDone
    val finished: Boolean get() = kind == "TODO" && completed.isNotBlank()
}

data class Species(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    private val legacyPrice: Int,
) {
    val profile get() = lifecycleProfile(id)
    val price get() = profile.seedPrice
    val tier get() = profile.tier
    val formCount get() = profile.stageCount
    val unlockType get() = "COINS"
    val growthBalanceVersion get() = profile.balanceVersion
}

data class Tree(
    val id: Long,
    val speciesId: String,
    val name: String,
    val growth: Int,
    val planted: String,
    val decoration: String = "NONE",
    val wateredToday: Boolean = false,
    val status: TreeStatus = TreeStatus(),
)

data class GardenPlot(
    val id: Int,
    val isUnlocked: Boolean,
    val treeId: Long? = null,
)

object GardenPlotPolicy {
    const val INITIAL_PLOTS = 5
}

data class Garden(
    val trees: List<Tree> = emptyList(),
    val inventory: Map<String, Int> = emptyMap(),
    val plots: List<GardenPlot> = emptyList(),
    val coins: Int = 0,
    val equippedCan: String = WateringCanTier.BASIC.itemId,
    val waterAll: WaterAllSummary = WaterAllSummary(),
)

data class Statistics(
    val coins: Int = 0,
    val total: Int = 0,
    val today: Int = 0,
    val week: Int = 0,
    val streak: Int = 0,
    val treeCount: Int = 0,
    val recentDailyCompletions: List<Int> = emptyList(),
)

enum class TreeWateringResult { WATERED, ALREADY_WATERED, DEAD, CLOCK_CHANGED, TREE_NOT_FOUND }

val catalog get() =
    listOf(
        Species(
            "succulent",
            AppText.text(TextKey.CopySenDaNgocAba46f),
            "🪴",
            AppText.text(TextKey.CopyMongNuocCanhLaXepTangXinhXan371fcf),
            150,
        ),
        Species(
            "monstera",
            AppText.text(TextKey.CopyTrauBaLaXeF22d98),
            "🌿",
            AppText.text(TextKey.CopyLaXanhMuotMangKhongKhiTrongLanh7b7c5e),
            200,
        ),
        Species("ficus", AppText.text(TextKey.CopyCayBangBupEb6814), "🌳", AppText.text(TextKey.CopyTanLaTronTriaDonAnhMatTroi6377c3), 500),
        Species("snake", AppText.text(TextKey.CopyLuoiHoXanh998396), "🎋", AppText.text(TextKey.CopyVuonThangManhMeVaKienCuongC241b5), 200),
        Species(
            "cactus",
            AppText.text(TextKey.CopyXuongRongHoa723f84),
            "🌵",
            AppText.text(TextKey.CopyNuHoaHongBeXiuNoTrenDinhF74815),
            200,
        ),
        Species("aloe", AppText.text(TextKey.CopyNhaDamMongD63ca0), "🌱", AppText.text(TextKey.CopyMemMaiTuoiMatSuotBonMua13904b), 200),
        Species(
            "lavender",
            AppText.text(TextKey.CopyOaiHuongTim2ffaf9),
            "🪻",
            AppText.text(TextKey.CopyHuongThomDiuDangGiupThuGianEcbc41),
            200,
        ),
        Species("pilea", AppText.text(TextKey.CopyCoDongTienF85b17), "🪙", AppText.text(TextKey.CopyLaTronNhuDongXuMayManEf4f42), 500),
        Species("cherry", AppText.text(TextKey.CopyDaoHongXuanC2fd99), "🌸", AppText.text(TextKey.CopyCanhHoaDaoRucRoNgayXuan69657f), 1200),
        Species("pine", AppText.text(TextKey.CopyThongXanhBiecBb5db6), "🌲", AppText.text(TextKey.CopyBenBiXanhNgatQuanhNam2af5a1), 4500),
        Species(
            "mandarin",
            AppText.text(TextKey.CopyQuytNang1c3e1e),
            "🍊",
            AppText.text(TextKey.CopyTraiNgotNhoMangNangAmDenKhuVuonAf1fc6),
            500,
        ),
    )
