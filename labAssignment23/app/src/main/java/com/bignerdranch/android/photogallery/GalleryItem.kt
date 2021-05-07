package com.bignerdranch.android.photogallery

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class GalleryItem(
    @SerializedName("title") var jgTitle: String = "",
    @SerializedName("id") var jgId: String = "",
    @SerializedName("url_s") var url: String = "",
    @SerializedName("owner") var owner: String = ""
) {
    val jgPhotoPageUri: Uri
        get() {
            return Uri.parse("https://www.flickr.com/photos/")
                    .buildUpon()
                    .appendPath(owner)
                    .appendPath(jgId)
                    .build()
        }
}