package com.rafsan.picsumphotoapp.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.rafsan.picsumphotoapp.R
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.databinding.ItemImageBinding
import com.rafsan.picsumphotoapp.di.GlideApp
import javax.inject.Inject

class ImageAdapter @Inject constructor() :
    PagingDataAdapter<ImageListItem, ImageAdapter.ImageAdapterViewHolder>(ItemComparator) {

    var imageClickListener: ImageItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImageAdapterViewHolder(
            ItemImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ImageAdapterViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ImageAdapterViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                imageClickListener?.onImageClicked(
                    binding,
                    getItem(absoluteAdapterPosition) as ImageListItem
                )
            }
        }

        fun bind(item: ImageListItem) = with(binding) {
            GlideApp.with(itemView.context)
                .load(item.download_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .downsample(DownsampleStrategy.AT_MOST)
                .placeholder(R.drawable.placeholder)
                .into(imageItem)
        }
    }

    object ItemComparator : DiffUtil.ItemCallback<ImageListItem>() {
        override fun areItemsTheSame(oldItem: ImageListItem, newItem: ImageListItem) =
            oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: ImageListItem, newItem: ImageListItem) =
            oldItem == newItem
    }

    interface ImageItemClickListener {
        fun onImageClicked(binding: ItemImageBinding, item: ImageListItem)
    }
}