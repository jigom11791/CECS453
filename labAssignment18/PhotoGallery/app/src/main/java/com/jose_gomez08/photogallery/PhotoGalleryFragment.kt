package com.jose_gomez08.photogallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {

    private lateinit var jgPhotoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var jgPhotoRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        jgPhotoGalleryViewModel =
            ViewModelProviders.of(this).get(PhotoGalleryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

    private class JGPhotoHolder(itemTextView: TextView)
        : RecyclerView.ViewHolder(itemTextView) {

        val bindTitle: (CharSequence) -> Unit = itemTextView::setText
    }

    private class JGPhotoAdapter(private val galleryItems: List<GalleryItem>)
        : RecyclerView.Adapter<JGPhotoHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): JGPhotoHolder {
            val jgTextView = TextView(parent.context)
            return JGPhotoHolder(jgTextView)
        }

        override fun getItemCount(): Int = galleryItems.size

        override fun onBindViewHolder(holder: JGPhotoHolder, position: Int) {
            val jgGalleryItem = galleryItems[position]
            holder.bindTitle(jgGalleryItem.jgTitle)
        }
    }

    companion object {
        fun jgNewInstance() = PhotoGalleryFragment()
    }
}