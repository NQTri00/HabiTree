package com.mamxanh.app.feature.tree.data.repository

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.tree.domain.repository.TreeCareReceipt
import com.mamxanh.app.feature.tree.domain.repository.TreeRepository
import com.mamxanh.app.feature.tree.domain.repository.WateringOutcome
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.ActionResult
import com.mamxanh.shared.model.GardenPlacement
import com.mamxanh.shared.model.LivingGardenAction
import com.mamxanh.shared.model.LivingGardenSnapshot
import com.mamxanh.shared.model.TreeWateringResult
import com.mamxanh.shared.model.gardenCosmetics
import java.util.UUID

class TreeRepositoryImpl(
    private val storage: GameStorage,
) : TreeRepository {
    override fun water(treeId: Long): WateringOutcome =
        when (storage.water(treeId)) {
            TreeWateringResult.WATERED -> WateringOutcome.Watered
            TreeWateringResult.ALREADY_WATERED -> WateringOutcome.AlreadyWatered
            TreeWateringResult.DEAD -> WateringOutcome.Dead
            TreeWateringResult.CLOCK_CHANGED -> WateringOutcome.ClockChanged
            TreeWateringResult.TREE_NOT_FOUND -> WateringOutcome.MissingTree
        }

    override fun applyFertilizer(
        treeId: Long,
        tier: String,
    ): RepositoryResult<TreeCareReceipt> = storage.applyFertilizer(treeId, tier).toReceipt()

    override fun saleQuote(treeId: Long): RepositoryResult<TreeCareReceipt> = storage.treeSaleQuote(treeId).toReceipt()

    override fun dispose(
        treeId: Long,
        sell: Boolean,
        key: String,
    ): RepositoryResult<TreeCareReceipt> = storage.disposeTree(treeId, sell, key).toReceipt()

    override fun decorate(
        treeId: Long,
        decoration: String,
    ) = storage.decorate(treeId, decoration)

    override fun buyAccessory(
        sku: String,
        purchaseKey: String,
    ): RepositoryResult<String> {
        val snapshot = storage.livingGarden()
        purchaseIfMissing(snapshot, sku, purchaseKey)?.let { return it }
        return RepositoryResult.Success(AppText.text(TextKey.CopyDaThemPhuKienVaoTui40b7d7))
    }

    override fun equipAccessory(
        sku: String,
        treeId: Long?,
        purchaseKey: String,
    ): RepositoryResult<String> {
        var snapshot = storage.livingGarden()
        if (snapshot.owned.getOrDefault(sku, 0) == 0) {
            purchaseIfMissing(snapshot, sku, purchaseKey)?.let { return it }
            snapshot = storage.livingGarden()
        }
        if (treeId == null) return accessoryError(TextKey.CopyChonMotCayTruocKhiDeoPhuKien4f5562)
        return updateAccessory(snapshot, treeId, sku)
    }

    override fun unequipAccessory(treeId: Long): RepositoryResult<String> = updateAccessory(storage.livingGarden(), treeId, sku = null)

    private fun purchaseIfMissing(
        snapshot: LivingGardenSnapshot,
        sku: String,
        key: String,
    ): RepositoryResult.Error? {
        if (snapshot.owned.getOrDefault(sku, 0) > 0) return null
        val result = storage.designGarden(LivingGardenAction.Buy(sku, key))
        return if (result.success) null else RepositoryResult.Error(result.message)
    }

    private fun updateAccessory(
        snapshot: LivingGardenSnapshot,
        treeId: Long,
        sku: String?,
    ): RepositoryResult<String> {
        val garden = storage.garden()
        if (garden.trees.none { it.id == treeId && it.status.alive }) {
            return accessoryError(TextKey.CopyChonMotCayConSong6d5572)
        }
        val plot =
            garden.plots.firstOrNull { it.treeId == treeId }
                ?: return accessoryError(TextKey.CopyKhongTimThayOTrong4ab339)
        val treeSkus = gardenCosmetics.filter { it.slot == "TREE" }.map { it.id }.toSet()
        val placements = snapshot.placements.filterNot { it.treeId == treeId && it.sku in treeSkus }.toMutableList()
        if (sku != null) {
            if (placements.count { it.sku == sku } >= snapshot.owned.getOrDefault(sku, 0)) {
                val previous = placements.firstOrNull { it.sku == sku }
                if (previous != null) placements.remove(previous)
            }
            placements.add(GardenPlacement(UUID.randomUUID().toString(), sku, plot.id, treeId))
        }
        val result = storage.designGarden(LivingGardenAction.Layout(snapshot.version, placements, UUID.randomUUID().toString()))
        return if (result.success) RepositoryResult.Success(result.message) else RepositoryResult.Error(result.message)
    }

    private fun accessoryError(key: TextKey) = RepositoryResult.Error(AppText.text(key))

    private fun ActionResult.toReceipt(): RepositoryResult<TreeCareReceipt> =
        if (success) RepositoryResult.Success(TreeCareReceipt(message, nominal, granted)) else RepositoryResult.Error(message)
}
