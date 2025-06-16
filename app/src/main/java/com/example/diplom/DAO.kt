package com.example.diplom

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface StorageDao {
    @Insert
    suspend fun insert(storage: StorageEntity): Long

    @Query("SELECT * FROM storages WHERE id = :id")
    suspend fun getById(id: Int): StorageEntity?

    @Query("SELECT * FROM storages")
    fun getAll(): Flow<List<StorageEntity>>

    @Delete
    suspend fun delete(storage: StorageEntity)
}

@Dao
interface FileDao {
    @Insert
    suspend fun insert(file: FileEntity): Long

    @Update
    suspend fun update(file: FileEntity)

    @Query("SELECT * FROM files WHERE cloudPath = :cloudPath")
    suspend fun getByCloudPath(cloudPath: String): FileEntity?

    @Query("SELECT * FROM files WHERE localPath IS NOT NULL")
    fun getDownloadedFiles(): Flow<List<FileEntity>>

    @Query("DELETE FROM files WHERE id = :id")
    suspend fun delete(id: Int)
}