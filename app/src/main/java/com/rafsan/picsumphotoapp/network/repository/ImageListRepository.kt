package com.rafsan.picsumphotoapp.network.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import kotlinx.coroutines.flow.Flow

interface ImageListRepository {
    suspend fun getImages(): Flow<PagingData<ImageListItem>>
    suspend fun saveImageItem(item: ImageListItem): Long
    suspend fun getSavedImages(): PagingSource<Int, ImageListItem>
    suspend fun getSavedImagesList(): List<ImageListItem>
    suspend fun deleteAllImages()
}