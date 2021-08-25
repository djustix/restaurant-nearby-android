package com.djustix.nearbites.features.search.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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

class NearbyVenuesMapFragment : Fragment(),
    OnMapReadyCallback,
    LocationPermissionProvider {
    private var _binding: FragmentNearbyVenuesMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NearbyVenuesViewModel by viewModel()

    // Google Maps & Location Services
    private var mapView: GoogleMap? = null
    private val markers = mutableMapOf<String, Marker>()

    private val isMapViewReady get() = mapView != null

    private val locationProvider by lazy { LocationProvider(requireContext()) }

    override val locationPermissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if (isGranted) {
            locationProvider.startLocationUpdates()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.venues.observe(this) { viewState ->
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

        /*binding.button.setOnClickListener {
            val venue = Venue(id = "","The Hilton", Venue.Location(54.0, 45.0))
            val action = NearbyVenuesMapFragmentDirections.showVenueDetail(venue)

            findNavController().navigate(action)
        }*/

        binding.loadingView.show()

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapView = googleMap

        binding.loadingView.text = getString(R.string.nearby_searching_location)
        binding.loadingView.show()

        locationProvider
            .startLocationUpdates(this)
            .observe(this) { data ->
                if (data is LocationProvider.LocationData.Available) {
                    binding.loadingView.text = getString(R.string.nearby_searching_venues)
                    viewModel.searchVenues(
                        latitude = data.location.latitude,
                        longitude = data.location.longitude
                    )
                }
            }
        
    }

    private fun displayVenues(venues: List<Venue>) {
        binding.loadingView.hide()

        clearMarkers()

        venues.forEach {
            addVenueMarker(it)
        }

        zoomToFitMarkers()
    }

    private fun clearMarkers() {
        for (marker in markers.values) {
            marker.remove()
        }

        markers.clear()
    }

    private fun addVenueMarker(venue: Venue) = mapView?.apply {
        val location = LatLng(venue.location.latitude, venue.location.longitude)
        //val icon = bitmapDescriptorFromVector(R.drawable.ic_marker)
        val options = MarkerOptions()
            //.icon(icon)
            .anchor(0.5f, 0.9f)
            .position(location)

        val addedMarker = addMarker(options)
        if (addedMarker != null) {
            markers[venue.id] = addedMarker
        }
    }

    private fun zoomToFitMarkers() = mapView?.apply {
        if (markers.isNotEmpty()) {

            val boundsBuilder = LatLngBounds.Builder()
            for (marker in markers.values) {
                boundsBuilder.include(marker.position)
            }

            val bounds = boundsBuilder.build()

            val targetWidth = (binding.root.width * .8).toInt()
            val targetHeight = (binding.root.height * .8).toInt()

            val update = CameraUpdateFactory.newLatLngBounds(bounds, targetWidth, targetHeight, 0)

            animateCamera(update)
        }
    }

    private fun bitmapDescriptorFromVector(@DrawableRes vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(requireContext(), vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}