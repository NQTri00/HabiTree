package com.mamxanh.app.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.data.local.GardenDatabase
import com.mamxanh.app.data.repository.OfflineGameRepository
import com.mamxanh.app.domain.model.State
import com.mamxanh.app.domain.usecase.GameUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GardenViewModel(context: Context) : ViewModel() {
    private val useCases = GameUseCases(OfflineGameRepository(GardenDatabase(context)))
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    init { refresh() }

    fun refresh(message: String = "") {
        viewModelScope.launch {
            val next = withContext(Dispatchers.IO) {
                State(useCases.loadTasks(), useCases.loadGarden(), useCases.loadStatistics(), false, message)
            }
            _state.value = next
        }
    }

    fun add(title: String, kind: String, category: String, reward: Int, frequency: String, due: String) =
        viewModelScope.launch(Dispatchers.IO) {
            if (title.isNotBlank()) useCases.addTask(title, kind, category, reward, frequency, due)
            refresh("Đã thêm nhiệm vụ")
        }

    fun delete(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        useCases.deleteTask(id)
        refresh("Đã xóa nhiệm vụ")
    }

    fun complete(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val done = useCases.completeTask(id)
        refresh(if (done) "Bạn nhận xu và tất cả cây đã lớn thêm!" else "Nhiệm vụ này đã hoàn thành hôm nay")
    }

    fun bonus() = viewModelScope.launch(Dispatchers.IO) {
        refresh(if (useCases.claimDailyBonus()) "Tuyệt vời! Bạn nhận thêm 20 xu." else "Hoàn thành mọi nhiệm vụ hằng ngày để nhận thưởng.")
    }

    fun buy(id: String) = viewModelScope.launch(Dispatchers.IO) {
        refresh(if (useCases.buySeed(id)) "Đã mua hạt giống." else "Bạn chưa đủ xu.")
    }

    fun plant(id: String, name: String) = viewModelScope.launch(Dispatchers.IO) {
        refresh(if (useCases.plantSeed(id, name)) "Hạt giống đã được gieo." else "Bạn không còn hạt giống này.")
    }

    fun buyFertilizer() = viewModelScope.launch(Dispatchers.IO) {
        refresh(if (useCases.buyFertilizer()) "Đã mua phân bón." else "Bạn cần ít nhất 25 xu.")
    }

    fun fertilize(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        refresh(if (useCases.fertilizeTree(id)) "Cây đã lớn thêm 25 điểm." else "Bạn chưa có phân bón.")
    }

    fun water(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        refresh(if (useCases.waterTree(id)) "Bạn đã tưới cây. Cây nhận thêm 8 điểm phát triển." else "Cây này đã được tưới hôm nay.")
    }

    fun decorate(id: Long, decoration: String) = viewModelScope.launch(Dispatchers.IO) {
        useCases.decorateTree(id, decoration)
        refresh("Đã cập nhật trang trí cho cây.")
    }

    companion object {
        fun factory(context: Context) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(GardenViewModel::class.java)) return GardenViewModel(context) as T
                error("Unknown ViewModel")
            }
        }
    }
}
