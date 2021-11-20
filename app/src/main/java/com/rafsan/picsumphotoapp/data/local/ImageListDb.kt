package com.rafsan.picsumphotoapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rafsan.picsumphotoapp.data.model.ImageListItem

@Database(
    entities = [ImageListItem::class],
    version = 1,
    exportSchema = false
)
abstract class ImageListDb : RoomDatabase() {

    abstract fun getImageListDao(): ImageListDao

    companion object {
        @Volatile
        private var instance: ImageListDb? = null

        fun getDatabase(context: Context): ImageListDb =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }

        //Build a local database to store data
        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, ImageListDb::class.java, "picsum_db")
                .fallbackToDestructiveMigration()
                .build()
    }
}