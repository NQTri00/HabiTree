package com.mamxanh.app.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.feature.home.domain.LoadHomeDashboard
import com.mamxanh.app.feature.home.domain.HomeDashboardRepository
import com.mamxanh.app.feature.home.domain.model.HomeDashboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val repository: HomeDashboardRepository) : ViewModel() {
    private val _dashboard = MutableStateFlow(HomeDashboard())
    val dashboard = _dashboard.asStateFlow()
    init { refresh() }
    fun refresh() = viewModelScope.launch { _dashboard.value = withContext(Dispatchers.IO) { LoadHomeDashboard(repository)() } }
}
