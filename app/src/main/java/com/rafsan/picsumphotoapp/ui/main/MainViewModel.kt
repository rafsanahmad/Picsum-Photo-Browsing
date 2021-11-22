package com.rafsan.picsumphotoapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafsan.picsumphotoapp.data.model.ImageList
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.network.repository.ImageListRepository
import com.rafsan.picsumphotoapp.utils.CoroutinesDispatcherProvider
import com.rafsan.picsumphotoapp.utils.NetworkHelper
import com.rafsan.picsumphotoapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ImageListRepository,
    private val networkHelper: NetworkHelper,
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val TAG = "MainViewModel"
    private val _errorToast = MutableLiveData<String>()
    val errorToast: LiveData<String>
        get() = _errorToast

    private val _imageResponse = MutableLiveData<NetworkResult<ImageList>>()
    val imageResponse: LiveData<NetworkResult<ImageList>>
        get() = _imageResponse

    private var imageListResponse: ImageList? = null
    var imageListPage = 1

    init {
        fetchImages()
    }

    fun fetchImages() {
        if (networkHelper.isNetworkConnected()) {
            _imageResponse.postValue(NetworkResult.Loading())

            val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
                onError(exception)
            }
            viewModelScope.launch(coroutinesDispatcherProvider.io + coroutineExceptionHandler) {
                when (val response = repository.getImages(imageListPage)) {
                    is NetworkResult.Success -> {
                        _imageResponse.postValue(handleImageListResponse(response))
                    }
                    is NetworkResult.Error -> {
                        _imageResponse.postValue(
                            NetworkResult.Error(
                                response.message ?: "Error"
                            )
                        )
                    }
                }

            }
        } else {
            _errorToast.value = "No internet available"
            getSavedImages()
        }
    }

    private fun handleImageListResponse(response: NetworkResult<ImageList>): NetworkResult<ImageList> {
        response.data?.let { resultResponse ->
            if (imageListResponse == null) {
                imageListPage = 2
                imageListResponse = resultResponse
            } else {
                imageListPage++
                imageListResponse?.addAll(resultResponse)
            }
            imageListResponse?.let {
                saveResponseToCache(it)
            }
            return NetworkResult.Success(imageListResponse ?: resultResponse)
        }
        return NetworkResult.Error("No data found")
    }

    fun hideErrorToast() {
        _errorToast.value = ""
    }

    fun saveResponseToCache(imageListResponse: ImageList) {
        for (i in 0 until imageListResponse.size) {
            val item = imageListResponse[i]
            saveImageItem(item)
        }
    }

    fun saveImageItem(item: ImageListItem) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            onError(exception)
        }
        viewModelScope.launch(coroutinesDispatcherProvider.io + coroutineExceptionHandler) {
            repository.saveImageItem(item)
        }
    }

    fun getSavedImages() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            onError(exception)
        }
        viewModelScope.launch(coroutinesDispatcherProvider.io + coroutineExceptionHandler) {
            val imageList = ImageList()
            val savedList = repository.getSavedImagesList()
            imageList.addAll(savedList)
            _imageResponse.postValue(NetworkResult.Success(imageList))
        }
    }

    fun refresh() {
        imageListResponse = null
        imageListPage = 1
    }

    private fun onError(throwable: Throwable) {
        _errorToast.value = throwable.message
    }
}