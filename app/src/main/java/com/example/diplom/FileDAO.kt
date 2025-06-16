package com.example.diplom

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {
    @Insert
    suspend fun insert(file: FileEntity)

    @Update
    suspend fun update(file: FileEntity)

    @Query("SELECT * FROM files WHERE cloudPath = :cloudPath")
    suspend fun getByCloudPath(cloudPath: String): FileEntity?

    @Query("SELECT * FROM files WHERE localPath IS NOT NULL")
    fun getDownloadedFiles(): Flow<List<FileEntity>>

    @Query("DELETE FROM files WHERE id = :id")
    suspend fun delete(id: Int)
}