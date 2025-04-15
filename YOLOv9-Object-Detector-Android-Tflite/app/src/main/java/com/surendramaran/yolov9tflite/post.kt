package com.surendramaran.yolov9tflite

data class post(
    val id: String? = null,
    val title: String,
    val content: String,
    val imageUrl: String,
    val date: String
)