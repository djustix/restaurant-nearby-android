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
import kotlinx.coroutines.flow.*

// Placeholder for VenueType to illustrate making searches for different categories more dynamic.
private const val DEFAULT_VENUE_TYPE = "Food"

class NearbyVenuesViewModel(
    private val venueRepository: VenueRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _state = MutableLiveData<ViewState>()
    val state: LiveData<ViewState> = _state

    fun searchVenues(
        latitude: Double,
        longitude: Double,
        radiusInMeters: Int
    ) = viewModelScope.launch(dispatcher) {
        _state.postValue(ViewState.Loading)

        val request = VenueRepository.SearchRequest(
            latitude = latitude,
            longitude = longitude,
            radiusInMeters = radiusInMeters,
            type = DEFAULT_VENUE_TYPE
        )

        venueRepository
            .searchVenues(request)
            .catch { throwable ->
                // Here we'd evaluate the actual throwable and determine whether we need to bring this to the user attention or not.
                _state.postValue(ViewState.Error(Exception(throwable.localizedMessage)))
            }
            .collect { venues ->
                _state.postValue(ViewState.VenuesAvailable(venues))
            }
    }

    sealed class ViewState {
        object Loading: ViewState()
        data class VenuesAvailable(val data: List<Venue>): ViewState()
        data class Error(val exception: Exception): ViewState()
    }
}