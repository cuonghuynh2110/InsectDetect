package com.surendramaran.yolov9tflite

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class infor_insect : AppCompatActivity() {
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
