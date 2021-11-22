package com.rafsan.picsumphotoapp.network.repository

import com.rafsan.picsumphotoapp.data.local.ImageListDao
import com.rafsan.picsumphotoapp.data.model.ImageList
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.network.api.ApiHelper
import com.rafsan.picsumphotoapp.utils.NetworkResult
import javax.inject.Inject

class ImageListRepository @Inject constructor(
    private val remoteDataSource: ApiHelper,
    private val localDataSource: ImageListDao
) {

    suspend fun getImages(pageNumber: Int): NetworkResult<ImageList> {
        return try {
            val response = remoteDataSource.getImages(pageNumber)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                NetworkResult.Success(result)
            } else {
                NetworkResult.Error("An error occurred")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Error occurred ${e.localizedMessage}")
        }
    }

    suspend fun saveImageItem(item: ImageListItem) = localDataSource.upsert(item)

    suspend fun getSavedImagesList() = localDataSource.getAllImages()

    suspend fun deleteImage(item: ImageListItem) = localDataSource.deleteImage(item)

    suspend fun deleteAllImages() = localDataSource.deleteAllImages()
}