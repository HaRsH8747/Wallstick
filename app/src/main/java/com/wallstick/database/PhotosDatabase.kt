package com.wallstick.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [LatestPhoto::class, TrendingTag::class, TrendingPhoto::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PhotosDatabase: RoomDatabase() {

    abstract fun photoDao(): PhotoDao

    companion object{
        @Volatile
        var INSTANCE: PhotosDatabase? = null

        fun getDatabase(context: Context): PhotosDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhotosDatabase::class.java,
                    "photos_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return  instance
            }
        }
    }
}