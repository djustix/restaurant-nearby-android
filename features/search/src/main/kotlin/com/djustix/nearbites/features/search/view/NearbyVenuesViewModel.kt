package com.djustix.nearbites.features.search.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djustix.nearbites.features.search.domain.models.Venue
import com.djustix.nearbites.features.search.domain.repository.VenueRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Placeholder for VenueType to illustrate making searches for different categories more dynamic.
private const val DEFAULT_VENUE_TYPE = "Food"

class NearbyVenuesViewModel(
    private val venueRepository: VenueRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _venues = MutableLiveData<ViewState>()
    val venues: LiveData<ViewState> = _venues

    fun searchVenues(
        latitude: Double,
        longitude: Double,
    ) = viewModelScope.launch(dispatcher) {
        val request = VenueRepository.SearchRequest(
            latitude = latitude,
            longitude = longitude,
            type = DEFAULT_VENUE_TYPE
        )

        val result = venueRepository.searchVenues(request)

        _venues.postValue(ViewState.VenuesAvailable(result))
    }

    sealed class ViewState() {
        object Loading: ViewState()
        data class VenuesAvailable(val data: List<Venue>): ViewState()
    }
}