package com.djustix.nearbites.features.search.data.api

/**
 * Enum Class to map a Bar Type to categoryId as supported by FourSquare.
 *
 * https://developer.foursquare.com/docs/build-with-foursquare/categories/
 */
enum class FourSquareCategory(val identifier: String) {
    FOOD("4d4b7105d754a06374d81259");

    companion object {
        fun getByType(type: String) : FourSquareCategory {
            return when (type) {
                else -> FOOD
            }
        }
    }
}