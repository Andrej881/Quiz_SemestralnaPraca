package com.example.semestralnapraca.userInterface

import android.os.CountDownTimer
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
                startCountdown(quiz.time.toLong())
            }
        }
    }
    private suspend fun loadAllQuestions() {
        val data: List<QuestionData> = database.loadQuestionsFromDatabase(_gameUiState.value.quizID)
        data.forEach {
            _gameUiState.value.questions.add(it)
            Log.d("Loading question $it","Loading question $it")
        }
        Log.d("LOADED",_gameUiState.value.questions.size.toString())
        loadAnswersFromDatabase()
        loadDataFromQuestion(0)
    }
    private fun loadDataFromQuestion(i: Int) {
        val question = _gameUiState.value.questions[i]
        _gameUiState.value = _gameUiState.value.copy(currentQuestionID = question.questionID, currentQuestionContent = question.content)
        showAnswers(question.questionID)
    }

    private fun showAnswers(questionID: String) {
        _gameUiState.value = _gameUiState.value.copy(answers = _gameUiState.value.allQuizAnswers[questionID]?: listOf())
    }

    private suspend fun loadAnswersFromDatabase() {
        for (id:QuestionData in _gameUiState.value.questions) {
            val answers = database.loadAnswersFromDatabase(
                quizID = _gameUiState.value.quizID,
                questionID = id.questionID
            )
            _gameUiState.value.allQuizAnswers.put(id.questionID,answers)
        }
    }

    fun move(forward: Boolean) {
        var position = _gameUiState.value.currentQuestionNumber-1
        val size = _gameUiState.value.questions.size

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

    fun startCountdown(minutes: Long) {
        val milliseconds = minutes * 60 * 1000
        object : CountDownTimer(milliseconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)
                _gameUiState.value = _gameUiState.value.copy(quizTime = timeString)
            }

            override fun onFinish() {
            }
        }.start()
    }

    fun changeAnswerClickedState(id : String) {
        if (_gameUiState.value.clickedAnswers.contains(id)) {
            _gameUiState.value.clickedAnswers.remove(id)
        } else {
            _gameUiState.value.clickedAnswers.add(id)
        }
    }

}
data class QuizGameUIState(
    val quizID:String = "",
    val questions: ArrayList<QuestionData> = arrayListOf(),
    val numberOfQuestions:Int = 0,
    val currentQuestionNumber:Int = 1,
    val quizTime:String = "",
    val answers: List<AnswerData> = listOf(),
    val currentQuestionID:String = "",
    val currentQuestionContent:String = "",
    val points: Int = 0,
    val clickedAnswers: ArrayList<String> = arrayListOf(),
    val allQuizAnswers: HashMap<String, List<AnswerData>> = hashMapOf()
)