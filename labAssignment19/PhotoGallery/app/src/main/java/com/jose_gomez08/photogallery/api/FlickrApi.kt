package com.jose_gomez08.photogallery.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

//https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=7cce343472e86d4e9e226a422121af12&format=json&nojsoncallback=1&extras=url_s

interface FlickrApi {

    @GET("services/rest/?method=flickr.interestingness.getList" +
            "&api_key=7cce343472e86d4e9e226a422121af12" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s")
    fun jgFetchPhotos(): Call<FlickrResponse>

    @GET
    fun jgFetchUrlBytes(@Url url: String): Call<ResponseBody>
}