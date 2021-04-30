package com.jose_gomez08.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {

    private lateinit var jgPhotoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var jgPhotoRecyclerView: RecyclerView
    private lateinit var jgThumbnailDownloader: ThumbnailDownloader<JGPhotoHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        jgPhotoGalleryViewModel =
            ViewModelProviders.of(this).get(PhotoGalleryViewModel::class.java)

        val jgResponseHandler = Handler()
        jgThumbnailDownloader =
            ThumbnailDownloader(jgResponseHandler) { photoHolder, bitmap ->
                val jgDrawable = BitmapDrawable(resources, bitmap)
                photoHolder.jgBindDrawable(jgDrawable)
            }
        lifecycle.addObserver(jgThumbnailDownloader.jgFragmentLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewLifecycleOwner.lifecycle.addObserver(
            jgThumbnailDownloader.jgViewLifecycleObserver
        )
        val jgView = inflater.inflate(R.layout.fragment_photo_gallery, container, false)

        jgPhotoRecyclerView = jgView.findViewById(R.id.photo_recycler_view)
        jgPhotoRecyclerView.layoutManager = GridLayoutManager(context, 3)

        return jgView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        jgPhotoGalleryViewModel.jgGalleryItemLiveData.observe(
            viewLifecycleOwner,
            Observer { galleryItems ->
                Log.d(TAG, "Have gallery items from view model $galleryItems")
                jgPhotoRecyclerView.adapter = JGPhotoAdapter(galleryItems)
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(
            jgThumbnailDownloader.jgViewLifecycleObserver
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(
            jgThumbnailDownloader.jgFragmentLifecycleObserver
        )
    }

    private class JGPhotoHolder(private val jgItemImageView: ImageView)
        : RecyclerView.ViewHolder(jgItemImageView) {

        val jgBindDrawable: (Drawable) -> Unit = jgItemImageView::setImageDrawable
    }

    private inner class JGPhotoAdapter(private val galleryItems: List<GalleryItem>)
        : RecyclerView.Adapter<JGPhotoHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): JGPhotoHolder {
            val jgView = layoutInflater.inflate(
                R.layout.list_item_gallery,
                parent,
                false
            ) as ImageView
            return JGPhotoHolder(jgView)
        }

        override fun getItemCount(): Int = galleryItems.size

        override fun onBindViewHolder(holder: JGPhotoHolder, position: Int) {
            val jgGalleryItem = galleryItems[position]
            val jgPlaceHolder: Drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bill_up_close
            )?: ColorDrawable()
            holder.jgBindDrawable(jgPlaceHolder)
            jgThumbnailDownloader.jgQueueThumbnail(holder, jgGalleryItem.jgUrl)
        }
    }

    companion object {
        fun jgNewInstance() = PhotoGalleryFragment()
    }
}