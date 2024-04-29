package com.example.semestralnapraca.userInterface

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semestralnapraca.data.Database
import com.example.semestralnapraca.data.QuizData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow

class QuizLibraryViewModel(): ViewModel() {
    private val _quizzesState = MutableStateFlow(QuizLibraryUiState())
    val quizzesState: StateFlow<QuizLibraryUiState> = _quizzesState

    val database = Database()
    init {
        loadQuizzesFromDatabase()
    }
    fun loadQuizzesFromDatabase(){
        database.loadQuizFromDatabase(object : Database.QuizLoadListener {
            override fun onQuizzesLoaded(quizList: List<QuizData>) {
                _quizzesState.value = QuizLibraryUiState(quizzes = quizList)
            }
        })
    }

}

data class QuizLibraryUiState(val quizzes: List<QuizData> = listOf())
