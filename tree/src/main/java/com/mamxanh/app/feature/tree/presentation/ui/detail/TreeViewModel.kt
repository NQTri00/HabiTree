package com.mamxanh.app.feature.tree.presentation.ui.detail

import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.tree.domain.repository.TreeCareReceipt
import com.mamxanh.app.feature.tree.domain.repository.TreeRepository
import com.mamxanh.app.feature.tree.domain.repository.WateringOutcome
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TreeViewModel(
    private val repository: TreeRepository,
) : BaseViewModel<TreeState, TreeEvent, TreeAction>(
        initialState = TreeState(),
    ) {
    val uiState get() = state
    val effects get() = events

    override fun handleAction(action: TreeAction) {
        val intent = action
        when (intent) {
            TreeAction.Back -> {
                if (mutableState.value.isPanelExpanded) {
                    mutableState.value = mutableState.value.copy(isPanelExpanded = false)
                } else {
                    viewModelScope.launch { emitEvent(TreeEvent.Back) }
                }
                return
            }
            is TreeAction.SetPanelExpanded -> {
                mutableState.value = mutableState.value.copy(isPanelExpanded = intent.expanded)
                return
            }
            TreeAction.DismissDisposition -> {
                mutableState.value = mutableState.value.copy(dispositionId = null, quote = null)
                return
            }
            TreeAction.OpenFertilizerShop -> {
                viewModelScope.launch { emitEvent(TreeEvent.OpenFertilizerShop) }
                return
            }
            else -> Unit
        }
        if (mutableState.value.isLoading)return
        val pending = mutableState.value
        mutableState.value = pending.copy(isLoading = true)
        viewModelScope.launch {
            try {
                if (intent is TreeAction.AskDisposition) {
                    val quote =
                        if (intent.sell) {
                            withContext(Dispatchers.IO) { repository.saleQuote(intent.id) }
                        } else {
                            success(TextKey.CopyDonCayKhongHoanXuHoacHatCdff29)
                        }
                    when (quote) {
                        is RepositoryResult.Success ->
                            mutableState.value =
                                mutableState.value.copy(
                                    dispositionId = intent.id,
                                    selling = intent.sell,
                                    quote = quote.value,
                                    actionKey =
                                        java.util.UUID
                                            .randomUUID()
                                            .toString(),
                                )
                        is RepositoryResult.Error -> emitEvent(TreeEvent.Feedback(quote.message))
                    }
                    return@launch
                }
                val result =
                    withContext(Dispatchers.IO) {
                        when (intent) {
                            is TreeAction.Water ->
                                when (repository.water(intent.id)) {
                                    WateringOutcome.Watered -> success(TextKey.CopyDaTuoiCayCuaSoTangTruongDaBatDau1da977)
                                    WateringOutcome.AlreadyWatered -> failure(TextKey.CopyCayDangDuocTuoiThoiGianKhongCongDonA33cd0)
                                    WateringOutcome.Dead -> failure(TextKey.CopyCayDaKhoHeoVinhVienChiCoTheDonCay6ddff8)
                                    WateringOutcome.ClockChanged -> failure(TextKey.CopyDongHoDaLuiHayChoGioThucBatKip822584)
                                    WateringOutcome.MissingTree -> failure(TextKey.CopyCayKhongConTrongVuon40c7ae)
                                }
                            is TreeAction.Fertilize -> repository.applyFertilizer(intent.id, intent.tier)
                            is TreeAction.Decorate -> {
                                repository.decorate(intent.id, intent.decoration)
                                success(TextKey.CopyDaCapNhatTrangTri472665)
                            }
                            TreeAction.ConfirmDisposition ->
                                pending.dispositionId?.let {
                                    repository.dispose(it, pending.selling, pending.actionKey)
                                } ?: failure(TextKey.CopyChonCayTruoc84691e)
                            else -> failure(TextKey.CopyKhongCoThaoTac432536)
                        }
                    }
                when (result) {
                    is RepositoryResult.Success -> {
                        emitEvent(TreeEvent.TreeChanged)
                        if (intent == TreeAction.ConfirmDisposition) {
                            mutableState.value = mutableState.value.copy(dispositionId = null, quote = null, isPanelExpanded = false)
                            emitEvent(TreeEvent.Back)
                        }
                        emitEvent(TreeEvent.Feedback(result.value.message))
                    }
                    is RepositoryResult.Error -> emitEvent(TreeEvent.Feedback(result.message))
                }
            } finally {
                mutableState.value = mutableState.value.copy(isLoading = false)
            }
        }
    }

    private fun success(key: TextKey) = RepositoryResult.Success(TreeCareReceipt(AppText.text(key)))

    private fun failure(key: TextKey) = RepositoryResult.Error(AppText.text(key))
}
