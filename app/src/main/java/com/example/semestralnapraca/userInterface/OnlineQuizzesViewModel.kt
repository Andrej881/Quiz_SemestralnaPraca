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
/**
 * viewModel pre obrazovku OnlineQuizzes
 * */
class OnlineQuizzesViewModel : ViewModel() {
    private val database = Database.getInstance()

    private val _quizzesState = MutableStateFlow(OnlineQuizzesUiState())
    val quizzesState: StateFlow<OnlineQuizzesUiState> = _quizzesState
    /**
     * @param id zmený hodnotu hladaného id na dané id
     * */
    fun changeSearchedQuizID(id: String) {
        _quizzesState.value = _quizzesState.value.copy(searchedShareID = id)
    }
    /**
     * načíta kvízy na zobrazenie
     * */
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
    /***
     * Pokúsi sa najsť a spustiť kvíz
     *
     * @param searchedShareID id posla, ktorého sa hľadá kvíz
     * */
    fun searchQuizWihtID(searchedShareID: String) {
        var quizID = ""
        _quizzesState.value.quizzes.forEach {
            if (it.shareID == searchedShareID) {
                quizID = it.quizId
            }
        }
        if (quizID == "") {
            changeShowQuizDoesNotExist(true)
        } else {
            _quizzesState.value = _quizzesState.value.copy(quizID = quizID)
        }
    }
    /**
     * @param b zmený hodnotu toho či sa má zobraziť to, že sa nenašiel kvíz
     * */
    fun changeShowQuizDoesNotExist(b: Boolean) {
        _quizzesState.value = _quizzesState.value.copy(showQuizDoesNotExist = b)
    }
    /**
     * @param s zmený hodnotu quizID
     * */
    fun changeQuizID(s: String) {
        _quizzesState.value = _quizzesState.value.copy(quizID = s)
    }
}

/**
 * informácia potrebne pre viewModel
 *
 * @param quizzes list načítaných kvízov
 * @param quizID id kvízu, ktorý sa spúšťa
 * @param searchedShareID id zdielania pre najdenie kvízu
 * @param showQuizDoesNotExist rozhoduje či sa ma zobraziť upozornenie, že sa hladaný kvíz nenašiel
 * */
data class OnlineQuizzesUiState(
    val quizzes: List<QuizData> = listOf(),
    val quizID: String = "",
    val searchedShareID: String = "",
    val showQuizDoesNotExist: Boolean = false
)