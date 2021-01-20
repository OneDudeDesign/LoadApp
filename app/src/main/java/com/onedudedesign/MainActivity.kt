package com.onedudedesign

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
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
    private var downloadedFile = ""
    private var downloadStatus = ""

    private val NOTIFICATION_ID = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var downloadManager: DownloadManager

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

            //query the status and set variable for the intent

            if (id != null) {
                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()){
                    val status: Int =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    cursor.close()

                    if(status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloadStatus = "Succeeded"
                    } else {
                        downloadStatus = "Failed"
                    }

                    Timber.i("Status is: %S", downloadStatus)
                }

            }

            Timber.i("Received something")
            notificationManager = ContextCompat.getSystemService(
                this@MainActivity,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.sendNotification(
                "Downloaded file: $downloadedFile",
                CHANNEL_ID,
                this@MainActivity
            )

            //cleanup
            downloadStatus = ""

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
                downloadedFile = radioButtonGlide.text.toString()
                Timber.i("Glide selected for Download")
            }
            radioButtonLoadApp.id -> {
                radioSelected = true
                radioURL = URLGitHub
                downloadedFile = radioButtonLoadApp.text.toString()
                Timber.i("LoadApp selected for download")
            }
            rbRetrofit.id -> {
                radioSelected = true
                radioURL = URLRetrofit
                downloadedFile = rbRetrofit.text.toString()
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

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        //cleanup
        radioSelected = false
        radioURL = ""
        clearRadioGroupSelection()
    }

    fun NotificationManager.sendNotification(
        message: String,
        channelId: String,
        applicationContext: Context
    ) {

        val detailIntent = Intent(applicationContext, DetailActivity::class.java)
        detailIntent.putExtra("DETAIL_FILE", downloadedFile)
        detailIntent.putExtra("DETAIL_STATUS", downloadStatus)
        detailIntent.putExtra("NOTIFICATION_ID", NOTIFICATION_ID)


        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(
            applicationContext, channelId
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.download_channel_name))
            .setContentText(message)
            //.setContentIntent(pendingIntent)
            //.setAutoCancel(true)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "CheckStatus",
                pendingIntent
            )

        notify(NOTIFICATION_ID, builder.build())
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
