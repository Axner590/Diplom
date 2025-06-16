package com.example.diplom

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context


@Database(
    entities = [FileEntity::class, StorageEntity::class],
    version = 1, // Начинаем с версии 1
    exportSchema = false // Если не нужна документация схемы
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fileDao(): FileDao
    abstract fun storageDao(): StorageDao

    companion object {
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "file_manager.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
