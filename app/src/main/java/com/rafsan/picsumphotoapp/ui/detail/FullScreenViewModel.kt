package com.rafsan.picsumphotoapp.ui.detail

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.utils.NetworkHelper
import com.rafsan.picsumphotoapp.utils.saveToInternalStorage
import com.rafsan.picsumphotoapp.utils.toBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class FullScreenViewModel @Inject constructor(
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "FullScreenViewModel"
    private val _errorToast = MutableLiveData<String>()
    val errorToast: LiveData<String>
        get() = _errorToast

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri>
        get() = _imageUri

    fun downloadImage(imageItem: ImageListItem) {
        if (networkHelper.isNetworkConnected()) {
            // async task to get / download bitmap from url
            val urlImage: URL = URL(imageItem.download_url)
            val result: Deferred<Bitmap?> = viewModelScope.async(Dispatchers.IO) {
                urlImage.toBitmap()
            }

            viewModelScope.launch(Dispatchers.Main) {
                // get the downloaded bitmap
                val bitmap: Bitmap? = result.await()

                // if downloaded then saved it to internal storage
                bitmap?.apply {
                    // get saved bitmap internal storage uri
                    val savedUri: Uri? = saveToInternalStorage()

                    //update livedata
                    _imageUri.value = savedUri
                }
            }
        } else {
            _errorToast.value = "No internet available"
        }
    }

    fun hideErrorToast() {
        _errorToast.value = ""
    }
}