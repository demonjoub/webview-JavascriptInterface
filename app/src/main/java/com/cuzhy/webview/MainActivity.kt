package com.cuzhy.webview

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cuzhy.webview.request.WebViewInterface

const val TAG: String = "MainActivity";
class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView;
    private lateinit var button: Button;
    private lateinit var reload: Button;

    private lateinit var managePermissions: ManagePermissions;
    private val PermissionsRequestCode = 123

    private val SELECT_PHOTO = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val list = listOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        managePermissions = ManagePermissions(this, list, PermissionsRequestCode)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) managePermissions.checkPermissions()

        setupWebView();
        setupButton();
    }

    private fun setupButton() {
        button = findViewById(R.id.webViewButton);
        button.setOnClickListener{ view ->
            Log.e(TAG, "Call javascript function")
            webView.loadUrl("javascript:increment()")
        }

        reload = findViewById(R.id.reload);
        reload.setOnClickListener { view ->
            webView.reload();
        }
    }

    private fun setupWebView() {
        webView = findViewById(R.id.webView1);
        webView.webViewClient = MyWebChromeClient();

//        webView.loadUrl("https://web.dmarket-dev.cleverse.com/")
        webView.loadUrl("http://10.0.2.2:9000/")
        webView.settings.javaScriptEnabled = true;
        webView.settings.domStorageEnabled = true;
        webView.settings.builtInZoomControls = true;
        webView.settings.mediaPlaybackRequiresUserGesture = false;
        webView.addJavascriptInterface(WebViewInterface(this), "Native");
        registerForContextMenu(webView);

    }

    fun download(imageUrl: String) {
        if (managePermissions.isPermissionsGranted() == PackageManager.PERMISSION_GRANTED) {
            if (URLUtil.isNetworkUrl(imageUrl)) {
                if (imageUrl != null) {
                    val request = DownloadManager.Request(Uri.parse(imageUrl))
                    request.setDescription("Downloading requested image....")

                    request.allowScanningByMediaScanner()

                    request.setNotificationVisibility(
                            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                    val fileName = URLUtil.guessFileName(imageUrl, null, null)
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    request.setTitle("Image Download : $fileName")

                    val dManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                    dManager.enqueue(request)

                    toast("Image saved.")
                };
            } else {
                toast("Invalid image url.")
            }
        } else {
            toast("Please open permissions");
        }

    }

    fun photoPicker () {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, SELECT_PHOTO)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    Log.e("WebViewInterface", data.toString());
                    val selectedImage: Uri? = data?.data
                    Log.e("WebViewInterface SELECTED IMAGE", selectedImage.toString());
                    webView.loadUrl("javascript:setFileUri('" + selectedImage.toString() + "')");
                    val path = getRealPathFromURI(this, selectedImage!!)
                    Log.e("WebViewInterface PATH", path.toString());
                    webView.loadUrl("javascript:setFilePath('" + path + "')");

                }
            }
        }
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri):String {
        var cursor:Cursor? = null;
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null);
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor!!.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }


//    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
//        super.onCreateContextMenu(menu, v, menuInfo)
//        var result = webView.hitTestResult;
//        if (result.type == WebView.HitTestResult.IMAGE_TYPE || result.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
//            Log.e(TAG, "test")
//            if (menu != null) {
//                menu.setHeaderTitle("test")
//                menu.add(0, 1, 0, "Save Image").setOnMenuItemClickListener {
//                    val imgUrl = result.extra;
//                    if (URLUtil.isNetworkUrl(imgUrl)) {
//                        if (managePermissions.isPermissionsGranted() == PackageManager.PERMISSION_GRANTED) {
//                            if (imgUrl != null) {
//                                download(imgUrl)
//                                toast("Image saved.")
//                            };
//                        } else {
//                            managePermissions.checkPermissions()
//                            toast("Grant permission(s) & try again.")
//                        }
//                    } else {
//                        toast("Invalid image url.")
//                    }
//                    false
//                }
//            }
//        } else {
//            toast("App can't handle target type.")
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

class MyWebChromeClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
    }

    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)
    }
}

