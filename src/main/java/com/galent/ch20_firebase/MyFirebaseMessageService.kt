package com.galent.ch20_firebase

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("25android","MyFirebaseMessageService_onMessageReceived")
        if(message.data.isNotEmpty()){
            Log.d("25android","${message.data}")

            // 알림 보여주기
            val helper = MyNotificationHelper(this)
            helper.showNotification(message.data.get("title"),message.data.get("value"))
        }
    }
}