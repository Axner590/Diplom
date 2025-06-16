package com.example.diplom

import java.io.File

class FileData {
    var name: String = "UNDEFINED NAME"
    var localPath: String = "UNDEFINED PARENT"
    var isFolder: Boolean = false
    var isCloud: Boolean = false
    var size: Long? = 0
    var gdFolderID: String = "" // IF FILE IS A FOLDER ON GOOGLE DRIVE
    constructor(file: File){
        name = file.name
        localPath = file.canonicalPath
        isFolder = file.isDirectory
        isCloud = false
        size = file.length()
    }

    constructor(file: com.google.api.services.drive.model.File){
        name = file.name
        localPath = ""
        isFolder = file.mimeType == "application/vnd.google-apps.folder"
        isCloud = true
        gdFolderID = file.id
    }

    constructor(special: String){
        if (special == "back_button"){
            name = "..."
            isFolder = true
        }
    }
}