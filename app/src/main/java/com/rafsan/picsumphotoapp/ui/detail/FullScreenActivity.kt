package com.rafsan.picsumphotoapp.ui.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.NavUtils
import com.bumptech.glide.Glide
import com.rafsan.picsumphotoapp.R
import com.rafsan.picsumphotoapp.base.BaseActivity
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.databinding.ActivityDetailBinding

class FullScreenActivity : BaseActivity<ActivityDetailBinding>() {

    override fun onViewReady(savedInstanceState: Bundle?) {
        super.onViewReady(savedInstanceState)
        val bundle = intent.extras
        if (bundle != null) {
            val item = bundle.getSerializable("item")
            setupUI(item as ImageListItem)
        }
    }

    private fun setupUI(item: ImageListItem) {
        with(binding) {
            Glide.with(this@FullScreenActivity)
                .load(item.download_url)
                .placeholder(R.drawable.placeholder)
                .into(imageFullScreen)

            fabOptions.setOnClickListener {
                onOptionButtonClick()
            }

            //share fab button on click
            fabShare.setOnClickListener {

            }
            //download fab button on click
            fabDownload.setOnClickListener {

            }
        }
    }

    private fun onOptionButtonClick() {

    }

    override fun setBinding(): ActivityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Respond to the action bar's Up/Home button
        if (item.itemId == R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}