package com.galent.ch20_firebase

import android.os.Build
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kakao.sdk.common.KakaoSdk
import javax.net.ssl.SSLContext

class MyApplication : MultiDexApplication() {
    companion object{
        lateinit var auth : FirebaseAuth
        lateinit var db : FirebaseFirestore

        var email: String? = null

        fun checkAuth(): Boolean {
            val currentUser = auth.currentUser
            return currentUser?.let{
                email = currentUser.email
                if(currentUser.isEmailVerified){
                    true
                }
                else{
                    false
                }
            } ?:let {false}
        }
    }

    override fun onCreate() {
        super.onCreate()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        KakaoSdk.init(this, getString(R.string.kakao_native_key))

        //  TLS 1.2 강제 활성화 (Android 5.x 대응용)
        if (Build.VERSION.SDK_INT in 16..20) { // Android 4.1 ~ 4.4
            try {
                ProviderInstaller.installIfNeeded(applicationContext)
                val sslContext = SSLContext.getInstance("TLSv1.2")
                sslContext.init(null, null, null)
                sslContext.socketFactory
                Log.d("TLS", "TLS 1.2 활성화 성공")
            } catch (e: Exception) {
                Log.e("TLS", "TLS 1.2 활성화 실패", e)
            }
        }
    }
}