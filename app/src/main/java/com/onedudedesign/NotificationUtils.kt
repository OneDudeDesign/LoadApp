package com.onedudedesign

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun NotificationManager.sendNotification(message : String, channelId : String, applicationContext: Context) {
    val builder = NotificationCompat.Builder(
        applicationContext, channelId
    )
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(applicationContext.getString(R.string.download_channel_name))
        .setContentText(message)

    notify(NOTIFICATION_ID, builder.build())
}