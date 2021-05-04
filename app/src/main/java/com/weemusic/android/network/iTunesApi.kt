package com.weemusic.android.network

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface iTunesApi {

    @GET("/api/v1/us/apple-music/coming-soon/all/{limit}/explicit.json")
    fun getTopAlbums(@Path("limit") limit: Int = 10): Single<JsonObject>
}