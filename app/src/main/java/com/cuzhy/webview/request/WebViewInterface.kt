package com.cuzhy.webview.request

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.cuzhy.webview.MainActivity

class WebViewInterface(private val mContext: Context) {
    @JavascriptInterface
    fun showDialog(message: String) {
        Log.e("WebViewInterface", "showToast")
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun saveImage(imageUrl: String) {
        Log.e("TEST", imageUrl);
        (mContext as MainActivity).download(imageUrl);
    }

    @JavascriptInterface
    fun choosePhoto(): String {
        val file = "test"
        (mContext as MainActivity).photoPicker();
        return file
    }
}