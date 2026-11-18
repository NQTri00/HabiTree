package com.mamxanh.app.presentation.bio

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

/** Only launcher aliases change; the activity, application ID and stored data stay intact. */
internal object HabiTreeLauncherIcon {
    private val species = listOf("succulent", "monstera", "ficus", "snake", "cactus", "aloe", "lavender", "pilea", "cherry", "pine", "mandarin")

    @Synchronized fun update(context: Context, treeSpecies: String) {
        val selected = treeSpecies.takeIf { it in species } ?: "succulent"
        val manager = context.packageManager
        fun component(id: String) = ComponentName(context.packageName, context.packageName + ".launcher.Icon_" + id)
        fun enabled(id: String): Boolean = when (manager.getComponentEnabledSetting(component(id))) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> id == "succulent"
            else -> false
        }
        runCatching {
            val active = species.filter(::enabled)
            if (active == listOf(selected)) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val changed = (active + selected).distinct().map { id ->
                    PackageManager.ComponentEnabledSetting(
                        component(id),
                        if (id == selected) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP,
                    )
                }
                manager.setComponentEnabledSettings(changed.toMutableList())
            } else {
                // Enable the replacement first so the app always has a launchable entry.
                manager.setComponentEnabledSetting(component(selected), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
                active.filter { it != selected }.forEach { id ->
                    manager.setComponentEnabledSetting(component(id), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                }
            }
        }.onFailure { Log.w("HabiTreeLauncherIcon", "Launcher icon update could not be applied", it) }
    }
}
