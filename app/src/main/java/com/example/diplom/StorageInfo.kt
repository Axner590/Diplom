package com.example.diplom

import android.util.Log
import com.example.diplom.driveServices
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.services.drive.Drive
import java.io.File

class StorageInfo {
    var isCloud: Boolean = false
    var name: String
    var rootDir: String
    var driveService: Drive?
    var account: GoogleSignInAccount?

    var navigator: FileSystemNavigator? = null
    constructor(name: String, rootDir: String ,driveService: Drive?, account: GoogleSignInAccount?){
        if (account != null) this.isCloud = true
        this.name = name
        this.rootDir = rootDir
        this.driveService = driveService
        this.account = account
    }
    constructor(name: String, rootDir: String) : this(name, rootDir, null, null)
}

fun checkForStorages(){
    // Clear the list
    storages.clear()
    // Add external
    val extStorage = StorageInfo("Внутреннее хранилище", "/storage/emulated/0")
    storages.add(extStorage)
    currentStorage.value = extStorage
    // Now check for SD cards
    val sdCards = File("/storage/").listFiles()
    sdCards?.forEachIndexed { index, item ->
        if (item.absolutePath != "/storage/emulated/0"){
            storages.add(StorageInfo("SD-карта $index", item.absolutePath) )
        }
    }
    // Now check for saved cloud accounts
    accounts.forEachIndexed { index, item ->
        storages.add(StorageInfo(item.displayName!!, "root", driveServices[index] ,item))
    }
}