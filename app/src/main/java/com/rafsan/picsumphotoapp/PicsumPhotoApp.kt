package com.rafsan.picsumphotoapp

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PicsumPhotoApp : Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: PicsumPhotoApp? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}