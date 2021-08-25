package com.djustix.nearbites.common.location

import androidx.activity.result.ActivityResultLauncher

interface LocationPermissionProvider {
    val locationPermissionLauncher: ActivityResultLauncher<String>
}