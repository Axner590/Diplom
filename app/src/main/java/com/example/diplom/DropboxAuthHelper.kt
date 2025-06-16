package com.example.diplom

import android.content.Context
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth
import com.dropbox.core.v2.DbxClientV2

class DropboxAuthHelper(private val context: Context) {
    val APP_KEY = "31vnmvok6a0d7a8"

    private val config = DbxRequestConfig("Hermes_File_Manager_Diplom")
    var client: DbxClientV2? = null
        private set

    fun startAuth() {
        Auth.startOAuth2PKCE(context, APP_KEY, config)
    }

    fun handleAuthResult(): Boolean {
        val authToken = Auth.getOAuth2Token()
        if (authToken != null) {
            client = DbxClientV2(config, authToken)
            return true
        }
        return false
    }
}