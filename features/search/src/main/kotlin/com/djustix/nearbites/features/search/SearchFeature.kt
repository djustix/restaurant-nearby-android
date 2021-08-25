package com.djustix.nearbites.features.search

import com.djustix.nearbites.features.search.data.FourSquareVenueRepository
import com.djustix.nearbites.features.search.data.api.FourSquareVenueApi
import com.djustix.nearbites.features.search.domain.repository.VenueRepository
import com.djustix.nearbites.features.search.view.NearbyVenuesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {
    viewModel { NearbyVenuesViewModel(get()) }

    single<VenueRepository> { FourSquareVenueRepository(get()) }

    single<FourSquareVenueApi> {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.foursquare.com/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(FourSquareVenueApi::class.java)
    }
}