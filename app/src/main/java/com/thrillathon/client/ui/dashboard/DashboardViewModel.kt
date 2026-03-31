package com.thrillathon.client.ui.dashboard

import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.facemesh.*

class DashboardViewModel : ViewModel() {

    private val detector: FaceMeshDetector


    init {
        val options = FaceMeshDetectorOptions.Builder()
            .setUseCase(FaceMeshDetectorOptions.FACE_MESH)
            .build()

        detector = FaceMeshDetection.getClient(options)
    }

    fun detectFaceMesh(
        image: InputImage,
        onSuccess: (List<FaceMesh>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        detector.process(image)
            .addOnSuccessListener { meshes ->
                onSuccess(meshes)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    override fun onCleared() {
        super.onCleared()
        detector.close()
    }
}