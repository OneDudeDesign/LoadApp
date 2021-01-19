package com.onedudedesign

import android.app.DownloadManager
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
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


    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Timber.i("Received something")
        }
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

                //show a notification on completion
                .setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        //cleanup
        radioSelected = false
        radioURL = ""
        clearRadioGroupSelection()
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

}
