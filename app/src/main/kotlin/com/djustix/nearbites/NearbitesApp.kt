package com.djustix.nearbites

import android.app.Application
import com.djustix.nearbites.features.search.searchModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NearbitesApp : Application() {
    override fun onCreate() {
        super.onCreate()

        configureDependencyInjection()
    }

    /**
     * Configure DI using Koin
     *
     * For an expanding application with multiple features/modules, their associated Koin Modules
     * would be added based on their scope.
     */
    private fun configureDependencyInjection() {
        startKoin {
            androidContext(this@NearbitesApp)
            modules(searchModule)
        }
    }
}