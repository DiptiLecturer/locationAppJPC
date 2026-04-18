package org.freedu.locatiosharingappjpc.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            delay(1000) // Simulate checking saved credentials
            // Check if user is already logged in (e.g., SharedPreferences)
            val isLoggedIn = checkSavedLoginStatus()
            _authState.value = if (isLoggedIn) {
                AuthState.Authenticated
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    private fun checkSavedLoginStatus(): Boolean {
        // Implement your logic to check saved login status
        // For example, check SharedPreferences
        return false // Return true if user is already logged in
    }

    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500) // Simulate API call

            // Replace with actual authentication logic
            if (email == "test@example.com" && password == "123456") {
                saveLoginStatus(true)
                _authState.value = AuthState.Authenticated
                onResult(true, "Login successful!")
            } else {
                onResult(false, "Invalid email or password")
            }
            _isLoading.value = false
        }
    }

    fun signUp(email: String, password: String, confirmPassword: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500) // Simulate API call

            // Replace with actual signup logic
            if (password == confirmPassword) {
                saveLoginStatus(true)
                _authState.value = AuthState.Authenticated
                onResult(true, "Account created successfully!")
            } else {
                onResult(false, "Passwords do not match")
            }
            _isLoading.value = false
        }
    }

    fun signInWithGoogle(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500) // Simulate Google Sign-In

            // Implement actual Google Sign-In
            saveLoginStatus(true)
            _authState.value = AuthState.Authenticated
            onResult(true, "Google Sign-In successful!")
            _isLoading.value = false
        }
    }

    fun logout() {
        clearLoginStatus()
        _authState.value = AuthState.Unauthenticated
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        // Save to SharedPreferences or DataStore
        // For now, just a placeholder
    }

    private fun clearLoginStatus() {
        // Clear saved login status
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}