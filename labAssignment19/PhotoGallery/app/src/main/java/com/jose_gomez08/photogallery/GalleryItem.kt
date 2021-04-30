package com.jose_gomez08.photogallery

import com.google.gson.annotations.SerializedName

data class GalleryItem(
    @SerializedName("title") var jgTitle: String = "",
    @SerializedName("id") var jgId: String = "",
    @SerializedName("url_s") var jgUrl: String = ""
)