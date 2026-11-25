package com.mamxanh.app.feature.shop.data.repository

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.shop.domain.model.ShopWallet
import com.mamxanh.app.feature.shop.domain.repository.PlantCheckoutReceipt
import com.mamxanh.app.feature.shop.domain.repository.ShopRepository
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.ActionResult
import java.util.concurrent.CancellationException

class ShopRepositoryImpl(
    private val storage: GameStorage,
) : ShopRepository {
    override fun equipCan(id: String): RepositoryResult<String> = storage.equipCan(id).toResult()

    override fun purchase(
        id: String,
        quantity: Int,
        key: String,
    ): RepositoryResult<String> = storage.purchase(id, quantity, key).toResult()

    override fun purchaseAndEquip(
        id: String,
        quantity: Int,
        key: String,
    ): RepositoryResult<String> =
        when (val bought = purchase(id, quantity, key)) {
            is RepositoryResult.Error -> bought
            is RepositoryResult.Success -> {
                val equipped =
                    try {
                        equipCan(id)
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (_: Exception) {
                        RepositoryResult.Error(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac))
                    }
                when (equipped) {
                    is RepositoryResult.Success -> equipped
                    is RepositoryResult.Error -> RepositoryResult.Success("${bought.value}\n${equipped.message}")
                }
            }
        }

    override fun purchaseAndPlant(
        id: String,
        quantity: Int,
        preferredPlot: Int?,
        key: String,
    ): RepositoryResult<PlantCheckoutReceipt> {
        val available = storage.garden().plots.filter { it.isUnlocked && it.treeId == null }
        val plot =
            available.firstOrNull { it.id == preferredPlot } ?: available.firstOrNull()
                ?: return RepositoryResult.Error(AppText.text(TextKey.CopyVuonDaDayBanVanCoTheMuaHatVaoTuiE14bdf))
        val bought = purchase(id, quantity, key)
        if (bought is RepositoryResult.Error) return bought
        val planted =
            try {
                storage.plant(id, "", plot.id)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                false
            }
        if (!planted) {
            return RepositoryResult.Success(
                PlantCheckoutReceipt(AppText.text(TextKey.CopyHatDaOTrongTuiChonOTrongDeGieo3a2bef)),
            )
        }
        val treeId =
            try {
                storage
                    .garden()
                    .plots
                    .firstOrNull { it.id == plot.id }
                    ?.treeId
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                null
            }
        return RepositoryResult.Success(
            PlantCheckoutReceipt(AppText.text(TextKey.CopyDaGieoMotMamXanhB29310), treeId),
        )
    }

    override fun dailyEarnings(): List<Int> = storage.phaseSnapshot().days.map { it.earned }

    override fun loadWallet(): ShopWallet {
        val garden = storage.garden()
        return ShopWallet(
            coins = storage.statistics().coins,
            inventory = garden.inventory,
            equippedCan = garden.equippedCan,
            hasPlantingPlot = garden.plots.any { it.isUnlocked && it.treeId == null },
        )
    }

    private fun ActionResult.toResult(): RepositoryResult<String> =
        if (success) RepositoryResult.Success(message) else RepositoryResult.Error(message)
}
