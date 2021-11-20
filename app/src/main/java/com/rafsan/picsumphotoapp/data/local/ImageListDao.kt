package com.rafsan.picsumphotoapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rafsan.picsumphotoapp.data.model.ImageListItem

@Dao
interface ImageListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(imageItem: ImageListItem): Long

    @Query("SELECT * FROM image_list")
    fun getAllImages(): LiveData<List<ImageListItem>>

    @Delete
    suspend fun deleteImage(item: ImageListItem)

    @Query("Delete FROM image_list")
    suspend fun deleteAllImages()
}