package com.example.semestralnapraca.userInterface

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.example.semestralnapraca.data.AnswerData
import com.example.semestralnapraca.data.Database
import com.example.semestralnapraca.data.QuestionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizGameViewModel: ViewModel() {
    private val _gameUiState =  MutableStateFlow(QuizGameUIState())
    val gameUiState: StateFlow<QuizGameUIState> = _gameUiState

    private val database = Database.getInstance()

    fun loadQuiz(quizID: String) {
        if (quizID.equals("")) {
            Log.e("ERROR QuizGameViewModel","error loading quiz: quizID = ''")
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                _gameUiState.value = _gameUiState.value.copy(quizID = quizID)
                val quiz = database.loadQuizFromDatabase(quizID)
                _gameUiState.value = _gameUiState.value.copy(
                    quizID = quiz.quizId,
                    numberOfQuestions = quiz.numberOfQuestions,
                    quizTime = quiz.time.toString()
                )
                loadAllQuestions()
            }
        }
    }
    private suspend fun loadAllQuestions() {
        val data: List<QuestionData> = database.loadQuestionsFromDatabase(_gameUiState.value.quizID)
        data.forEach {
            _gameUiState.value.questionIDS.add(it.questionID)
            Log.d("Loading question $it","Loading question $it")
        }
        Log.d("LOADED",_gameUiState.value.questionIDS.size.toString())
        loadDataFromQuestion(0)
    }
    private suspend fun loadDataFromQuestion(i: Int) {
        val question = database.loadQuestionFromDatabase(_gameUiState.value.quizID,_gameUiState.value.questionIDS[i])
        _gameUiState.value = _gameUiState.value.copy(currentQuestionID = question.questionID, currentQuestionContent = question.content)
        loadAnswersFromDatabase()
    }

    private suspend fun loadAnswersFromDatabase() {
        val answers = database.loadAnswersFromDatabase(
            quizID = _gameUiState.value.quizID,
            questionID = _gameUiState.value.currentQuestionID
        )
        _gameUiState.value = _gameUiState.value.copy(answers = answers)
    }

    fun move(forward: Boolean) {
        var position = _gameUiState.value.currentQuestionNumber-1
        val size = _gameUiState.value.questionIDS.size

        CoroutineScope(Dispatchers.Main).launch {
            if (position == 0 && !forward) {
                // Do nothing
            } else if (position == size - 1 && forward) {
                // endQuiz()
            } else {
                var newPosition = position
                if (!forward) {
                    newPosition -= 1
                } else {
                    newPosition += 1
                }
                Log.d("MOVEINGAME", newPosition.toString())
                loadDataFromQuestion(newPosition)
                _gameUiState.value = _gameUiState.value.copy(currentQuestionNumber = (newPosition + 1))
            }
        }
    }


}
data class QuizGameUIState(
    val quizID:String = "",
    val questionIDS: ArrayList<String> = arrayListOf(),
    val numberOfQuestions:Int = 0,
    val currentQuestionNumber:Int = 1,
    val quizTime:String = "",
    val answers: List<AnswerData> = listOf(),
    val currentQuestionID:String = "",
    val currentQuestionContent:String = "",
    val points: Int = 0
)