package com.bignerdranch.android.photogallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

class PhotoGalleryViewModel(private val jgApp: Application) : AndroidViewModel(jgApp) {

    val jgGalleryItemLiveData: LiveData<List<GalleryItem>>

    private val jgFlickrFetchr = FlickrFetchr()
    private val jgMutableSearchTerm = MutableLiveData<String>()

    init {

        jgMutableSearchTerm.value = QueryPreferences.jgGetStoredQuery(jgApp)

        jgGalleryItemLiveData =
                Transformations.switchMap(jgMutableSearchTerm) { searchTerm ->
                    if (searchTerm.isBlank()) {
                        jgFlickrFetchr.jgFetchPhotos()
                    } else {
                        jgFlickrFetchr.jgSearchPhotos(searchTerm)
                    }
                }
    }

    fun jgFetchPhotos(query: String = "") {
        QueryPreferences.jgSetStoredQuery(jgApp, query)
        jgMutableSearchTerm.value = query
    }
}