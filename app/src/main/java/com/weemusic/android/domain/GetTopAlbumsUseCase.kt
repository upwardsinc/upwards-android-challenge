package com.weemusic.android.domain

import com.google.gson.JsonObject
import com.weemusic.android.network.iTunesApi
import io.reactivex.Single
import javax.inject.Inject

class GetTopAlbumsUseCase @Inject constructor(private val iTunesApi: iTunesApi) {

    fun perform(): Single<JsonObject> = iTunesApi.getTopAlbums(limit = 10)
}