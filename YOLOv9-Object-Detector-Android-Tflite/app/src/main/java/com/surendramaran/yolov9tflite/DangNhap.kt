package com.surendramaran.yolov9tflite

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.common.SignInButton
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

class DangNhap : AppCompatActivity() {

    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://ydavhbhglxgzeiggzpgg.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlkYXZoYmhnbHhnemVpZ2d6cGdnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM4MjQ4NjEsImV4cCI6MjA1OTQwMDg2MX0.7az8QQl04TZigqDsz5RPmTzeGGG_4TRc1QU-ROKTiPc"
        ) {
            install(Postgrest)
            install(Storage)
        }
    }
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var saveLoginCheckBox: CheckBox
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var recoverPasswordTextView: TextView
    private lateinit var googleLoginBtn: SignInButton
    private lateinit var welcomeTextView: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_NAME = "LoginPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dang_nhap)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ánh xạ view
        usernameEditText = findViewById(R.id.edittext_username_login)
        passwordEditText = findViewById(R.id.edittext_password_login)
        saveLoginCheckBox = findViewById(R.id.checkSavePass)
        loginButton = findViewById(R.id.btnLogin)
        registerButton = findViewById(R.id.txtcreateAccount)
        recoverPasswordTextView = findViewById(R.id.recoverPassTv)
        googleLoginBtn = findViewById(R.id.googleLoginBtn)
        welcomeTextView = findViewById(R.id.welcome)

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Load dữ liệu đã lưu nếu có
        loadSavedLogin()

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show()
            } else {
                // Nếu checkbox được chọn thì lưu lại thông tin
                if (saveLoginCheckBox.isChecked) {
                    saveLogin(username, password)
                } else {
                    clearSavedLogin()
                }



            }
        }

        registerButton.setOnClickListener {
            startActivity(Intent(applicationContext, dangky::class.java))

        }

        recoverPasswordTextView.setOnClickListener {
            // TODO: Chuyển sang màn hình quên mật khẩu
            Toast.makeText(this, "Chuyển sang quên mật khẩu", Toast.LENGTH_SHORT).show()
        }

        googleLoginBtn.setOnClickListener {
            Toast.makeText(this, "Google Login chưa được tích hợp", Toast.LENGTH_SHORT).show()
        }
    }

    // Hàm lưu login
    private fun saveLogin(username: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putBoolean("isSaved", true)
        editor.apply()
    }

    // Hàm đọc login
    private fun loadSavedLogin() {
        val isSaved = sharedPreferences.getBoolean("isSaved", false)
        if (isSaved) {
            val savedUsername = sharedPreferences.getString("username", "")
            val savedPassword = sharedPreferences.getString("password", "")
            usernameEditText.setText(savedUsername)
            passwordEditText.setText(savedPassword)
            saveLoginCheckBox.isChecked = true
        }
    }

    // Hàm xoá login nếu không cần lưu nữa
    private fun clearSavedLogin() {
        sharedPreferences.edit().clear().apply()
    }
}
