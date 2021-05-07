package com.bignerdranch.android.photogallery.api

import okhttp3.Interceptor
import okhttp3.Response

//https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=7cce343472e86d4e9e226a422121af12&format=json&nojsoncallback=1&extras=url_s

private const val API_KEY = "7cce343472e86d4e9e226a422121af12"

class PhotoInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val jgRequest = chain.request()
        // Add necessary query parameters to all Flickr calls
        val jgNewUrl = jgRequest.url().newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .addQueryParameter("format", "json")
                .addQueryParameter("nojsoncallback", "1")
                .addQueryParameter("extras", "url_s")
                .build()
        // Build new request using new Url
        val jgNewRequest = jgRequest.newBuilder()
                .url(jgNewUrl)
                .build()

        return chain.proceed(jgNewRequest)
    }
}