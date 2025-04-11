package com.surendramaran.yolov9tflite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.http.ContentType
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import com.bumptech.glide.load.resource.bitmap.CircleCrop
class Home : AppCompatActivity() {
    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://ydavhbhglxgzeiggzpgg.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlkYXZoYmhnbHhnemVpZ2d6cGdnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM4MjQ4NjEsImV4cCI6MjA1OTQwMDg2MX0.7az8QQl04TZigqDsz5RPmTzeGGG_4TRc1QU-ROKTiPc"
        ) {
            install(Postgrest)
            install(Storage)
        }
    }

    private lateinit var avatarImageView: ImageView
    private lateinit var username: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //Anh xa
        avatarImageView = findViewById(R.id.imageAvatar)
        username = findViewById(R.id.textName)


        //nhan data
        val email = intent.getStringExtra("email")

        username.setText(email)
        // Dùng email này để truy vấn ảnh người dùng trên Supabase
        if (email != null) {
//            Toast.makeText(this, "Đã nhận được email: $email", Toast.LENGTH_SHORT).show()
            fetchUserAvatarByEmail(email)
        } else {
            Toast.makeText(this, "Không nhận được email", Toast.LENGTH_SHORT).show()
        }


        val listView = findViewById<ListView>(R.id.listInsects)

        val insectList = listOf(
            Insect("acalymma", R.drawable.acalymma),
            Insect("alticini", R.drawable.alticini),
            Insect("squashbug", R.drawable.squashbug),
            Insect("asparagus", R.drawable.asparagus),
            Insect("aulacophora", R.drawable.aulacophora),
            Insect("dermaptera", R.drawable.alticini),
            Insect("leptinotarsa", R.drawable.leptinotarsa),
            Insect("Achatina_fulica", R.drawable.achatinafulica),
            Insect("mantodea", R.drawable.mantodea),
            Insect("Cerotoma_trifurcata", R.drawable.cerotomatrifurcata)
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
                    val email = username.text.toString()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_me -> {
                    val email = username.text.toString()
                    val intent = Intent(applicationContext, me::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                else -> false
            }
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
                    Glide.with(this@Home)
                        .load(avatarPath)
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .error(R.drawable.ic_baseline_person_24)
                        .transform(CircleCrop()) // ✅ Bo tròn ảnh
                        .into(avatarImageView)
                } else {
                    Toast.makeText(this@Home, "Người dùng chưa có ảnh đại diện", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Home, "Lỗi truy vấn avatar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

