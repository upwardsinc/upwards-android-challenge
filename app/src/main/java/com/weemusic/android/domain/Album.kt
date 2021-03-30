package com.weemusic.android.domain

import org.threeten.bp.LocalDate

data class Album(
    val id: Int,
    val name: String,
    val title: String,
    val artist: String,
    val releaseDate: LocalDate
)
