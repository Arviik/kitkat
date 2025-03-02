package com.example.kitkat

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.kitkat.app_utils.NotificationScheduler
import com.example.kitkat.app_utils.notification.NotificationHelper
import com.example.kitkat.app_utils.isTokenExpired
import com.example.kitkat.databinding.ActivityMainBinding
import com.example.kitkat.repositories.UserRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var previousFragmentId: Int? = null
    private lateinit var sharedPref: SharedPreferences
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userRepository = UserRepository(this)

        //Notifaction
        NotificationHelper.createNotificationChannel(this)
        val token=userRepository.getToken()
        Log.d("MainActivity", "Token récupéré: $token")
        if (userRepository.getToken() != null && !isTokenExpired(userRepository.getToken()!!)) {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            binding.navView.setupWithNavController(navController)

            // les _ c pour evité de mettre des parametre que on va pas utilisé exemple ici ca prend un listener en parmaetre composé de 3 argument mais moi j'ai besoin que de destination
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.navigation_camera -> {
                        binding.navView.visibility = View.GONE
                    }
                    else -> {
                        binding.navView.visibility = View.VISIBLE
                        previousFragmentId = destination.id
                    }
                }
            }
            fun navigateToCameraFragment() {
                val bundle = Bundle().apply {
                    putInt("previousFragmentId", previousFragmentId ?: R.id.navigation_home)
                }
                navController.navigate(R.id.navigation_camera, bundle)
            }
            binding.navView.setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.navigation_camera -> {
                        navigateToCameraFragment() // Appeler la méthode centralisée
                        true
                    }
                    else -> {
                        navController.navigate(menuItem.itemId) // Navigation par défaut
                        true
                    }
                }
            }
        } else {
            setContentView(R.layout.activity_login)
            findNavController(R.id.nav_host_fragment_activity_login)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Toast.makeText(this,
                    "Permissions has been granted.",
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}