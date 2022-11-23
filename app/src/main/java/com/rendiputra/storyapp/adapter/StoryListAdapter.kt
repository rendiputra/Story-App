package com.rendiputra.storyapp.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.rendiputra.storyapp.R
import com.rendiputra.storyapp.databinding.ItemStoryBinding
import com.rendiputra.storyapp.domain.Story

class StoryListAdapter(private val onStoryClick: OnStoryClick) : ListAdapter<Story, StoryListAdapter.StoryViewHolder>(DiffCallback) {

    private companion object DiffCallback : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem == newItem
        }
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) : ViewHolder(binding.root) {
        fun bind(story: Story) {
            binding.ivStory.load(story.photoUrl) {
                placeholder(R.drawable.ic_loading_item_story)
                error(R.drawable.ic_error_item_story)
            }
            val descriptionStory = SpannableStringBuilder().apply {
                append(story.name).setSpan(StyleSpan(Typeface.BOLD), 0, story.name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(" ")
                append(story.description)
            }
            binding.tvStory.text = descriptionStory

            itemView.setOnClickListener { onStoryClick.onStoryClicked(story, binding) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnStoryClick {
        fun onStoryClicked(story: Story, binding: ItemStoryBinding)
    }
}