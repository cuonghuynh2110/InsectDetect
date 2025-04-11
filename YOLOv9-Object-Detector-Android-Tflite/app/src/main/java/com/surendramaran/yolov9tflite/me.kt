package com.surendramaran.yolov9tflite

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.launch

class me : AppCompatActivity() {

    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://ydavhbhglxgzeiggzpgg.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlkYXZoYmhnbHhnemVpZ2d6cGdnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM4MjQ4NjEsImV4cCI6MjA1OTQwMDg2MX0.7az8QQl04TZigqDsz5RPmTzeGGG_4TRc1QU-ROKTiPc"
        ) {
            install(Postgrest)
            install(Storage)
        }
    }
    private lateinit var imgAvt: ImageView
    private lateinit var imgSetting: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvTitle: TextView
    private lateinit var rvCollections: RecyclerView
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_me)

        // Áp padding cho hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ánh xạ view
        imgAvt = findViewById(R.id.img_avt)
        imgSetting = findViewById(R.id.img_settting)
        tvUsername = findViewById(R.id.tv_username)
        tvTitle = findViewById(R.id.tv_title)
        rvCollections = findViewById(R.id.rv_collections)
        bottomNav = findViewById(R.id.bottom_navigation)

        //nhan data
        val email = intent.getStringExtra("email")

        tvUsername.setText(email)
        // Dùng email này để truy vấn ảnh người dùng trên Supabase
        if (email != null) {
            fetchUserAvatarByEmail(email)
        } else {
            Toast.makeText(this, "Không nhận được email", Toast.LENGTH_SHORT).show()
        }

        // Dữ liệu mẫu
        val sampleData = listOf(
            CollectionItem("abc", "Western striped cucumber beetle", "2025-03-26"),
            CollectionItem("abc", "Striped cucumber beetle", "2025-03-20"),
            CollectionItem("abc", "Green stink bug", "2025-03-18"),

        )

        // Thiết lập RecyclerView
        rvCollections.layoutManager = GridLayoutManager(this, 2)
        rvCollections.adapter = CollectionAdapter(sampleData)

        // Xử lý bottom navigation
        bottomNav.selectedItemId = R.id.nav_me
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_me -> true
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

        // Optional: xử lý nút setting nếu muốn
        imgSetting.setOnClickListener {
            // Ví dụ mở activity cài đặt
            // startActivity(Intent(this, SettingActivity::class.java))
        }
    }
    private fun fetchUserAvatarByEmail(email: String) {
        lifecycleScope.launch {
            try {
                val user = supabase.postgrest["nguoidung"]
                    .select {
                        filter {
                            eq("email", email)
                        }
                    }
                    .decodeSingle<NguoiDung>()

                val avatarPath = user.anh

                if (!avatarPath.isNullOrBlank()) {
                    Glide.with(this@me)
                        .load(avatarPath)
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .error(R.drawable.ic_baseline_person_24)
                        .transform(CircleCrop())
                        .into(imgAvt)
                } else {
                    Toast.makeText(this@me, "Người dùng chưa có ảnh đại diện", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@me, "Lỗi truy vấn avatar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}