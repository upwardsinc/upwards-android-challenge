package com.weemusic.android.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
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
import javax.inject.Inject

//TODO: create class to separate sorting implementation (?)
//TODO: add tag "new" to anything less than 1 month
//TODO: restore activity after phone rotation
//TODO: add links to iTunes
//TODO: create sorting dialog(?)

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var getTopAlbumsUseCase: GetTopAlbumsUseCase
    private lateinit var adapter: AlbumsAdapter
    private lateinit var topAlbumsDisposable: Disposable
    private lateinit var albumListObjects: List<JsonObject>
    var albumsList = ArrayList<Album>()
    var albumsListInitial = ArrayList<Album>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate started")
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

        Log.d(TAG, "onCreate finished")
    }

    override fun onStart() {
        Log.d(TAG, "onStart started")
        super.onStart()
        topAlbumsDisposable = getTopAlbumsUseCase
            .perform()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                Log.d(TAG, "onStart: getting response to fill a map")
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

        Log.d(TAG, "onStart finished")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu started")
        // add sorting items to the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        Log.d(TAG, "onCreateOptionsMenu finished")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected started")
        when (item.itemId) {
            R.id.sort_new_to_old -> {
                Log.d(TAG, "onOptionsItemSelected: sort_new_to_old was clicked")
                albumsList.sortWith(compareByDescending { it.releaseDate })
                sortUpdater(getString(R.string.sorted_new_to_old))
                Toast.makeText(this@MainActivity, R.string.sorted_new_to_old, Toast.LENGTH_LONG)
                    .show()
            }
            R.id.sort_old_to_new -> {
                Log.d(TAG, "onOptionsItemSelected: sort_old_to_new was clicked")
                albumsList.sortWith(compareBy { it.releaseDate })
                sortUpdater(getString(R.string.sorted_old_to_new))
                Toast.makeText(this@MainActivity, R.string.sorted_old_to_new, Toast.LENGTH_LONG)
                    .show()
                return true
            }
            R.id.sort_alphabetically_artist -> {
                Log.d(TAG, "onOptionsItemSelected: sort_alphabetically_artist")
                albumsList.sortWith(compareBy { it.artist })
                sortUpdater(getString(R.string.sorted_alphabetically_artist))
                Toast.makeText(
                    this@MainActivity,
                    R.string.sorted_alphabetically_artist,
                    Toast.LENGTH_LONG
                ).show()
                return true
            }
            R.id.sort_alphabetically_title -> {
                Log.d(TAG, "onOptionsItemSelected: sort_alphabetically_title")
                albumsList.sortWith(compareBy { it.title })
                sortUpdater(getString(R.string.sorted_alphabetically_title))
                Toast.makeText(
                    this@MainActivity,
                    R.string.sorted_alphabetically_title,
                    Toast.LENGTH_LONG
                ).show()
                return true
            }
            R.id.sort_popularity -> {
                Log.d(TAG, "onOptionsItemSelected: sort_popularity")
                sortDrop()
                adapter.notifyDataSetChanged()
                tvToolbar.text = getString(R.string.sorted_popularity)
                Toast.makeText(this@MainActivity, R.string.sorted_popularity, Toast.LENGTH_LONG)
                    .show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

        Log.d(TAG, "onOptionsItemSelected finished")

        return super.onOptionsItemSelected(item)
    }

    private fun sortUpdater(textForToolbar: String) {
        Log.d(TAG, "sortUpdater started")
        adapter.notifyDataSetChanged()
        tvToolbar.text = textForToolbar
        Log.d(TAG, "sortNewToOld adapter was notifyDataSetChanged")
    }

    private fun sortDrop() {
        albumsList = ArrayList(albumsListInitial)
        adapter.setAlbumList(albumsList)
    }

}
