package com.example.diplom

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class FileViewModel(
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _files = mutableStateOf<List<FileEntity>>(emptyList())
    val files: State<List<FileEntity>> = _files

    fun loadLocalFiles() {
        viewModelScope.launch {
            _files.value = fileRepository.getLocalFiles()
        }
    }

    fun syncFromCloud() {
        viewModelScope.launch {
            fileRepository.syncWithCloud()
            loadLocalFiles()
        }
    }
}
