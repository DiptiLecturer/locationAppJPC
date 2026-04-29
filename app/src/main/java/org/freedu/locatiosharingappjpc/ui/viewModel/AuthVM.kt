package org.freedu.locatiosharingappjpc.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.freedu.locatiosharingappjpc.repository.UserRepository

class AuthViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val currentUser = repository.getCurrentUser()
        _authState.value = if (currentUser != null) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }

    // ─── Sign Up ─────────────────────────────────────────────────────────────
    fun signUp(
        email: String,
        password: String,
        confirmPassword: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (password != confirmPassword) {
            onResult(false, "Passwords do not match")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            repository.signUp(email, password)
                .onSuccess {
                    _authState.value = AuthState.Authenticated
                    onResult(true, "Account created successfully!")
                }
                .onFailure { e ->
                    onResult(false, e.localizedMessage ?: "Sign-up failed")
                }

            _isLoading.value = false
        }
    }

    // ─── Login ───────────────────────────────────────────────────────────────
    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.login(email, password)
                .onSuccess {
                    _authState.value = AuthState.Authenticated
                    onResult(true, "Login successful!")
                }
                .onFailure { e ->
                    onResult(false, e.localizedMessage ?: "Login failed")
                }

            _isLoading.value = false
        }
    }

    // ─── Google Sign-In ──────────────────────────────────────────────────────
    fun signInWithGoogle(
        idToken: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.signInWithGoogle(idToken)
                .onSuccess {
                    _authState.value = AuthState.Authenticated
                    onResult(true, "Google Sign-In successful!")
                }
                .onFailure { e ->
                    onResult(false, e.localizedMessage ?: "Google Sign-In failed")
                }

            _isLoading.value = false
        }
    }

    // ─── Logout ──────────────────────────────────────────────────────────────
    fun logout() {
        repository.logout()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}