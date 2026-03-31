package com.thrillathon.client.ui.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.mlkit.vision.common.InputImage
import com.thrillathon.client.R
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Suppress("DEPRECATION")
class DashboardFragment : Fragment() {

    private lateinit var previewView: PreviewView
    private lateinit var faceOverlay: FaceOverlayView
    private val viewModel: DashboardViewModel by viewModels()

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture

    private var isProcessing = false
    private var isCaptured = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previewView = view.findViewById(R.id.previewView)
        faceOverlay = view.findViewById(R.id.faceOverlay)

        previewView.scaleType = PreviewView.ScaleType.FILL_CENTER

        cameraExecutor = Executors.newSingleThreadExecutor()

        // 🔥 Auto capture when progress = 100
        faceOverlay.onProgressComplete = {
            Handler(Looper.getMainLooper()).postDelayed({
                capturePhoto()
            }, 400)
        }

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
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
        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            // ✅ Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // ✅ Image Capture (IMPORTANT)
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // ✅ Image Analysis
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->

                val mediaImage = imageProxy.image

                if (mediaImage == null || isProcessing) {
                    imageProxy.close()
                    return@setAnalyzer
                }

                isProcessing = true

                val rotation = imageProxy.imageInfo.rotationDegrees

                val image = InputImage.fromMediaImage(mediaImage, rotation)

                val isFront = true

                viewModel.detectFaceMesh(
                    image,
                    onSuccess = { meshes ->

                        requireActivity().runOnUiThread {
                            faceOverlay.setFaceMesh(
                                meshes,
                                imageProxy.width,
                                imageProxy.height,
                                rotation,
                                isFront
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

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis,
                imageCapture
            )

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // 🔥 Capture Photo when progress = 100
    private fun capturePhoto() {

        if (isCaptured) return
        isCaptured = true

        val file = File(
            requireContext().cacheDir,
            "face_${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    println("✅ Photo captured: ${file.absolutePath}")

                    // 🔁 reset for next capture
                    isCaptured = false
                    faceOverlay.resetProgress()
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    isCaptured = false
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }
}