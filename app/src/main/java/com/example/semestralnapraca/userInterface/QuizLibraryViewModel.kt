package com.example.semestralnapraca.userInterface

import androidx.lifecycle.viewModelScope
import com.example.semestralnapraca.data.Quiz
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow

class QuizLibraryViewModel() {

    /*val quizLibraryUiState: StateFlow<QuizLibraryUiState> =
        itemsRepository.getAllItemsStream().map { QuizLibraryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )*/
}

data class QuizLibraryUiState(val quizzList: List<Quiz> = listOf())