package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var url: String
    private lateinit var project: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if(url == "") {
                Toast.makeText(this, getString(R.string.no_option_selected), Toast.LENGTH_SHORT).show()
            } else {
                download()
                project = when (url) {
                    URL_GLIDE -> "Glide"
                    URL_LOADAPP -> "LoadApp"
                    URL_RETROFIT -> "Retrofit"
                    else -> "Unknown"
                }
            }
        }

        radioGroup.setOnCheckedChangeListener { _, i ->
            url = when(i) {
                R.id.radioButtonGlide -> URL_GLIDE
                R.id.radioButtonLoadApp -> URL_LOADAPP
                R.id.radioButtonRetrofit -> URL_RETROFIT
                else -> ""
            }
        }

        url = ""

        notificationManager = ContextCompat.getSystemService(application, NotificationManager::class.java) as NotificationManager

        createChannel(getString(R.string.download_notification_channel_id), getString(R.string.download_notification_channel_name))
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                notificationManager.sendNotification(
                    "The Project $project repository is downloaded",
                    if (getDownloadStatus() == DownloadManager.STATUS_SUCCESSFUL) {
                        "SUCCESS"
                    } else {
                        "FAILED"
                    },
                    project,
                    application
                )
            }
        }
    }

    private fun getDownloadStatus(): Int {
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val query = DownloadManager.Query()
        query.setFilterById(downloadID)

        val cursor = downloadManager.query(query)
        if(cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            return cursor.getInt(index)
        }
        return DownloadManager.STATUS_FAILED
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
    }

    private fun createChannel(channelId: String, channelName: String) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Donwload ready!"

            val notificationManager = application.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val URL_GLIDE = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val URL_LOADAPP = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private const val URL_RETROFIT = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
