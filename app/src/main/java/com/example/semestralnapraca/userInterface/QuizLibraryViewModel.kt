package com.example.semestralnapraca.userInterface

import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import com.example.semestralnapraca.R
import com.example.semestralnapraca.data.Database
import com.example.semestralnapraca.data.QuizData
import kotlinx.coroutines.flow.MutableStateFlow
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
                _quizzesState.value = _quizzesState.value.copy(quizzes = quizList)
            }
        })
    }

    fun removeQuiz(quizID: String) {
        database.removeQuizFromDatabase(quizID)
        loadQuizzesFromDatabase()
    }

    fun rename() {
        val updateInfo = hashMapOf(
            "name" to _quizzesState.value.textForRenaming,)
        database.updateQuizInDatabase(_quizzesState.value.quizID, updateInfo)
        _quizzesState.value = _quizzesState.value.copy(quizID = "")
        loadQuizzesFromDatabase()
    }

    fun changeRenamingState(state: Boolean) {
        _quizzesState.value = _quizzesState.value.copy(renaming = state)
    }

    fun showRenamingDialog(quizID: String) {
        changeRenamingState(true)
        _quizzesState.value = _quizzesState.value.copy(quizID = quizID)
    }

    fun updateRenaming(it: String) {
        _quizzesState.value = _quizzesState.value.copy(textForRenaming = it)
    }

}

data class QuizLibraryUiState(val quizzes: List<QuizData> = listOf(),
    val renaming: Boolean = false,
    val quizID: String = "",
    val textForRenaming: String = "")
