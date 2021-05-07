package com.bignerdranch.android.photogallery.api

import com.google.gson.annotations.SerializedName

class FlickrResponse {
    @SerializedName("photos")
    lateinit var JGphotos: PhotoResponse
}