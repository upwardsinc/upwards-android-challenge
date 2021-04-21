package com.weemusic.android.domain

data class Album(
    val id: Int,
    val image: String,
    val title: String,
    val artist: String,
    val releaseDate: String
)