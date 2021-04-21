package com.weemusic.android.network

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface iTunesApi {

    @GET("/api/v1/us/apple-music/top-albums/all/{limit}/explicit.json")
    fun getTopAlbums(@Path("limit") limit: Int = 25): Single<JsonObject>
}

// base url: https://rss.itunes.apple.com

// http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topalbums/limit=25/xml
// http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topalbums/limit=25/xml

// https://rss.itunes.apple.com/api/v1/us/apple-music/coming-soon/all/10/explicit.json


// "https://rss.itunes.apple.com/api/v1/us/apple-music/top-albums/all/10/non-explicit.json"


// http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topalbums/limit=10/xml