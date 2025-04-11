package com.surendramaran.yolov9tflite

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

class Setting : AppCompatActivity() {
    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://ydavhbhglxgzeiggzpgg.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlkYXZoYmhnbHhnemVpZ2d6cGdnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM4MjQ4NjEsImV4cCI6MjA1OTQwMDg2MX0.7az8QQl04TZigqDsz5RPmTzeGGG_4TRc1QU-ROKTiPc"
        ) {
            install(Postgrest)
            install(Storage)
        }
    }

    // View references
    private lateinit var btnBack: ImageView
    private lateinit var cardEditProfile: CardView
    private lateinit var cardSuggestion: CardView
    private lateinit var cardLogout: CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ánh xạ giao diện
        btnBack = findViewById(R.id.btn_back)
        cardEditProfile = findViewById(R.id.card_edit_profile)
        cardSuggestion = findViewById(R.id.card_suggestion)
        cardLogout = findViewById(R.id.card_logout)


        // Lấy email từ Intent
        val email = intent.getStringExtra("email")
        Toast.makeText(this, "Email: $email", Toast.LENGTH_SHORT).show()

        // Sự kiện click nút quay lại
        btnBack.setOnClickListener {
            finish()
        }

        // Các xử lý click khác (tuỳ ý bạn mở rộng)
        cardEditProfile.setOnClickListener {
            val intent = Intent(this, Editprofile::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        cardSuggestion.setOnClickListener {
            Toast.makeText(this, "Suggestion clicked", Toast.LENGTH_SHORT).show()
        }

        cardLogout.setOnClickListener {
            // Inflate layout custom loading
            val view = layoutInflater.inflate(R.layout.dialog_loading_dangxuat, null)

            // Tạo dialog với nội dung vừa inflate
            val progressDialog = AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create()

            // Hiển thị dialog
            progressDialog.show()

            // Giả lập delay để tắt loading, sau đó chuyển màn hình
            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()

                val intent = Intent(this@Setting, DangNhap::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }, 1500) // Delay 1.5s
        }


    }
}
