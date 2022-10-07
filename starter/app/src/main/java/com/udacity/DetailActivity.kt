package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val project = findViewById<TextView>(R.id.fileName)
        val status = findViewById<TextView>(R.id.status)
        val button = findViewById<Button>(R.id.button)

        val notificationManager = ContextCompat.getSystemService(application, NotificationManager::class.java) as NotificationManager
        notificationManager.cancelAll()

        project.text = intent.getStringExtra(MainActivity.EXTRA_PROJECT)
        status.text = intent.getStringExtra(MainActivity.EXTRA_STATUS)
        button.setOnClickListener {
            finish()
        }
    }

}
