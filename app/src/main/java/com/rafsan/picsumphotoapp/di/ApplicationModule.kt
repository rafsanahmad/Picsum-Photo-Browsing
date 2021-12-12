package com.rafsan.picsumphotoapp.di

import android.content.Context
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import com.google.gson.GsonBuilder
import com.rafsan.picsumphotoapp.BuildConfig
import com.rafsan.picsumphotoapp.data.db.ImageListDb
import com.rafsan.picsumphotoapp.data.db.dao.ImageListDao
import com.rafsan.picsumphotoapp.network.api.PicsumApi
import com.rafsan.picsumphotoapp.network.repository.ImageListRepositoryImpl
import com.rafsan.picsumphotoapp.utils.Constants.Companion.BASE_URL
import com.rafsan.picsumphotoapp.utils.Constants.Companion.TAG
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
@OptIn(ExperimentalPagingApi::class)
object ApplicationModule {

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

    @Singleton
    @Provides
    fun provideImageListDb(@ApplicationContext appContext: Context) =
        ImageListDb.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideImageListDao(db: ImageListDb) = db.getImageListDao()

    @Singleton
    @Provides
    fun provideRepository(
        picsumApi: PicsumApi,
        localDataSource: ImageListDao,
        imageListDb: ImageListDb
    ) = ImageListRepositoryImpl(picsumApi, localDataSource, imageListDb)
}