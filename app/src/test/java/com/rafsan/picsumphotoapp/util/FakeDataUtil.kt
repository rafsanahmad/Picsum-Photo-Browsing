package com.rafsan.picsumphotoapp.util

import com.rafsan.picsumphotoapp.data.model.ImageListItem
import retrofit2.Response

object FakeDataUtil {
    fun getFakeImagesResponse(): Response<List<ImageListItem>> {
        val images = getFakeImages()
        images.removeAt(2) //remove last item
        return Response.success(images)
    }

    fun getFakeImages(): MutableList<ImageListItem> {
        val imageList: MutableList<ImageListItem> = arrayListOf()
        val image1 = ImageListItem(
            id = "1", author = "author1", height = 100, width = 100, url = "https://abc.com",
            download_url = "https://picsum.photos/id/0/5616/3744", page = 1
        )
        val image2 = ImageListItem(
            id = "2", author = "author2", height = 200, width = 200, url = "https://def.com",
            download_url = "https://picsum.photos/id/1/5616/3744", page = 1
        )

        val image3 = ImageListItem(
            id = "3", author = "author3", height = 200, width = 200, url = "https://def.com",
            download_url = "https://picsum.photos/id/1/5616/3744", page = 2
        )

        imageList.add(image1)
        imageList.add(image2)
        imageList.add(image3)
        return imageList
    }

    fun getFakeImage(id: String = "1"): ImageListItem {
        val image1 = ImageListItem(
            id = id, author = "author1", height = 100, width = 100, url = "https://abc.com",
            download_url = "https://abc.com", page = 1
        )
        return image1
    }
}