package com.rafsan.picsumphotoapp.ui.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.app.NavUtils
import com.bumptech.glide.Glide
import com.rafsan.picsumphotoapp.R
import com.rafsan.picsumphotoapp.base.BaseActivity
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.databinding.ActivityDetailBinding

class FullScreenActivity : BaseActivity<ActivityDetailBinding>() {

    // and initializing it with animation files that we have created
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }

    //used to check if fab menu are opened or closed
    private var menuClosed = false

    override fun onViewReady(savedInstanceState: Bundle?) {
        super.onViewReady(savedInstanceState)
        val bundle = intent.extras
        if (bundle != null) {
            val item = bundle.getSerializable("item")
            setupUI(item as ImageListItem)
        }
    }

    override fun setBinding(): ActivityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)

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
        setVisibility(menuClosed)
        setAnimation(menuClosed)
        menuClosed = !menuClosed;
    }

    // A Function used to set the Animation effect
    private fun setAnimation(menuClosed: Boolean) {
        with(binding) {
            if (!menuClosed) {
                fabDownload.startAnimation(fromBottom)
                fabShare.startAnimation(fromBottom)
                fabOptions.startAnimation(rotateOpen)
            } else {
                fabDownload.startAnimation(toBottom)
                fabShare.startAnimation(toBottom)
                fabOptions.startAnimation(rotateClose)
            }
        }
    }

    // used to set visibility to VISIBLE / INVISIBLE
    private fun setVisibility(menuClosed: Boolean) {
        with(binding) {
            if (!menuClosed) {
                fabDownload.visibility = View.VISIBLE
                fabShare.visibility = View.VISIBLE
            } else {
                fabDownload.visibility = View.INVISIBLE
                fabShare.visibility = View.INVISIBLE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Respond to the action bar's Up/Home button
        if (item.itemId == R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}