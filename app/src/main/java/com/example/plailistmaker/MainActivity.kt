package com.example.plailistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val btnMediaLibrary = findViewById<ImageButton>(R.id.media_library)
        val btnSearch = findViewById<ImageButton>(R.id.btnSearch)
        val btnSetting = findViewById<ImageButton>(R.id.setting)

        btnSearch.setOnClickListener {
            val btnSearch = Intent(this, SearchActivity::class.java)
            startActivity(btnSearch)
        }

        btnMediaLibrary.setOnClickListener {
            val btnMediaLibrary = Intent(this, MediaLibraryActivity::class.java)
            startActivity(btnMediaLibrary)
        }

        btnSetting.setOnClickListener {
            val btnSetting = Intent(this, SettingsActivity::class.java)
            startActivity(btnSetting)
        }


    }
}