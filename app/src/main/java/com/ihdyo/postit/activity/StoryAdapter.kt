package com.ihdyo.postit.activity

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.ihdyo.postit.data.model.Story
import com.ihdyo.postit.databinding.ItemStoryRowBinding
import com.ihdyo.postit.utils.ConstVal
import com.ihdyo.postit.utils.ext.setImageUrl
import com.ihdyo.postit.utils.ext.timeStamptoString

class StoryAdapter(private val storyList: List<Story>) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(storyList[position])
    }

    override fun getItemCount(): Int = storyList.size

    inner class StoryViewHolder(private val binding: ItemStoryRowBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                    putExtra(ConstVal.BUNDLE_KEY_STORY, storyList[adapterPosition])
                }

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(binding.imgStoryThumbnail, "thumbnail"),
                    Pair(binding.tvStoryTitle, "title"),
                    Pair(binding.tvStoryDesc, "description")
                )

                itemView.context.startActivity(intent, options.toBundle())
            }
        }

        fun bind(story: Story) {
            with(binding) {
                tvStoryTitle.text = story.name
                tvStoryDesc.text = story.description
                tvStoryDate.text = story.createdAt.timeStamptoString()
                imgStoryThumbnail.setImageUrl(story.photoUrl, true)
            }
        }
    }
}