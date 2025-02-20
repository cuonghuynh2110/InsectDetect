package com.surendramaran.yolov9tflite

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.PixelCopy
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Timer
import java.util.TimerTask

class JetSonTrapCamera : AppCompatActivity(), MediaPlayer.OnPreparedListener, SurfaceHolder.Callback, Detector.DetectorListener {
    private lateinit var detector: Detector
    private lateinit var surfaceView: SurfaceView
    private lateinit var overlayView: OverlayView
    private var frameCaptureTimer: Timer? = null

    companion object {
        const val USERNAME = "admin"
        const val PASSWORD = "camera"
        const val RTSP_URL = "rtsp://192.168.2.135:8080/h264_opus.sdp"
    }

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var surfaceHolder: SurfaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jet_son_trap_camera)

        surfaceView = findViewById(R.id.surfaceView)
        overlayView = findViewById(R.id.overlay)

        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)

        detector = Detector(
            this, "v9.tflite", "labels.txt", this
        ) { message ->
            runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDisplay(surfaceHolder)
        val headers = getRtspHeaders()
        try {
            mediaPlayer.setDataSource(applicationContext, Uri.parse(RTSP_URL), headers)
            mediaPlayer.setOnPreparedListener(this)
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPrepared(mp: MediaPlayer) {
        mediaPlayer.start()
        startFrameCapture()
    }

    private fun startFrameCapture() {
        frameCaptureTimer = Timer()
        frameCaptureTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                captureFrameFromSurfaceView()
            }
        }, 0, 500)
    }

    private fun captureFrameFromSurfaceView() {
        val bitmap = Bitmap.createBitmap(surfaceView.width, surfaceView.height, Bitmap.Config.ARGB_8888)
        PixelCopy.request(surfaceView, bitmap, { result ->
            if (result == PixelCopy.SUCCESS) {
                detector.detect(bitmap)
            }
        }, Handler(Looper.getMainLooper()))
    }

    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        runOnUiThread {
            overlayView.setResults(boundingBoxes)
            overlayView.invalidate()

            if (boundingBoxes.isNotEmpty()) {
                val detectedClasses = boundingBoxes.joinToString(", ") { it.clsName }
                Toast.makeText(this, "Phát hiện: $detectedClasses", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEmptyDetect() {
        runOnUiThread {
            overlayView.clear()
            Toast.makeText(this, "Không phát hiện đối tượng nào", Toast.LENGTH_SHORT).show()
        }
    }


    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) { mediaPlayer.release() }

    override fun onDestroy() {
        super.onDestroy()
        frameCaptureTimer?.cancel()
        mediaPlayer.release()
    }

    private fun getRtspHeaders(): Map<String, String> {
        val headers = HashMap<String, String>()
        headers["Authorization"] = "Basic " + Base64.encodeToString("$USERNAME:$PASSWORD".toByteArray(), Base64.NO_WRAP)
        return headers
    }
}
