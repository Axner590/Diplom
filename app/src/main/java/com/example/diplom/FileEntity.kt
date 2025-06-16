package com.example.diplom

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val size: Long?,
    val localPath: String?,
    val cloudPath: String,
    val type: String
)