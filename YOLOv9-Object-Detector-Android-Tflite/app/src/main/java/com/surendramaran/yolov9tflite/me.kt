package com.surendramaran.yolov9tflite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.provider.FontsContractCompat
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
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

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
    private lateinit var userId: String
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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
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

        val adapter = PostAdapter(mutableListOf()) { selectedPost ->
            val intent = Intent(this, ChiTietBaiViet::class.java)

            intent.putExtra("title", selectedPost.title)
            intent.putExtra("content", selectedPost.content)
            intent.putExtra("imageUrl", selectedPost.imageUrl)
            intent.putExtra("date", selectedPost.date)
            intent.putExtra("userId", userId)
            intent.putExtra("postId", selectedPost.id)
            startActivity(intent)
        }

        rvCollections.adapter = adapter
        if (email != null) {
            fetchUserAvatarByEmail(email) {
                fetchPosts { postList ->
                    adapter.updateData(postList)
                }
            }
        } else {
            Toast.makeText(this, "Không nhận được email", Toast.LENGTH_SHORT).show()
        }


        // Dữ liệu mẫu
        rvCollections.layoutManager = GridLayoutManager(this, 2)

// Khởi tạo adapter với callback khi click vào item





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
                R.id.nav_chat -> {
                    val email = intent.getStringExtra("email")
                    val intent = Intent(applicationContext, Chatgpt::class.java)
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
            val intent = Intent(this, Setting::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }
    }
    private fun fetchUserAvatarByEmail(email: String, onUserIdReady: () -> Unit) {
        lifecycleScope.launch {
            try {
                val user = supabase.postgrest["nguoidung"]
                    .select { filter { eq("email", email) } }
                    .decodeSingle<NguoiDung>()

                userId = user.id // Gán giá trị trước khi gọi callback
                onUserIdReady() // Gọi callback khi userId đã có

                val avatarPath = user.anh
                if (!avatarPath.isNullOrBlank()) {
                    Glide.with(this@me)
                        .load(avatarPath)
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .error(R.drawable.ic_baseline_person_24)
                        .transform(CircleCrop())
                        .into(imgAvt)
                }
            } catch (e: Exception) {
                Toast.makeText(this@me, "Lỗi truy vấn avatar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun fetchPosts(callback: (List<post>) -> Unit) {
        lifecycleScope.launch {
            try {
//                val baiVietList = supabase.postgrest["baiviet"]
//                    .select(Columns.raw("*")) {
//                        filter {
//                            eq("like", 1)
//                        }
//                    }
//                    .decodeList<BaiViet>()
                val baiVietList = supabase.from("baiviet").select(
                    Columns.raw("""
        id,
        tieu_de,
        anh,
        noi_dung,
        created_at,
        nguoidung_id,
        yeuthich_baiviet!inner (
            baiviet_id,
            nguoidung_id
        )
    """.trimIndent())
                ).decodeList<BaiViet>()

                val postList = baiVietList.map {


                    post(
                        id = it.id,
                        title = it.tieu_de,
                        imageUrl = it.anh,
                        content = it.noi_dung,
                        date = it.created_at.toString(),
                    )
                }

                // ✅ Thông báo số lượng bài viết
                Toast.makeText(this@me, "Tải ${postList.size} bài viết thành công!", Toast.LENGTH_SHORT).show()

                callback(postList)

            } catch (e: Exception) {
                Log.e("Supabase", "Lỗi tải bài viết: ${e.message}")
                // ✅ Thông báo lỗi
                Toast.makeText(this@me, "Lỗi tải bài viết: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Serializable
data class YeuThichBaiViet(
    val id: String? = null,
    val nguoidung_id: String,
    val baiviet_id: String,
    val created_at: String? = null
)
