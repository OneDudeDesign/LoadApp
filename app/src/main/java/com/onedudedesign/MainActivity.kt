package com.onedudedesign

import android.app.DownloadManager
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var radioSelected: Boolean = false
    private var radioURL = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            setUrlFromRadioSelection()

            if (radioSelected) {
                download()
            }
        }

        createChannel(CHANNEL_ID, getString(R.string.download_channel_name))


    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Timber.i("Received something")
            notificationManager = ContextCompat.getSystemService(
                this@MainActivity,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.sendNotification("Is this foo or bar ID: $id", CHANNEL_ID, this@MainActivity)

        }
    }

    companion object {
        private const val URLGitHub =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URLGlide =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val URLRetrofit =
            "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }


    private fun clearRadioGroupSelection() {
        rg_main.clearCheck()
    }

    private fun setUrlFromRadioSelection() {

        when (rg_main.checkedRadioButtonId) {
            radioButtonGlide.id -> {
                radioSelected = true
                radioURL = URLGlide
                Timber.i("Glide selected for Download")
            }
            radioButtonLoadApp.id -> {
                radioSelected = true
                radioURL = URLGitHub
                Timber.i("LoadApp selected for download")
            }
            rbRetrofit.id -> {
                radioSelected = true
                radioURL = URLRetrofit
                Timber.i("Retrofit selected for download")
            }
            else -> {
                Timber.i("Nothing Selected in the radiogroup")
                Toast.makeText(
                    applicationContext,
                    "Nothing was selected to download, please make a selection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(radioURL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        //cleanup
        radioSelected = false
        radioURL = ""
        clearRadioGroupSelection()
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download Completed"

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

}
