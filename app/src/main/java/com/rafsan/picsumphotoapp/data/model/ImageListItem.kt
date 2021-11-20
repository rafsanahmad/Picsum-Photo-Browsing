package com.rafsan.picsumphotoapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "image_list")
data class ImageListItem(
    @PrimaryKey(autoGenerate = true)
    var key: Int? = null,
    val author: String?,
    val download_url: String?,
    val height: Int?,
    val id: String?,
    val url: String?,
    val width: Int?
) : Serializable