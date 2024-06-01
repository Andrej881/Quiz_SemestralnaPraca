package com.example.semestralnapraca.userInterface

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semestralnapraca.AuthorizationServices
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
/**
 * viewModel pre obrazovku autorizácie (https://firebase.google.com/docs/auth/android/start)
 * */
class AuthorizationViewModel : ViewModel() {

    var errorMessage by mutableStateOf(false)
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    /**
     * @param newEmail nova hodnota emailu, ktorá sa nastaví
     * */
    fun updateEmail(newEmail: String) {
        email = newEmail
    }
    /**
     * @param newPassword nová hodnota hesla, ktorá sa nastaví
     * */
    fun updatePassword(newPassword: String) {
        password = newPassword
    }
    /**
     * Prihlásenie do aplikácie
     * */
    fun logInClick(onNavigationUp: () -> Unit = {}) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")
        viewModelScope.launch(
            CoroutineExceptionHandler {
                    _, _ -> errorMessage = true
            }
        ) {
            AuthorizationServices().logIn(email, password)
            onNavigationUp()
        }
    }
    /**
     * Registrácia do aplikácie
     * */
    fun signUpClick(onNavigationUp: () -> Unit = {}) {
        viewModelScope.launch(
            CoroutineExceptionHandler {
                    _, _ -> errorMessage = true
            }
        ) {
            AuthorizationServices().signUp(email, password)
            onNavigationUp()
        }
    }
}