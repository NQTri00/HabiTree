package com.mamxanh.shared.model

const val HOUR_MS = 3_600_000L
const val DAY_MS = 24 * HOUR_MS
const val GROWTH_DAY_UNITS = 8 * HOUR_MS * 1000L

enum class TreeGrowthState { GROWING, MATURE, DEAD }
enum class CareCue(val label: String) {
    HEALTHY("Khỏe mạnh"), THIRSTY("Cần tưới"), WILTING("Đang héo"), CRITICAL("Cần chăm sóc sớm"), DEAD("Đã khô héo")
}
data class SpeciesLifecycleProfile(
    val id: String, val tier: String, val seedPrice: Int, val stageCount: Int,
    val minimumAgeDays: Int, val requiredGrowthDays: Int, val salePrice: Int,
    val balanceVersion: Int = 3
) {
    val stageThresholds: List<Long> get() = UNIVERSAL_THRESHOLDS.take(stageCount)
    val finalUnits get() = stageThresholds.last()
    val minimumAgeMillis get() = UNIVERSAL_AGE_MILLIS[stageCount - 1]
}
// Cumulative universal 1.4× schedule, rounded once from the common 14-day base.
// Effective effort is independent from integer successful daily credits.
private val UNIVERSAL_THRESHOLDS = listOf(0L,36836719778L,88408127467L,160608098231L,261688057302L,403200000000L,601316719778L,878680127467L,1266988898231L,1810621177302L,2571706368000L,3637225634978L)
private val UNIVERSAL_AGE_MILLIS = listOf(0L,110510160L,265224383L,481824295L,785064172L,1209600000L,1803950160L,2636040383L,3800966695L,5431863532L,7715119104L,10911676905L)
val lifecycleProfiles = listOf(
    SpeciesLifecycleProfile("succulent","Common",200,6,14,14,40),
    SpeciesLifecycleProfile("monstera","Common",200,6,14,14,40),
    SpeciesLifecycleProfile("ficus","Rare",900,9,44,44,180),
    SpeciesLifecycleProfile("snake","Common",200,6,14,14,40),
    SpeciesLifecycleProfile("cactus","Common",200,6,14,14,40),
    SpeciesLifecycleProfile("aloe","Common",200,6,14,14,40),
    SpeciesLifecycleProfile("lavender","Common",200,6,14,14,40),
    SpeciesLifecycleProfile("pilea","Rare",900,9,44,44,180),
    SpeciesLifecycleProfile("cherry","Rare",900,9,44,44,180),
    SpeciesLifecycleProfile("pine","Legendary",4500,12,127,127,900),
    SpeciesLifecycleProfile("mandarin","Rare",900,9,44,44,180)
)
fun lifecycleProfile(id: String) = lifecycleProfiles.first { it.id == id }

enum class WateringCanTier(val itemId: String, val label: String, val price: Int, val hours: Int) {
    BASIC("can_basic","Bình cơ bản",0,8), COPPER("can_copper","Bình đồng",300,12),
    SILVER("can_silver","Bình bạc",900,18), GOLD("can_gold","Bình vàng",2000,24);
    companion object { fun fromId(id: String) = entries.firstOrNull { it.itemId == id } ?: BASIC }
}
enum class FertilizerTier(val itemId: String, val label: String, val price: Int, val rate: Int, val hours: Int) {
    BASIC("fertilizer","Phân cơ bản",35,1100,4), RICH("fertilizer_rich","Phân giàu dưỡng",75,1200,6),
    PREMIUM("fertilizer_premium","Phân cao cấp",140,1350,8);
    companion object { fun fromId(id: String) = entries.firstOrNull { it.itemId == id } }
}
data class CareProduct(val id: String,val name: String,val price: Int,val description: String,val permanent: Boolean)
val careProducts = WateringCanTier.entries.map {
    CareProduct(it.itemId,it.label,it.price,"Tưới giúp cây phát triển trong ${it.hours} giờ. Dụng cụ dùng lâu dài.",true)
} + FertilizerTier.entries.map {
    CareProduct(it.itemId,it.label,it.price,"Tăng tốc ${it.rate / 1000.0}× trong ${it.hours} giờ khi cây được tưới. Mỗi cây dùng một lần trong 24 giờ.",false)
}

/** Safe presentation model: deliberately contains no numeric neglect/death progress. */
data class TreeStatus(
    val state: TreeGrowthState = TreeGrowthState.GROWING,
    val stageIndex: Int = 0, val stageCount: Int = 6, val stageProgress: Float = 0f,
    val growthDays: Int = 0, val requiredDays: Int = 14,
    val wateredUntil: Long = 0, val lastWateredAt: Long = 0,
    val fertilizerId: String = "", val fertilizerEndsAt: Long = 0, val fertilizerAvailableAt: Long = 0,
    val cue: CareCue = CareCue.THIRSTY, val plantedAt: Long = 0,
    val maturedAt: Long = 0, val deathAt: Long = 0, val salePrice: Int = 0,
    val sampledAt: Long = 0, val nextStageActiveMillis: Long = 0
) {
    val alive get() = state != TreeGrowthState.DEAD
    val waterActive get() = wateredUntil > sampledAt && alive
    val canWater get() = alive && !waterActive
    val canFertilize get() = state == TreeGrowthState.GROWING && waterActive &&
        sampledAt >= fertilizerEndsAt && sampledAt >= fertilizerAvailableAt
    val stageLabel get() = "Giai đoạn ${stageIndex+1}/$stageCount"
    val stateLabel get() = when(state) { TreeGrowthState.GROWING -> "Đang lớn"; TreeGrowthState.MATURE -> "Trưởng thành"; TreeGrowthState.DEAD -> "Đã khô héo" }
    val waterLabel get() = if (!alive) "Không thể tưới" else if (waterActive) "Còn ${remainingTime(wateredUntil,sampledAt)}" else "Cần tưới"
    val fertilizerLabel get() = if (fertilizerEndsAt > sampledAt)
        "${FertilizerTier.fromId(fertilizerId)?.label ?: "Phân bón"} · ${remainingTime(fertilizerEndsAt,sampledAt)}" else ""
}
fun remainingTime(until: Long, now: Long): String {
    val minutes = ((until-now).coerceAtLeast(0)+59_999)/60_000
    return if(minutes>=60)"${minutes/60} giờ ${minutes%60} phút" else "$minutes phút"
}
data class WaterAllSummary(val eligible: Int = 0,val active: Int = 0,val dead: Int = 0,val affected: Int = 0)
data class TreeDispositionEvent(val treeId: Long,val name: String,val kind: String,val at: Long,val nominal: Int,val granted: Int)
