package com.surendramaran.yolov9tflite

import java.io.Serializable

data class ConTrung(
    val id: Int,
    val tenTiengViet: String,
    val tenKhoaHoc: String,
    val loai: String,
    val moTa: String,
    val phanBo: String,
    val hinhThai: String,
    val phongTru: String
) : Serializable
