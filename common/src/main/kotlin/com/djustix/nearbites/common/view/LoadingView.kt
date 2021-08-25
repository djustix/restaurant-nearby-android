package com.djustix.nearbites.common.view

import android.content.Context
import android.transition.ChangeBounds
import android.transition.Scene
import android.transition.Slide
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.djustix.common.R
import com.djustix.common.databinding.CommonViewLoaderBinding

class LoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {
    private var binding = CommonViewLoaderBinding.inflate(LayoutInflater.from(context), this)

    init {
        background = ContextCompat.getDrawable(context, R.drawable.white_background_rounded)
    }

    var text: CharSequence = binding.labelView.text
        set(value) {
            binding.labelView.text = value
            field = value
        }

    fun hide() {
        val container = parent
        if (container is ConstraintLayout) {
            val slide = Slide()
            slide.duration = 150
            slide.slideEdge = Gravity.TOP
            slide.mode = Slide.MODE_OUT
            slide.interpolator = OvershootInterpolator()
            slide.addTarget(this)

            TransitionManager.beginDelayedTransition(container, slide)

            visibility = View.GONE
        }
    }

    fun show() {
        val container = parent
        if (container is ConstraintLayout) {
            val slide = Slide()
            slide.duration = 150
            slide.slideEdge = Gravity.TOP
            slide.mode = Slide.MODE_IN
            slide.interpolator = OvershootInterpolator()
            slide.addTarget(this)

            TransitionManager.beginDelayedTransition(container, slide)

            visibility = View.VISIBLE
        }
    }
}