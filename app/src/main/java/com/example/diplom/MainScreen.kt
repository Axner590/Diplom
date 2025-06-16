package com.example.diplom
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.diplom.ui.theme.DiplomTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(drawerStateBool: DrawerState, coroutineScope: CoroutineScope){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hermes File Manager") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!drawerStateBool.isOpen){
                            coroutineScope.launch{
                                drawerStateBool.open() // ✅ Открыть меню по нажатию
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Open Drawer")
                    }
                }
            )
        },
        content = { paddingValues ->
            FilesList(modifier = Modifier.padding(paddingValues))
        }
    )
}

// Обновление переменной происходит в навигаторах
var childrenFiles = mutableStateListOf<FileData>()
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun FilesList(modifier: Modifier = Modifier){
    val storage by currentStorage
    val context = LocalContext.current
    if(currentStorage.value != null) checkNavigatorExists(currentStorage.value!!)
    // Достаем текущие файлы на пути, преобразуя их в FileData
    val filesList = remember { childrenFiles}
    val backButton = FileData("back_button")
    val childrenFoldersWithBackButton = listOf(backButton) + filesList
    LazyColumn(modifier = modifier){
        items(childrenFoldersWithBackButton) { file ->
            FileRow(name = file.name, onClick = {
                if (file.name == "...") {
                    GlobalScope.launch { storage?.navigator!!.goBack() }
                } else {
                    if(file.isFolder){
                        GlobalScope.launch { storage?.navigator!!.moveToFolder(file)  }
                    }
                    else{
                        // Try to open the file
                        storage?.navigator!!.openFile(context, file)
                    }
                }
            })
        }
    }
}


@Composable
fun FileRow(name: String, onClick:() -> Unit){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 8.dp)
            .clickable { onClick() },
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = name, modifier = Modifier
            .padding(vertical = 4.dp))
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
    }
}