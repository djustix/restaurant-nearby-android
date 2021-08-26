package com.djustix.nearbites.features.search.view.components

import android.content.Context
import android.transition.Slide
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.djustix.common.R
import com.djustix.nearbites.features.search.databinding.ViewVenueHighlightBinding
import com.djustix.nearbites.features.search.domain.models.Venue

class VenueHighlightView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {
    private var binding = ViewVenueHighlightBinding.inflate(LayoutInflater.from(context), this)

    init {
        background = ContextCompat.getDrawable(context, R.drawable.white_background_rounded)
    }

    fun hide() {
        val container = parent
        if (container is ViewGroup) {
            val slide = Slide()
            slide.duration = 250
            slide.slideEdge = Gravity.BOTTOM
            slide.mode = Slide.MODE_OUT
            slide.interpolator = OvershootInterpolator()
            slide.addTarget(this)

            TransitionManager.beginDelayedTransition(container, slide)

            visibility = View.GONE
        }
    }

    fun show(venue: Venue) {
        binding.titleLabel.text = venue.name

        val container = parent
        if (container is ViewGroup) {
            val slide = Slide()
            slide.duration = 250
            slide.slideEdge = Gravity.BOTTOM
            slide.mode = Slide.MODE_IN
            slide.addTarget(this)

            TransitionManager.beginDelayedTransition(container, slide)

            visibility = View.VISIBLE
        }
    }
}