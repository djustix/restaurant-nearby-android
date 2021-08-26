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
            val location: Location
        ) {
            data class Location(
                val lat: Double,
                val lng: Double,
                val address: String,
                val city: String
            )
        }
    }
}