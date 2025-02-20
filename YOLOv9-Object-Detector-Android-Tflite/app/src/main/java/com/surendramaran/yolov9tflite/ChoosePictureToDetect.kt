package com.surendramaran.yolov9tflite

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.IOException

class ChoosePictureToDetect : AppCompatActivity(), Detector.DetectorListener {

    private lateinit var btnSelectImage: Button
    private lateinit var btnDetect: Button
    private lateinit var imageView: ImageView
    private var selectedBitmap: Bitmap? = null
    private lateinit var detector: Detector
    private lateinit var overlayView:OverlayView

            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_picture_to_detect)

        // Ánh xạ các nút và ImageView
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnDetect = findViewById(R.id.btnDetect)
        imageView = findViewById(R.id.imageView)
        overlayView = findViewById(R.id.overlayView)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        try {
            // Khởi tạo đối tượng Detector
            detector = Detector(
                this,
                "v9.tflite",  // Đường dẫn tệp mô hình
                "labels.txt",    // Đường dẫn tệp nhãn
                this             // Listener để nhận kết quả
            ) { message ->
                runOnUiThread {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Lỗi khởi tạo Detector: ${e.message}", Toast.LENGTH_LONG).show()
        }

        // Thêm sự kiện onClick cho nút Chọn Ảnh
        btnSelectImage.setOnClickListener {
            selectImage()
        }

        // Thêm sự kiện onClick cho nút Nhận Diện
        // Ví dụ cách sử dụng resizeBitmap trước khi nhận diện ảnh
        btnDetect.setOnClickListener {
            if (selectedBitmap != null) {
                try {
                    // Thay đổi kích thước ảnh xuống 416 x 416 (hoặc kích thước khác phù hợp với mô hình của bạn)
                    val resizedBitmap = resizeBitmap(selectedBitmap!!, 416, 416)

                    // In thông tin của bitmap đã thay đổi kích thước để kiểm tra
//                    Toast.makeText(this, "Kích thước ảnh sau khi thay đổi: ${resizedBitmap.width} x ${resizedBitmap.height}", Toast.LENGTH_SHORT).show()

                    detector.detect(resizedBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Lỗi khi nhận diện ảnh: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh trước", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun resizeBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    private fun selectImage() {
        overlayView.clear()  // Xóa bounding box cũ
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                imageView.setImageBitmap(selectedBitmap)
                // Log thông tin về ảnh đã chọn
                Toast.makeText(this, "Ảnh đã chọn: $imageUri", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Không thể chọn ảnh: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onEmptyDetect() {
        runOnUiThread {
            Toast.makeText(this, "Không phát hiện được đối tượng nào", Toast.LENGTH_SHORT).show()
        }
    }

//    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
//        runOnUiThread {
//            if (boundingBoxes.isNotEmpty()) {
//                val detectedClasses = boundingBoxes.joinToString(", ") { it.clsName }
//                Toast.makeText(this, "Phát hiện: $detectedClasses", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Không phát hiện được đối tượng nào", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
    runOnUiThread {
        if (boundingBoxes.isNotEmpty()) {
            val detectedClasses = boundingBoxes.joinToString(", ") { it.clsName }
            Toast.makeText(this, "Phát hiện: $detectedClasses", Toast.LENGTH_SHORT).show()
            // Đặt kết quả vào overlayView và hiển thị bounding boxes
            overlayView.setResults(boundingBoxes)
        } else {
            Toast.makeText(this, "Không phát hiện được đối tượng nào", Toast.LENGTH_SHORT).show()
        }
    }
}




    override fun onDestroy() {
        super.onDestroy()
        try {
            detector.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Lỗi khi đóng Detector: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 1
    }

    // Phương thức vẽ bounding box lên bitmap
//    private fun drawBoundingBoxes(bitmap: Bitmap, boundingBoxes: List<BoundingBox>): Bitmap {
//        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//        val canvas = Canvas(mutableBitmap)
//        val paint = Paint().apply {
//            color = Color.RED
//            style = Paint.Style.STROKE
//            strokeWidth = 4f
//        }
//
//        for (box in boundingBoxes) {
//            canvas.drawRect(box.x1, box.y1, box.x2, box.y2, paint)
//        }
//
//        return mutableBitmap
//    }
}
