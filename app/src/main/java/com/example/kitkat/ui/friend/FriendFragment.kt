package com.example.kitkat.ui.friend

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.kitkat.app_utils.SHARED_PREF_KEY
import com.example.kitkat.databinding.FragmentHomeBinding
import com.example.kitkat.network.ApiClient
import com.example.kitkat.network.dto.VideoWithAuthor
import com.example.kitkat.network.services.VideoService
import com.example.kitkat.ui.home.video.VideoPagerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: VideoPagerAdapter

    private val videoService: VideoService by lazy {
        ApiClient.retrofit.create(VideoService::class.java)
    }

    private var videos = listOf<VideoWithAuthor>()


    private fun fetchFriendVideos() {
        val sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("AUTH_TOKEN", null)

        if (token == null) {
            Log.e("FriendFragment", "Token JWT introuvable, l'utilisateur doit se reconnecter")
            return
        }

        val videoService = ApiClient.retrofit.create(VideoService::class.java)
        videoService.getFriendsVideos("Bearer $token").enqueue(object :
            Callback<List<VideoWithAuthor>> {
            override fun onResponse(call: Call<List<VideoWithAuthor>>, response: Response<List<VideoWithAuthor>>) {
                if (response.isSuccessful) {
                    val videos = response.body() ?: emptyList()
                    Log.d("FriendFragment", "${videos.size} vidéos récupérées")
                    adapter.updateVideos(videos)
                } else {
                    Log.e("FriendFragment", "Erreur API: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<VideoWithAuthor>>, t: Throwable) {
                Log.e("FriendFragment", "Erreur réseau: ${t.message}", t)
            }
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = VideoPagerAdapter(emptyList(), parentFragmentManager)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                adapter.updateActivePosition(position)
            }
        })

        fetchFriendVideos()

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        Log.d("FriendFragment", "onPause called: Releasing active player")
        if (::adapter.isInitialized) {
            adapter.releaseAllPlayers()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FriendFragment", "onDestroyView: Releasing all players")
        if (::adapter.isInitialized) {
            adapter.releaseAllPlayers()
        }
        _binding = null
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPref = requireContext().getSharedPreferences("SHARED_PREF_KEY", 0)
        return sharedPref.getString("AUTH_TOKEN", null)
    }
}
