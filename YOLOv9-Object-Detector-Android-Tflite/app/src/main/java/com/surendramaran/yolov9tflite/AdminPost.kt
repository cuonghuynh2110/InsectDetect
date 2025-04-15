package com.surendramaran.yolov9tflite

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.Date
import java.util.UUID

class AdminPost : AppCompatActivity() {


    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://ydavhbhglxgzeiggzpgg.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlkYXZoYmhnbHhnemVpZ2d6cGdnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM4MjQ4NjEsImV4cCI6MjA1OTQwMDg2MX0.7az8QQl04TZigqDsz5RPmTzeGGG_4TRc1QU-ROKTiPc"
        ) {
            install(Postgrest)
            install(Storage)
        }
    }

    private lateinit var titleEditText: EditText
    private lateinit var imageView: ImageView
    private lateinit var descriptionEditText: EditText
    private lateinit var uploadButton: Button
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Ánh xạ view
        titleEditText = findViewById(R.id.pTitleEt)
        imageView = findViewById(R.id.pImageIv)
        descriptionEditText = findViewById(R.id.pDescriptionEt)
        uploadButton = findViewById(R.id.pUploadBtn)

        // Sự kiện click chọn ảnh
        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST)
        }
        // Xử lý sự kiện nếu có
        uploadButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()

            if (title.isEmpty() || description.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Điền đầy đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val imageUrl = uploadImageToSupabase(imageUri!!)
                    val baiViet = BaiViet(
                        tieu_de = title,
                        noi_dung = description,
                        anh = imageUrl,
                        nguoidung_id = "30d3a5ff-6173-49ba-977e-704d20999e1e",
                        created_at = Date.from(Instant.now()).toString()
                    )

                    supabase.from("baiviet").insert(baiViet)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AdminPost, "Đăng bài thành công", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AdminPost, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // === Ánh xạ BottomNavigationView và xử lý click ===
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_post // hoặc nav_chat nếu đang ở Chatgpt

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_post -> {

                    true
                }
                R.id.nav_home -> {
                    val intent = Intent(this, Admin::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
    // Nhận kết quả chọn ảnh từ thư viện
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri) // hiển thị ảnh lên ImageView
        }
    }
    private suspend fun uploadImageToSupabase(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: throw Exception("Không thể đọc ảnh")
        val fileName = "post_${UUID.randomUUID()}.jpg"

        val bucket = supabase.storage.from("images")
        bucket.upload(fileName, bytes) {
            upsert = true
        }

        return bucket.publicUrl(fileName)
    }

}
@Serializable
data class BaiViet(
    val id: String? = null,
    val tieu_de: String,
    val noi_dung: String,
    val anh: String,
    val nguoidung_id: String,
    val created_at: String
)