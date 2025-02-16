package com.example.kitkat.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.kitkat.R
import com.example.kitkat.api.models.dataclass.UserWithoutPasswordDTO
import androidx.navigation.fragment.findNavController
import com.example.kitkat.api.models.dataclass.VideoDTO
import com.example.kitkat.api.models.dataclass.VideoWithAuthor
import com.example.kitkat.databinding.FragmentProfileBinding
import com.example.kitkat.model.VideoItem
import com.example.kitkat.network.ApiClient
import com.example.kitkat.network.services.VideoService
import com.example.kitkat.ui.profile.video.ViewPagerRecyclerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: ViewPagerRecyclerAdapter

    private var user: UserWithoutPasswordDTO? = null
    private var isOwnProfile: Boolean = true // Par défaut, afficher le profil de l'utilisateur connecté

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get le user sinon null
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

        val navBar = requireActivity().findViewById<View>(R.id.nav_view)

        if (user != null) {
            binding.backButton.visibility = View.VISIBLE
            navBar?.visibility = View.GONE
            binding.backButton.setOnClickListener {
                findNavController().popBackStack() // Retourne au fragment précédent
            }
        } else {
            binding.backButton.visibility = View.GONE
            navBar?.visibility = View.VISIBLE
        }

        setupUserProfile(user)

        return binding.root
    }

    private fun restoreVideoPosition(position: Int) {
        binding.viewPager.setCurrentItem(position, false) // Restaurer la position
    }

    private fun setupUserProfile(user: UserWithoutPasswordDTO?) {
        if (user == null) {
            // load les info d'user connecté
            loadCurrentUserProfile()
        } else {
            // load les info du user passé en paramètre
            loadUserProfile(user)
        }
    }

    private fun loadCurrentUserProfile() {
        // temporaire
        val currentUser = UserWithoutPasswordDTO(
            id = 1,
            name = "Talal",
            email = "talal@example.com",
            profilePictureUrl = "https://www.pngplay.com/wp-content/uploads/12/User-Avatar-Profile-PNG-Photos.png",
            bio = "Développeur passionné",
            followersCount = 100,
            followingCount = 200
        )
        loadUserProfile(currentUser)
    }
    private fun fetchUserVideos(user: UserWithoutPasswordDTO, onSuccess: (List<VideoWithAuthor>) -> Unit, onError: (String) -> Unit) {
        if (user.id == null) {
            onError("L'ID utilisateur est nul. Impossible de récupérer les vidéos.")
            return
        }
        val videoService = ApiClient.retrofit.create(VideoService::class.java)
        user.id?.let {
            videoService.getVideosByAuthor(it).enqueue(object : Callback<List<VideoDTO>> {
                override fun onResponse(call: Call<List<VideoDTO>>, response: Response<List<VideoDTO>>) {
                    if (response.isSuccessful) {
                        val videos = response.body() ?: emptyList()
                        val videosWithAuthor = videos.map { video ->
                            VideoWithAuthor(
                                first = video,
                                second = user
                            )
                        }
                        onSuccess(videosWithAuthor)
                    } else {
                        onError("Erreur ${response.code()}: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<VideoDTO>>, t: Throwable) {
                    onError("Erreur réseau : ${t.message}")
                }
            })
        }
    }
    private fun fetchUserVideosWithAuthor(
        userId: Int,
        onSuccess: (List<VideoWithAuthor>) -> Unit,
        onError: (String) -> Unit
    ) {
        val videoService = ApiClient.retrofit.create(VideoService::class.java)
        videoService.getVideosByAuthor(userId).enqueue(object : Callback<List<VideoDTO>> {
            override fun onResponse(call: Call<List<VideoDTO>>, response: Response<List<VideoDTO>>) {
                if (response.isSuccessful) {
                    val videoDTOs = response.body() ?: emptyList()
                    val videosWithAuthors = videoDTOs.map { videoDTO ->
                        VideoWithAuthor(
                            first = videoDTO,
                            second = user!!
                        )
                    }
                    onSuccess(videosWithAuthors)
                } else {
                    onError("Erreur ${response.code()}: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<VideoDTO>>, t: Throwable) {
                onError("Erreur réseau : ${t.message}")
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

        fetchUserVideos(user, { videosWithAuthor ->
            Log.d("ProfileFragment", "Vidéos récupérées : $videosWithAuthor")

            // Passer les données directement en tant que `List<VideoWithAuthor>`
            viewPagerAdapter = ViewPagerRecyclerAdapter(requireActivity(), listOf(videosWithAuthor,videosWithAuthor))
            binding.viewPager.adapter = viewPagerAdapter

            // Configurer l'action au clic pour naviguer vers le `VideoPagerFragment`
            binding.viewPager.setOnClickListener {
                navigateToProfileVideos(videosWithAuthor, binding.viewPager.currentItem)
            }
        }, { error ->
            Log.e("ProfileFragment", "Erreur lors de la récupération des vidéos : $error")
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        })

        if (isOwnProfile) {
            binding.myList.visibility = View.VISIBLE // Bouton d'édition visible pour le profil connecté
        } else {
            binding.myList.visibility = View.GONE // Masquer les options d'édition pour un autre utilisateur
        }

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