package com.bignerdranch.android.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.photogallery.api.FlickrApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val JG_TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {
    private lateinit var jgPhotoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var jgPhotoRecyclerView: RecyclerView
    private lateinit var jgThumbnailDownloader: ThumbnailDownloader<JGPhotoHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)

        val jgSearchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        val jgSearchView = jgSearchItem.actionView as SearchView
        jgSearchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(queryText: String): Boolean {
                    Log.d(JG_TAG, "QueryTextSubmit: $queryText")
                    jgPhotoGalleryViewModel.jgFetchPhotos(queryText)
                    return true
                }
                override fun onQueryTextChange(queryText: String): Boolean {
                    Log.d(JG_TAG, "QueryTextChange: $queryText")
                    return false
                }
            })
            setOnSearchClickListener {
                jgSearchView.setQuery(jgPhotoGalleryViewModel.jgSearchTerm, false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                jgPhotoGalleryViewModel.jgFetchPhotos("")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
            val jgPlaceholder: Drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bill_up_close
            ) ?: ColorDrawable()
            holder.jgBindDrawable(jgPlaceholder)
            jgThumbnailDownloader.jgQueueThumbnail(holder, jgGalleryItem.jgUrl)
        }
    }

    companion object {
        fun jgNewInstance() = PhotoGalleryFragment()
    }
}
