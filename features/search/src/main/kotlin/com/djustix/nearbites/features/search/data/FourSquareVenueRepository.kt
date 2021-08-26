package com.djustix.nearbites.features.search.data

import com.djustix.nearbites.features.search.data.api.FourSquareCategory
import com.djustix.nearbites.features.search.data.api.FourSquareVenueApi
import com.djustix.nearbites.features.search.domain.models.Venue
import com.djustix.nearbites.features.search.domain.repository.VenueRepository
import kotlinx.coroutines.flow.flow

// Experimental value to concentrate FourSquare results compared to actual radius
private const val RADIUS_CORRECTION = 0.5

class FourSquareVenueRepository(private val api: FourSquareVenueApi) : VenueRepository {
    private val cache = mutableMapOf<String, Venue>()

    override suspend fun searchVenues(request: VenueRepository.SearchRequest) = flow {
        val cachedResults = searchVenuesFromCache(request)
        if (cachedResults.isNotEmpty()) {
            emit(cachedResults)
        }

        // Get venues from the Api.
        // - Only add new entries to the cache.
        // - Using the mapping proceed with just the new entries .
        val apiResults = searchVenuesWithApi(request)

        val newVenues = apiResults.mapNotNull { venue ->
            val existingValue = cache.putIfAbsent(venue.id, venue)
            if (existingValue != null) {
                    null
            } else venue
        }

        emit(newVenues)
    }

    private suspend fun searchVenuesWithApi(request: VenueRepository.SearchRequest) : List<Venue> {
        val result = api.getVenues(
            location = "${request.latitude},${request.longitude}",
            radius = (request.radiusInMeters * RADIUS_CORRECTION).toInt(),
            categories = FourSquareCategory.getByType(request.type).identifier
        )

        return result.response.venues.mapNotNull {
            try {
                Venue(
                    id = it.id,
                    name = it.name,
                    location = Venue.Location(
                        latitude = it.location.lat,
                        longitude = it.location.lng,
                        address = it.location.address,
                        city = it.location.city
                    )
                )
            } catch(exception: Exception) {
                // Suppress failed object mapping.
                // Tracking exception could hint developers how to prepare/handle this situation if it occurs more often.
                null
            }
        }
    }

    /**
     * Iterates through the cache and returns venue whose location is within the required
     * distance from the requested latitude/longitude.
     *
     * NOTE: Worth a discussion on the exact needs for the user. This implementation could be
     * optimized for performance and removing irrelevant/outdated results.
     */
    private fun searchVenuesFromCache(request: VenueRepository.SearchRequest) : List<Venue> {
        return cache.mapNotNull { pair ->
            val distance = distanceInMeters(
                pair.value.location.latitude,
                pair.value.location.longitude,
                request.latitude,
                request.longitude
            )

            if (distance < request.radiusInMeters) {
                pair.value
            } else null
        }
    }

    /**
     * Convenience method to assist in determining venues from cache within radius of the requested
     * latitude/longitude. We want to keep logic in this layer free from the Android framework,
     * which explains why we're not using convenience methods available in that framework.
     *
     * Credit: https://handyopinion.com/find-distance-between-two-locations-in-kotlin/
     */
    private fun distanceInMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        dist = dist * 1.609344
        return dist * 1000
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}
