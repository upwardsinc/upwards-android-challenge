package com.weemusic.android.adapter

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

class AlbumsAdapter(val albums: List<JsonObject>) : RecyclerView.Adapter<AlbumsViewHolder>() {

    private var albumsListPresentation = ArrayList<Album>()

    fun setAlbumList(sortedAlbumList: ArrayList<Album>) {
        albumsListPresentation = sortedAlbumList
    }

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
        holder.onBind(albumsListPresentation[position])

    fun createAlbumList(): ArrayList<Album> {
        Log.d("AlbumsAdapter", "createAlbumList called")
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
        Log.d("AlbumsAdapter", "createAlbumList finished, returns $albumsListPresentation ")
        return albumsListPresentation
    }
}

class AlbumsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun onBind(albumsList: Album) {
        Log.d("AlbumsViewHolder", "onBind started")

        val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)

        Picasso.with(itemView.context).load(albumsList.image).into(ivCover)
        tvTitle.text = albumsList.title
        tvArtist.text = albumsList.artist

        Log.d("AlbumsViewHolder", "onBind finished")
    }


}