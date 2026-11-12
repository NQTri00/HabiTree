package com.mamxanh.app.data

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.mamxanh.app.data.local.GardenDatabase
import com.mamxanh.shared.model.ReminderDelivery
import java.util.concurrent.Executors

/** Inexact, opt-in local scheduling. Receipts and validation live in shared SQLite. */
class TaskReminderReceiver:BroadcastReceiver() {
    override fun onReceive(context:Context,intent:Intent) {
        val pending=goAsync()
        TaskReminders.executor.execute {
            try {
                GardenDatabase(context).use { db ->
                    val id=intent.getLongExtra("id",0)
                    val date=intent.getStringExtra("date").orEmpty()
                    val kind=intent.getStringExtra("kind").orEmpty()
                    val permitted=Build.VERSION.SDK_INT<33 ||
                        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED
                    if(id!=0L && permitted && db.claimReminder(id,date,kind)) {
                        val launch=context.packageManager.getLaunchIntentForPackage(context.packageName)
                            ?.putExtra("engagement_task",id)?.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        if(launch!=null) {
                            val manager=context.getSystemService(NotificationManager::class.java)
                            if(Build.VERSION.SDK_INT>=26)manager.createNotificationChannel(NotificationChannel("gentle_tasks","Nhắc nhẹ",
                                NotificationManager.IMPORTANCE_DEFAULT))
                            val open=PendingIntent.getActivity(context,id.hashCode(),launch,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                            val builder=if(Build.VERSION.SDK_INT>=26)Notification.Builder(context,"gentle_tasks") else Notification.Builder(context)
                            try {
                                manager.notify(id.hashCode(),builder.setSmallIcon(android.R.drawable.ic_menu_agenda)
                                    .setContentTitle(intent.getStringExtra("title") ?: "Một việc nhỏ")
                                    .setContentText(if(id<0)"Cây cần được chăm sóc khi bạn có thời gian." else "Bạn có thể bắt đầu khi phù hợp.")
                                    .setContentIntent(open).setAutoCancel(true).build())
                            }catch(_:SecurityException){ /* Revocation never blocks task/focus behavior. */ }
                        }
                    }
                }
                TaskReminders.schedule(context,emptyList())
            } finally{pending.finish()}
        }
    }
}
class ReminderRescheduleReceiver:BroadcastReceiver() {
    override fun onReceive(context:Context,intent:Intent) {
        if(intent.action !in setOf(Intent.ACTION_BOOT_COMPLETED,Intent.ACTION_TIMEZONE_CHANGED,
                Intent.ACTION_TIME_CHANGED,Intent.ACTION_MY_PACKAGE_REPLACED))return
        val pending=goAsync()
        TaskReminders.executor.execute {try{TaskReminders.schedule(context,emptyList())}finally{pending.finish()}}
    }
}
object TaskReminders {
    internal val executor=Executors.newSingleThreadExecutor()
    @Synchronized
    fun schedule(context:Context,@Suppress("UNUSED_PARAMETER")tasks:List<com.mamxanh.shared.model.Task>) {
        val manager=context.getSystemService(AlarmManager::class.java)
        val prefs=context.getSharedPreferences("scheduled_gentle_reminders",Context.MODE_PRIVATE)
        // Persist pending identities so completed/archived/deleted and disabled alerts can be cancelled too.
        prefs.getStringSet("keys",emptySet()).orEmpty().forEach { identity ->
            val pending=PendingIntent.getBroadcast(context,0,Intent(context,TaskReminderReceiver::class.java)
                .setData(Uri.parse(identity)),PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
            if(pending!=null){manager.cancel(pending);pending.cancel()}
        }
        val candidates=GardenDatabase(context).use{it.reminderCandidates()}
        val keys=mutableSetOf<String>()
        candidates.forEach { d ->
            val identity="mamxanh://reminder/${d.taskId}/${d.date}/${Uri.encode(d.kind)}"
            val intent=Intent(context,TaskReminderReceiver::class.java).setData(Uri.parse(identity))
                .putExtra("id",d.taskId).putExtra("date",d.date).putExtra("kind",d.kind).putExtra("title",d.title)
            val pending=PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            try {
                manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,d.at,pending);keys+=identity
            }catch(_:SecurityException){pending.cancel()}
        }
        prefs.edit().putStringSet("keys",keys).apply()
    }
}
