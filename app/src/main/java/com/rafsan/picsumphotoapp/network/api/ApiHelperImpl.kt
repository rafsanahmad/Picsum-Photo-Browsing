package com.rafsan.picsumphotoapp.network.api

import com.rafsan.picsumphotoapp.data.model.ImageList
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val picsumApi: PicsumApi) : ApiHelper {

    override suspend fun getImages(pageNumber: Int): Response<ImageList> =
        picsumApi.getImages(pageNumber)

}