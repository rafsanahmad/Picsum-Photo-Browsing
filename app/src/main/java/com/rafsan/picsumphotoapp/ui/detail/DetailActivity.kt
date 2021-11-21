package com.rafsan.picsumphotoapp.ui.detail

import android.os.Bundle
import com.rafsan.picsumphotoapp.base.BaseActivity
import com.rafsan.picsumphotoapp.databinding.ActivityDetailBinding

class DetailActivity : BaseActivity<ActivityDetailBinding>() {

    override fun onViewReady(savedInstanceState: Bundle?) {
        super.onViewReady(savedInstanceState)
    }

    override fun setBinding(): ActivityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)

}