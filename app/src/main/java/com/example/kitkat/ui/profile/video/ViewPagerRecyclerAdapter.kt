package com.example.kitkat.ui.profile.video

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kitkat.network.dto.VideoWithAuthor

class ViewPagerRecyclerAdapter(
    fragmentActivity: FragmentActivity,
    private val itemsList: List<List<VideoWithAuthor>> // Utiliser VideoWithAuthor
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = itemsList.size

    override fun createFragment(position: Int): Fragment {
        return RecyclerPageFragment.newInstance(itemsList[position])
    }
}
