package com.rafsan.picsumphotoapp.network.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rafsan.picsumphotoapp.data.db.ImageListDb
import com.rafsan.picsumphotoapp.data.db.dao.ImageListDao
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.data.paging.datasource.ImagePagingDataSource
import com.rafsan.picsumphotoapp.data.paging.remotemediator.ImageListRemoteMediator
import com.rafsan.picsumphotoapp.network.api.PicsumApi
import com.rafsan.picsumphotoapp.utils.Constants.Companion.QUERY_PER_PAGE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
class ImageListRepositoryImpl @Inject constructor(
    private val picsumApi: PicsumApi,
    private val localDataSource: ImageListDao,
    private val imageListDb: ImageListDb
) : ImageListRepository {

    override suspend fun getImages(): Flow<PagingData<ImageListItem>> {
        val pagingSourceRemote = { ImagePagingDataSource(picsumApi) }
        val pagingSourceLocal = { imageListDb.getImageListDao().getAllImages() }
        return Pager(
            config = PagingConfig(pageSize = QUERY_PER_PAGE, prefetchDistance = 2),
            remoteMediator = ImageListRemoteMediator(picsumApi, imageListDb),
            pagingSourceFactory = pagingSourceLocal
        ).flow
    }

    override suspend fun saveImageItem(item: ImageListItem) = localDataSource.upsert(item)

    override suspend fun getSavedImages() = localDataSource.getAllImages()

    override suspend fun getSavedImagesList() = localDataSource.getAllImagesList()

    override
    suspend fun deleteAllImages() = localDataSource.deleteAllImages()
}