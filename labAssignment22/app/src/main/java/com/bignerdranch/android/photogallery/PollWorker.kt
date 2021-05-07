package com.bignerdranch.android.photogallery

import QueryPreferences
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

class PollWorker(val context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {

    override fun doWork(): Result {
        //Log.i(TAG, "Work request triggered")
        val jgQuery = QueryPreferences.jgGetStoredQuery(context)
        val jgLastResultId = QueryPreferences.jgGetLastResultId(context)
        val jgItems: List<GalleryItem> = if(jgQuery.isEmpty()) {
            FlickrFetchr().jgFetchPhotosRequest()
                .execute()
                .body()
                ?.JGphotos
                ?.jgGalleryItems
        }else {
            FlickrFetchr().jgSearchPhotosRequest(jgQuery)
                .execute()
                .body()
                ?.JGphotos
                ?.jgGalleryItems
        } ?: emptyList()

        if(jgItems.isEmpty()) {
            return Result.success()
        }

        val jgResultId = jgItems.first().jgId
        if(jgResultId == jgLastResultId) {
            Log.i(TAG, "Got an old result: ${jgResultId}")
        } else {
            Log.i(TAG, "Got a new result: ${jgResultId}")
            QueryPreferences.jgSetLastResultId(context, jgResultId)

            val jgIntent = PhotoGalleryActivity.jgNewIntent(context)
            val jgPendingIntent = PendingIntent.getActivity(context, 0, jgIntent, 0)
            val jgResources = context.resources
            val jgNotification = NotificationCompat
                .Builder(context, NOTIFICATION_CHANNEL_ID)
                .setTicker(jgResources.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(jgResources.getString(R.string.new_pictures_title))
                .setContentText(jgResources.getString(R.string.new_pictures_text))
                .setContentIntent(jgPendingIntent)
                .setAutoCancel(true)
                .build()

            // Show notification when the app is running
//            val jgNotificationManager = NotificationManagerCompat.from(context)
//            jgNotificationManager.notify(0, jgNotification)
//
//            context.sendBroadcast(Intent(ACTION_SHOW_NOTIFICATION), PERM_PRIVATE)
            jgShowBackgroundNotification(0, jgNotification)
        }
        return Result.success()
    }

    private fun jgShowBackgroundNotification(
            requestCode: Int,
            notification: Notification
    ) {
        val intent = Intent(ACTION_SHOW_NOTIFICATION).apply {
            putExtra(REQUEST_CODE, requestCode)
            putExtra(NOTIFICATION, notification)
        }
        context.sendOrderedBroadcast(intent, PERM_PRIVATE)
    }

    companion object {
        const val ACTION_SHOW_NOTIFICATION =
                "com.bignerdranch.android.photogallery.SHOW_NOTIFICATION"
        const val PERM_PRIVATE = "com.bignerdranch.android.photogallery.PRIVATE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val NOTIFICATION = "NOTIFICATION"
    }
}