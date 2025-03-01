package com.example.kitkat.ui.camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kitkat.R
import com.example.kitkat.databinding.FragmentCameraBinding
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private var isUsingBackCamera = true
    private var isRecording = false
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var outputFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val view = binding.root

        setupCamera()

        // Toggle caméra (avant/arrière)
        binding.btnSwitchCamera.setOnClickListener { switchCamera() }

        // Appui long pour enregistrer, relâcher pour arrêter
        binding.btnRecord.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> startRecording()
                MotionEvent.ACTION_UP -> stopRecording()
            }
            true
        }

        return view
    }

    private fun setupCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            val cameraSelector = if (isUsingBackCamera) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

            preview.setSurfaceProvider(binding.previewView.surfaceProvider)

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()

            videoCapture = VideoCapture.withOutput(recorder)

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun switchCamera() {
        isUsingBackCamera = !isUsingBackCamera
        setupCamera()
    }

    @SuppressLint("MissingPermission")
    private fun startRecording() {
        val videoCapture = videoCapture ?: return

        val name = "KitKat_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(System.currentTimeMillis()) + ".mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        }

        val outputOptions = MediaStoreOutputOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).setContentValues(contentValues).build()

        recording = videoCapture.output.prepareRecording(requireContext(), outputOptions)
            .apply { withAudioEnabled() }
            .start(ContextCompat.getMainExecutor(requireContext())) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.btnRecord.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.recordIndicator.visibility = View.VISIBLE
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val savedUri = recordEvent.outputResults.outputUri
                            navigateToVideoPreview(savedUri)
                        }
                        else {
                            Toast.makeText(context, "Erreur : ${recordEvent.error}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        isRecording = true
    }

    private fun stopRecording() {
        recording?.stop()
        recording = null
        isRecording = false
        binding.btnRecord.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.recordIndicator.visibility = View.INVISIBLE
    }

    private fun navigateToVideoPreview(videoUri: Uri) {
        val bundle = Bundle().apply {
            putString("videoUri", videoUri.toString()) // Convertir Uri en String
        }
        findNavController().navigate(R.id.action_camera_to_videoPreview, bundle)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
