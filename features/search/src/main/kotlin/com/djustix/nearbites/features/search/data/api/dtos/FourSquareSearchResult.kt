package com.djustix.nearbites.features.search.data.api.dtos

data class FourSquareSearchResult(
    val response: GetBarsResponse
) {
    data class GetBarsResponse(
        val venues: List<BarObject>
    ) {
        data class BarObject(
            val id: String,
            val name: String,
            val location: Location,
            val contact: Contact,
            val venueDetails: VenueDetails
        ) {
            data class Location(
                val lat: Double,
                val lng: Double,
                val address: String,
                val city: String
            )

            data class Contact(
                val phone: String,
            )

            data class VenueDetails(
                val id: String
            )
        }
    }
}