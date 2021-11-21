package com.rafsan.picsumphotoapp.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rafsan.picsumphotoapp.R
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.databinding.ItemImageBinding

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageAdapterViewHolder>() {

    inner class ImageAdapterViewHolder(val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<ImageListItem>() {
        override fun areItemsTheSame(oldItem: ImageListItem, newItem: ImageListItem): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: ImageListItem, newItem: ImageListItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdapterViewHolder {
        val binding =
            ItemImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ImageAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((ImageListItem) -> Unit)? = null

    override fun onBindViewHolder(holder: ImageAdapterViewHolder, position: Int) {
        val item = differ.currentList[position]
        with(holder) {
            Glide.with(itemView.context)
                .load(item.download_url)
                .dontTransform()
                .placeholder(R.drawable.placeholder)
                .into(binding.imageItem)
        }

        holder.itemView.apply {
            setOnClickListener {
                onItemClickListener?.let {
                    it(item)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (ImageListItem) -> Unit) {
        onItemClickListener = listener
    }
}