package com.rafsan.picsumphotoapp.di

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.rafsan.picsumphotoapp.BuildConfig
import com.rafsan.picsumphotoapp.data.local.ImageListDao
import com.rafsan.picsumphotoapp.data.local.ImageListDb
import com.rafsan.picsumphotoapp.network.api.ApiHelper
import com.rafsan.picsumphotoapp.network.api.ApiHelperImpl
import com.rafsan.picsumphotoapp.network.api.PicsumApi
import com.rafsan.picsumphotoapp.network.repository.ImageListRepository
import com.rafsan.picsumphotoapp.utils.Constants.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    private val TAG = "PicsumPhotoApp"

    @Provides
    @Singleton
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d(TAG, message)
            }
        })
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else OkHttpClient
        .Builder()
        .build()


    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun providePicsumApi(retrofit: Retrofit): PicsumApi = retrofit.create(PicsumApi::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        ImageListDb.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideImageListDao(db: ImageListDb) = db.getImageListDao()

    @Singleton
    @Provides
    fun provideRepository(
        remoteDataSource: ApiHelper,
        localDataSource: ImageListDao
    ) = ImageListRepository(remoteDataSource, localDataSource)
}