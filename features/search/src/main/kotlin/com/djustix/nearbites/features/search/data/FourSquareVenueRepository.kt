package com.djustix.nearbites.features.search.data

import com.djustix.nearbites.features.search.data.api.FourSquareCategory
import com.djustix.nearbites.features.search.data.api.FourSquareVenueApi
import com.djustix.nearbites.features.search.domain.models.Venue
import com.djustix.nearbites.features.search.domain.repository.VenueRepository
import kotlinx.coroutines.flow.flow

// Experimental value to concentrate FourSquare results compared to actual radius
private const val RADIUS_CORRECTION = 0.5

class FourSquareVenueRepository(private val api: FourSquareVenueApi) : VenueRepository {
    override suspend fun searchVenues(request: VenueRepository.SearchRequest) = flow {
        val cachedResults = searchVenuesFromCache(request)
        if (cachedResults.isNotEmpty()) {
            emit(cachedResults)
        }

        val newVenues = searchVenuesWithApi(request)

        emit(newVenues)
    }

    private suspend fun searchVenuesWithApi(request: VenueRepository.SearchRequest) : List<Venue> {

        val result = api.getVenues(
            location = "${request.latitude},${request.longitude}",
            radius = (request.radiusInMeters * RADIUS_CORRECTION).toInt(),
            categories = FourSquareCategory.getByType(request.type).identifier
        )

        return result.response.venues.map {
            Venue(
                id = it.id,
                name = it.name,
                location = Venue.Location(
                    latitude = it.location.lat,
                    longitude = it.location.lng
                )
            )
        }
    }

    private fun searchVenuesFromCache(request: VenueRepository.SearchRequest) : List<Venue> {
        return emptyList()
    }
}
