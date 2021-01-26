package com.onedudedesign

import android.app.NotificationManager
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import timber.log.Timber

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
            toolbar.logo = ContextCompat.getDrawable(applicationContext,R.mipmap.ic_loadapp_round)


        //set the intent and receive extras
        val intent: Intent = intent
        if (intent.hasExtra("DETAIL_FILE")) {
            content_detail_filename.text = intent.getStringExtra("DETAIL_FILE")}
        if (intent.hasExtra("DETAIL_STATUS")){
            val status = intent.getStringExtra("DETAIL_STATUS")
            if (status == "Failed") {
                content_detail_status.setTextColor(Color.RED)
            } else {
                content_detail_status.setTextColor(Color.GREEN)
            }
            content_detail_status.text = status
        }


        //get the Notification manager
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        // and cancel the notification using the id
        if (intent.hasExtra("NOTIFICATION_ID")) {
            val nId: Int = intent.getIntExtra("NOTIFICATION_ID",-1)
            notificationManager.cancel(nId)
        }

        //set the OK button listener to stop the animation(just in case) and go back to the main
        ok_button.setOnClickListener {
            LoadingButton(applicationContext).stopProgressAnimation()
            finish()
        }
    }

}
