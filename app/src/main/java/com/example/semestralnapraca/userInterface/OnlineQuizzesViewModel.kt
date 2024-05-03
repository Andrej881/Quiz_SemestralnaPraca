package com.example.semestralnapraca.userInterface

import androidx.lifecycle.ViewModel
import com.example.semestralnapraca.data.Database
import com.example.semestralnapraca.data.QuizData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OnlineQuizzesViewModel() : ViewModel() {
    private val database = Database.getInstance()

    private val _quizzesState = MutableStateFlow(OnlineQuizzesUiState())
    val quizzesState: StateFlow<OnlineQuizzesUiState> = _quizzesState

    fun changeSearchedQuizID(id: String) {
        _quizzesState.value = _quizzesState.value.copy(searchedShareID = id)
    }

    fun loadQuizzesFromDatabase(){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val quizzes = withContext(Dispatchers.IO) {
                    database.loadQuizzesFromDatabase(sharedQuizzes = true)
                }
                _quizzesState.value = _quizzesState.value.copy(quizzes = quizzes)
            } catch (e: Exception) {
                // Handle error here
                e.printStackTrace()
            }
        }
    }
    fun searchQuizWihtID(searchedShareID: String) {
        var quizID = ""
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