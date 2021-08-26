package com.djustix.nearbites.features.search.data.api

import com.djustix.nearbites.features.search.BuildConfig
import com.djustix.nearbites.features.search.data.api.dtos.FourSquareSearchResult
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Api to get Bars Nearby for a given location.
 * This uses the FourSquare Api.
 *
 * NOTE: If more methods would be required, we would add an interceptor to add client key & secret on each request.
 *
 * NOTE: Calls to FourSquare Api require versioning: https://developer.foursquare.com/docs/places-api/versioning/
 */

private const val API_VERSION = "20210801"
private const val DEFAULT_RADIUS = 250
private const val DEFAULT_LIMIT = 50
private const val DEFAULT_CATEGORIES = ""
interface FourSquareVenueApi {
    @GET("venues/search")
    suspend fun getVenues(
        @Query("ll") location: String,
        @Query("categoryId") categories: String = DEFAULT_CATEGORIES,
        @Query("radius") radius: Int = DEFAULT_RADIUS,
        @Query("limit") limit: Int = DEFAULT_LIMIT,
        @Query("v") version: String = API_VERSION,
        @Query("client_id") clientId: String = BuildConfig.FOUR_SQUARE_CLIENT_ID,
        @Query("client_secret") clientSecret: String = BuildConfig.FOUR_SQUARE_CLIENT_SECRET
    ): FourSquareSearchResult
}