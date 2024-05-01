package com.example.semestralnapraca.userInterface

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.semestralnapraca.data.Database
import com.example.semestralnapraca.data.QuestionData
import com.example.semestralnapraca.data.QuizData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizCreationViewModel(): ViewModel() {
    private val _creationState = MutableStateFlow(QuizCreationUiState())
    val creationState: StateFlow<QuizCreationUiState> = _creationState
    val database = Database.getInstance()
    private fun createNewQuiz() {
        CoroutineScope(Dispatchers.Main).launch {
            val id = database.addQuizToDatabase(QuizData("Name"))
            _creationState.value = _creationState.value.copy(quizID = id)
            loadQuiz(id)
        }
    }
    fun loadQuiz(quizID:String) {
        if (quizID.equals("")){
            createNewQuiz()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                _creationState.value = _creationState.value.copy(quizID = quizID)
                val quiz = database.loadQuizFromDatabase(quizID)
                _creationState.value = _creationState.value.copy(quiz = MutableStateFlow(quiz))
                //loadAllQuestions()
            }
    }
}

    private fun loadAllQuestions() {
        TODO("Not yet implemented")
    }

    fun move(forward: Boolean) {
        var position = _creationState.value.curentPositionInList
        val size = _creationState.value.qustionsIDS.size

        CoroutineScope(Dispatchers.Main).launch {
            if (size == 0) {
                createNewQuestionAndLoadData(0)
            } else if (position == 0 && !forward) {
                // Do nothing
            } else if (position == size - 1 && forward) {
                saveQuestionData(position)
                createNewQuestionAndLoadData(position + 1)
                _creationState.value = _creationState.value.copy(curentPositionInList = position + 1)
            } else {
                var newPosition = position
                if (!forward) {
                    newPosition -= 1
                } else {
                    newPosition += 1
                }
                saveQuestionData(position)
                loadDataFromQuestions(newPosition)
                _creationState.value = _creationState.value.copy(curentPositionInList = newPosition)
            }
        }
    }

    private suspend fun createNewQuestionAndLoadData(position: Int) {
        createNewQuestion()
        loadDataFromQuestions(position)
    }

    private suspend fun saveQuestionData(position: Int) {
        val info: HashMap<String, Any> = hashMapOf(
            "content" to (_creationState.value.currentQuestion?.content ?: ""),
            "numberOfAnswers" to (_creationState.value.currentQuestion?.numberOfAnswers ?: 0)
        )

        val qustionsIDS = _creationState.value.qustionsIDS
        if (qustionsIDS.isEmpty()) {
            Log.e("ERROR", "Questions list is empty")
            return
        }
        if (qustionsIDS.size == position) {
            Log.e("ERROR", "position is out of bound")
            return
        }

        database.updateContentInDatabase("quizzes",  listOf(
            _creationState.value.quizID,
            "questions",
            qustionsIDS[position]
        ),info)
    }

    private suspend fun loadDataFromQuestions(i: Int) {
        _creationState.value = _creationState.value.copy(currentQuestion = database.loadQuestionFromDatabase(_creationState.value.quizID,_creationState.value.qustionsIDS[i]))
        _creationState.value.currentQuestion?.let { changeContent(it.content) }
    }

    private suspend fun createNewQuestion() {
        _creationState.value.qustionsIDS.add(database.addQuestionToDatabase(_creationState.value.quizID,QuestionData(quizID = _creationState.value.quizID)))

        _creationState.value.quiz.value = _creationState.value.quiz.value.copy(numberOfQuestions = _creationState.value.quiz.value.numberOfQuestions+1)
        database.updateContentInDatabase("quizzes", listOf(_creationState.value.quizID), hashMapOf("numberOfQuestions" to _creationState.value.quiz.value.numberOfQuestions))
    }

    fun changeContent(it: String) {
        _creationState.value = _creationState.value.copy(currentQuestion = creationState.value.currentQuestion?.copy(content = it))
    }
}

data class QuizCreationUiState(
    val quizID: String = "",
    val quiz: MutableStateFlow<QuizData> = MutableStateFlow(QuizData("",quizID)),
    val qustionsIDS: ArrayList<String> = arrayListOf(),
    val curentPositionInList: Int = 0,
    val currentQuestion: QuestionData? = null,
    val questionContent:String = ""
)