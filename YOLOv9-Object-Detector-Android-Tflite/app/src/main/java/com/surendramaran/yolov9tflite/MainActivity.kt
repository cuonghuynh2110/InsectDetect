package com.surendramaran.yolov9tflite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.surendramaran.yolov9tflite.Constants.LABELS_PATH
import com.surendramaran.yolov9tflite.Constants.MODEL_PATH
import com.surendramaran.yolov9tflite.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), Detector.DetectorListener {
    private var detectedInsectName: String? = null

    private lateinit var binding: ActivityMainBinding
    private val isFrontCamera = false

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private var detector: Detector? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        //
        // Gắn sự kiện cho bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set item được chọn là Home
        bottomNav.selectedItemId = R.id.nav_detect

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_detect -> {
                    // Đang ở Home, không cần chuyển
                    true
                }
                R.id.nav_home -> {
                    val email = intent.getStringExtra("email")
                    val intent = Intent(applicationContext, Home::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_me -> {
                    val email = intent.getStringExtra("email")
                    val intent = Intent(applicationContext, me::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_chat -> {
                    val email = intent.getStringExtra("email")
                    val intent = Intent(applicationContext, Chatgpt::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
        //

        //map
        val btnChonAnh: Button = findViewById(R.id.chonanh)
        val btnJetsonCam: Button = findViewById(R.id.jetsoncam)
        //end map

        //event
        btnChonAnh.setOnClickListener {
            val intent = Intent(this, ChoosePictureToDetect::class.java)
            startActivity(intent)
        }
        val danhSachConTrung = listOf(
            ConTrung(
                id = 1,
                tenTiengViet = "Bọ sọc dưa",
                tenKhoaHoc = "Acalymma vittatum",
                loai = "Gây hại",
                moTa = "Gây hại chủ yếu trên cây họ bầu bí, ăn lá và truyền bệnh héo vi khuẩn.",
                phanBo = "Phân bố rộng rãi ở Bắc Mỹ, đặc biệt là vùng ôn đới.",
                hinhThai = "Cơ thể màu vàng nhạt với ba sọc đen chạy dọc lưng, dài khoảng 5-7mm.",
                phongTru = "Luân canh cây trồng, sử dụng lưới che, bẫy màu vàng, và thuốc trừ sâu sinh học."

            ),
            ConTrung(
                id = 2,
                tenTiengViet = "Bọ nhảy",
                tenKhoaHoc = "Alticini",
                loai = "Gây hại",
                moTa = "Là nhóm bọ nhỏ có khả năng nhảy xa, gây hại cây non bằng cách đục lá.",
                phanBo = "Phân bố toàn cầu, phổ biến ở khu vực nhiệt đới và cận nhiệt đới.",
                hinhThai = "Kích thước nhỏ (1-3mm), màu sắc đa dạng, chân sau phát triển mạnh để nhảy.",
                phongTru = "Che phủ đất, sử dụng thuốc trừ sâu sinh học, giữ độ ẩm đất để hạn chế hoạt động."
            ),
            ConTrung(
                id = 3,
                tenTiengViet = "Bọ xít bí",
                tenKhoaHoc = "Anasa tristis (Squash Bug)",
                loai = "Gây hại",
                moTa = "Hút nhựa cây bầu bí làm cây héo và suy yếu, có thể gây chết cây.",
                phanBo = "Bắc Mỹ và Trung Mỹ.",
                hinhThai = "Cơ thể dài khoảng 15mm, màu xám nâu, có mùi hôi khi bị đe dọa.",
                phongTru = "Loại bỏ trứng, dùng vải phủ cây, sử dụng thiên địch như ong ký sinh Trichopoda pennipes."
            ),
            ConTrung(
                id = 4,
                tenTiengViet = "Bọ măng tây",
                tenKhoaHoc = "Crioceris asparagi",
                loai = "Gây hại",
                moTa = "Chuyên gây hại cây măng tây, ăn lá và ngọn non.",
                phanBo = "Châu Âu và Bắc Mỹ.",
                hinhThai = "Cơ thể thon dài, màu cam hoặc đỏ với các đốm đen.",
                phongTru = "Thu gom và tiêu diệt bọ bằng tay, dùng neem oil hoặc thuốc trừ sâu sinh học."
            ),
            ConTrung(
                id = 5,
                tenTiengViet = "Bọ bí",
                tenKhoaHoc = "Aulacophora femoralis",
                loai = "Gây hại",
                moTa = "Phá hoại lá cây họ bầu bí, ăn lá làm giảm khả năng quang hợp.",
                phanBo = "Châu Á, đặc biệt phổ biến ở Đông Nam Á.",
                hinhThai = "Cánh cứng màu cam đỏ, cơ thể nhỏ, dài khoảng 6-8mm.",
                phongTru = "Sử dụng bẫy màu, thiên địch hoặc phun thuốc thảo mộc."
            ),
            ConTrung(
                id = 6,
                tenTiengViet = "Bọ tai kẹp",
                tenKhoaHoc = "Dermaptera",
                loai = "Không gây hại",
                moTa = "Sống về đêm, ăn xác côn trùng hoặc chất hữu cơ mục nát.",
                phanBo = "Phân bố toàn cầu.",
                hinhThai = "Cơ thể dẹt, màu nâu, phần đuôi có hai càng như kẹp.",
                phongTru = "Không cần thiết, có thể dùng nếu số lượng quá nhiều bằng cách dọn sạch nơi ẩn nấp."
            ),
            ConTrung(
                id = 7,
                tenTiengViet = "Bọ khoai tây Colorado",
                tenKhoaHoc = "Leptinotarsa decemlineata",
                loai = "Gây hại",
                moTa = "Phá hoại nghiêm trọng cây khoai tây và các cây họ cà khác.",
                phanBo = "Châu Mỹ, châu Âu và châu Á.",
                hinhThai = "Cơ thể hình bầu dục, màu vàng với 10 sọc đen trên cánh.",
                phongTru = "Thu gom trứng và ấu trùng, sử dụng cây bẫy, phun thuốc vi sinh như Bt."
            ),
            ConTrung(
                id = 8,
                tenTiengViet = "Bọ ngựa",
                tenKhoaHoc = "Mantodea",
                loai = "Không gây hại",
                moTa = "Là loài ăn thịt côn trùng khác, giúp kiểm soát sâu bệnh tự nhiên.",
                phanBo = "Phân bố toàn cầu, chủ yếu ở vùng nhiệt đới và cận nhiệt đới.",
                hinhThai = "Thân dài, đầu hình tam giác, chân trước có gai để bắt mồi.",
                phongTru = "Không cần, nên bảo vệ và khuyến khích phát triển."
            ),
            ConTrung(
                id = 9,
                tenTiengViet = "Ốc sên khổng lồ châu Phi",
                tenKhoaHoc = "Achatina fulica",
                loai = "Gây hại",
                moTa = "Gây hại cây trồng, đặc biệt rau màu, là loài xâm lấn mạnh.",
                phanBo = "Châu Phi, lan sang châu Á và châu Mỹ.",
                hinhThai = "Vỏ xoắn lớn, dài tới 20cm, màu nâu sẫm.",
                phongTru = "Thu gom thủ công, sử dụng bẫy, hạn chế độ ẩm để kiểm soát."
            ),
            ConTrung(
                id = 10,
                tenTiengViet = "Bọ sọc ba vạch",
                tenKhoaHoc = "Cerotoma trifurcata",
                loai = "Gây hại",
                moTa = "Phá hoại cây họ đậu, đặc biệt là đậu nành.",
                phanBo = "Chủ yếu ở Bắc Mỹ.",
                hinhThai = "Màu đen với ba sọc vàng cam trên cánh, dài khoảng 6mm.",
                phongTru = "Luân canh cây trồng, dùng lưới hoặc thuốc trừ sâu sinh học."
            )
        )
        btnJetsonCam.setOnClickListener {
            val name = detectedInsectName // lưu lại 1 bản copy bất biến

            if (!name.isNullOrEmpty()) {
                Toast.makeText(this, "Tên loài: $name", Toast.LENGTH_SHORT).show()
                val conTrung = when (name) {
                    "acalymma" -> danhSachConTrung[0]
                    "alticini" -> danhSachConTrung[1]
                    "Squash_Bug" -> danhSachConTrung[2]
                    "asparagus" -> danhSachConTrung[3]
                    "aulacophora" -> danhSachConTrung[4]
                    "dermaptera" -> danhSachConTrung[5]
                    "leptinotarsa" -> danhSachConTrung[6]
                    "mantodea" -> danhSachConTrung[7]
                    "Achatina_fulica" -> danhSachConTrung[8]
                    "Cerotoma_trifurcata" -> danhSachConTrung[9]
                    else -> null
                }

                conTrung?.let {
                    val intent = Intent(this, infor_insect::class.java)
                    intent.putExtra("conTrung", it)
                    startActivity(intent)
                }
            }
        }
        //end event
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraExecutor.execute {
            detector = Detector(baseContext, MODEL_PATH, LABELS_PATH, this) {
                toast(it)
            }
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        bindListeners()
    }

    private fun bindListeners() {
        binding.apply {
            isGpu.setOnCheckedChangeListener { buttonView, isChecked ->
                cameraExecutor.submit {
                    detector?.restart(isGpu = isChecked)
                }
                if (isChecked) {
                    buttonView.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.orange))
                } else {
                    buttonView.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.gray))
                }
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(baseContext)
        cameraProviderFuture.addListener({
            cameraProvider  = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val rotation = binding.viewFinder.display.rotation

        val cameraSelector = CameraSelector
            .Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview =  Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(rotation)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()

        imageAnalyzer?.setAnalyzer(cameraExecutor) { imageProxy ->
            val bitmapBuffer =
                Bitmap.createBitmap(
                    imageProxy.width,
                    imageProxy.height,
                    Bitmap.Config.ARGB_8888
                )
            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
            imageProxy.close()

            val matrix = Matrix().apply {
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

                if (isFrontCamera) {
                    postScale(
                        -1f,
                        1f,
                        imageProxy.width.toFloat(),
                        imageProxy.height.toFloat()
                    )
                }
            }

            val rotatedBitmap = Bitmap.createBitmap(
                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
                matrix, true
            )

            detector?.detect(rotatedBitmap)
        }

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalyzer
            )

            preview?.surfaceProvider = binding.viewFinder.surfaceProvider
        } catch(exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
        if (it[Manifest.permission.CAMERA] == true) { startCamera() }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector?.close()
        cameraExecutor.shutdown()
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()){
            startCamera()
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    companion object {
        private const val TAG = "Camera"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf (
            Manifest.permission.CAMERA
        ).toTypedArray()
    }

    override fun onEmptyDetect() {
        runOnUiThread {
            binding.overlay.clear()
            detectedInsectName = null
            binding.jetsoncam.text = "..."
        }
    }

    //    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
//        runOnUiThread {
//            binding.inferenceTime.text = "${inferenceTime}ms"
//            binding.overlay.apply {
//                setResults(boundingBoxes)
//                invalidate()
//            }
//        }
//    }
    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        runOnUiThread {
            binding.inferenceTime.text = "${inferenceTime}ms"
            binding.overlay.apply {
                setResults(boundingBoxes)
                invalidate()
            }

            // Nếu có ít nhất 1 đối tượng được nhận diện
            if (boundingBoxes.isNotEmpty()) {
                detectedInsectName = boundingBoxes[0].clsName // lưu tên lớp
                binding.jetsoncam.text = "Xem: ${detectedInsectName}"
            } else {
                detectedInsectName = null
                binding.jetsoncam.text = "..."
            }
        }
    }


    private fun toast(message: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(baseContext, message, Toast.LENGTH_LONG).show()
        }
    }
}