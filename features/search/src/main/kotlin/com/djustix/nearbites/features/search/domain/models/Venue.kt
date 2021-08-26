package com.djustix.nearbites.features.search.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Venue(
    val id: String,
    val name: String,
    val location: Location

) : Parcelable {

    @Parcelize
    data class Location(
        val latitude: Double,
        val longitude: Double,
        val address: String,
        val city: String
    ) : Parcelable
}