package com.mamxanh.app.data

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import com.mamxanh.app.data.local.GardenDatabase
import java.util.Calendar
import java.util.concurrent.Executors

/** Inexact local reminders; creating a task never depends on notification permission. */
class TaskReminderReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context,intent: Intent) {
        val pending=goAsync()
        executor.execute {
            try {
                val tasks=GardenDatabase(context).use{it.tasks()}
                val task=tasks.firstOrNull{it.id==intent.getLongExtra("id",-1)}
                if(task!=null && task.lifecycle=="ACTIVE" && task.scheduledToday && !task.doneToday &&
                    (Build.VERSION.SDK_INT<33 || context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED)) {
                    val manager=context.getSystemService(NotificationManager::class.java)
                    if(Build.VERSION.SDK_INT>=26)manager.createNotificationChannel(NotificationChannel("tasks","Nhắc việc",NotificationManager.IMPORTANCE_DEFAULT))
                    val launch=context.packageManager.getLaunchIntentForPackage(context.packageName)
                    val open=PendingIntent.getActivity(context,task.id.toInt(),launch,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                    val builder=if(Build.VERSION.SDK_INT>=26)Notification.Builder(context,"tasks") else Notification.Builder(context)
                    manager.notify(task.id.toInt(),builder.setSmallIcon(android.R.drawable.ic_menu_agenda).setContentTitle(task.title)
                        .setContentText("Một việc nhỏ cho hôm nay").setContentIntent(open).setAutoCancel(true).build())
                }
                TaskReminders.schedule(context,tasks)
            } finally{pending.finish()}
        }
    }
    companion object { private val executor=Executors.newSingleThreadExecutor() }
}
object TaskReminders {
    fun schedule(context:Context,tasks:List<com.mamxanh.shared.model.Task>) {
        val manager=context.getSystemService(AlarmManager::class.java)
        tasks.forEach { task ->
            val pending=PendingIntent.getBroadcast(context,task.id.toInt(),
                Intent(context,TaskReminderReceiver::class.java).putExtra("id",task.id),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            manager.cancel(pending)
            if(task.lifecycle=="ACTIVE" && !task.finished && task.config.reminderMinute>=0) {
                val time=Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY,task.config.reminderMinute/60)
                    set(Calendar.MINUTE,task.config.reminderMinute%60);set(Calendar.SECOND,0);set(Calendar.MILLISECOND,0)
                    if(timeInMillis<=System.currentTimeMillis())add(Calendar.DAY_OF_YEAR,1)
                }
                manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time.timeInMillis,pending)
            }
        }
    }
}
