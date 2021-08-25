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

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}