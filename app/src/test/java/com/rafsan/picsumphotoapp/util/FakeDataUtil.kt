package com.rafsan.picsumphotoapp.util

import com.rafsan.picsumphotoapp.data.model.ImageList
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.utils.NetworkResult

object FakeDataUtil {
    fun getFakeImagesResponse(): NetworkResult<ImageList> {
        val images = getFakeImages()
        val imageResponse = ImageList()
        imageResponse.addAll(images)
        return NetworkResult.Success(imageResponse)
    }

    fun getFakeImages(): MutableList<ImageListItem> {
        val imageList: MutableList<ImageListItem> = arrayListOf()
        val image1 = ImageListItem(
            id = "1", author = "author1", height = 100, width = 100, url = "https://abc.com",
            download_url = "https://abc.com"
        )
        val image2 = ImageListItem(
            id = "2", author = "author2", height = 200, width = 200, url = "https://def.com",
            download_url = "https://def.com"
        )

        imageList.add(image1)
        imageList.add(image2)
        return imageList
    }

    fun getFakeImage(): ImageListItem {
        val image1 = ImageListItem(
            id = "1", author = "author1", height = 100, width = 100, url = "https://abc.com",
            download_url = "https://abc.com"
        )
        return image1
    }
}