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
import android.os.Environment
import android.widget.RadioButton
import android.widget.RadioGroup
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
//TODO: Make it download the correct selection
        custom_button.setOnClickListener {
            setUriFromRadioSelection()

            if (radioSelected) {
                download()
                radioSelected = false
                clearRadioGroupSelection()
            }
        }


    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Timber.i("Received something")
        }
    }


    private fun clearRadioGroupSelection (){
        rg_main.clearCheck()
    }

    private fun setUriFromRadioSelection() {
        var id: Int = rg_main.checkedRadioButtonId
        if (id != -1) {
            // get the radio button if its checked
            val rb: RadioButton = findViewById(id)
            // set the radioSelected flag
            radioSelected = true
            Toast.makeText(
                applicationContext,
                "When the button was clicked, the radioButton was: ${rb.text}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            //nothing was selected
            Toast.makeText(
                applicationContext,
                "Nothing was selected, please make a selection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URLGitHub))
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
        rg_main.clearCheck()
    }

    companion object {
        private const val URLGitHub =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URLGlide =
            "https://github.com/bumptech/glide"
        private const val URLRetrofit =
            "https://github.com/square/retrofit"
        private const val CHANNEL_ID = "channelId"
    }

}
