package com.rafsan.picsumphotoapp.ui.main

import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rafsan.picsumphotoapp.base.BaseViewModel
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.network.repository.ImageListRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
@ExperimentalPagingApi
class MainViewModel @Inject constructor(
    private val repository: ImageListRepositoryImpl
) : BaseViewModel() {

    private val TAG = "MainViewModel"
    private lateinit var _imageResponse: Flow<PagingData<ImageListItem>>
    val imageResponse: Flow<PagingData<ImageListItem>>
        get() = _imageResponse

    init {
        fetchImages()
    }

    private fun fetchImages() {
        launchPagingAsync({
            repository.getImages().cachedIn(viewModelScope)
        }, {
            _imageResponse = it
        })
    }
}