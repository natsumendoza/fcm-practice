package com.rr.example.fcmpractice

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

private const val TAG = "MessagingService";
private const val NOTIFICATION_CHANNEL_ID = "101"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

//        Log.d(TAG, "onMessageReceived: From: ${remoteMessage.from} ")
        Log.d(TAG, "onMessageReceived: Topic: ${remoteMessage.data}")

        getImage(remoteMessage)
    }

    override fun onNewToken(refreshedToken: String) {
        sendRegistrationToServer(refreshedToken)
    }

    private fun sendRegistrationToServer(refreshedToken: String) {

    }

    private val target = object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            Log.d(TAG, "onBitmapLoaded: ")

        }

    }

    private fun sendNotification(bitmap: Bitmap?) {

//        val style = NotificationCompat.BigPictureStyle()
//        style.bigPicture(bitmap)

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Notification",
                NotificationManager.IMPORTANCE_DEFAULT)

            // Configure Notification Channel
            notificationChannel.description = "Game Notification"
            notificationChannel.enableLights(true)
            notificationChannel.vibrationPattern = listOf<Long>(0, 1000, 500, 1000).toLongArray()
            notificationChannel.enableVibration(true)

            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(Config.title)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentText(Config.content)
            .setContentIntent(pendingIntent)
//            .setStyle(style)
            .setLargeIcon(bitmap)
            .setWhen(System.currentTimeMillis())
            .setPriority(NotificationManager.IMPORTANCE_MAX)

        notificationManager.notify(1, notificationBuilder.build())

    }

    private fun getImage(remoteMessage: RemoteMessage) {

        val data: Map<String, String> = remoteMessage.data
        Config.title = data["title"].toString()
        Config.content = data["content"].toString()
        Config.imageUrl = data["imageUrl"].toString()
        Config.gameUrl = data["gameUrl"].toString()

        Log.d(TAG, "getImage: ${Config.gameUrl}")

        sendNotification(null)

        val uiHandler = Handler(Looper.getMainLooper())
        uiHandler.post {
            Log.d(TAG, "getImage: post")
            Picasso.get()
                .load(Config.imageUrl)
                .into(target)
        }

    }
}