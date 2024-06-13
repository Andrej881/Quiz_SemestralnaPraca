package com.example.semestralnapraca

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
/**
* Prihlasovanie do firebase
* trieda spravená podľa tutorialu https://firebase.google.com/docs/auth/android/start
* */
class AuthorizationServices
{
    /**
    * Prihlásenie do aplikácie
    *
    * @param email email, ktorým sa prihlásite
    * @param password heslo, ktorým sa prihlásite
    * */
    suspend fun logIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()

    }
    /**
    * Registrácia do aplikácie
    *
    * @param email email, ktorým sa prihlásite
    * @param password heslo, ktorým sa prihlásite
    * */
    suspend fun signUp(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
    }
    /**
    * Odhlásenie z aplikácie
    * */
    fun signOut() {
        Firebase.auth.signOut()
    }
}