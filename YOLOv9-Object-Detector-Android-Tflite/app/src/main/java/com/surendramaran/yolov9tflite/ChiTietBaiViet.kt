package com.surendramaran.yolov9tflite

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class ChiTietBaiViet : AppCompatActivity() {

    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://ydavhbhglxgzeiggzpgg.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlkYXZoYmhnbHhnemVpZ2d6cGdnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM4MjQ4NjEsImV4cCI6MjA1OTQwMDg2MX0.7az8QQl04TZigqDsz5RPmTzeGGG_4TRc1QU-ROKTiPc"
        ) {
            install(Postgrest)
            install(Storage)
        }
    }

    private lateinit var imgAnh: ImageView
    private lateinit var tvTieuDe: TextView
    private lateinit var tvNoiDung: TextView
    private lateinit var tvNgayDang: TextView
    private lateinit var btnLuuBai: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chi_tiet_bai_viet)


        // Khai báo các view
        val imgAnh = findViewById<ImageView>(R.id.imgAnh)
        val tvTieuDe = findViewById<TextView>(R.id.tvTieuDe)
        val tvNoiDung = findViewById<TextView>(R.id.tvNoiDung)
        val tvNgayDang = findViewById<TextView>(R.id.tvNgayDang)
        btnLuuBai = findViewById<Button>(R.id.btnLuuBai)

        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val imageUrl = intent.getStringExtra("imageUrl")
        val date = intent.getStringExtra("date")
        val userId = intent.getStringExtra("userId")
        val postId = intent.getStringExtra("postId")




        tvTieuDe.text = title
        tvNoiDung.text = content
        tvNgayDang.text = date

        Glide.with(this)
            .load(imageUrl)
            .into(imgAnh)

        btnLuuBai.setOnClickListener {
            if (userId != null && postId != null) {
                luuBaiViet(userId, postId)
            } else {
                Toast.makeText(this, "Thiếu userId hoặc postId", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun luuBaiViet(userId: String, postId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = yeuthich_baiviet(nguoidung_id = userId, baiviet_id = postId)
                supabase.from("yeuthich_baiviet").insert(data)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ChiTietBaiViet, "Đã lưu bài viết", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("SupabaseError", "Lỗi lưu bài viết", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ChiTietBaiViet, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

@Serializable
data class yeuthich_baiviet(
    val nguoidung_id: String,
    val baiviet_id: String
)
