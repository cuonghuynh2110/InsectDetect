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

class testsupabase : AppCompatActivity() {

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
        setContentView(R.layout.activity_testsupabase)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textView = findViewById<TextView>(R.id.txtview)

//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                // Gửi một truy vấn đơn giản để test kết nối (không cần limit)
//                supabase.from("test").select()
//
//                withContext(Dispatchers.Main) {
//                    textView?.text = "Kết nối Supabase thành công"
//                    Toast.makeText(applicationContext, "Kết nối thành công", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                Log.e("SupabaseError", "Lỗi kết nối Supabase", e)
//                withContext(Dispatchers.Main) {
//                    textView?.text = "Lỗi kết nối: ${e.message}"
//                    Toast.makeText(applicationContext, "Kết nối thất bại", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
    }
}
