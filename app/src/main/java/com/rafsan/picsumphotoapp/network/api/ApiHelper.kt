package com.rafsan.picsumphotoapp.network.api

import com.rafsan.picsumphotoapp.data.model.ImageList
import retrofit2.Response

interface ApiHelper {

    suspend fun getImages(pageNumber: Int): Response<ImageList>
}