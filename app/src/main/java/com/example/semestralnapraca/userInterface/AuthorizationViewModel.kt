package com.example.semestralnapraca.userInterface

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semestralnapraca.AuthorizationServices
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/*data class AuthorizationUiState(
)*/

class AuthorizationViewModel() : ViewModel() {

   //private val _uiState = MutableStateFlow(AuthorizationUiState())
    //val uiState: StateFlow<AuthorizationUiState> = _uiState.asStateFlow()

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    fun updateEmail(newEmail: String) {
        email = newEmail
    }
    fun updatePassword(newPassword: String) {
        password = newPassword
    }
    fun logInClick() {
        viewModelScope.launch(
            CoroutineExceptionHandler {
                _, throwable -> Log.d("Login Error", throwable.message.orEmpty())
            }
        ) {
            AuthorizationServices().logIn(email, password)
        }
    }

    fun signUpClick() {
        viewModelScope.launch(
            CoroutineExceptionHandler {
                    _, throwable -> Log.d("Signup Error", throwable.message.orEmpty())
            }
        ) {
            AuthorizationServices().signUp(email, password) }
    }
}