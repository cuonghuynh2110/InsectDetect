package com.surendramaran.yolov9tflite

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class me : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_me)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Gắn sự kiện cho bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set item được chọn là Home
        bottomNav.selectedItemId = R.id.nav_me

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_me -> {
                    // Đang ở Home, không cần chuyển
                    true
                }
                R.id.nav_detect -> {
                    val email = intent.getStringExtra("email")
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_home -> {
                    val email = intent.getStringExtra("email")
                    val intent = Intent(applicationContext, Home::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
}