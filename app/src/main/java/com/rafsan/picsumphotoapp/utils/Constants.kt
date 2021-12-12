package com.rafsan.picsumphotoapp.utils

import com.rafsan.picsumphotoapp.BuildConfig

class Constants {
    companion object {
        val BASE_URL = getBaseUrlByProductFlavor()
        const val QUERY_PER_PAGE = 20
        const val TOTAL_IMAGES = 1000
        const val TOTAL_PAGES = TOTAL_IMAGES / QUERY_PER_PAGE
        const val DEFAULT_PAGE_INDEX = 1
        const val TAG = "PicsumPhotoApp"
        const val IMAGE_QUALITY = 80

        private fun getBaseUrlByProductFlavor(): String {
            if (BuildConfig.IS_PROD) {
                //For prod environment
                return "https://picsum.photos/"
            } else {
                //For Dev environment
                return "https://picsum.photos/"
            }
        }
    }
}