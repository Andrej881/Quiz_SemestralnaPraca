package com.example.semestralnapraca.userInterface

import androidx.lifecycle.ViewModel
import com.example.semestralnapraca.AuthorizationServices

/**
 * viewModel pre obrazovku Main Menu
 * */
class MainMenuViewModel: ViewModel() {
    /**
     * odhlási uživateľa z aplikácie
     * */
    fun signOutButton() {
        AuthorizationServices().signOut()
    }

}