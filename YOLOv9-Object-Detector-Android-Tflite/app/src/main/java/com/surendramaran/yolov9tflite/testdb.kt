package com.surendramaran.yolov9tflite

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class testdb : AppCompatActivity() {

    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://ydavhbhglxgzeiggzpgg.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlkYXZoYmhnbHhnemVpZ2d6cGdnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM4MjQ4NjEsImV4cCI6MjA1OTQwMDg2MX0.7az8QQl04TZigqDsz5RPmTzeGGG_4TRc1QU-ROKTiPc"
        ) {
            install(Postgrest)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_testdb)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textView = findViewById<TextView>(R.id.txtview)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val newData = Instrument(id = 0, name = "Cường") // id sẽ được auto-gen nếu Supabase thiết lập auto-increment

                supabase.from("test").insert(newData)

                withContext(Dispatchers.Main) {
                    textView.text = "Đã chèn dữ liệu thành công"
                    Toast.makeText(applicationContext, "Insert thành công", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("SupabaseError", "Lỗi khi chèn dữ liệu", e)
                withContext(Dispatchers.Main) {
                    textView.text = "Lỗi khi chèn: ${e.message}"
                    Toast.makeText(applicationContext, "Insert thất bại", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Serializable
data class Instrument(
    val id: Int, // nếu Supabase tự tăng thì bạn vẫn cần để ở đây nhưng giá trị không quan trọng
    val name: String
)
