package com.example.kitkat.ui.camera

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.example.kitkat.MainActivity
import com.example.kitkat.MainActivity.Companion.REQUEST_CODE_PERMISSIONS
import com.example.kitkat.MainActivity.Companion.REQUIRED_PERMISSIONS
import com.example.kitkat.databinding.FragmentCameraBinding
import com.google.common.util.concurrent.ListenableFuture

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    //Variable pour la caméra
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider> //Permet de mieux gérer la caméra apperement, Future = asynchrone
    private var isUsingBackCamera = false
    private var isRecording = false

    val qualitySelector = QualitySelector.fromOrderedList(
        listOf(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD),
        FallbackStrategy.lowerQualityOrHigherThan(Quality.SD))


    fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()
        val cameraSelector: CameraSelector = if (isUsingBackCamera) {
            CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
        } else {
            CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        }

        preview.setSurfaceProvider(binding.previewView.surfaceProvider)

        val recorder = Recorder.Builder().setQualitySelector(qualitySelector).build()
        val videoCapture = VideoCapture.withOutput(recorder)

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview, videoCapture)
    }

    private fun switchCamera() {
        isUsingBackCamera = !isUsingBackCamera

        cameraProviderFuture = ProcessCameraProvider.getInstance(activity as MainActivity)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            if (_binding != null) {
                cameraProvider.unbindAll()
                bindPreview(cameraProvider)
            }
        }, ContextCompat.getMainExecutor(activity as MainActivity))
    }

    private fun toggleRecording() {
        isRecording = !isRecording
        if (isRecording) {
            binding.btnRecord.setBackgroundColor(Color.RED)
            binding.recordIndicator.visibility = ImageView.VISIBLE
        } else {

            binding.btnRecord.setBackgroundColor(Color.BLACK)
            binding.recordIndicator.visibility = ImageView.INVISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        val cameraViewModel =
            ViewModelProvider(this).get(CameraViewModel::class.java)

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (!(activity as MainActivity).allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                (activity as MainActivity),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }


        cameraProviderFuture = ProcessCameraProvider.getInstance(activity as MainActivity)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get();
            if (_binding != null) {
                bindPreview(cameraProvider)
            }
        }, ContextCompat.getMainExecutor(activity as MainActivity))

        binding.btnSwitchCamera.setOnClickListener {
            switchCamera()
        }

        binding.btnRecord.setOnClickListener {
            toggleRecording()
        }

        binding.btnRecord.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    toggleRecording()
                }
                MotionEvent.ACTION_UP -> {
                    toggleRecording()
                }
            }
            true
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
