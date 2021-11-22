package com.rafsan.picsumphotoapp.data.local

import androidx.room.*
import com.rafsan.picsumphotoapp.data.model.ImageListItem

@Dao
interface ImageListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(imageItem: ImageListItem): Long

    @Query("SELECT * FROM image_list")
    suspend fun getAllImages(): List<ImageListItem>

    @Delete
    suspend fun deleteImage(item: ImageListItem)

    @Query("Delete FROM image_list")
    suspend fun deleteAllImages()
}