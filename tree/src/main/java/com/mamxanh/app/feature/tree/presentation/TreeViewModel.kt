package com.mamxanh.app.feature.tree.presentation

import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.tree.domain.TreeRepository
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TreeViewModel(private val repository:TreeRepository) : BaseViewModel<TreeUiState, TreeEffect, TreeIntent>(
    initialState = TreeUiState(),
) {
    val uiState get() = state
    val effects get() = events
    override fun handleAction(action: TreeIntent) {
        val intent = action
        when(intent) {
            TreeIntent.Back -> {
                if(mutableState.value.isPanelExpanded)mutableState.value=mutableState.value.copy(isPanelExpanded=false)
                else viewModelScope.launch{emitEvent(TreeEffect.Back)}
                return
            }
            is TreeIntent.SetPanelExpanded -> {mutableState.value=mutableState.value.copy(isPanelExpanded=intent.expanded);return}
            TreeIntent.DismissDisposition -> {mutableState.value=mutableState.value.copy(dispositionId=null,quote=null);return}
            TreeIntent.OpenFertilizerShop -> {viewModelScope.launch{emitEvent(TreeEffect.OpenFertilizerShop)};return}
            else -> Unit
        }
        if(mutableState.value.isLoading)return
        val pending=mutableState.value
        mutableState.value=pending.copy(isLoading=true)
        viewModelScope.launch {
            try {
                if(intent is TreeIntent.AskDisposition) {
                    val quote=withContext(Dispatchers.IO){if(intent.sell)repository.saleQuote(intent.id) else ActionResult(true,AppText.text(TextKey.CopyDonCayKhongHoanXuHoacHatCdff29))}
                    if(quote.success)mutableState.value=mutableState.value.copy(dispositionId=intent.id,selling=intent.sell,
                        quote=quote,actionKey=java.util.UUID.randomUUID().toString())
                    else emitEvent(TreeEffect.Feedback(quote.message))
                    return@launch
                }
                val result=withContext(Dispatchers.IO) {
                    when(intent) {
                        is TreeIntent.Water -> {
                            val result=repository.water(intent.id)
                            ActionResult(result==TreeWateringResult.WATERED,when(result) {
                                TreeWateringResult.WATERED -> AppText.text(TextKey.CopyDaTuoiCayCuaSoTangTruongDaBatDau1da977)
                                TreeWateringResult.ALREADY_WATERED -> AppText.text(TextKey.CopyCayDangDuocTuoiThoiGianKhongCongDonA33cd0)
                                TreeWateringResult.DEAD -> AppText.text(TextKey.CopyCayDaKhoHeoVinhVienChiCoTheDonCay6ddff8)
                                TreeWateringResult.CLOCK_CHANGED -> AppText.text(TextKey.CopyDongHoDaLuiHayChoGioThucBatKip822584)
                                TreeWateringResult.TREE_NOT_FOUND -> AppText.text(TextKey.CopyCayKhongConTrongVuon40c7ae)
                            })
                        }
                        is TreeIntent.Fertilize -> repository.applyFertilizer(intent.id,intent.tier)
                        is TreeIntent.Decorate -> {repository.decorate(intent.id,intent.decoration);ActionResult(true,AppText.text(TextKey.CopyDaCapNhatTrangTri472665))}
                        TreeIntent.ConfirmDisposition -> pending.dispositionId?.let{repository.dispose(it,pending.selling,pending.actionKey)}
                            ?: ActionResult(false,AppText.text(TextKey.CopyChonCayTruoc84691e))
                        else -> ActionResult(false,AppText.text(TextKey.CopyKhongCoThaoTac432536))
                    }
                }
                if(result.success)emitEvent(TreeEffect.TreeChanged)
                if(intent==TreeIntent.ConfirmDisposition && result.success) {
                    mutableState.value=mutableState.value.copy(dispositionId=null,quote=null,isPanelExpanded=false)
                    emitEvent(TreeEffect.Back)
                }
                emitEvent(TreeEffect.Feedback(result.message))
            } finally {mutableState.value=mutableState.value.copy(isLoading=false)}
        }
    }
}
