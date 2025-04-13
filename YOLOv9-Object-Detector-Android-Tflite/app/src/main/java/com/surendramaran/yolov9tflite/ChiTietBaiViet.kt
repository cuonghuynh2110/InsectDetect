package com.surendramaran.yolov9tflite

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ChiTietBaiViet : AppCompatActivity() {

    private lateinit var imgAnh: ImageView
    private lateinit var tvTieuDe: TextView
    private lateinit var tvNoiDung: TextView
    private lateinit var tvNgayDang: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chi_tiet_bai_viet)


        // Khai báo các view
        val imgAnh = findViewById<ImageView>(R.id.imgAnh)
        val tvTieuDe = findViewById<TextView>(R.id.tvTieuDe)
        val tvNoiDung = findViewById<TextView>(R.id.tvNoiDung)
        val tvNgayDang = findViewById<TextView>(R.id.tvNgayDang)

        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val imageUrl = intent.getStringExtra("imageUrl")
        val date = intent.getStringExtra("date")

        tvTieuDe.text = title
        tvNoiDung.text = content
        tvNgayDang.text = date

        Glide.with(this)
            .load(imageUrl)
            .into(imgAnh)
    }
}