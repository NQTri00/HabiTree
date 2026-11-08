package com.mamxanh.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mamxanh.app.data.local.GardenDatabase
import com.mamxanh.app.data.repository.GameStorage
import com.mamxanh.app.feature.garden.data.OfflineGardenRepository
import com.mamxanh.app.feature.garden.presentation.GardenViewModel
import com.mamxanh.app.feature.home.data.OfflineHomeDashboardRepository
import com.mamxanh.app.feature.home.presentation.HomeViewModel
import com.mamxanh.app.feature.shop.data.OfflineShopRepository
import com.mamxanh.app.feature.shop.presentation.ShopViewModel
import com.mamxanh.app.feature.stats.data.OfflineStatisticsRepository
import com.mamxanh.app.feature.stats.presentation.StatsViewModel
import com.mamxanh.app.feature.tasks.data.OfflineTaskRepository
import com.mamxanh.app.feature.tasks.presentation.TasksViewModel
import com.mamxanh.app.feature.tree.data.OfflineTreeRepository
import com.mamxanh.app.feature.tree.presentation.TreeViewModel
import com.mamxanh.app.presentation.ui.AppViewModel
import com.mamxanh.app.presentation.ui.MamXanhApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val game = GameStorage(GardenDatabase(applicationContext))
        setContent {
            val gardenVm: GardenViewModel = viewModel(factory = vmFactory { GardenViewModel(OfflineGardenRepository(game)) })
            val homeVm: HomeViewModel = viewModel(factory = vmFactory { HomeViewModel(OfflineHomeDashboardRepository(game)) })
            val statsVm: StatsViewModel = viewModel(factory = vmFactory { StatsViewModel(OfflineStatisticsRepository(game)) })
            val shopVm: ShopViewModel = viewModel(factory = vmFactory { ShopViewModel(OfflineShopRepository(game)) })
            val tasksVm: TasksViewModel = viewModel(factory = vmFactory { TasksViewModel(OfflineTaskRepository(game)) })
            val treeVm: TreeViewModel = viewModel(factory = vmFactory { TreeViewModel(OfflineTreeRepository(game)) })
            val appVm: AppViewModel = viewModel(factory = vmFactory { AppViewModel() })
            MamXanhApp(tasksVm, gardenVm, shopVm, treeVm, statsVm, homeVm, appVm)
        }
    }
}

private fun <T : ViewModel> vmFactory(create: () -> T) = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = create() as VM
}
