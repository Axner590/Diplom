package com.example.diplom

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "files",
    foreignKeys = [ForeignKey(
        entity = StorageEntity::class,
        parentColumns = ["id"],
        childColumns = ["storage_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("storage_id")])
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val size: Long?,
    val localPath: String?,
    val cloudPath: String,
    val type: String,
    @ColumnInfo(name = "storage_id")
    val storageId: Int // FOREIGN KEY
)