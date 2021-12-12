package com.rafsan.picsumphotoapp.data.paging.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rafsan.picsumphotoapp.data.db.ImageListDb
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.data.model.PageKey
import com.rafsan.picsumphotoapp.network.api.PicsumApi
import com.rafsan.picsumphotoapp.utils.Constants.Companion.DEFAULT_PAGE_INDEX

@OptIn(ExperimentalPagingApi::class)
class ImageListRemoteMediator(val service: PicsumApi, val db: ImageListDb) :
    RemoteMediator<Int, ImageListItem>() {
    private val imageListDao = db.getImageListDao()
    private val pageKeyDao = db.pageKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ImageListItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val apiResponse = service.getImages(page)
            val resBody = apiResponse.body()
            val images = resBody
            var endOfPaginationReached = false
            images?.let {
                endOfPaginationReached = it.isEmpty()
            }
            db.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    pageKeyDao.clearAll()
                    imageListDao.deleteAllImages()
                }
                val prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = images?.map {
                    PageKey(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                if (keys != null) {
                    pageKeyDao.insertAll(keys)
                }
                if (images != null) {
                    imageListDao.insertAll(images)
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ImageListItem>): PageKey? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { item ->
                // Get the remote keys of the last item retrieved
                pageKeyDao.getNextPageKey(item.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ImageListItem>): PageKey? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { item ->
                // Get the remote keys of the first items retrieved
                pageKeyDao.getNextPageKey(item.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ImageListItem>
    ): PageKey? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                pageKeyDao.getNextPageKey(id)
            }
        }
    }
}