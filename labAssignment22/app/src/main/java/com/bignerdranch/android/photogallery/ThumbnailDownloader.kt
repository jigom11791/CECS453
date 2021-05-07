package com.bignerdranch.android.photogallery

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.ConcurrentHashMap

private const val JG_TAG = "ThumbnailDownloader"
private const val JG_MESSAGE_DOWNLOAD = 0

class ThumbnailDownloader<in T> (
    private val jgResponseHandler: Handler,
    private val jgOnThumbnailDownloaded: (T, Bitmap) -> Unit
): HandlerThread(JG_TAG){

    val jgFragmentLifecycleObserver: LifecycleObserver =
        object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun setup() {
                Log.i(JG_TAG, "Starting background thread")
                start()
                looper
            }
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun tearDown() {
                Log.i(JG_TAG, "Destroying background thread")
                quit()
            }
        }

    val jgViewLifecycleObserver: LifecycleObserver =
        object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun clearQueue() {
                Log.i(JG_TAG, "Clearing all requests from queue")
                jgRequestHandler.removeMessages(JG_MESSAGE_DOWNLOAD)
                jgRequestMap.clear()
            }
        }

    private var jgHasQuit = false
    private lateinit var jgRequestHandler: Handler
    private val jgRequestMap = ConcurrentHashMap<T, String>()
    private val jgFlickrFetchr = FlickrFetchr()

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        jgRequestHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == JG_MESSAGE_DOWNLOAD) {
                    val jgTarget = msg.obj as T
                    Log.i(JG_TAG, "Got a request for URL: ${jgRequestMap[jgTarget]}")
                    jgHandleRequest(jgTarget)
                }
            }
        }
    }

    override fun quit(): Boolean {
        jgHasQuit = true
        return super.quit()
    }

    fun jgQueueThumbnail(target: T, url: String) {
        Log.i(JG_TAG, "Got a URL: $url")
        jgRequestMap[target] = url
        jgRequestHandler.obtainMessage(JG_MESSAGE_DOWNLOAD, target)
            .sendToTarget()
    }

    private fun jgHandleRequest(target: T) {
        val jgUrl = jgRequestMap[target] ?: return
        val jgBitmap = jgFlickrFetchr.jgFetchPhoto(jgUrl) ?: return

        jgResponseHandler.post(Runnable {
            if (jgRequestMap[target] != jgUrl || jgHasQuit) {
                return@Runnable
            }
            jgRequestMap.remove(target)
            jgOnThumbnailDownloaded(target, jgBitmap)
        })
    }
}