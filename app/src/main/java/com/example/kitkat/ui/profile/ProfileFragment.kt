package com.example.kitkat.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.kitkat.R
import com.example.kitkat.network.dto.UserWithoutPasswordDTO
import androidx.navigation.fragment.findNavController
import com.example.kitkat.app_utils.SHARED_PREF_KEY
import com.example.kitkat.databinding.FragmentProfileBinding
import com.example.kitkat.network.ApiClient
import com.example.kitkat.network.dto.VideoDTO
import com.example.kitkat.network.dto.VideoWithAuthor
import com.example.kitkat.network.services.UserService
import com.example.kitkat.network.services.VideoService
import com.example.kitkat.repositories.UserRepository
import com.example.kitkat.ui.profile.video.ViewPagerRecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private val PICK_IMAGE_REQUEST = 1001
    private var imageUri: Uri? = null
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: ViewPagerRecyclerAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private var user: UserWithoutPasswordDTO? = null
    private var isOwnProfile: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)

        arguments?.let {
            user = it.getSerializable("user") as? UserWithoutPasswordDTO
            isOwnProfile = user == null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.plusIcon.setOnClickListener {
            openGallery()
        }
        val navBar = requireActivity().findViewById<View>(R.id.nav_view)

        if (user != null) {
            binding.backButton.visibility = View.VISIBLE
            navBar?.visibility = View.GONE
            binding.backButton.setOnClickListener {
                findNavController().popBackStack()
            }
        } else {
            binding.backButton.visibility = View.GONE
            navBar?.visibility = View.VISIBLE
        }

        setupUserProfile(user)

        return binding.root
    }

    private fun setupUserProfile(user: UserWithoutPasswordDTO?) {
        if (user == null) {
            loadCurrentUserProfile()
        } else {
            loadUserProfile(user)
        }
    }

    private fun loadCurrentUserProfile() {
        val token = sharedPreferences.getString("AUTH_TOKEN", null)

        if (token == null) {
            Log.e("ProfileFragment", "Token non trouvé, l'utilisateur doit se reconnecter")
            Toast.makeText(requireContext(), "Erreur d'authentification", Toast.LENGTH_SHORT).show()
            return
        }

        val userService = ApiClient.retrofit.create(UserService::class.java)

        userService.getCurrentUser("Bearer $token").enqueue(object : Callback<UserWithoutPasswordDTO> {
            override fun onResponse(call: Call<UserWithoutPasswordDTO>, response: Response<UserWithoutPasswordDTO>) {
                if (response.isSuccessful) {
                    val currentUser = response.body()
                    if (currentUser != null) {
                        loadUserProfile(currentUser)
                    } else {
                        Log.e("ProfileFragment", "Réponse vide de /auth/me")
                    }
                } else {
                    Log.e("ProfileFragment", "Erreur API: ${response.code()}")
                    Toast.makeText(requireContext(), "Erreur API: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserWithoutPasswordDTO>, t: Throwable) {
                Log.e("ProfileFragment", "Erreur réseau: ${t.message}")
                Toast.makeText(requireContext(), "Erreur réseau", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadUserProfile(user: UserWithoutPasswordDTO) {
        Glide.with(this)
            .load(user.profilePictureUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_profil_black_font_grey_24dp)
            .into(binding.imageViewProfile)

        binding.textViewProfileName.text = "@${user.name}"
        binding.textProfile.text = user.bio ?: "+ Ajouter une bio"
        binding.followersCount.text = user.followersCount.toString()
        binding.followingCount.text = user.followingCount.toString()

        fetchUserVideos(user)

        val currentUserId = sharedPreferences.getInt("USER_ID", -1)

        if (user.id == currentUserId) {
            binding.editProfileButton.visibility = View.VISIBLE
            binding.shareProfileButton.visibility = View.VISIBLE
            binding.followButton.visibility = View.GONE
            binding.messageButton.visibility = View.GONE
        } else {
            binding.editProfileButton.visibility = View.GONE
            binding.shareProfileButton.visibility = View.GONE
            binding.followButton.visibility = View.VISIBLE
            binding.messageButton.visibility = View.VISIBLE

            user.id?.let { checkIfFollowing(it, currentUserId) }

            binding.followButton.setOnClickListener {
                user.id?.let { it1 -> toggleFollow(it1, currentUserId) }
            }
        }
    }



    private fun fetchUserVideos(user: UserWithoutPasswordDTO) {
        if (user.id == null) {
            Log.e("ProfileFragment", "ID utilisateur est null")
            return
        }
        val videoService = ApiClient.retrofit.create(VideoService::class.java)

        videoService.getVideosByAuthor(user.id).enqueue(object : Callback<List<VideoDTO>> {
            override fun onResponse(call: Call<List<VideoDTO>>, response: Response<List<VideoDTO>>) {
                if (response.isSuccessful) {
                    val videos = response.body() ?: emptyList()
                    val videosWithAuthor = videos.map { video -> VideoWithAuthor(first = video, second = user) }

                    viewPagerAdapter = ViewPagerRecyclerAdapter(requireActivity(), listOf(videosWithAuthor, videosWithAuthor))
                    binding.viewPager.adapter = viewPagerAdapter

                    binding.viewPager.setOnClickListener {
                        navigateToProfileVideos(videosWithAuthor, binding.viewPager.currentItem)
                    }
                } else {
                    Log.e("ProfileFragment", "Erreur ${response.code()}: ${response.message()}")
                    Toast.makeText(requireContext(), "Erreur chargement vidéos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<VideoDTO>>, t: Throwable) {
                Log.e("ProfileFragment", "Erreur réseau: ${t.message}")
                Toast.makeText(requireContext(), "Erreur réseau", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun checkIfFollowing(userId: Int, currentUserId: Int) {
        val userService = ApiClient.retrofit.create(UserService::class.java)

        userService.isFollowingUser(userId, currentUserId).enqueue(object : Callback<Map<String, Boolean>> {
            override fun onResponse(call: Call<Map<String, Boolean>>, response: Response<Map<String, Boolean>>) {
                if (response.isSuccessful) {
                    val isFollowing = response.body()?.get("isFollowing") ?: false
                    updateFollowButton(isFollowing)
                } else {
                    Log.e("ProfileFragment", "Erreur lors de la vérification du follow: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Boolean>>, t: Throwable) {
                Log.e("ProfileFragment", "Erreur réseau: ${t.message}")
            }
        })
    }

    private fun toggleFollow(userId: Int, currentUserId: Int) {
        val userService = ApiClient.retrofit.create(UserService::class.java)

        val call = if (binding.followButton.text == "Suivre") {
            userService.followUser(userId, currentUserId)
        } else {
            userService.unfollowUser(userId, currentUserId)
        }

        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    val newStatus = binding.followButton.text == "Suivre"
                    updateFollowButton(newStatus)
                    Log.d("ProfileFragment", "Follow status changé pour $userId")
                } else {
                    Log.e("ProfileFragment", "Erreur API: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("ProfileFragment", "Erreur réseau: ${t.message}")
            }
        })
    }

    private fun updateFollowButton(isFollowing: Boolean) {
        if (isFollowing) {
            binding.followButton.text = "Suivi"
            binding.followButton.setBackgroundColor(resources.getColor(R.color.white, null))
            binding.followButton.setTextColor(resources.getColor(R.color.black, null))
        } else {
            binding.followButton.text = "Suivre"
            binding.followButton.setBackgroundColor(resources.getColor(R.color.red, null))
            binding.followButton.setTextColor(resources.getColor(R.color.white, null))
        }
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            imageUri?.let { uri ->
                binding.imageViewProfile.setImageURI(uri)
                uploadImageToAzure(uri)
            }
        }
    }
    private fun uploadImageToAzure(imageUri: Uri) {
        val userId = sharedPreferences.getInt("USER_ID", -1)
        if (userId == -1) {
            Log.e("ProfileFragment", "User ID introuvable")
            return
        }

        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        val byteArray = inputStream?.readBytes() ?: return

        val fileName = "user_${userId}_${System.currentTimeMillis()}.jpg"
        val azureBlobUrl = "https://kitkatstorage.blob.core.windows.net/profilepicture/$fileName" +
                "?sp=racwdli&st=2025-03-02T22:06:34Z&se=2025-05-08T05:06:34Z&sv=2022-11-02&sr=c&sig=HjJSbqWdjeDdF%2F8JzWCn1vo7zFOsX7Bk1hRs29JimJU%3D"

        val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(azureBlobUrl)
            .put(requestBody)
            .addHeader("x-ms-blob-type", "BlockBlob") // Obligatoire pour Azure
            .build()

        val client = OkHttpClient()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("ProfileFragment", "Image uploadée avec succès sur Azure: $azureBlobUrl")
                    withContext(Dispatchers.Main) {
                        updateProfilePicture(azureBlobUrl) // Mettre à jour la DB avec la nouvelle image
                    }
                } else {
                    Log.e("ProfileFragment", "Erreur d'upload: ${response.code} ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Erreur réseau: ${e.message}")
            }
        }
    }
    private fun updateProfilePicture(imageUrl: String) {
        val token = sharedPreferences.getString("AUTH_TOKEN", null)
        if (token == null) {
            Log.e("ProfileFragment", "Token JWT introuvable, impossible de mettre à jour l'image")
            return
        }

        val userService = ApiClient.retrofit.create(UserService::class.java)
        userService.updateProfilePicture("Bearer $token", imageUrl).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    // Met à jour l'UI avec la nouvelle image
                    Glide.with(requireContext()).load(imageUrl).into(binding.imageViewProfile)
                    Toast.makeText(requireContext(), "Photo de profil mise à jour", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("ProfileFragment", "Échec de la mise à jour du profil: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("ProfileFragment", "Erreur réseau: ${t.message}")
            }
        })
    }

    private fun navigateToProfileVideos(videos: List<VideoWithAuthor>, position: Int) {
        val bundle = Bundle().apply {
            putSerializable("videos", ArrayList(videos))
            putInt("position", position)
        }
        findNavController().navigate(R.id.action_profile_to_profileVideos, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
