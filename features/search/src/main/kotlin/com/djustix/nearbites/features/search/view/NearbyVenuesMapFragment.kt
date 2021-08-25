package com.djustix.nearbites.features.search.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.djustix.nearbites.features.search.R
import com.djustix.nearbites.features.search.databinding.FragmentNearbyVenuesMapBinding
import com.djustix.nearbites.features.search.domain.models.Venue

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NearbyVenuesMapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentNearbyVenuesMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NearbyVenuesViewModel by viewModel()

    // Google Maps
    private var mapView: GoogleMap? = null
    private val markers = mutableMapOf<String, Marker>()

    private val isMapViewReady get() = mapView != null

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
        mapView = googleMap

    }
}