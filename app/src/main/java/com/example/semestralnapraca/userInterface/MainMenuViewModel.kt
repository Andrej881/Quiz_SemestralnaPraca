package com.example.semestralnapraca.userInterface

import androidx.lifecycle.ViewModel
import com.example.semestralnapraca.AuthorizationServices

class MainMenuViewModel: ViewModel() {
    fun signOutButton() {
        AuthorizationServices().signOut()
    }

}