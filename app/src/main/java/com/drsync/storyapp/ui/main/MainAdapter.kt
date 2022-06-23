package com.drsync.storyapp.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drsync.storyapp.R
import com.drsync.storyapp.databinding.ItemStoriesBinding
import com.drsync.storyapp.models.Story
import com.drsync.storyapp.ui.detailstory.DetailActivity
import com.drsync.storyapp.util.Constant.KEY_STORY
import com.drsync.storyapp.util.Constant.createProgress
import java.util.*

class MainAdapter : ListAdapter<Story, MainAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Story) {
            binding.apply {
                val context = itemView.context
                val firstLetter = data.name?.first().toString()
                val date = itemView.context.getString(R.string.created_at,
                    data.createdAt?.split("T")?.get(0) ?: ""
                )

                tvNama.text = data.name
                tvCreatedAt.text = date
                tvFirstLetter.text = firstLetter

                val rnd = Random()
                val color: Int =
                    Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                cardView.backgroundTintList = ColorStateList.valueOf(color)

                Glide.with(context)
                    .load(data.photoUrl)
                    .placeholder(itemView.context.createProgress())
                    .error(android.R.color.darker_gray)
                    .into(imgStories)

                itemView.setOnClickListener {
                    Intent(context, DetailActivity::class.java).also {
                        it.putExtra(KEY_STORY, data)

                        val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(tvNama, context.getString(R.string.image)),
                            Pair(tvCreatedAt, context.getString(R.string.date))
                        )
                        context.startActivity(it, optionsCompat.toBundle())
                    }
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Story> =
            object : DiffUtil.ItemCallback<Story>() {
                override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                    return oldItem == newItem
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                    return oldItem.id == newItem.id
                }
            }
    }
}