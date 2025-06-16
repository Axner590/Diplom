package com.example.diplom

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import com.google.api.services.drive.model.FileList
import com.google.api.services.drive.model.File as driveFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun checkNavigatorExists(storage: StorageInfo){
    // Checking if navigator for given storage exists
    if (storage.navigator == null){
        if (storage.isCloud){
            storage.navigator = GoogleDriveNavigator(storage)
        }else{
            storage.navigator = AndroidNavigator(storage)
        }
    }
}
abstract class FileSystemNavigator{
    var storage: StorageInfo
    var currentDirectory: File? = null

    // Functions to navigate (THEY HAVE TO CHANGE GLOBAL childrenFiles!)
    abstract suspend fun refresh()
    abstract suspend fun moveToFolder(folder: FileData)
    abstract suspend fun goBack()

    // Functions to open a file
    abstract fun openFile(context: Context, fileData: FileData)
    abstract fun getMimeType(file: File): String
    fun getType(): String{
        if (this is AndroidNavigator){
            return "AndroidNavigator"
        } else if(this is GoogleDriveNavigator){
            return "GoogleDriveNavigator"
        } else{
            return "Undefined"
        }
    }
    constructor(storage: StorageInfo){
        this.storage = storage
    }
}

class GoogleDriveNavigator : FileSystemNavigator{
    val googleDrive = storage.driveService
    override suspend fun refresh(){
        return withContext(Dispatchers.IO) {
            childrenFiles.clear()
            val query = "trashed = false" // We don't include files in trash bin

            val result = googleDrive!!.files().list().setQ(query).setSpaces("drive").setFields("files(id, name)").execute()
            result.files.forEach { item ->
                childrenFiles.add(FileData(item))
            }
        }
    }
    override suspend fun moveToFolder(folder: FileData) {
        childrenFiles.clear()
        val query = "${folder.gdFolderID} in parents and trashed = false" // find files with folder in parents
        val result = googleDrive!!.files().list().setQ(query).setSpaces("drive").setFields("files(name, size)").execute()
        result.files.forEach { item ->
            childrenFiles.add(FileData(item))
        }
    }
    override suspend fun goBack() {
        return withContext(Dispatchers.IO) {
            childrenFiles.clear()
            val query = "trashed = false" // We don't include files in trash bin

            val result = googleDrive!!.files().list().setQ(query).setSpaces("drive").setFields("files(id, name)").execute()
            result.files.forEach { item ->
                childrenFiles.add(FileData(item))
            }
        }
    }

    override fun openFile(context: Context, fileData: FileData) {
        TODO("Not yet implemented")
    }

    override fun getMimeType(file: File): String {
        TODO("Not yet implemented")
    }

    constructor(storage: StorageInfo) : super(storage){
        currentDirectory = File(storage.rootDir)
        childrenFiles.clear()
        currentDirectory!!.listFiles()?.forEach{ item ->
            childrenFiles.add(FileData(item))
        }
    }
}

class AndroidNavigator : FileSystemNavigator{
    override suspend fun refresh() {
        // Refresh current directory children files
        childrenFiles.clear()
        currentDirectory!!.listFiles()?.forEach{ item ->
            childrenFiles.add(FileData(item))
        }
    }
    override suspend fun moveToFolder(folder: FileData){
        // Get File from fileData
        val file: File = File(folder.localPath)
        // Update current directory
        currentDirectory = file
        // Update the childrenFiles list to the clicked directory files
        childrenFiles.clear()
        file.listFiles()?.forEach{ item ->
            childrenFiles.add(FileData(item))
        }
    }
    override suspend fun goBack() {
        // Update the current folder to parent directory
        if (currentDirectory != null &&
            currentDirectory!!.path != "/storage/emulated/0" ){
            currentDirectory = currentDirectory!!.parentFile!!
            childrenFiles.clear()
            currentDirectory!!.listFiles()?.forEach{ item ->
                childrenFiles.add(FileData(item))
            }
        }
    }

    override fun openFile(context: Context, fileData: FileData){
        // FileData -> File
        val file = File(fileData.localPath)
        val uri = FileProvider.getUriForFile(context,"${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, getMimeType(file))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try{
            context.startActivity(intent)
        } catch(e: Exception){
            Toast.makeText(context, "Ошибка при открытии файла", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getMimeType(file: File): String {
        return when (file.extension.lowercase()){
            "pdf" -> "application/pdf"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "txt" -> "txt/plain"
            "mp4" -> "video/mp4"
            else -> "*/*"
        }
    }
    constructor(storage: StorageInfo) : super(storage){
        currentDirectory = File(storage.rootDir)
        childrenFiles.clear()
        currentDirectory!!.listFiles()?.forEach{ item ->
            childrenFiles.add(FileData(item))
        }
    }
}