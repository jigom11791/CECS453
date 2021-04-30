package com.jose_gomez08.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PhotoGalleryViewModel : ViewModel() {

    val jgGalleryItemLiveData: LiveData<List<GalleryItem>>

    init {
        jgGalleryItemLiveData = FlickrFetchr().jgFetchPhotos()
    }
}