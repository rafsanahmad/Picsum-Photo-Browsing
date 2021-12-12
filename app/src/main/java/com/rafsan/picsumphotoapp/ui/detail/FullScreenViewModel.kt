package com.rafsan.picsumphotoapp.ui.detail

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rafsan.picsumphotoapp.base.BaseViewModel
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.ext.saveToInternalStorage
import com.rafsan.picsumphotoapp.ext.toBitmap
import com.rafsan.picsumphotoapp.utils.CoroutinesDispatcherProvider
import com.rafsan.picsumphotoapp.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class FullScreenViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider
) : BaseViewModel() {

    private val TAG = "FullScreenViewModel"
    private val _errorToast = MutableLiveData<String>()
    val errorToast: LiveData<String>
        get() = _errorToast

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri>
        get() = _imageUri

    private val _showProgressbar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressbar

    var downloadedFileUri: Uri? = null
    var shareImage = false

    fun downloadImage(imageItem: ImageListItem) {
        if (networkHelper.isNetworkConnected()) {
            //Update progress bar live data
            _showProgressbar.value = true
            // async task to get / download bitmap from url
            val urlImage: URL = URL(imageItem.download_url)

            val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
                onError(exception)
            }

            val result: Deferred<Bitmap?> =
                viewModelScope.async(coroutinesDispatcherProvider.io + coroutineExceptionHandler) {
                    urlImage.toBitmap()
                }

            viewModelScope.launch(coroutinesDispatcherProvider.main + coroutineExceptionHandler) {
                // get the downloaded bitmap
                val bitmap: Bitmap? = result.await()

                // if downloaded then saved it to internal storage
                bitmap?.apply {
                    // get saved bitmap internal storage uri
                    val savedUri: Uri? = saveToInternalStorage(imageItem.id)

                    //update livedata
                    _imageUri.value = savedUri
                    _showProgressbar.value = false
                }
            }
        } else {
            _errorToast.postValue("No internet available.")
        }
    }

    fun hideErrorToast() {
        _errorToast.postValue("")
    }

    private fun onError(throwable: Throwable) {
        throwable.message?.let {
            _errorToast.postValue(it)
        }
    }
}