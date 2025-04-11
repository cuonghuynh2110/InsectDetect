package com.surendramaran.yolov9tflite

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
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
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.UUID

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
    private val REQUEST_CODE_PICK_IMAGE = 1001
    private var selectedImageUri: Uri? = null


    private lateinit var btnBack: ImageButton
    private lateinit var tvEditProfileTitle: TextView
    private lateinit var imgAvt: ImageView
    private lateinit var tvProfilePhotoLabel: TextView
    private lateinit var tvNicknameLabel: TextView
    private lateinit var etNickname: EditText
    private lateinit var tvEmailLabel: TextView
    private lateinit var etEmail: EditText
    private lateinit var btnSave: Button

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
        btnSave = findViewById(R.id.btnSave)

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

        imgAvt.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }
        btnSave.setOnClickListener {
            val username = etNickname.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (username.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val imageUrl = selectedImageUri?.let { uploadAvatarToSupabase(it) }

                    val request = UpdateUserRequest(username = username, anh = imageUrl)

                    supabase.from("nguoidung")
                        .update(request) {
                            filter {
                                eq("email", email)
                            }
                        }


                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Editprofile, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("UpdateError", "Cập nhật thất bại", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Editprofile, "Lỗi cập nhật: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
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
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                selectedImageUri = it
                imgAvt.setImageURI(it)
            }
        }
    }
    private suspend fun uploadAvatarToSupabase(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes() ?: return null
            val fileName = "avatar_${UUID.randomUUID()}.jpg"
            val bucket = supabase.storage.from("images")

            bucket.upload(fileName, bytes) {
                upsert = true
            }

            bucket.publicUrl(fileName)
        } catch (e: Exception) {
            Log.e("UploadError", "Không thể upload ảnh", e)
            null
        }
    }
}
@Serializable
data class UpdateUserRequest(
    val username: String,
    val anh: String? = null
)