package com.bignerdranch.android.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import java.util.concurrent.TimeUnit

private const val TAG = "PhotoGalleryFragment"
private const val POLL_WORK = "POLL_WORK"

class PhotoGalleryFragment : VisibleFragment() {

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
                Log.d(TAG, "Have gallery items from view model $galleryItems")
                jgPhotoRecyclerView.adapter = JGPhotoAdapter(galleryItems)
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        jgThumbnailDownloader.jgClearQueue()
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

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu)

        val jgSearchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        val jgSearchView = jgSearchItem.actionView as SearchView
        jgSearchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(queryText: String): Boolean {
                    Log.d(TAG, "QueryTextSubmit: $queryText")
                    jgPhotoGalleryViewModel.jgFetchPhotos(queryText)
                    return true
                }

                override fun onQueryTextChange(queryText: String): Boolean {
                    Log.d(TAG, "QueryTextChange: $queryText")
                    return false
                }
            })

            setOnSearchClickListener {
                val jgQuery = QueryPreferences.jgGetStoredQuery(requireContext())
                jgSearchView.setQuery(jgQuery, false)
            }
        }

        val jgToggleItem = menu.findItem(R.id.menu_item_toggle_polling)
        val jgIsPolling = QueryPreferences.jgIsPolling(requireContext())
        val jgToggleItemTitle = if (jgIsPolling) {
            R.string.stop_polling
        } else {
            R.string.start_polling
        }
        jgToggleItem.setTitle(jgToggleItemTitle)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                jgPhotoGalleryViewModel.jgFetchPhotos()
                true
            }
            R.id.menu_item_toggle_polling -> {
                val jgIsPolling = QueryPreferences.jgIsPolling(requireContext())
                if (jgIsPolling) {
                    WorkManager.getInstance().cancelUniqueWork(POLL_WORK)
                    QueryPreferences.jgSetPolling(requireContext(), false)
                } else {
                    val jgConstraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .build()
                    val jgPeriodicRequest = PeriodicWorkRequest
                        .Builder(PollWorker::class.java, 15, TimeUnit.MINUTES)
                        .setConstraints(jgConstraints)
                        .build()
                    WorkManager.getInstance().enqueueUniquePeriodicWork(
                        POLL_WORK,
                        ExistingPeriodicWorkPolicy.KEEP,
                        jgPeriodicRequest)
                    QueryPreferences.jgSetPolling(requireContext(), true)
                }
                activity?.invalidateOptionsMenu()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private inner class JGPhotoHolder(private val itemImageView: ImageView)
        : RecyclerView.ViewHolder(itemImageView),
        View.OnClickListener {

        private lateinit var jgGalleryItem: GalleryItem

        init {
            itemView.setOnClickListener(this)
        }

        val jgBindDrawable: (Drawable) -> Unit = itemImageView::setImageDrawable

        fun jgBindGalleryItem(item: GalleryItem) {
            jgGalleryItem = item
        }
        
        override fun onClick(view: View) {
            val jgIntent = PhotoPageActivity
                .jgNewIntent(requireContext(), jgGalleryItem.jgPhotoPageUri)
            startActivity(jgIntent)
        }
    }

    private inner class JGPhotoAdapter(private val galleryItems: List<GalleryItem>) :
        RecyclerView.Adapter<JGPhotoHolder>() {

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
            holder.jgBindGalleryItem(jgGalleryItem)
            val jgPlaceholder: Drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bill_up_close
            ) ?: ColorDrawable()
            holder.jgBindDrawable(jgPlaceholder)
            jgThumbnailDownloader.jgQueueThumbnail(holder, jgGalleryItem.url)
        }
    }

    companion object {
        fun jgNewInstance() = PhotoGalleryFragment()
    }
}