package com.example.diplom
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(tableName = "storages",
    indices = [Index(value = ["email"], unique = true)])
data class StorageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val displayedName: String,
    val storageType: String, // "GoogleDrive" или "DropBox"
    val accessToken: String,
    val refreshToken: String,
    val firstName: String,
    val lastName: String,
    val email: String
)