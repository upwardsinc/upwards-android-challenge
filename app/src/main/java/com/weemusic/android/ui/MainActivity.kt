package com.weemusic.android.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import com.weemusic.android.R
import com.weemusic.android.core.DaggerAppComponent
import com.weemusic.android.core.DaggerDomainComponent
import com.weemusic.android.core.DaggerNetworkComponent
import com.weemusic.android.domain.GetTopAlbumsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var getTopAlbumsUseCase: GetTopAlbumsUseCase
    private lateinit var adapter: AlbumsAdapter
    private lateinit var topAlbumsDisposable: Disposable

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
                response.getAsJsonObject("feed")
                    .getAsJsonArray("results")
                    .map { it.asJsonObject }
            }
            .subscribe(Consumer {
                adapter = AlbumsAdapter(it)
                rvFeed.adapter = adapter
                rvFeed.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
            })
        Log.d(TAG, "onStart finished")
    }

    class AlbumsAdapter(val albums: List<JsonObject>) : RecyclerView.Adapter<AlbumsViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumsViewHolder {
            Log.d("AlbumsAdapter", "onCreateViewHolder started")
            val itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.album_view_holder, parent, false)

            Log.d("AlbumsAdapter", "onCreateViewHolder finished, returns $itemView ")
            return AlbumsViewHolder(itemView)
        }

        override fun getItemCount(): Int = albums.size

        override fun onBindViewHolder(holder: AlbumsViewHolder, position: Int) =
            holder.onBind(albums[position])
    }

    class AlbumsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(album: JsonObject) {
            Log.d("AlbumsViewHolder", "onBind started")
            val coverUrl = album
                .getAsJsonPrimitive("artworkUrl100")
                .asString
            val title = album
                .getAsJsonPrimitive("name")
                .asString
            val artist = album
                .getAsJsonPrimitive("artistName")
                .asString

            val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
            val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
            val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)

            Picasso.with(itemView.context).load(coverUrl).into(ivCover)
            tvTitle.text = title
            tvArtist.text = artist
            Log.d("AlbumsViewHolder", "onBind finished")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu started")
        // Inflate the menu; this adds items to the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_new_to_old -> {
                Log.d(TAG, "onOptionsItemSelected: sort_new_to_old")
                Toast.makeText(this@MainActivity, "New to old", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.sort_old_to_new -> {
                Log.d(TAG, "onOptionsItemSelected: sort_old_to_new")
                Toast.makeText(this@MainActivity, "Old to New", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.sort_alphabetically -> {
                Log.d(TAG, "onOptionsItemSelected: sort_alphabetically")
                Toast.makeText(this@MainActivity, "Alphabetically", Toast.LENGTH_LONG).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item)
    }


}
