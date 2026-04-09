package com.thrillathon.client.ui.organiser

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.view.View
import android.widget.*
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.vision.common.InputImage
import com.thrillathon.client.R
import com.thrillathon.client.ui.dashboard.DashboardViewModel
import com.thrillathon.client.ui.dashboard.FaceOverlayView
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FaceCheckInActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var faceOverlay: FaceOverlayView
    private lateinit var tvInstruction: TextView
    private lateinit var uploadingOverlay: FrameLayout
    private lateinit var resultCard: LinearLayout
    private lateinit var tvResultMessage: TextView
    private lateinit var tvResponseRaw: TextView
    private lateinit var tvColorBadge: TextView
    private lateinit var btnScanAgain: Button
    private lateinit var btnBack: ImageButton

    private lateinit var viewModel: DashboardViewModel
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService

    private val client = OkHttpClient()
    private val BASE_URL = "https://api-ashy-three-56.vercel.app"

    private var isProcessing = false
    private var isCaptured = false
    private var eventId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_checkin)

        eventId = intent.getStringExtra("event_id") ?: ""

        previewView = findViewById(R.id.previewView)
        faceOverlay = findViewById(R.id.faceOverlay)
        tvInstruction = findViewById(R.id.tvInstruction)
        uploadingOverlay = findViewById(R.id.uploadingOverlay)
        resultCard = findViewById(R.id.resultCard)
        tvResultMessage = findViewById(R.id.tvResultMessage)
        tvResponseRaw = findViewById(R.id.tvResponseRaw)
        tvColorBadge = findViewById(R.id.tvColorBadge)
        btnScanAgain = findViewById(R.id.btnScanAgain)
        btnBack = findViewById(R.id.btnBack)

        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        cameraExecutor = Executors.newSingleThreadExecutor()

        previewView.scaleType = PreviewView.ScaleType.FILL_CENTER

        faceOverlay.onProgressComplete = {
            Handler(Looper.getMainLooper()).postDelayed({ captureAndVerify() }, 400)
        }

        btnBack.setOnClickListener { finish() }

        btnScanAgain.setOnClickListener {
            resultCard.visibility = View.GONE
            tvInstruction.visibility = View.VISIBLE
            isCaptured = false
            faceOverlay.resetProgress()
        }

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage == null || isProcessing || isCaptured) {
                    imageProxy.close()
                    return@setAnalyzer
                }
                isProcessing = true

                val image = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )

                viewModel.detectFaceMesh(
                    image,
                    onSuccess = { meshes ->
                        runOnUiThread {
                            faceOverlay.setFaceMesh(
                                meshes,
                                imageProxy.width,
                                imageProxy.height,
                                imageProxy.imageInfo.rotationDegrees,
                                isFrontCamera = true
                            )
                        }
                        isProcessing = false
                        imageProxy.close()
                    },
                    onFailure = {
                        isProcessing = false
                        imageProxy.close()
                    }
                )
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                preview,
                imageAnalysis,
                imageCapture
            )

        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureAndVerify() {
        if (isCaptured) return
        isCaptured = true

        val file = File(cacheDir, "checkin_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    showUploading(true)
                    uploadToApi(file, eventId)
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    isCaptured = false
                    faceOverlay.resetProgress()
                }
            }
        )
    }

    private fun uploadToApi(imageFile: File, eventId: String) {
        val prefs = getSharedPreferences("whoopie_prefs", 0)
        val token = prefs.getString("auth_token", "") ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("eventId", eventId)
                    .addFormDataPart(
                        "image",
                        imageFile.name,
                        imageFile.asRequestBody("image/jpeg".toMediaType())
                    )
                    .build()

                val request = Request.Builder()
                    .url("$BASE_URL/api/face-verify/final")
                    .addHeader("Authorization", "Bearer $token")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: "{}"
                val json = JSONObject(body)

                val color = json.optString("color", "black")
                val message = json.optString("message", "Verification failed")

                withContext(Dispatchers.Main) {
                    showUploading(false)
                    showResult(color, message, body)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showUploading(false)
                    showResult("black", "Network error: ${e.message}", e.toString())
                }
            } finally {
                imageFile.delete()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showResult(color: String, message: String, rawBody: String = "") {
        tvInstruction.visibility = View.GONE
        resultCard.visibility = View.VISIBLE
        tvResultMessage.text = message

        val accentColor = when (color.lowercase()) {
            "green" -> Color.parseColor("#00C853")
            "red"   -> Color.parseColor("#FF5252")
            else    -> Color.parseColor("#555555")
        }
        val bgColor = when (color.lowercase()) {
            "green" -> Color.parseColor("#1A4A2E")
            "red"   -> Color.parseColor("#4A1A1A")
            else    -> Color.parseColor("#1C1C1C")
        }

        resultCard.setBackgroundColor(bgColor)
        tvResultMessage.setTextColor(accentColor)

        // Color badge
        tvColorBadge.text = "● ${color.uppercase()}"
        tvColorBadge.setTextColor(accentColor)

        // Raw API response
        val formatted = try {
            JSONObject(rawBody).toString(2)
        } catch (e: Exception) {
            rawBody
        }
        tvResponseRaw.text = formatted
        tvResponseRaw.visibility = if (formatted.isNotBlank()) View.VISIBLE else View.GONE
    }

    private fun showUploading(show: Boolean) {
        uploadingOverlay.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
