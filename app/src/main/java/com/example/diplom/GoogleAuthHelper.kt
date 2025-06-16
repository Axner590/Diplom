package com.example.diplom

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes

class GoogleAuthHelper(private val activity: Activity, private val launcher: ActivityResultLauncher<Intent>){
    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(activity, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Scope(DriveScopes.DRIVE))
        .build())

    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    fun handleSignInResult(result: Intent?, onSuccess: (GoogleSignInAccount) -> Unit) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result)
            val account = task.getResult(ApiException::class.java)
            onSuccess(account)
        } catch (e: ApiException) {
            Log.e("GoogleAuthHelper", "Ошибка входа: ${e.statusCode}")
        }
    }
}