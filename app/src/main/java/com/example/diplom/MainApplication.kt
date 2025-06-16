package com.example.diplom

import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateListOf
import com.example.diplom.ui.theme.DiplomTheme
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

var storages = mutableStateListOf<StorageInfo>()
var accounts = mutableListOf<GoogleSignInAccount>()
var driveServices = mutableListOf<Drive>()
class MainApplication : Application() {
    // БД
    val database by lazy { AppDatabase.create(this) }

    fun checkForPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        }
    }
    override fun onCreate() {
        super.onCreate()
        // Checking permissions for android 10+
        checkForPermissions()
        checkForStorages()
    }
}