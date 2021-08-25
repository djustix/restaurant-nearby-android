package com.djustix.nearbites.features.search.domain.repository

import com.djustix.nearbites.features.search.domain.models.Venue

interface VenueRepository {
    suspend fun getBars(request: SearchRequest) : List<Venue>

    data class SearchRequest(
        val latitude: Double,
        val longitude: Double,
        val radiusInMeters: Int = 250,
        val type: String
    )
}