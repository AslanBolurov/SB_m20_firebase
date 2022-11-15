package com.skillbox.aslanbolurov.photogallery.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillbox.aslanbolurov.photogallery.data.NewPhoto
import com.skillbox.aslanbolurov.photogallery.data.PhotoDao
import kotlinx.coroutines.launch

class TakingPhotoViewModel(
    val photoDao: PhotoDao
):ViewModel() {
    fun insertNewPhoto(newPhoto: NewPhoto) {
        viewModelScope.launch {
            photoDao.insert(newPhoto)
        }
    }


}