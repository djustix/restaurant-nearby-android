package com.djustix.nearbites.features.search

import com.djustix.nearbites.features.search.data.FourSquareVenueRepository
import com.djustix.nearbites.features.search.data.api.FourSquareVenueApi
import com.djustix.nearbites.features.search.domain.repository.VenueRepository
import com.djustix.nearbites.features.search.view.NearbyVenuesViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Create Koin Module for Feature Dependencies
 */
val searchModule = module {
    viewModel { NearbyVenuesViewModel(get()) }

    single<VenueRepository> { FourSquareVenueRepository(get()) }

    single<FourSquareVenueApi> {
        val httpClient = OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                addInterceptor(logging)
            }
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.foursquare.com/v2/")
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(FourSquareVenueApi::class.java)
    }
}