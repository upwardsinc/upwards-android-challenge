package com.weemusic.android.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import com.weemusic.android.R
import com.weemusic.android.domain.Album
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class AlbumsAdapter(private val albums: List<JsonObject>) : RecyclerView.Adapter<AlbumsViewHolder>() {

    private var albumsListPresentation = ArrayList<Album>()

    fun setAlbumList(sortedAlbumList: ArrayList<Album>) {
        albumsListPresentation = sortedAlbumList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumsViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.album_view_holder, parent, false)

        return AlbumsViewHolder(itemView)
    }

    override fun getItemCount(): Int = albums.size

    override fun onBindViewHolder(holder: AlbumsViewHolder, position: Int) =
        holder.onBind(albumsListPresentation[position])

    fun createAlbumList(): ArrayList<Album> {
        // populates albumsList
        for (album in albums) {
            val albumModel = Album(
                album.getAsJsonPrimitive("id").asInt,
                album.getAsJsonPrimitive("artworkUrl100").asString,
                album.getAsJsonPrimitive("name").asString,
                album.getAsJsonPrimitive("artistName").asString,
                album.getAsJsonPrimitive("releaseDate").asString
            )
            albumsListPresentation.add(albumModel)
        }
        return albumsListPresentation
    }
}

class AlbumsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun onBind(albumsList: Album) {

        val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
        val ivTagNew: ImageView = itemView.findViewById(R.id.ivTagNew)

        // adds tag "new" if album was released less than 30 days ago
        if (calcDaysFromRelease(albumsList) < 30) {
            ivTagNew.visibility = View.VISIBLE
        } else ivTagNew.visibility = View.GONE

        Picasso.with(itemView.context).load(albumsList.image).into(ivCover)
        tvTitle.text = albumsList.title
        tvArtist.text = albumsList.artist

        Log.d("AlbumsViewHolder", "onBind finished")
    }

    @SuppressLint("SimpleDateFormat")
    fun calcDaysFromRelease(albumsList: Album): Long {
        // calculates difference between current date and release date of album
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val releaseDate = albumsList.releaseDate
        val currentDate = System.currentTimeMillis()
        val millSecDiff = currentDate - df.parse(releaseDate).time
        val days = TimeUnit.MILLISECONDS.toDays(millSecDiff)

        return days
    }
}