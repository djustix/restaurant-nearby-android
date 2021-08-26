package com.djustix.nearbites.features.search.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.djustix.nearbites.features.search.databinding.FragmentVenueDetailBinding

class VenueDetailFragment : Fragment() {
    private var _binding: FragmentVenueDetailBinding? = null
    private val binding get() = _binding!!

    private val arguments: VenueDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVenueDetailBinding.inflate(inflater, container, false)

        // Get the requested details from the passed arguments.
        // In a more purposeful application we'd inject a viewModel in this class and send the Venue,
        // for the ViewModel to determine ViewState and execute possible domain logic / get details.
        val venue = arguments.venue

        val addressDescription = "${venue.location.address}, {${venue.location.city}"
        binding.titleLabel.text = venue.name
        binding.addressLabel.text = addressDescription

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}