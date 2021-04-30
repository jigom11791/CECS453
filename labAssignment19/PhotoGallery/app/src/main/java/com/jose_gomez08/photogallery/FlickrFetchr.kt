package com.jose_gomez08.photogallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jose_gomez08.photogallery.api.FlickrApi
import com.jose_gomez08.photogallery.api.FlickrResponse
import com.jose_gomez08.photogallery.api.PhotoResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "FlickrFetchr"

class FlickrFetchr {

    private val jgFlickrApi: FlickrApi

    init {
        val jgRetrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        jgFlickrApi = jgRetrofit.create(FlickrApi::class.java)
    }

    fun jgFetchPhotos(): LiveData<List<GalleryItem>> {
        val jgResponseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        val jgFlickrRequest: Call<FlickrResponse> = jgFlickrApi.jgFetchPhotos()

        jgFlickrRequest.enqueue(object : Callback<FlickrResponse> {

            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t)
            }

            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                Log.d(TAG, "Response received")
                val jgFlickrResponse: FlickrResponse? = response.body()
                val jgPhotoResponse: PhotoResponse? = jgFlickrResponse?.jgPhotos
                var jgGalleryItems: List<GalleryItem> = jgPhotoResponse?.jgGalleryItems
                    ?: mutableListOf()
                jgGalleryItems = jgGalleryItems.filterNot {
                    it.jgUrl.isBlank()
                }
                jgResponseLiveData.value = jgGalleryItems
            }
        })

        return jgResponseLiveData
    }

    @WorkerThread
    fun jgFetchPhoto(url: String): Bitmap? {
        val jgResponse: Response<ResponseBody> = jgFlickrApi.jgFetchUrlBytes(url).execute()
        val jgBitmap = jgResponse.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.i(TAG, "Decoded bitmap=$jgBitmap from Response=$jgResponse")
        return jgBitmap
    }
}