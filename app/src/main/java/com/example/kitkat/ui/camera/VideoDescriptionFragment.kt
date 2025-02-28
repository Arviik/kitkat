package com.example.kitkat.ui.camera

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kitkat.R
import com.example.kitkat.api.models.dataclass.VideoDTO
import com.example.kitkat.network.ApiClient
import com.example.kitkat.network.services.AzureUploadService
import com.example.kitkat.network.services.VideoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VideoDescriptionFragment : Fragment() {

    private lateinit var thumbnailView: ImageView
    private lateinit var descriptionInput: EditText
    private lateinit var uploadButton: Button
    private var videoUri: Uri? = null
    private var thumbnailUrl: String? = null
    private var videoUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_video_description, container, false)

        thumbnailView = view.findViewById(R.id.thumbnailView)
        descriptionInput = view.findViewById(R.id.descriptionInput)
        uploadButton = view.findViewById(R.id.uploadButton)

        val uriString = arguments?.getString("videoUri")
        if (uriString.isNullOrEmpty()) {
            Log.e("VideoDescriptionFragment", "L'URI de la vidéo est null ou vide")
            Toast.makeText(requireContext(), "Erreur : URI vidéo introuvable", Toast.LENGTH_SHORT).show()
        } else {
            videoUri = Uri.parse(uriString)
            Log.d("VideoDescriptionFragment", "URI vidéo reçue : $videoUri")
            loadVideoThumbnail(videoUri!!)
        }

        uploadButton.setOnClickListener { uploadVideoToAzure() }

        return view
    }

    /**
     * Récupérer la première frame de la vidéo et l'afficher comme miniature
     */
    private fun loadVideoThumbnail(uri: Uri) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(requireContext(), uri)
        val bitmap = retriever.getFrameAtTime(1000000) // Récupère la frame à 1 seconde
        retriever.release()

        if (bitmap != null) {
            thumbnailView.setImageBitmap(bitmap)
            uploadBitmapToAzure(bitmap) // Upload la miniature
        } else {
            Log.e("VideoDescriptionFragment", "Erreur lors de la génération de la miniature")
        }
    }

    /**
     * Convertir un URI de vidéo en un fichier temporaire pour l'upload
     */
    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload_video", ".mp4", context.cacheDir)
        tempFile.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }
        return tempFile
    }

    /**
     * Upload la vidéo sur Azure
     */
    private fun uploadVideoToAzure() {
        if (videoUri == null) {
            Toast.makeText(requireContext(), "Erreur : Aucune vidéo à uploader", Toast.LENGTH_SHORT).show()
            return
        }

        val file = getFileFromUri(requireContext(), videoUri!!) ?: run {
            Toast.makeText(requireContext(), "Erreur : Impossible de lire la vidéo", Toast.LENGTH_SHORT).show()
            return
        }

        val requestFile = file.asRequestBody("video/mp4".toMediaTypeOrNull())

        val progressDialog = ProgressDialog(requireContext()).apply {
            setMessage("Upload en cours vers Azure...")
            setCancelable(false)
            show()
        }

        val azureService = ApiClient.retrofit.create(AzureUploadService::class.java)

        // 🔹 URL SAS pour l’upload de la vidéo
        val azureSasUrl = "https://kitkatstorage.blob.core.windows.net/videos/${file.name}?sp=racwdli&st=2025-02-23T23:52:01Z&se=2025-04-05T06:52:01Z&sv=2022-11-02&sr=c&sig=IcuKyMaAzjJEDQSCqUvbhj2cUaBo3B9LtM6dtkWnRHE%3D"

        azureService.uploadVideoToAzure(azureSasUrl, "BlockBlob", requestFile)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    progressDialog.dismiss()
                    if (response.isSuccessful) {
                        Log.d("AzureUpload", "Vidéo uploadée avec succès sur Azure")
                        videoUrl = azureSasUrl
                        postVideoToAPI() // Une fois la vidéo uploadée, on enregistre en base
                    } else {
                        Log.e("AzureUpload", "Échec de l'upload Azure")
                        Toast.makeText(requireContext(), "Échec de l'upload Azure", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    progressDialog.dismiss()
                    Log.e("AzureUpload", "Erreur : ${t.message}")
                    Toast.makeText(requireContext(), "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    /**
     * Upload la miniature sur Azure
     */
    private fun uploadBitmapToAzure(bitmap: Bitmap) {
        val containerUrl = "https://kitkatstorage.blob.core.windows.net/miniatures"
        val filename = "thumbnail_${System.currentTimeMillis()}.jpg"
        val sasToken = "sp=racwdli&st=2025-02-24T23:27:39Z&se=2025-04-10T06:27:39Z&sv=2022-11-02&sr=c&sig=mJDJd3BIuFxuMbBHqjkof9OcSUtwkEPRdWWLpA%2FHACY%3D"
        val uploadUrl = "$containerUrl/$filename?$sasToken"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val byteArray = stream.toByteArray()
                stream.close()

                val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val request = okhttp3.Request.Builder()
                    .url(uploadUrl)
                    .put(requestBody)
                    .addHeader("x-ms-blob-type", "BlockBlob")
                    .build()

                val response = okhttp3.OkHttpClient().newCall(request).execute()

                if (response.isSuccessful) {
                    thumbnailUrl = uploadUrl
                    Log.d("AzureUpload", "Miniature uploadée : $uploadUrl")
                } else {
                    Log.e("AzureUpload", "Erreur d'upload miniature : ${response.code} - ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("AzureUpload", "Erreur miniature: ${e.message}", e)
            }
        }
    }

    /**
     * Enregistre la vidéo dans l'API après upload Azure
     */
    private fun postVideoToAPI() {
        val videoTitle = descriptionInput.text.toString().trim().ifEmpty { "Vidéo sans titre" }
        val videoDuration = 120
        val authorId = 1
        val createdAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())

        val videoDTO = VideoDTO(
            title = videoTitle,
            duration = videoDuration,
            authorId = authorId,
            videoUrl = videoUrl!!,
            thumbnailUrl =  thumbnailUrl!!,
            createdAt = createdAt,
            isPublic = true
        )
        ApiClient.retrofit.create(VideoService::class.java).postVideo(videoDTO)
            .enqueue(object : Callback<VideoDTO> {
                override fun onResponse(call: Call<VideoDTO>, response: Response<VideoDTO>) {
                    Toast.makeText(requireContext(), "Vidéo publiée !", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.navigation_profile)
                }
                override fun onFailure(call: Call<VideoDTO>, t: Throwable) {
                    Log.e("APIUpload", "Erreur réseau: ${t.message}", t)
                }
            })
    }
}
