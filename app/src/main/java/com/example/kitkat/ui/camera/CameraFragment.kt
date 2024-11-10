package com.example.kitkat.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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

    fun bindPreview(cameraProvider: ProcessCameraProvider){
        var preview: Preview = Preview.Builder().build();
        var cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        preview.setSurfaceProvider(binding.previewView.surfaceProvider)

        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
    }

    private fun takePhoto() {}

    private fun captureVideo() {}

    private fun startCamera() {

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

        if ((activity as MainActivity).allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions((activity as MainActivity), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }


        // Set up the listeners for take photo and video capture buttons

        cameraProviderFuture = ProcessCameraProvider.getInstance(activity as MainActivity)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get();
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(activity as MainActivity))
        /*val textView: TextView = binding.textCamera
        cameraViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
