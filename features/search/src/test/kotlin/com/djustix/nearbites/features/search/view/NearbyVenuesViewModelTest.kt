package com.djustix.nearbites.features.search.view

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.djustix.nearbites.features.search.domain.models.Venue
import com.djustix.nearbites.features.search.domain.repository.VenueRepository
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import testutils.DispatcherTestWatcher
import kotlin.random.Random

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class NearbyVenuesViewModelTest {
    private lateinit var viewModel: NearbyVenuesViewModel

    @Mock
    private lateinit var repository: VenueRepository
    @Mock
    private lateinit var observer: Observer<NearbyVenuesViewModel.ViewState>


    @Rule
    @JvmField
    val instantTaskExecutor = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val testDispatcher = DispatcherTestWatcher()

    @Before
    fun setUp() {
        viewModel = NearbyVenuesViewModel(repository, testDispatcher.ioDispatcher)

        viewModel.state.observeForever(observer)
    }

    @After
    fun tearDown() {
        viewModel.state.removeObserver(observer)
    }

    @Test
    fun `searchVenues sends correct request to Repository`() = runBlockingTest {
        val lat = Random.nextDouble()
        val lng = Random.nextDouble()
        val radius = Random.nextInt()

        viewModel.searchVenues(lat, lng, radius)

        argumentCaptor<VenueRepository.SearchRequest> {
            verify(repository).searchVenues(capture())

            // Convert coordinates to String, because of double precision problems
            assertEquals(lat.toString(), firstValue.latitude.toString())
            assertEquals(lat.toString(), firstValue.latitude.toString())
            assertEquals(radius, firstValue.radiusInMeters)
        }
    }

    @Test
    fun `searchVenues emits data from Repository`() = runBlockingTest {
        val lat = Random.nextDouble()
        val lng = Random.nextDouble()
        val radius = Random.nextInt()
        val expectedVenues = givenRepositoryReturnsVenues()

        viewModel.searchVenues(lat, lng, radius)

        val viewStates = ArgumentCaptor.forClass(NearbyVenuesViewModel.ViewState::class.java)
        verify(observer, times(2)).onChanged(viewStates.capture())
        assertTrue(viewStates.allValues[0] is NearbyVenuesViewModel.ViewState.Loading)
        val last = viewStates.allValues[1]
        if (last is NearbyVenuesViewModel.ViewState.VenuesAvailable) {
            assertEquals(expectedVenues, last.data)
        } else fail("Last Element is not of expected type VenuesAvailable'.")
    }

    private fun givenRepositoryReturnsVenues(): List<Venue> {
        val venues = listOf(
            Venue(
                id = "SomeId",
                name = "SomeName",
                location = Venue.Location(
                    Random.nextDouble(),
                    Random.nextDouble(),
                    "Street",
                    "City"
                )
            )
        )

        runBlockingTest {
            given(repository.searchVenues(any())).willReturn(flowOf(venues))
        }

        return venues
    }
}