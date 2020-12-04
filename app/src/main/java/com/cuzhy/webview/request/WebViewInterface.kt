package com.cuzhy.webview.request

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cuzhy.webview.MainActivity
import java.io.File
import java.io.FileOutputStream
import java.net.URL

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
}