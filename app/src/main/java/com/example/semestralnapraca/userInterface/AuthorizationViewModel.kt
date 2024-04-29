package com.example.semestralnapraca.userInterface

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semestralnapraca.AuthorizationServices
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/*data class AuthorizationUiState(
)*/

class AuthorizationViewModel() : ViewModel() {

   //private val _uiState = MutableStateFlow(AuthorizationUiState())
    //val uiState: StateFlow<AuthorizationUiState> = _uiState.asStateFlow()
    var errorMessage by mutableStateOf(false)
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
    fun logInClick(onNavigationUp: () -> Unit = {}) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!");
        viewModelScope.launch(
            CoroutineExceptionHandler() {
                _, throwable -> errorMessage = true
            }
        ) {
            AuthorizationServices().logIn(email, password)
            onNavigationUp()
        }
    }

    fun signUpClick(onNavigationUp: () -> Unit = {}) {
        viewModelScope.launch(
            CoroutineExceptionHandler {
                    _, throwable -> errorMessage = true
            }
        ) {
            AuthorizationServices().signUp(email, password)
            onNavigationUp()
        }
    }
}