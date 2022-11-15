package com.skillbox.aslanbolurov.photogallery.data


import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.skillbox.aslanbolurov.photogallery.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class FcmService:FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notification= NotificationCompat.Builder(this,App.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(message.data["nickname"])
            .setContentText(message.data["message"]+convertToDate(message.data["timestamp"]))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(Random.nextInt(),notification)

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

    private fun convertToDate(timestamp:String?):String{

        timestamp ?: return ""
        val dateFormat= SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date(timestamp.toLong()*1_000))
    }

}