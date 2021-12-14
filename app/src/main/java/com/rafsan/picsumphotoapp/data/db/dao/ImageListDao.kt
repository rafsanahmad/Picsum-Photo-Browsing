package com.rafsan.picsumphotoapp.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rafsan.picsumphotoapp.data.model.ImageListItem

@Dao
interface ImageListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(imageItem: ImageListItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ImageListItem>)

    @Query("SELECT * FROM image_list")
    fun getAllImages(): PagingSource<Int, ImageListItem>

    @Query("SELECT * FROM image_list")
    fun getAllImagesList(): List<ImageListItem>

    @Query("Delete FROM image_list")
    suspend fun deleteAllImages()
}