package com.galent.ch20_firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportMultipleWindows(true)
        webView.webChromeClient = WebChromeClient()
        webView.addJavascriptInterface(AndroidBridge(), "AndroidBridge")
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.settings.domStorageEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.addJavascriptInterface(AndroidBridge(), "AndroidBridge")


        webView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: android.os.Message): Boolean {
                val newWebView = WebView(this@WebViewActivity)
                newWebView.settings.javaScriptEnabled = true
                newWebView.webChromeClient = this
                val dialog = android.app.Dialog(this@WebViewActivity)
                dialog.setContentView(newWebView)
                dialog.show()

                (resultMsg.obj as WebView.WebViewTransport).webView = newWebView
                resultMsg.sendToTarget()
                return true
            }
        }
        //  assets/ daum_address.html 파일을 로드
        webView.loadUrl("file:///android_asset/daum_address.html")
    }

    inner class AndroidBridge {
        @JavascriptInterface
        fun processAddress(address: String) {
            Log.d("ADDRESS", "선택된 주소: $address")
            val intent = Intent().apply {
                putExtra("selectedAddress", address)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

}


