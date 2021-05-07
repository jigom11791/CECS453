package com.bignerdranch.android.photogallery

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

const val NOTIFICATION_CHANNEL_ID = "flickr_poll"
class PhotoGalleryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val jgName = getString(R.string.notification_channel_name)
            val jgImportance = NotificationManager.IMPORTANCE_DEFAULT
            val jgChannel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, jgName, jgImportance)
            val jgnotificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            jgnotificationManager.createNotificationChannel(jgChannel)
        }
    }
}