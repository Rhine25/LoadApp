package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

// Notification ID.
private val NOTIFICATION_ID = 1
private val REQUEST_CODE = 0

fun NotificationManager.sendNotification(messageBody: String, status: String, project: String, applicationContext: Context) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(MainActivity.EXTRA_STATUS, status)
    contentIntent.putExtra(MainActivity.EXTRA_PROJECT, project)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        //.setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.resources.getString(R.string.check_status),
            contentPendingIntent
        )

    notify(NOTIFICATION_ID, builder.build())
}