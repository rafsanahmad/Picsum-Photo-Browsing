package com.rafsan.picsumphotoapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "image_list")
data class ImageListItem(
    @PrimaryKey()
    val id: String = "1",
    val author: String?,
    val download_url: String?,
    val height: Int?,
    val url: String?,
    val width: Int?,
    var page: Int?
) : Serializable