package com.example.semestralnapraca

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class AuthorizationServices
{
    suspend fun logIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()

    }

    suspend fun signUp(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
    }

    fun signOut() {
        Firebase.auth.signOut()
    }
}