package com.example.kitkat.ui.camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Video
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.kitkat.MainActivity
import com.example.kitkat.MainActivity.Companion.REQUEST_CODE_PERMISSIONS
import com.example.kitkat.MainActivity.Companion.REQUIRED_PERMISSIONS
import com.example.kitkat.R
import com.example.kitkat.databinding.FragmentCameraBinding
import com.google.common.util.concurrent.ListenableFuture
import java.text.SimpleDateFormat
import java.util.Locale

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    //Variable pour la caméra
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider> //Permet de mieux gérer la caméra apperement, Future = asynchrone
    private var isUsingBackCamera = false
    private var isRecording = false
    private var isVideoCaptureInitialized = false

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

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

        if (!isVideoCaptureInitialized){
            val recorder = Recorder.Builder().setQualitySelector(qualitySelector).build()
            videoCapture = VideoCapture.withOutput(recorder)
            isVideoCaptureInitialized = true
        }

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

    @SuppressLint("MissingPermission")
    private fun toggleRecording() {
        val videoCapture = videoCapture ?: return

        if (isRecording) {
            recording?.pause()
            binding.btnRecord.setBackgroundColor(Color.BLACK)
            binding.recordIndicator.visibility = ImageView.INVISIBLE

            isRecording = false
        } else {
            if (recording != null){
                recording?.resume()
                binding.btnRecord.setBackgroundColor(Color.RED)
                binding.recordIndicator.visibility = ImageView.VISIBLE
                isRecording = true
                return
            }

            //Début enregistrement
            val name = "KitKatRecord-" + SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.FRANCE).format(System.currentTimeMillis()) + ".mp4"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            }
            val mediaStoreOutput = MediaStoreOutputOptions.Builder(requireContext().contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                                    .setContentValues(contentValues)
                                    .build()

            recording = videoCapture.output
                .prepareRecording(requireContext(), mediaStoreOutput)
                .apply { withAudioEnabled() }
                .start(ContextCompat.getMainExecutor(requireContext())) { recordEvent ->
                    when (recordEvent) {
                        is VideoRecordEvent.Start -> {
                            Toast.makeText(context, "Enregistrement commencé", Toast.LENGTH_SHORT).show()
                        }
                        is VideoRecordEvent.Resume -> {
                            Toast.makeText(context, "Enregistrement repris", Toast.LENGTH_SHORT).show()
                        }
                        is VideoRecordEvent.Pause -> {
                            Toast.makeText(context, "Enregistrement mis en pause", Toast.LENGTH_SHORT).show()
                        }
                        is VideoRecordEvent.Finalize -> {
                            if (!recordEvent.hasError()) {
                                Toast.makeText(context, "Enregistrement exporté avec succès dans votre galerie", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Erreur : ${recordEvent.error}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

            binding.btnRecord.setBackgroundColor(Color.RED)
            binding.recordIndicator.visibility = ImageView.VISIBLE
            isRecording = true
        }
    }

    private fun exportVideo() {
        if (recording != null){
            recording?.stop()
            recording = null
            isRecording = false
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

        binding.btnBack.setOnClickListener {
            val previousFragmentId = arguments?.getInt("previousFragmentId") ?: R.id.navigation_home
            // je met ?: R.id.navigation_home car navigate prend pas de int?
            findNavController().navigate(previousFragmentId);
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

        binding.btnFinishVideo.setOnClickListener {
            exportVideo()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
