package org.freedu.locatiosharingappjpc.ui.presentation


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import org.freedu.locatiosharingappjpc.ui.viewModel.FriendListViewModel

@Composable
fun MapScreen(
    lat: Double? = null,
    lng: Double? = null,
    showAll: Boolean = false,
    viewModel: FriendListViewModel
) {
    val friends by viewModel.friends.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val bangladeshCenter = LatLng(23.6850, 90.3563)
    val isSingleLocValid = lat != null && lng != null && lat != 0.0 && lng != 0.0

    val cameraPositionState = rememberCameraPositionState {
        val startPos = if (isSingleLocValid) LatLng(lat!!, lng!!) else bangladeshCenter
        position = CameraPosition.fromLatLngZoom(startPos, if (isSingleLocValid) 15f else 7f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        // FIX: This pushes the zoom buttons and Google logo up from the bottom nav bar
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        if (showAll) {
            val allUsers = friends + listOfNotNull(currentUser)
            val validUsers = allUsers.filter {
                it.latitude != null && it.longitude != null &&
                        it.latitude != 0.0 && it.longitude != 0.0
            }

            validUsers.forEach { userItem ->
                val userLatLng = LatLng(userItem.latitude!!, userItem.longitude!!)
                val markerState = rememberMarkerState(position = userLatLng)

                Marker(
                    state = markerState,
                    title = userItem.username.ifEmpty { userItem.email }
                )
            }
        } else if (isSingleLocValid) {
            // FIX: Find the specific user from the data to get their name/email
            // If it's a single location, we check if it belongs to a friend or the current user
            val targetUser = (friends + listOfNotNull(currentUser)).find {
                it.latitude == lat && it.longitude == lng
            }

            val singleMarkerState = rememberMarkerState(position = LatLng(lat!!, lng!!))

            Marker(
                state = singleMarkerState,
                // If we found the user in our list, show their name, else show a default
                title = targetUser?.let { it.username.ifEmpty { it.email } } ?: "Selected Location"
            )
        }
    }
}