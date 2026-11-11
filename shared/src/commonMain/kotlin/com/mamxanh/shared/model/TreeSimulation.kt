package com.mamxanh.shared.model

import com.mamxanh.shared.time.treeDateAt
import com.mamxanh.shared.time.treeMidnightAfter

/** Internal persistence/simulation state. Never copy neglect units into screen state. */
data class TreeSimulation(
    val plantedAt: Long, val zone: String, val at: Long,
    val growthUnits: Long = 0, val creditedDays: Int = 0,
    val day: String = "", val dayUnits: Long = 0,
    val wateredUntil: Long = 0, val lastWateredAt: Long = 0,
    val fertilizerId: String = "", val fertilizerEndsAt: Long = 0, val lastFertilizedAt: Long = 0,
    val neglectMillis: Long = 0, val state: TreeGrowthState = TreeGrowthState.GROWING,
    val maturedAt: Long = 0, val deathAt: Long = 0
)
data class DailyTreeGrowth(val date: String,val units: Long,val credited: Boolean)
data class TreeSimulationResult(val value: TreeSimulation,val days: List<DailyTreeGrowth>)

fun simulateTree(input: TreeSimulation,profile: SpeciesLifecycleProfile,now: Long): TreeSimulationResult {
    if(now<=input.at || input.state==TreeGrowthState.DEAD)return TreeSimulationResult(input,emptyList())
    var s=input
    val days=linkedMapOf<String,DailyTreeGrowth>()
    val ageFloor=input.plantedAt+profile.minimumAgeMillis
    fun matureIfReady() {
        if(s.state==TreeGrowthState.GROWING && s.at>=ageFloor && s.creditedDays>=profile.requiredGrowthDays && s.growthUnits>=profile.finalUnits)
            s=s.copy(state=TreeGrowthState.MATURE,maturedAt=s.at,growthUnits=profile.finalUnits)
    }
    while(s.at<now && s.state!=TreeGrowthState.DEAD) {
        val date=treeDateAt(s.at,s.zone)
        if(s.day!=date)s=s.copy(day=date,dayUnits=0)
        matureIfReady()
        val wet=s.at<s.wateredUntil
        val tier=FertilizerTier.fromId(s.fertilizerId)
        val rate=if(wet && s.state==TreeGrowthState.GROWING && s.dayUnits<GROWTH_DAY_UNITS)
            if(tier!=null && s.at<s.fertilizerEndsAt)tier.rate.toLong() else 1000L else 0L
        var end=minOf(now,treeMidnightAfter(s.at,s.zone))
        if(wet)end=minOf(end,s.wateredUntil)
        if(s.fertilizerEndsAt>s.at)end=minOf(end,s.fertilizerEndsAt)
        if(s.state==TreeGrowthState.GROWING && ageFloor>s.at)end=minOf(end,ageFloor)
        if(!wet)end=minOf(end,s.at+(30*DAY_MS-s.neglectMillis).coerceAtLeast(0))
        if(rate>0) {
            fun crossing(units: Long) = s.at+(units+rate-1)/rate
            end=minOf(end,crossing(GROWTH_DAY_UNITS-s.dayUnits))
            val next=profile.stageThresholds.firstOrNull{it>s.growthUnits}
            if(next!=null)end=minOf(end,crossing(next-s.growthUnits))
        }
        if(end<=s.at) {
            s=s.copy(state=TreeGrowthState.DEAD,deathAt=s.at,neglectMillis=30*DAY_MS)
            break
        }
        val elapsed=end-s.at
        val added=minOf(elapsed*rate,GROWTH_DAY_UNITS-s.dayUnits)
        val dayUnits=s.dayUnits+added
        val credit=if(s.dayUnits<GROWTH_DAY_UNITS && dayUnits>=GROWTH_DAY_UNITS)1 else 0
        s=s.copy(at=end,growthUnits=minOf(profile.finalUnits,s.growthUnits+added),
            dayUnits=dayUnits,creditedDays=s.creditedDays+credit,
            neglectMillis=if(wet)s.neglectMillis else minOf(30*DAY_MS,s.neglectMillis+elapsed))
        if(added>0)days[date]=DailyTreeGrowth(date,s.dayUnits,s.dayUnits>=GROWTH_DAY_UNITS)
        if(s.neglectMillis>=30*DAY_MS)s=s.copy(state=TreeGrowthState.DEAD,deathAt=end)
        else matureIfReady()
    }
    return TreeSimulationResult(s,days.values.toList())
}

fun treeStatus(s: TreeSimulation,p: SpeciesLifecycleProfile,freeStarter: Boolean): TreeStatus {
    val mature=s.state==TreeGrowthState.MATURE || s.maturedAt>0
    val thresholds=p.stageThresholds
    val computed=thresholds.indexOfLast{it<=s.growthUnits}.coerceAtLeast(0)
    val index=if(mature)p.stageCount-1 else computed.coerceAtMost(p.stageCount-2)
    val fraction=if(mature)1f else ((s.growthUnits-thresholds[index]).toDouble() /
        (thresholds[index+1]-thresholds[index])).toFloat().coerceIn(0f,1f)
    val cue=when {
        s.state==TreeGrowthState.DEAD -> CareCue.DEAD
        s.neglectMillis>=24*DAY_MS -> CareCue.CRITICAL
        s.neglectMillis>=14*DAY_MS -> CareCue.WILTING
        s.at>=s.wateredUntil -> CareCue.THIRSTY
        else -> CareCue.HEALTHY
    }
    return TreeStatus(s.state,index,p.stageCount,fraction,s.creditedDays,p.requiredGrowthDays,
        s.wateredUntil,s.lastWateredAt,s.fertilizerId,s.fertilizerEndsAt,
        if(s.lastFertilizedAt>0)s.lastFertilizedAt+DAY_MS else 0, cue,s.plantedAt,s.maturedAt,s.deathAt,
        if(freeStarter)0 else p.salePrice,s.at,
        if(mature || s.state==TreeGrowthState.DEAD)0 else ((thresholds[index+1]-s.growthUnits).coerceAtLeast(0)+999)/1000)
}
