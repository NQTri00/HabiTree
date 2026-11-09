package com.mamxanh.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import com.mamxanh.app.feature.garden.presentation.GardenViewModel
import com.mamxanh.app.feature.home.presentation.HomeViewModel
import com.mamxanh.app.feature.shop.presentation.ShopViewModel
import com.mamxanh.app.feature.stats.presentation.StatsViewModel
import com.mamxanh.app.feature.tasks.presentation.TasksViewModel
import com.mamxanh.app.feature.tree.presentation.TreeViewModel
import com.mamxanh.app.presentation.ui.AppViewModel
import com.mamxanh.app.presentation.ui.MamXanhApp
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        if (android.os.Build.VERSION.SDK_INT >= 29) window.isNavigationBarContrastEnforced = false
        setContent {
            val gardenVm: GardenViewModel = koinViewModel()
            val homeVm: HomeViewModel = koinViewModel()
            val statsVm: StatsViewModel = koinViewModel()
            val shopVm: ShopViewModel = koinViewModel()
            val tasksVm: TasksViewModel = koinViewModel()
            val treeVm: TreeViewModel = koinViewModel()
            val appVm: AppViewModel = koinViewModel()
            MamXanhApp(tasksVm, gardenVm, shopVm, treeVm, statsVm, homeVm, appVm)
        }
    }
}
