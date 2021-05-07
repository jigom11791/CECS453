package com.bignerdranch.android.photogallery

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val TAG = "PollWorker"

class PollWorker(val jgContext: Context, workerParameters: WorkerParameters)
    : Worker(jgContext, workerParameters) {

    override fun doWork(): Result {
        val jgQuery = QueryPreferences.jgGetStoredQuery(jgContext)
        val jgLastResultId = QueryPreferences.jgGetLastResultId(jgContext)
        val jgItems: List<GalleryItem> = if (jgQuery.isEmpty()) {
            FlickrFetchr().jgFetchPhotosRequest()
                .execute()
                .body()
                ?.jgPhotos
                ?.jgGalleryItems
        } else {
            FlickrFetchr().jgSearchPhotosRequest(jgQuery)
                .execute()
                .body()
                ?.jgPhotos
                ?.jgGalleryItems
        } ?: emptyList<GalleryItem>()

        if (jgItems.isEmpty()) {
            return Result.success()
        }

        val jgResultId = jgItems.first().jgId
        if (jgResultId == jgLastResultId) {
            Log.i(TAG, "Got an old result: $jgResultId")
        } else {
            Log.i(TAG, "Got a new result: $jgResultId")
            QueryPreferences.jgSetLastResultId(jgContext, jgResultId)

            val jgIntent = PhotoGalleryActivity.jgNewIntent(jgContext)
            val jgPendingIntent = PendingIntent.getActivity(jgContext, 0, jgIntent, 0)

            val jgResources = jgContext.resources
            val jgNotification = NotificationCompat
                .Builder(jgContext, NOTIFICATION_CHANNEL_ID)
                .setTicker(jgResources.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(jgResources.getString(R.string.new_pictures_title))
                .setContentText(jgResources.getString(R.string.new_pictures_text))
                .setContentIntent(jgPendingIntent)
                .setAutoCancel(true)
                .build()

            jgShowBackgroundNotification(0, jgNotification)
        }

        return Result.success()
    }

    private fun jgShowBackgroundNotification(
        requestCode: Int,
        notification: Notification
    ) {
        val jgIntent = Intent(ACTION_SHOW_NOTIFICATION).apply {
            putExtra(REQUEST_CODE, requestCode)
            putExtra(NOTIFICATION, notification)
        }

        jgContext.sendOrderedBroadcast(jgIntent, PERM_PRIVATE)
    }

    companion object {
        const val ACTION_SHOW_NOTIFICATION =
            "com.bignerdranch.android.photogallery.SHOW_NOTIFICATION"
        const val PERM_PRIVATE = "com.bignerdranch.android.photogallery.PRIVATE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val NOTIFICATION = "NOTIFICATION"
    }
}