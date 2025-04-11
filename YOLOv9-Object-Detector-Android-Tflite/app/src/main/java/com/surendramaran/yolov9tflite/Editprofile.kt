package com.surendramaran.yolov9tflite

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.launch

class Editprofile : AppCompatActivity() {
    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://ydavhbhglxgzeiggzpgg.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlkYXZoYmhnbHhnemVpZ2d6cGdnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM4MjQ4NjEsImV4cCI6MjA1OTQwMDg2MX0.7az8QQl04TZigqDsz5RPmTzeGGG_4TRc1QU-ROKTiPc"
        ) {
            install(Postgrest)
            install(Storage)
        }
    }
    private lateinit var btnBack: ImageButton
    private lateinit var tvEditProfileTitle: TextView
    private lateinit var imgAvt: ImageView
    private lateinit var tvProfilePhotoLabel: TextView
    private lateinit var tvNicknameLabel: TextView
    private lateinit var etNickname: EditText
    private lateinit var tvEmailLabel: TextView
    private lateinit var etEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editprofile)

        // Áp dụng lề cho hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ánh xạ view
        btnBack = findViewById(R.id.btnBack)
        tvEditProfileTitle = findViewById(R.id.tvEditProfileTitle)
        imgAvt = findViewById(R.id.img_avt)
        tvProfilePhotoLabel = findViewById(R.id.tvProfilePhotoLabel)
        tvNicknameLabel = findViewById(R.id.tvNicknameLabel)
        etNickname = findViewById(R.id.etNickname)
        tvEmailLabel = findViewById(R.id.tvEmailLabel)
        etEmail = findViewById(R.id.etEmail)

        // Lấy email từ Intent và hiển thị
        val email = intent.getStringExtra("email")
        etEmail.setText(email ?: "No email")
        if (email != null) {
            fetchUserAvatarByEmail(email)
        }

        // Sự kiện nút back
        btnBack.setOnClickListener {
            finish()
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
                etNickname.setText(user.username)

                if (!avatarPath.isNullOrBlank()) {
                    Glide.with(this@Editprofile)
                        .load(avatarPath)
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .error(R.drawable.ic_baseline_person_24)
                        .transform(CircleCrop()) // ✅ Bo tròn ảnh
                        .into(imgAvt)
                } else {
                    Toast.makeText(this@Editprofile, "Người dùng chưa có ảnh đại diện", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Editprofile, "Lỗi truy vấn avatar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
