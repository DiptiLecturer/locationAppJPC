package org.freedu.locatiosharingappjpc.ui.presentation


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

import org.freedu.locatiosharingappjpc.model.AppUsers

@Composable
fun MapScreen(users: List<AppUsers>) {
    // 1. Define the starting view (Center on the first user found)
    val firstUserLocation = users.firstOrNull { it.latitude != null }?.let {
        LatLng(it.latitude!!, it.longitude!!)
    } ?: LatLng(0.0, 0.0)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(firstUserLocation, 12f)
    }

    // 2. Render the Map
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // 3. Add Markers for every user in the list
        users.forEach { user ->
            if (user.latitude != null && user.longitude != null) {
                Marker(
                    state = MarkerState(position = LatLng(user.latitude, user.longitude)),
                    title = user.username,
                    snippet = "Last seen here"
                )
            }
        }
    }
}
