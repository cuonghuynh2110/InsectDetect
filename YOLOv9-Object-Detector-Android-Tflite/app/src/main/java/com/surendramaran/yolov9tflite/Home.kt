package com.surendramaran.yolov9tflite

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✅ Hiển thị danh sách côn trùng
        val listView = findViewById<ListView>(R.id.listInsects)

        val insectList = listOf(
            Insect("acalymma", R.drawable.acalymma),
            Insect("alticini", R.drawable.alticini),
            Insect("squashbug", R.drawable.squashbug),
            Insect("asparagus", R.drawable.asparagus)
        )

        val adapter = InsectAdapter(this, insectList)
        listView.adapter = adapter

        // 🔽 Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_detect -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_me -> {
                    startActivity(Intent(this, me::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
}
