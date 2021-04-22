package com.weemusic.android.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.JsonObject
import com.weemusic.android.R
import com.weemusic.android.adapter.AlbumsAdapter
import com.weemusic.android.core.DaggerAppComponent
import com.weemusic.android.core.DaggerDomainComponent
import com.weemusic.android.core.DaggerNetworkComponent
import com.weemusic.android.domain.Album
import com.weemusic.android.domain.GetTopAlbumsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var getTopAlbumsUseCase: GetTopAlbumsUseCase
    var albumsList = ArrayList<Album>()
    private lateinit var adapter: AlbumsAdapter
    private lateinit var topAlbumsDisposable: Disposable
    private lateinit var albumListObjects: List<JsonObject>
    private lateinit var swipeLayout: SwipeRefreshLayout
    private var albumsListInitial = ArrayList<Album>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val networkComponent = DaggerNetworkComponent.create()
        val domainComponent = DaggerDomainComponent
            .builder()
            .networkComponent(networkComponent)
            .build()

        DaggerAppComponent
            .builder()
            .domainComponent(domainComponent)
            .build()
            .inject(this)

        // updates activity
        swipeLayout = findViewById(R.id.swipeContainer)
        swipeLayout.setOnRefreshListener {
            recreate()
            swipeLayout.isRefreshing = false
        }
    }

    override fun onStart() {
        super.onStart()
        topAlbumsDisposable = getTopAlbumsUseCase
            .perform()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                response.getAsJsonObject("feed")
                    .getAsJsonArray("results")
                    .map { it.asJsonObject }
            }
            .subscribe(Consumer {
                albumListObjects = it
                adapter = AlbumsAdapter(albumListObjects)
                albumsList = adapter.createAlbumList()
                albumsListInitial = ArrayList(albumsList)
                rvFeed.adapter = adapter
                rvFeed.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // adds sorting items to the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // sorts albums when user selects sorting method in menu
        when (item.itemId) {
            R.id.sort_new_to_old -> {
                albumsList.sortWith(compareByDescending { it.releaseDate })
                sortUpdater(getString(R.string.sorted_new_to_old))
            }
            R.id.sort_old_to_new -> {
                albumsList.sortWith(compareBy { it.releaseDate })
                sortUpdater(getString(R.string.sorted_old_to_new))
                return true
            }
            R.id.sort_alphabetically_artist -> {
                albumsList.sortWith(compareBy { it.artist })
                sortUpdater(getString(R.string.sorted_alphabetically_artist))
                return true
            }
            R.id.sort_alphabetically_title -> {
                albumsList.sortWith(compareBy { it.title })
                sortUpdater(getString(R.string.sorted_alphabetically_title))
                return true
            }
            R.id.sort_popularity -> {
                sortDrop()
                adapter.notifyDataSetChanged()
                tvToolbar.text = getString(R.string.sorted_popularity)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun sortUpdater(textForToolbar: String) {
        // updates card views and toolbar according to sorting
        adapter.notifyDataSetChanged()
        tvToolbar.text = textForToolbar
    }

    private fun sortDrop() {
        // returns album list to presorted state
        albumsList = ArrayList(albumsListInitial)
        adapter.setAlbumList(albumsList)
    }

}
