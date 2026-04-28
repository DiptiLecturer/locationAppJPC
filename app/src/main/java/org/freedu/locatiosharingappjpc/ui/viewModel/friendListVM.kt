package org.freedu.locatiosharingappjpc.ui.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.freedu.locatiosharingappjpc.model.AppUsers
import org.freedu.locatiosharingappjpc.repository.UserRepository

class FriendListViewModel(
    private val repository: UserRepository = UserRepository(),
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _friends = MutableStateFlow<List<AppUsers>>(emptyList())
    val friends: StateFlow<List<AppUsers>> = _friends.asStateFlow()

    private val _currentUser = MutableStateFlow<AppUsers?>(null)
    val currentUser: StateFlow<AppUsers?> = _currentUser.asStateFlow()

    init {
        observeUsersRealtime()
    }

    private fun observeUsersRealtime() {
        val currentUid = repository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            repository.listenToFriendsLocation().collect { allUsers ->
                _currentUser.value = allUsers.find { it.userId == currentUid }
                _friends.value = allUsers.filter { it.userId != currentUid }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    updateLocationInFirestore(loc.latitude, loc.longitude)
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }

    private fun updateLocationInFirestore(lat: Double, lng: Double) {
        val uid = repository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            repository.updateUserLocation(uid, lat, lng)
        }
    }

    fun logout() {
        repository.logout()
    }
}