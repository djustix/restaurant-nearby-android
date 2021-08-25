package com.djustix.nearbites.features.search.data

import com.djustix.nearbites.features.search.data.api.FourSquareCategory
import com.djustix.nearbites.features.search.data.api.FourSquareVenueApi
import com.djustix.nearbites.features.search.domain.models.Venue
import com.djustix.nearbites.features.search.domain.repository.VenueRepository

class FourSquareVenueRepository(private val api: FourSquareVenueApi) : VenueRepository {
    override suspend fun searchVenues(request: VenueRepository.SearchRequest): List<Venue> {
        val result = api.getVenues(
            location = "${request.latitude},${request.longitude}",
            radius = request.radiusInMeters,
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
}
