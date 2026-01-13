package com.example.quotevault.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.data.repository.AuthRepository
import com.example.quotevault.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResetPasswordUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun updatePassword(password: String, confirmPassword: String) {
        if (password.length < 6) {
            _uiState.value = ResetPasswordUiState(error = "Password must be at least 6 characters")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = ResetPasswordUiState(error = "Passwords do not match")
            return
        }

        viewModelScope.launch {
            _uiState.value = ResetPasswordUiState(isLoading = true)

            when (val result = authRepository.updatePassword(password)) {
                is AuthResult.Success -> {
                    _uiState.value = ResetPasswordUiState(isSuccess = true)
                }
                is AuthResult.Error -> {
                    _uiState.value = ResetPasswordUiState(error = result.message)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}