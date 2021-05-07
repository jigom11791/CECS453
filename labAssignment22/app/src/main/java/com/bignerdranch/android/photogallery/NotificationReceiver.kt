package com.bignerdranch.android.photogallery

import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

private const val TAG = "NotificationReceiver"

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "received result $resultCode")
        if (resultCode != Activity.RESULT_OK) {
            //foreground activity canceled the broadcast
            return
        }

        val jgRequestCode = intent.getIntExtra(PollWorker.REQUEST_CODE, 0)
        val jgNotification: Notification? =
                intent.getParcelableExtra(PollWorker.NOTIFICATION)
        val jgNotificationManager = NotificationManagerCompat.from(context)
        if (jgNotification != null) {
            jgNotificationManager.notify(jgRequestCode, jgNotification)
        }
    }
}