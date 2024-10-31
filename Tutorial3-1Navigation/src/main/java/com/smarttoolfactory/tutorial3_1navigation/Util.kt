package com.smarttoolfactory.tutorial3_1navigation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri

const val id = "exampleId"

fun showNotification(context: Context, uriString: String) {

    val deepLinkIntent = Intent(
        Intent.ACTION_VIEW,
        uriString.toUri(),
        context,
        MainActivity::class.java
    )

    val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder
        .create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }


    val notification: Notification = NotificationCompat.Builder(
        context,
        "channelId"
    )
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Go to app")
        .setContentText("Click to open Profile")
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setAutoCancel(true)
        .setContentIntent(deepLinkPendingIntent)
        .build()

    val notificationManager = context.getSystemService() as NotificationManager?

    notificationManager?.run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.createNotificationChannel(
                NotificationChannel("channelId", "name", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        this.notify(1, notification)
    }

}