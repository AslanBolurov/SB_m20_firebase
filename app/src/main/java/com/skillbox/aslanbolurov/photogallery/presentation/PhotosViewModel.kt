package com.skillbox.aslanbolurov.photogallery.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillbox.aslanbolurov.photogallery.data.Photo
import com.skillbox.aslanbolurov.photogallery.data.PhotoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhotosViewModel(
    val photoDao: PhotoDao
) : ViewModel() {

    init {
//        loadPhotoList()

    }


    private var _flowPhotosList= MutableStateFlow<List<Photo>> (emptyList())
    val flowPhotosList=_flowPhotosList.asStateFlow()

//    val allPhotos=photoDao.getAll()

//    fun loadPhotoList(){
//        _flowPhotosList.value=allPhotos
//    }


    fun refreshloadPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                photoDao.getAll()
            }.fold(
                onSuccess = {
                    _flowPhotosList.value = it
                },
                onFailure = {
                    Log.d("PhotosViewModel", "loadPhotos: ${it.message ?: ""}")
                }
            )

        }
    }





}