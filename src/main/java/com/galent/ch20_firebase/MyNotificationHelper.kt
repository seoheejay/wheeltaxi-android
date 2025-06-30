package com.galent.ch20_firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat.getSystemService
import kotlin.jvm.java

class MyNotificationHelper(private val context:Context) {

    fun showNotification(title: String?, message: String?){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder

        // Android 8.0 이상 Notification Channel 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "one-channel"
            val channelName = "FCM Channel"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
            builder = NotificationCompat.Builder(context, channelId)
        }
        else {
            builder = NotificationCompat.Builder(context)
        }

        builder.run{
            setSmallIcon(R.drawable.ic_stat_ic_notification)
            setWhen(System.currentTimeMillis())
            setContentTitle(title)
            setContentText(message)
            setAutoCancel(true)
        }

        val remoteInput: RemoteInput = RemoteInput.Builder("key_text_reply").run{
            setLabel("답장")
            build()
        }
        val replyPendingIntent = PendingIntent.getBroadcast(
            context,
            30,
            Intent(context, ReplyReceiver::class.java),
            PendingIntent.FLAG_MUTABLE
            )

        builder.addAction(
            NotificationCompat.Action.Builder(R.drawable.ic_stat_ic_notification, " 답장", replyPendingIntent)
                .addRemoteInput(remoteInput).build()
        )

        notificationManager.notify(11, builder.build())

    }
}