package com.surendramaran.yolov9tflite

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.security.MessageDigest
import java.util.*

class dangky : AppCompatActivity() {

    private val REQUEST_CODE_PICK_IMAGE = 1001
    private var avatarUri: Uri? = null

    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://ydavhbhglxgzeiggzpgg.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlkYXZoYmhnbHhnemVpZ2d6cGdnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM4MjQ4NjEsImV4cCI6MjA1OTQwMDg2MX0.7az8QQl04TZigqDsz5RPmTzeGGG_4TRc1QU-ROKTiPc"
        ) {
            install(Postgrest)
            install(Storage)
        }
    }

    private lateinit var editTextUsername: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var editTextRePassword: TextInputEditText
    private lateinit var btnSignup: RelativeLayout
    private lateinit var haveAccountTv: TextView
    private lateinit var avatarImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dangky)

        editTextUsername = findViewById(R.id.edittext_username)
        editTextEmail = findViewById(R.id.edittext_email)
        editTextPassword = findViewById(R.id.edittext_password)
        editTextRePassword = findViewById(R.id.edittext_repassword)
        btnSignup = findViewById(R.id.btnSignup)
        haveAccountTv = findViewById(R.id.have_accountTv)
        avatarImageView = findViewById(R.id.avatarImageView)

        avatarImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }

        btnSignup.setOnClickListener { handleSignup() }

        haveAccountTv.setOnClickListener {
            startActivity(Intent(this, Loginn::class.java))
        }
    }

    private fun handleSignup() {
        val username = editTextUsername.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val rePassword = editTextRePassword.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin")
            return
        }

        if (password != rePassword) {
            showToast("Mật khẩu không khớp")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val avatarUrl = avatarUri?.let { uploadAvatarToSupabase(it) }
                val hashedPassword = hashPassword(password)
                val newUser = User(
                    username = username,
                    email = email,
                    password = hashedPassword,
                    anh = avatarUrl ?: ""
                )

                supabase.from("nguoidung").insert(newUser)

                withContext(Dispatchers.Main) {
                    showToast("Đăng ký thành công")
                    finish()
                }
            } catch (e: Exception) {
                Log.e("SupabaseError", "Lỗi đăng ký", e)
                withContext(Dispatchers.Main) {
                    showToast("Đăng ký thất bại: ${e.message}")
                }
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                avatarUri = it
                avatarImageView.setImageURI(it)
            }
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(bytes).joinToString("") { "%02x".format(it) }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}

@Serializable
data class User(
    val id: Int = 0,
    val username: String,
    val email: String,
    val password: String,
    val anh: String = "",
    val vai_tro: String = "user"
)