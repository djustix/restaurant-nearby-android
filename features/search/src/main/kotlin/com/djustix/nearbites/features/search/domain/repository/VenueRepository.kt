package com.djustix.nearbites.features.search.domain.repository

import com.djustix.nearbites.features.search.domain.models.Venue
import kotlinx.coroutines.flow.Flow

interface VenueRepository {
    suspend fun searchVenues(request: SearchRequest) : Flow<List<Venue>>

    data class SearchRequest(
        val latitude: Double,
        val longitude: Double,
        val radiusInMeters: Int = 250,
        val type: String
    )
}