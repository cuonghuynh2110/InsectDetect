package com.surendramaran.yolov9tflite

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

class infor_insect : AppCompatActivity() {
    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://ydavhbhglxgzeiggzpgg.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlkYXZoYmhnbHhnemVpZ2d6cGdnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM4MjQ4NjEsImV4cCI6MjA1OTQwMDg2MX0.7az8QQl04TZigqDsz5RPmTzeGGG_4TRc1QU-ROKTiPc"
        ) {
            install(Postgrest)
            install(Storage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_infor_insect)

        // Lấy đối tượng côn trùng truyền vào
        val conTrung = intent.getSerializableExtra("conTrung") as? ConTrung

        // Ánh xạ các view
        val imgAnh = findViewById<ImageView>(R.id.imgAnh)
        val tvTen = findViewById<TextView>(R.id.tvTen)
        val tvTenKH = findViewById<TextView>(R.id.tvTenKH)

        val tvTaptinh = findViewById<TextView>(R.id.tvTaptinh)
        val tvMota = findViewById<TextView>(R.id.tvMota)
        val tvPhanbo = findViewById<TextView>(R.id.tvPhanbo)
        val tvHinhtai = findViewById<TextView>(R.id.tvHinhtai)
        val tvPhongtru = findViewById<TextView>(R.id.tvPhongtru)
        val btnLuu = findViewById<Button>(R.id.btnLuuBaiViet)


        // Gán dữ liệu nếu có
        conTrung?.let {
            Toast.makeText(this, it.tenTiengViet, Toast.LENGTH_SHORT).show()
            tvTen.text = it.tenTiengViet
            tvTenKH.text = "Tên khoa học: ${it.tenKhoaHoc}"
            tvTaptinh.text = it.moTa
            tvMota.text = it.moTa
            tvPhanbo.text = it.phanBo
            tvHinhtai.text = it.hinhThai
            tvPhongtru.text = it.phongTru
            if(it.id == 1){
                imgAnh.setImageResource(R.drawable.acalymma)
            }
            else if(it.id == 2){
                imgAnh.setImageResource(R.drawable.alticini)
            }
            else if(it.id == 3) {
                imgAnh.setImageResource(R.drawable.squashbug)
            }
            else if(it.id == 4) {
                imgAnh.setImageResource(R.drawable.asparagus)
            }
            else if(it.id == 5) {
                imgAnh.setImageResource(R.drawable.aulacophora)}
            else if(it.id == 6) {
                imgAnh.setImageResource(R.drawable.dermaptera)
            }
            else if(it.id == 7) {
                imgAnh.setImageResource(R.drawable.leptinotarsa)
            }else if(it.id == 8) {
                imgAnh.setImageResource(R.drawable.mantodea)
            }else if(it.id == 9) {
                imgAnh.setImageResource(R.drawable.achatinafulica)
            }else if(it.id == 10) {
                imgAnh.setImageResource(R.drawable.cerotomatrifurcata)
            }
        } ?: Toast.makeText(this, "Không có dữ liệu côn trùng", Toast.LENGTH_SHORT).show()
    }
}