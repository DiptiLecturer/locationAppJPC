package org.freedu.locatiosharingappjpc.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.freedu.locatiosharingappjpc.model.AppUsers
import org.freedu.locatiosharingappjpc.repository.UserRepository

class FriendListViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {
    
    private val _friends = MutableStateFlow<List<AppUsers>>(emptyList())
    val friends: StateFlow<List<AppUsers>> = _friends.asStateFlow()
    
    private val _currentUser = MutableStateFlow<AppUsers?>(null)
    val currentUser: StateFlow<AppUsers?> = _currentUser.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadCurrentUser()
        loadFriends()
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            val firebaseUser = repository.getCurrentUser()
            if (firebaseUser != null) {
                _currentUser.value = AppUsers(
                    userId = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    username = firebaseUser.displayName ?: "User"
                )
            }
        }
    }
    
    private fun loadFriends() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Implement actual friend loading from Firestore
                _friends.value = emptyList()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addFriend(friend: AppUsers) {
        viewModelScope.launch {
            val currentFriends = _friends.value.toMutableList()
            currentFriends.add(friend)
            _friends.value = currentFriends
            // TODO: Save to Firestore
        }
    }
    
    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            val currentFriends = _friends.value.toMutableList()
            currentFriends.removeAll { it.userId == friendId }
            _friends.value = currentFriends
            // TODO: Remove from Firestore
        }
    }
}
