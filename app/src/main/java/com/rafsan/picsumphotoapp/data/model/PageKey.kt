package com.rafsan.picsumphotoapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pageKey")
data class PageKey(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)