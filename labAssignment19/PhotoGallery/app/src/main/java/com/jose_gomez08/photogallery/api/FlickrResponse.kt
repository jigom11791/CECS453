package com.jose_gomez08.photogallery.api

import com.google.gson.annotations.SerializedName

class FlickrResponse {
    @SerializedName("photos")
    lateinit var jgPhotos: PhotoResponse
}