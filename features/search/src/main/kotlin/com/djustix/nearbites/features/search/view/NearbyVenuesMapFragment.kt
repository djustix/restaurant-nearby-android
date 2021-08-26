package com.djustix.nearbites.features.search.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.djustix.nearbites.common.location.LocationPermissionProvider
import com.djustix.nearbites.common.location.LocationProvider
import com.djustix.nearbites.features.search.R
import com.djustix.nearbites.features.search.databinding.FragmentNearbyVenuesMapBinding
import com.djustix.nearbites.features.search.domain.models.Venue
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val DEFAULT_ZOOM_LEVEL = 15f

class NearbyVenuesMapFragment : Fragment(),
    OnMapReadyCallback,
    LocationPermissionProvider {
    private var _binding: FragmentNearbyVenuesMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NearbyVenuesViewModel by viewModel()

    // Google Maps & Location Services
    private lateinit var mapView: GoogleMap
    private val markers = mutableMapOf<String, Pair<Venue, Marker>>()

    private var isMapContentRequired: Boolean = false

    private val locationProvider by lazy { LocationProvider(requireContext()) }

    override val locationPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                locationProvider.startLocationUpdates()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.observe(this) { viewState ->
            if (viewState is NearbyVenuesViewModel.ViewState.VenuesAvailable) {
                displayVenues(viewState.data)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNearbyVenuesMapBinding.inflate(inflater, container, false)

        binding.loadingView.show()

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        binding.loadingView.text = getString(R.string.nearby_searching_location)
        binding.loadingView.show()

        mapView = googleMap.apply {
            setOnCameraMoveStartedListener { reason ->
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    isMapContentRequired = true
                    binding.highlightView.hide()
                }
            }
            setOnCameraIdleListener {
                if (isMapContentRequired) {
                    onNearbyVenuesRequested()
                }
            }

            setOnMarkerClickListener { marker ->
                val pair = markers[marker.tag]
                if (pair != null) {
                    binding.highlightView.show(pair.first)
                    binding.highlightView.setOnClickListener {
                        val action = NearbyVenuesMapFragmentDirections.showVenueDetail(pair.first)

                        findNavController().navigate(action)
                    }
                }
                true
            }
        }

        locationProvider
            .startLocationUpdates(this)
            .observe(this) { data ->
                if (data is LocationProvider.LocationData.Available) {
                    val update = CameraUpdateFactory.newLatLngZoom(
                        LatLng(data.location.latitude, data.location.longitude),
                        DEFAULT_ZOOM_LEVEL
                    )
                    mapView.moveCamera(update)
                    isMapContentRequired = true
                }
            }

    }

    private fun onNearbyVenuesRequested() {
        isMapContentRequired = false

        binding.loadingView.text = getString(R.string.nearby_searching_venues)
        binding.loadingView.show()

        val target = mapView.cameraPosition.target

        viewModel.searchVenues(
            latitude = target.latitude,
            longitude = target.longitude,
            radiusInMeters = mapView.getRadiusForVisibleRegion()
        )
    }

    private fun displayVenues(venues: List<Venue>) {
        binding.loadingView.hide()

        // Only attempt to add markers if the venue isn't already added
        venues.forEach { venue ->
            if (!markers.containsKey(venue.id)) {
                val marker = mapView.addVenueMarker(venue)
                marker?.also {
                    it.tag = venue.id
                    markers[venue.id] = Pair(venue, marker)
                }
            }
        }

        mapView.removeOutOfBoundsMarkers()
    }

    /**
     * Remove any markers that are out of bounds of the ViewPort
     */
    private fun GoogleMap.removeOutOfBoundsMarkers() {
        val mapBounds = projection.visibleRegion.latLngBounds
        /*val iterator = markers.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (!mapBounds.contains(entry.value.second.position)) {
                entry.value.second.remove()
                iterator.remove()
            }
        }*/
        markers.values.removeIf { pair ->
            if (!mapBounds.contains(pair.second.position)) {
                pair.second.remove()
                true
            } else false
        }
    }

    private fun GoogleMap.addVenueMarker(venue: Venue): Marker? {
        val location = LatLng(venue.location.latitude, venue.location.longitude)

        val options = MarkerOptions()
            .anchor(0.5f, 0.9f)
            .position(location)

        return addMarker(options)
    }

    private fun GoogleMap.getRadiusForVisibleRegion(): Int {
        val center = cameraPosition.target
        val bottomLeft = projection.visibleRegion.nearLeft
        val radius = FloatArray(1)

        Location.distanceBetween(
            center.latitude,
            center.longitude,
            bottomLeft.latitude,
            bottomLeft.longitude,
            radius
        )

        return radius[0].toInt()
    }
}