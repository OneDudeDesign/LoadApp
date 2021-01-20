package com.onedudedesign

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)


        val intent: Intent = intent
        if (intent.hasExtra("DETAIL_FILE")) {
            content_detail_filename.text = intent.getStringExtra("DETAIL_FILE")}
        if (intent.hasExtra("DETAIL_STATUS")){
        content_detail_status.text = intent.getStringExtra("DETAIL_STATUS")}


        //cancel the notification
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        if (intent.hasExtra("NOTIFICATION_ID")) {
            val nId: Int = intent.getIntExtra("NOTIFICATION_ID",-1)
            notificationManager.cancel(nId)
        }

    }
}
