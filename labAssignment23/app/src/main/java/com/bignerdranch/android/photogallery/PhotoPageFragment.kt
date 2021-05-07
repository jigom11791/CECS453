package com.bignerdranch.android.photogallery

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

private const val ARG_URI = "photo_page_url"

class PhotoPageFragment : VisibleFragment() {

    private lateinit var jgUri: Uri
    private lateinit var jgWebView: WebView
    private lateinit var jgProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        jgUri = arguments?.getParcelable(ARG_URI) ?: Uri.EMPTY
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val jgView = inflater.inflate(R.layout.fragment_photo_page, container, false)

        jgProgressBar = jgView.findViewById(R.id.progress_bar)
        jgProgressBar.max = 100

        jgWebView = jgView.findViewById(R.id.web_view)
        jgWebView.settings.javaScriptEnabled = true
        jgWebView.webViewClient = WebViewClient()
        jgWebView.loadUrl(jgUri.toString())
        jgWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(webView: WebView, newProgress: Int) {
                if (newProgress == 100) {
                    jgProgressBar.visibility = View.GONE
                } else {
                    jgProgressBar.visibility = View.VISIBLE
                    jgProgressBar.progress = newProgress
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                (activity as AppCompatActivity).supportActionBar?.subtitle = title
            }
        }

        return jgView
    }

    companion object {
        fun newInstance(uri: Uri): PhotoPageFragment {
            return PhotoPageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_URI, uri)
                }
            }
        }
    }
}