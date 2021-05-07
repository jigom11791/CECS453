package com.bignerdranch.android.photogallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bignerdranch.android.photogallery.api.FlickrApi
import com.bignerdranch.android.photogallery.api.FlickrResponse
import com.bignerdranch.android.photogallery.api.PhotoInterceptor
import com.bignerdranch.android.photogallery.api.PhotoResponse
import okhttp3.OkHttpClient
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
        val jgClient = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()

        val jgRetrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(jgClient)
            .build()

        jgFlickrApi = jgRetrofit.create(FlickrApi::class.java)
    }

    fun jgFetchPhotosRequest(): Call<FlickrResponse> {
        return jgFlickrApi.jgFetchPhotos()
    }

    fun jgFetchPhotos(): LiveData<List<GalleryItem>> {
        return jgFetchPhotoMetdadata(jgFetchPhotosRequest())
    }

    fun jgSearchPhotosRequest(query: String): Call<FlickrResponse> {
        return jgFlickrApi.jgSearchPhotos(query)
    }

    fun jgSearchPhotos(query: String): LiveData<List<GalleryItem>> {
        return jgFetchPhotoMetdadata(jgSearchPhotosRequest(query))
    }

    private fun jgFetchPhotoMetdadata(flickrRequest: Call<FlickrResponse>)
            : LiveData<List<GalleryItem>>{
        val jgResponseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()

        flickrRequest.enqueue(object : Callback<FlickrResponse> {

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
                    it.url.isNullOrBlank()
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