package com.example.semestralnapraca.userInterface

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.semestralnapraca.data.Database
import com.example.semestralnapraca.data.QuestionData
import com.example.semestralnapraca.data.QuizData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class QuizCreationViewModel(
): ViewModel() {
    private val _creationState = MutableStateFlow(QuizCreationUiState())
    val creationState: StateFlow<QuizCreationUiState> = _creationState
    val database = Database.getInstance()
    private fun createNewQuiz() {
        val id = database.addQuizToDatabase(QuizData("Name"))
        _creationState.value = _creationState.value.copy(quizID = (id))
        loadQuiz(id)
    }
    fun loadQuiz(quizID:String) {
        if (quizID.equals("")){
            createNewQuiz()
        } else {
            database.loadQuizFromDatabase(quizID, object : Database.QuizLoadListener {
                override fun onQuizLoaded(quiz:QuizData) {
                    _creationState.value = _creationState.value.copy(quiz = quiz)
                }
        })
    }
}

    fun move(forward: Boolean) {
        val position = _creationState.value.curentPositionInList
        if (forward) {
            if (position == _creationState.value.qustionsIDS.size) {
                createNewQuestion()
            }
            _creationState.value = _creationState.value.copy(curentPositionInList = (position + 1))
            loadDataFromQuestions(position)

        } else {
            if (position != 0) {
                _creationState.value = _creationState.value.copy(curentPositionInList = (position - 1))
                loadDataFromQuestions(position - 1)
            }
        }


    }
    private fun loadDataFromQuestions(i: Int) {
        database.loadQuestionFromDatabase(_creationState.value.qustionsIDS[i], object : Database.QuestionLoadListener {
            override fun onQuestionLoaded(question:QuestionData) {
                _creationState.value = _creationState.value.copy(currentQuestion = question)
            }
        })
    }

    private fun createNewQuestion() {
        _creationState.value.qustionsIDS.add(database.addQuestionToDatabase(_creationState.value.quizID,QuestionData(quizID = _creationState.value.quizID)))
    }
}

data class QuizCreationUiState(
    val quizID: String = "",
    val quiz: QuizData? = null,
    val qustionsIDS: ArrayList<String> = arrayListOf(),
    val curentPositionInList: Int = 0,
    val currentQuestion: QuestionData? = null)