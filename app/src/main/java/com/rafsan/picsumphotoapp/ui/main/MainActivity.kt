package com.rafsan.picsumphotoapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rafsan.picsumphotoapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}