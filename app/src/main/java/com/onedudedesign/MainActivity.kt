package com.onedudedesign

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.*
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber
import java.lang.Exception
import java.util.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var radioSelected: Boolean = false
    private var radioURL = ""
    private var downloadedFile = ""
    private var downloadStatus = ""

    private val NOTIFICATION_ID = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
            toolbar.logo = ContextCompat.getDrawable(applicationContext,R.mipmap.ic_loadapp_round)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


        custom_button.setOnClickListener {

            //when clicked set the download url or ask for a selection
            setUrlFromRadioSelection()

            //check for network then radio selection then download
            if (checkNetwork()) {
                Timber.i("Network is on")
                if (radioSelected) {
                    custom_button.buttonState = ButtonState.Clicked
                    disableRadioGroup()
                    try {
                        download()
                    } catch (e: Exception) {
                        Timber.i(e)
                    }

                }
            } else {
                Timber.i("Network is off")
                Toast.makeText(
                    this,
                    "Your Network may be disabled or in Airplane Mode, please check and try again",
                    Toast.LENGTH_SHORT).show()
            }
        }
        //create notification channel
        createChannel(CHANNEL_ID, getString(R.string.download_channel_name))
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //reset the radiogroup
            enableRadioGroup()

            //stop the progress animation and set the button state
            custom_button.buttonState=ButtonState.Completed
            LoadingButton(applicationContext).stopProgressAnimation()

            //query the status from the DownloadManagher and set variable for the intent

            if (id != null) {
                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status: Int =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    cursor.close()

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
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
                "$downloadStatus downloading file: $downloadedFile",
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
        //clearing the radiogroup selection after download clicked
        rg_main.clearCheck()
    }

    private fun setUrlFromRadioSelection() {
        //check the radio selections and setup
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
        Timber.i("Download ID: %s", downloadID)


        //set to checkstatus
        downloadStatusTimer(downloadID)
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
        //set the intent
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
        //Build the notification
        val builder = NotificationCompat.Builder(
            applicationContext, channelId
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.mipmap.ic_loadapp_foreground))
            .setContentTitle(applicationContext.getString(R.string.download_channel_name))
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setChannelId(channelId)

            .addAction(
                R.drawable.ic_launcher_foreground,
                "CheckStatus",
                pendingIntent
            )

        notify(NOTIFICATION_ID, builder.build())
    }
    //notification channel creation
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

    //NOTE I have left the timer code here as it works and could be used to set progress; however,
    //the total download size gets reported as -1 until the download completes so it makes it
    //impossible to make a calculation on percentage of progress. Will need some work but is not
    //necessary for the rubric

    private fun downloadStatusTimer(id: Long) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {

                var status = 0
                val size: String
                val downloadSoFar: String

                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    status =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    // no matter where I run this it returns -1 until the download is successful then
                    //returns the correct size, useless for animating.....
                    size =
                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    downloadSoFar =
                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    Timber.i("Size: %s, Download so far: %s", size, downloadSoFar)
                    cursor.close()
                }
                //report on status
                when (status) {
                    0 -> Timber.i("Check Cursor??")
                    DownloadManager.STATUS_PENDING -> Timber.i("Pending")
                    DownloadManager.STATUS_RUNNING -> Timber.i("Running")
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        Timber.i("Succesful")
                        timer.cancel()
                    }
                    DownloadManager.STATUS_PAUSED -> Timber.i("Paused")
                    DownloadManager.STATUS_FAILED -> {
                        Timber.i("Failed")
                        timer.cancel()
                    }
                    else -> Timber.i("Something Else: %s", status)

                }

            }

        }, 1, 500)
    }


    private fun checkNetwork(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = cm.activeNetwork

        return nw != null
    }
    //disable the radio group while downloading
    private fun disableRadioGroup(){
        for (i in 0..rg_main.childCount-1) {
            rg_main.getChildAt(i).isEnabled = false
        }
    }
    //enable the radio group
    private fun enableRadioGroup(){
        for (i in 0..rg_main.childCount-1) {
            rg_main.getChildAt(i).isEnabled = true
        }
    }
}
