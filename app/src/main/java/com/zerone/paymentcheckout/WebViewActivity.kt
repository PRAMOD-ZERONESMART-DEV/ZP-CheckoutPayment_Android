package com.zerone.paymentcheckout

import Globals
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.internal.ServiceSpecificExtraArgs.PlusExtraArgs
import com.zerone.paymentcheckout.databinding.WebLayoutBinding


class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: WebLayoutBinding
    private var WEB_URL: String? = ""
    private var IS_HOME_HIT: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WebLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.hide()

        // Enable JavaScript (optional)
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportMultipleWindows(true)

        webSettings.pluginState = WebSettings.PluginState.ON
        webSettings.javaScriptCanOpenWindowsAutomatically = true;
        binding.webView.webChromeClient = WebChromeClient();
        binding.webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY;
        binding.webView.webViewClient = MyWebViewClient()


        // Get the URL from the Intent
        WEB_URL = intent.getStringExtra("url")

        if (WEB_URL != null) {
            // Load the URL
            binding.webView.loadUrl(WEB_URL!!)
        } else {
            Toast.makeText(this, "Not valid url or null", Toast.LENGTH_SHORT).show()
            finish()
            // Handle the case when the URL is not available
            // Display an error message or handle it in your desired way
        }
    }


    override fun onBackPressed() {
        // Handle back button press (optional)
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            val uri = Uri.parse(url)
            val path = uri.path

            Log.i("TAG_URI", "" + uri)
            Log.i("TAG_PATH", "" + path)

            handleUri(uri)

//            if (path?.contains(Globals.RETURN_URL, ignoreCase = true)!!) {
//                val status = uri.getQueryParameter("status")
//                val reason = uri.getQueryParameter("reason")
//                val code = uri.getQueryParameter("code")
//
//                if (status.equals("SUCCESS", ignoreCase = true)) {
//                    val txnId = uri.getQueryParameter("txnId")
//                    val bankRRN = uri.getQueryParameter("bankRRN")
//
//
//                } else {
//                    // Handle other status scenarios here (if needed)
//                }
//            }

        }

        // Optional: If you want to handle page loading errors, you can override the following method
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            // Handle the error here
        }
    }

    private fun isAppInstalled(packageName: String): Boolean {
        val pm: PackageManager = packageManager
        return try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun handleUri(uri: Uri?) {

        Log.i("TAG", "=====> url triggered ======>>>>>>  $uri");
        if (uri.toString().startsWith("phonepe")) {
            val packageName = "com.phonepe.app";
            val pm: PackageManager = applicationContext.packageManager
            val appStartIntent = pm.getLaunchIntentForPackage(packageName)
            if (null != appStartIntent) {
                startActivity(appStartIntent)
            } else {
                Toast.makeText(this, "App not installed!", Toast.LENGTH_SHORT).show()
            }
        } else if (checkURLContains(uri.toString())) {
            if (!IS_HOME_HIT) {

                val tranId = getTxnIdFromUrl(uri.toString())
                Log.i("TAG", "trans_id ===>>>>>>>  $tranId")
                if (tranId != null) {
                    IS_HOME_HIT = true
                    val intent = Intent(this, TransactionStatusActivity::class.java)
                    // intent.putExtra("status", "Success") // Replace with your actual status value
                    intent.putExtra(
                        "txnId",
                        tranId
                    ) // Replace with your actual transaction ID value
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Transaction id is null", Toast.LENGTH_SHORT).show()
                }

            }

        } else {
            Log.i("TAG", "not return url contains like ${Globals.RETURN_URL}")
        }
        // return false
    }

    fun checkURLContains(url: String): Boolean {
        return url.contains(Globals.RETURN_URL, ignoreCase = true)
    }

    fun getTxnIdFromUrl(url: String): String? {
        val uri = Uri.parse(url)
        return uri.getQueryParameter("txnId")
    }

}
