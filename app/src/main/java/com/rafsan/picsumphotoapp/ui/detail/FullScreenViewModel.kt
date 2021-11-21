package com.rafsan.picsumphotoapp.ui.detail

import androidx.lifecycle.ViewModel
import com.rafsan.picsumphotoapp.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FullScreenViewModel @Inject constructor(
    private val networkHelper: NetworkHelper
) : ViewModel() {

    fun downloadImage() {

    }

}