package com.galent.ch20_firebase

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.RemoteInput

class ReplyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 알림 사용자 입력(RemoteInput)의 Intent(PendingIntent)에서 입력(key_text_reply) 값 가져옴
        val replyTxt = RemoteInput.getResultsFromIntent(intent)?.getCharSequence("key_text_reply")
        Log.d("25android","replyText:$replyTxt")

        // 해당 알림 제거
        val manager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE)
                as NotificationManager
        manager.cancel(11)  // MyNotificationHelper의 ID:11 알림 완료되었으므로
    }

}