package com.bignerdranch.android.photogallery

import android.app.Application
import androidx.lifecycle.*

class PhotoGalleryViewModel(private val jgApp: Application) : AndroidViewModel(jgApp) {
    val jgGalleryItemLiveData: LiveData<List<GalleryItem>>

    private val jgFlickrFetchr = FlickrFetchr()
    private val jgMutableSearchTerm = MutableLiveData<String>()
    val jgSearchTerm: String
        get() = jgMutableSearchTerm.value ?: ""
    init {
        jgMutableSearchTerm.value = QueryPreferences.jgGetStoredQuery(jgApp)
        jgGalleryItemLiveData = Transformations.switchMap(jgMutableSearchTerm) { KTsearchTerm ->
            if (KTsearchTerm.isBlank()) {
                jgFlickrFetchr.jgFetchPhotos()
            } else {
                jgFlickrFetchr.jgSearchPhotos(KTsearchTerm)
            }
        }
    }

    fun jgFetchPhotos(query: String = "") {
        QueryPreferences.jgSetStoredQuery(jgApp, query)
        jgMutableSearchTerm.value = query
    }

}