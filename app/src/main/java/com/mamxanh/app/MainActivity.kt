package com.mamxanh.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.mamxanh.app.feature.tasks.presentation.ui.list.TasksViewModel
import com.mamxanh.app.presentation.ui.MamXanhApp
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    private companion object {
        const val REMINDER_ENTITY_EXTRA = "engagement_task"
        const val PENDING_REMINDER_STATE = "pending_reminder_entity"
    }

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(
            com.mamxanh.app.core.components.localization.AppLanguageController
                .wrap(newBase),
        )
    }

    private var incomingEntity by mutableStateOf<Long?>(null)

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        incomingEntity = intent.getLongExtra(REMINDER_ENTITY_EXTRA, 0).takeIf { it != 0L }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pendingEntity =
            if (savedInstanceState == null) {
                intent.getLongExtra(REMINDER_ENTITY_EXTRA, 0)
            } else {
                savedInstanceState.getLong(PENDING_REMINDER_STATE)
            }
        incomingEntity = pendingEntity.takeIf { it != 0L }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
        )
        if (android.os.Build.VERSION.SDK_INT >= 29) window.isNavigationBarContrastEnforced = false
        setContent {
            val tasksVm: TasksViewModel = koinViewModel()
            val engagementVm: com.mamxanh.app.feature.tasks.presentation.ui.engagement.EngagementViewModel = koinViewModel()
            MamXanhApp(
                tasksVm,
                engagementVm,
                incomingEntity = incomingEntity,
                onIncomingHandled = { incomingEntity = null },
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        incomingEntity?.let { outState.putLong(PENDING_REMINDER_STATE, it) }
    }
}
