package com.jose_gomez08.photogallery.api

import com.google.gson.annotations.SerializedName
import com.jose_gomez08.photogallery.GalleryItem

class PhotoResponse {
    @SerializedName("photo")
    lateinit var jgGalleryItems: List<GalleryItem>
}