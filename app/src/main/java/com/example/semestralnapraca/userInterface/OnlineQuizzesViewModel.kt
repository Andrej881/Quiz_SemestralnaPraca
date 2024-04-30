package com.example.semestralnapraca.userInterface

import androidx.lifecycle.ViewModel
import com.example.semestralnapraca.data.Database
import com.example.semestralnapraca.data.QuizData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OnlineQuizzesViewModel() : ViewModel() {
    private val database = Database()

    init {
        loadQuizzesFromDatabase()
    }

    private val _quizzesState = MutableStateFlow(OnlineQuizzesUiState())
    val quizzesState: StateFlow<OnlineQuizzesUiState> = _quizzesState

    fun changeSearchedQuizID(id: String) {
        _quizzesState.value = _quizzesState.value.copy(searchedShareID = id)
    }

    fun loadQuizzesFromDatabase(){
        database.loadQuizFromDatabase(object : Database.QuizLoadListener {
            override fun onQuizzesLoaded(quizList: List<QuizData>) {
                _quizzesState.value = _quizzesState.value.copy(quizzes = quizList)
            }
        }, sharedQuizzes = true)
    }

    fun searchQuizWihtID(searchedShareID: String) {
        var quizID: String = ""
        _quizzesState.value.quizzes.forEach {
            if (it.shareID == searchedShareID) {
                quizID = it.quizId
            }
        }
        if (quizID.equals("")) {
            changeShowQuizDoesNotExist(true)
        } else {
            _quizzesState.value = _quizzesState.value.copy(quizID = quizID)
        }
    }

    fun changeShowQuizDoesNotExist(b: Boolean) {
        _quizzesState.value = _quizzesState.value.copy(showQuizDoesNotExist = b)
    }

    fun changeQuizID(s: String) {
        _quizzesState.value = _quizzesState.value.copy(quizID = s)
    }
}

data class OnlineQuizzesUiState(
    val quizzes: List<QuizData> = listOf(),
    val quizID: String = "",
    val searchedShareID: String = "",
    val showQuizDoesNotExist: Boolean = false
)