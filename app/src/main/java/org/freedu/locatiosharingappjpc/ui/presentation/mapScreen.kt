package org.freedu.locatiosharingappjpc.ui.presentation


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

import org.freedu.locatiosharingappjpc.model.AppUsers
import org.freedu.locatiosharingappjpc.ui.viewModel.FriendListViewModel
import org.freedu.locatiosharingappjpc.ui.viewModel.MapViewModel

@Composable
fun MapScreen(
    lat: Double? = null,
    lng: Double? = null,
    showAll: Boolean = false,
    viewModel: FriendListViewModel
) {
    val friends by viewModel.friends.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(lat ?: 0.0, lng ?: 0.0), 15f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        if (showAll) {
            val allUsers = friends + listOfNotNull(currentUser)
            allUsers.forEach { user ->
                val pos = LatLng(user.latitude ?: 0.0, user.longitude ?: 0.0)
                Marker(
                    state = MarkerState(position = pos),
                    // Show username if exists, otherwise show email
                    title = user.username.ifEmpty { user.email }
                )
            }
        } else {
            // Single User Marker
            lat?.let { l -> lng?.let { g ->
                Marker(
                    state = MarkerState(position = LatLng(l, g)),
                    title = "Selected Location"
                )
            }}
        }
    }
}