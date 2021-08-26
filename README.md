# Nearbites (restaurant-nearby-android)
Example Kotlin app with common patterns and stack.

This app requests permission to the device location and uses the *FourSquare Api* to display restaurants nearby on a map-view.

**Add Configuration for Third Party integrations**

The Search feature module depends on several third-party integrations that require authentication. Required keys are supplied through **'search/build.gradle**. Please refer to your contact for valid data for this configuration.

**Architecture**

The app depends on a Common Module and a Search Feature Module, aligning the application for scale and faster build times. Feature Modules are organized according to Clean Architecture concepts, separating presentation, domain and data-access.

**Stack**

- Kotlin, Architecture Components and Coroutines
- Navigation Component for Navigation
- Koin for Dependency Injection
- Retrofit for Remoting
- Google Maps for Map display

**Improvements to consider**

- FourSquare Api finds venues in a radius, yet the app displays results on a rectangular map. Therefore logic in the Data Layer and Presentation layer can be at odds with eachother, which warrants a brainstorm on code-efficiency vs user-needs.
- Expand on the Venue Model, allowing for images and more qualitative content
- Add a more design and content to VenueDetail
- Use lastLocation functionality of FusedLocationProviderClient for faster location fix and map update
- Always more testing, but NearbyVenuesModelTest shows good practices
