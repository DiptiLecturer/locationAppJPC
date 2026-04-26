package org.freedu.locatiosharingappjpc.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.freedu.locatiosharingappjpc.model.AppUsers
import org.freedu.locatiosharingappjpc.repository.UserRepository

class myProfileVM : ViewModel() {
    private val repository = UserRepository()
    
    private val _currentUser = MutableStateFlow<AppUsers?>(null)
    val currentUser: StateFlow<AppUsers?> = _currentUser.asStateFlow()
    
    init {
        loadUserData()
    }
    
    private fun loadUserData() {
        viewModelScope.launch {
            val firebaseUser = repository.getCurrentUser()
            if (firebaseUser != null) {
                // For now, create a basic user profile
                _currentUser.value = AppUsers(
                    userId = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    username = firebaseUser.displayName ?: "User"
                )
            }
        }
    }
    
    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                val updatedUser = user.copy(username = newUsername)
                _currentUser.value = updatedUser
                // TODO: Update in Firebase/Firestore
            }
        }
    }
}
