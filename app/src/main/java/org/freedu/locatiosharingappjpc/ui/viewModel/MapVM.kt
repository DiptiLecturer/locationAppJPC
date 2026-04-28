package org.freedu.locatiosharingappjpc.ui.viewModel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {
    private val _targetLocation = MutableStateFlow<LatLng?>(null)
    val targetLocation: StateFlow<LatLng?> = _targetLocation.asStateFlow()

    fun setTargetLocation(lat: Double, lng: Double) {
        _targetLocation.value = LatLng(lat, lng)
    }
}