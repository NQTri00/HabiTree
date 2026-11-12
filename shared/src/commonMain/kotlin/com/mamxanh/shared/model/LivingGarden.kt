package com.mamxanh.shared.model

/** Versioned cosmetic catalog: footprints occupy a distinct slot, never a tree/path cell. */
data class GardenCosmetic(val id:String,val name:String,val category:String,val price:Int,val icon:String,
    val permanent:Boolean=true,val slot:String=category,val art:String="",val tier:String="Common",val footprint:String="1 ô")
val gardenCosmetics=listOf(
    GardenCosmetic("ground_grass","Thảm cỏ","GROUND",100,"🌱"),
    GardenCosmetic("ground_moss","Rêu mềm","GROUND",250,"🍃"),
    GardenCosmetic("border_wood","Hàng rào gỗ","BORDER",150,"🪵",footprint="1 cạnh"),
    GardenCosmetic("accent_lantern","Đèn vườn","ACCENT",300,"🏮",false,art="LANTERNS"),
    GardenCosmetic("accent_stone","Đá bình yên","ACCENT",250,"🪨"),
    GardenCosmetic("tree_ribbon","Nơ dịu dàng","TREE",150,"🎀",art="RIBBON"),
    GardenCosmetic("tree_flowers","Vòng hoa","TREE",200,"🌼",art="FLOWERS"),
    GardenCosmetic("tree_lantern","Đèn nhỏ","TREE",300,"🏮",art="LANTERNS"),
    GardenCosmetic("ambient_lights","Ánh đom đóm","AMBIENT",700,"✨",footprint="Toàn vườn"),
    GardenCosmetic("theme_stone","Sân đá","THEME",1500,"🏛",slot="THEME",footprint="Toàn vườn"),
    GardenCosmetic("theme_night","Vườn đêm","THEME",1500,"🌙",slot="THEME",footprint="Toàn vườn"),
    GardenCosmetic("reward_sign","Biển nhà vườn","ACCENT",0,"🪧",tier="Reward"),
    GardenCosmetic("reward_petals","Cánh hoa","AMBIENT",0,"🌸",tier="Reward",footprint="Toàn vườn"),
    GardenCosmetic("reward_path","Lối đá","BORDER",0,"🪨",tier="Reward",footprint="1 cạnh"),
    GardenCosmetic("reward_plaque","Bảng sưu tầm","ACCENT",0,"🏅",tier="Reward")
)
data class GardenPlacement(val id:String,val sku:String,val plotId:Int=0,val treeId:Long=0)
data class GardenSection(val id:Int,val plots:List<Int>,val unlocked:Boolean,val price:Int)
data class GardenAppearance(val nickname:String="Vườn bình yên",val createdAt:Long=0,val theme:String="MEADOW",
    val density:String="OFF",val batterySaver:Boolean=false,val muted:Boolean=true) {
    fun particlesEnabled(reducedMotion:Boolean)=!reducedMotion && !batterySaver && density!="OFF"
}
data class SpeciesRecord(val id:String,val discoveredAt:Long=0,val highestStage:Int=-1,val planted:Int=0,
    val firstPlanted:Long=0,val firstMatured:Long=0,val ownedSeeds:Int=0,val living:Int=0,
    val tallestStage:Int=-1,val flourished:Boolean=false) {
    val status get()=when { flourished->"Nở rộ";firstMatured>0->"Trưởng thành";planted>0->"Đã trồng";ownedSeeds>0->"Có hạt";discoveredAt>0->"Đã khám phá";else->"Chưa khám phá" }
}
data class CollectionGoal(val id:String,val title:String,val progress:Int,val target:Int,val reward:String,val unlockedAt:Long=0)
data class LivingGardenSnapshot(val version:Long=0,val placements:List<GardenPlacement> = emptyList(),
    val appearance:GardenAppearance=GardenAppearance(),val sections:List<GardenSection> = emptyList(),
    val species:List<SpeciesRecord> = emptyList(),val goals:List<CollectionGoal> = emptyList(),
    val owned:Map<String,Int> = emptyMap(),val coins:Int=0,val earningRate:Double=40.0,
    val estimatedRate:Boolean=true)
sealed interface LivingGardenAction {
    data class Layout(val expectedVersion:Long,val placements:List<GardenPlacement>,val key:String):LivingGardenAction
    data class Buy(val sku:String,val key:String):LivingGardenAction
    data class Expand(val section:Int,val key:String):LivingGardenAction
    data class Appearance(val value:GardenAppearance):LivingGardenAction
    data class Discover(val ids:List<String>):LivingGardenAction
}
