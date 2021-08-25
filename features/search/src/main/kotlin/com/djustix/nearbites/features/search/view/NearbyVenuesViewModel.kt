package com.djustix.nearbites.features.search.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.djustix.nearbites.features.search.domain.models.Venue
import com.djustix.nearbites.features.search.domain.repository.VenueRepository

class NearbyVenuesViewModel(
    private val venueRepository: VenueRepository
): ViewModel() {

    private val _venues = MutableLiveData<ViewState>()
    val venues: LiveData<ViewState> = _venues

    sealed class ViewState() {
        object Loading: ViewState()
        data class VenuesAvailable(val data: List<Venue>): ViewState()
    }
}