package com.mamxanh.app

import android.os.Bundle
import androidx.compose.runtime.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import com.mamxanh.app.feature.tasks.presentation.TasksViewModel
import com.mamxanh.app.presentation.ui.MamXanhApp
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase:android.content.Context){
        super.attachBaseContext(com.mamxanh.app.presentation.ui.localization.AppLanguageController.wrap(newBase))
    }
    private var incomingEntity by mutableStateOf<Long?>(null)
    override fun onNewIntent(intent:android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        incomingEntity=intent.getLongExtra("engagement_task",0).takeIf{it!=0L}
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        incomingEntity=intent.getLongExtra("engagement_task",0).takeIf{it!=0L}
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
        )
        if (android.os.Build.VERSION.SDK_INT >= 29) window.isNavigationBarContrastEnforced = false
        setContent {
            val tasksVm: TasksViewModel = koinViewModel()
            val engagementVm: com.mamxanh.app.feature.tasks.presentation.EngagementViewModel = koinViewModel()
            MamXanhApp(tasksVm,engagementVm,
                incomingEntity=incomingEntity,onIncomingHandled={incomingEntity=null})
        }
    }
}
