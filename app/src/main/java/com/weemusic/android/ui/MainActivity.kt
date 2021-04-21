package com.weemusic.android.ui

import android.content.SharedPreferences
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

//TODO: update toolbar (?)
//TODO: add sorting by popularity
//TODO: add price(?)

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var getTopAlbumsUseCase: GetTopAlbumsUseCase
    private lateinit var adapter: AlbumsAdapter
    private lateinit var topAlbumsDisposable: Disposable
    var albumsList = ArrayList<Album>()

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
                adapter = AlbumsAdapter(it)
                albumsList = adapter.createAlbumList()
                rvFeed.adapter = adapter
                rvFeed.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
            })

        Log.d(TAG, "onStart finished")
    }

    private fun sortNewToOld(adapter: AlbumsAdapter) {
    Log.d(TAG, "sortNewToOld started")
        albumsList.sortWith(compareByDescending { it.releaseDate })
        Log.d(TAG, "sortNewToOld albumsList was sorted $albumsList")
        adapter.notifyDataSetChanged()
        Log.d(TAG, "sortNewToOld adapter was notifyDataSetChanged")
    }

    private fun sortOldToNew(adapter: AlbumsAdapter) {
        Log.d(TAG, "sortOldToNew started")
        albumsList.sortWith(compareBy{it.releaseDate})
        adapter.notifyDataSetChanged()
    }

    private fun sortAlphabetically(adapter: AlbumsAdapter) {
        Log.d(TAG, "sortAlphabetically started")
        albumsList.sortWith(compareBy{it.artist})
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu started")
        // add items to the action bar
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
                sortNewToOld(adapter)
                Toast.makeText(this@MainActivity, "Sorted new to old", Toast.LENGTH_LONG)
                    .show()
            }
            R.id.sort_old_to_new -> {
                Log.d(TAG, "onOptionsItemSelected: sort_old_to_new was clicked")
                sortOldToNew(adapter)
                Toast.makeText(this@MainActivity, "Sorted old to new", Toast.LENGTH_LONG)
                    .show()
                return true
            }
            R.id.sort_alphabetically -> {
                Log.d(TAG, "onOptionsItemSelected: sort_alphabetically")
                sortAlphabetically(adapter)
                Toast.makeText(this@MainActivity, "Sorted alphabetically", Toast.LENGTH_LONG).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

        Log.d(TAG, "onOptionsItemSelected finished")

        return super.onOptionsItemSelected(item)
    }
}
