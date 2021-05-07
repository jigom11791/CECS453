package com.bignerdranch.android.photogallery

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PhotoPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_page)

        val jgfm = supportFragmentManager
        val jgCurrentFragment = jgfm.findFragmentById(R.id.fragment_container)

        if (jgCurrentFragment == null) {
            val jgFragment = PhotoPageFragment.newInstance(intent.data)
            jgfm.beginTransaction()
                .add(R.id.fragment_container, jgFragment)
                .commit()
        }
    }

    companion object {
        fun jgNewIntent(context: Context, photoPageUri: Uri): Intent {
            return Intent(context, PhotoPageActivity::class.java).apply {
                data = photoPageUri
            }
        }
    }
}