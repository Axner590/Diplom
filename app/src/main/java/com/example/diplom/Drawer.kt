package com.example.diplom

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color

var currentStorage: MutableState<StorageInfo?> = mutableStateOf(null)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Drawer(googleAuthorizer: () -> Unit, dropboxAuthorizer: () -> Unit){

    val drawerStateBool = rememberDrawerState(DrawerValue.Closed) // Состояние бокового меню
    val coroutineScope = rememberCoroutineScope()
    val showAddStorageDialog = remember { mutableStateOf(false) } // Состояние окна добавления хранилища
    ModalNavigationDrawer(
        drawerState = drawerStateBool,
        drawerContent = {
            Column(modifier = Modifier
                .fillMaxHeight()
                .width(280.dp)
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
            ){
                storages.forEach {
                    // Calling chooseStorage fun upon clicking on DrawerItem
                        item -> DrawerItem(item.name, Icons.AutoMirrored.Filled.ArrowForward, item) { chooseStorage(drawerStateBool, coroutineScope, item)} }
                DrawerItem("", Icons.Default.Add, null) { showAddStorageDialog.value = true }
            }
        },
        content = {
            MainScreen(drawerStateBool, coroutineScope)
        }
    )
    if (showAddStorageDialog.value){
        NewStorageTypeDialog(
            onGoogleSelected = {
                addGoogleStorage(drawerStateBool, coroutineScope, googleAuthorizer)
            },
            onDropboxSelected = {
                addDropboxStorage(drawerStateBool, coroutineScope, dropboxAuthorizer)
            },
            onDismiss = {
                showAddStorageDialog.value = false}
        )
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun chooseStorage(drawerStateBool: DrawerState, coroutineScope: CoroutineScope, storage: StorageInfo){
    // Closing the menu
    coroutineScope.launch { drawerStateBool.close() }
    // Open the
    currentStorage.value = storage
    // Заставляем навигатор обновить текущие файлы
    checkNavigatorExists(storage)
    GlobalScope.launch { currentStorage.value?.navigator?.refresh() ?: checkNavigatorExists(storage) }
}

@Composable
fun DrawerItem(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, storage: StorageInfo?, onClick: () -> Unit){
    var background = if(storage == currentStorage.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
            .background(background, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(imageVector = icon, contentDescription = text, tint = MaterialTheme.colorScheme.onPrimaryContainer)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
fun NewStorageTypeDialog(
    // Функции, вызываемые при выборе каждого из вариантов
    onGoogleSelected: () -> Unit,
    onDropboxSelected: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить хранилище") },
        text = { Text("Выберите тип облачного хранилища") },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Вариант Google Drive
                Button(
                    onClick = {
                        onGoogleSelected()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0061FF),
                        contentColor = Color.White
                    )
                ) {
                    Text("Google Drive")
                }

                // Вариант Dropbox
                Button(
                    onClick = {
                        onDropboxSelected()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray,
                        contentColor = Color.White
                    )
                ) {
                    Text("Dropbox")
                }

                // Кнопка отмены
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Отмена")
                }
            }
        }
    )
}

fun addGoogleStorage(drawerStateBool: DrawerState, coroutineScope: CoroutineScope, googleAuthorizer: () -> Unit){
    // Closing the menu
    coroutineScope.launch { drawerStateBool.close() }
    // Initiate Auth
    googleAuthorizer()
}

fun addDropboxStorage(drawerStateBool: DrawerState, coroutineScope: CoroutineScope, dropboxAuthorizer: () -> Unit){
    // Closing the menu
    coroutineScope.launch {drawerStateBool.close()}
    // Initiate Auth
    dropboxAuthorizer()
}