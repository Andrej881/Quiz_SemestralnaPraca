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

class QuizCreationViewModel(
): ViewModel() {
    private val _creationState = MutableStateFlow(QuizCreationUiState())
    val creationState: StateFlow<QuizCreationUiState> = _creationState
    val database = Database.getInstance()
    private fun createNewQuiz() {
        CoroutineScope(Dispatchers.Main).launch {
            val id = database.addQuizToDatabase(QuizData("Name"))
            _creationState.value = _creationState.value.copy(quizID = (id))
            loadQuiz(id)
        }
    }
    fun loadQuiz(quizID:String) {
        if (quizID.equals("")){
            createNewQuiz()
        } else {
            _creationState.value = _creationState.value.copy(quizID = quizID)
            CoroutineScope(Dispatchers.Main).launch {
                _creationState.value = _creationState.value.copy(quiz = MutableStateFlow(database.loadQuizFromDatabase(quizID)))
                //loadAllQuestions()
            }
    }
}

    private fun loadAllQuestions() {
        TODO("Not yet implemented")
    }

    fun move(forward: Boolean) {
        var position = _creationState.value.curentPositionInList
        if (forward) {
            if (position >= _creationState.value.qustionsIDS.size-1) {
                createNewQuestion() //Creates question in database with current quizID
                if (position == 0) {
                    _creationState.value = _creationState.value.copy(curentPositionInList = (position))
                    saveQuestionData(position) //Changes data in database of current question(content to currentContent, and numberOfAnswers to number of answers)
                    return
                }
            }
            _creationState.value = _creationState.value.copy(curentPositionInList = (position + 1))
            saveQuestionData(position)
            loadDataFromQuestions(position)//load data from nextPosition()

        } else {

            if (position > 0) {
                _creationState.value =
                    _creationState.value.copy(curentPositionInList = (position - 1))
                saveQuestionData(position)
                loadDataFromQuestions(position - 1)
            }
        }
        _creationState.value.qustionsIDS.forEach(){Log.d("MOVING", it)}

    }

    private fun saveQuestionData(position: Int) {
        val info: HashMap<String, Any> = hashMapOf(
            "content" to (_creationState.value.currentQuestion?.content ?: ""),
            "numberOfAnswers" to (_creationState.value.currentQuestion?.numberOfAnswers ?: 0)
        )

        CoroutineScope(Dispatchers.Main).launch {
            database.updateContentInDatabase("quizzes",  listOf(
                _creationState.value.quizID,
                "questions",
                _creationState.value.qustionsIDS[position]
            ),info)
        }
    }

    private fun loadDataFromQuestions(i: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            _creationState.value = _creationState.value.copy(currentQuestion = database.loadQuestionFromDatabase(_creationState.value.quizID,_creationState.value.qustionsIDS[i]))
            _creationState.value.currentQuestion?.let { changeContent(it.content) }
        }
    }

    private fun createNewQuestion() {

        CoroutineScope(Dispatchers.Main).launch {
            _creationState.value.qustionsIDS.add(database.addQuestionToDatabase(_creationState.value.quizID,QuestionData(quizID = _creationState.value.quizID)))

            _creationState.value.quiz.value = _creationState.value.quiz.value.copy(numberOfQuestions = _creationState.value.quiz.value.numberOfQuestions+1)
            database.updateContentInDatabase("quizzes", listOf(_creationState.value.quizID), hashMapOf("numberOfQuestions" to _creationState.value.quiz.value.numberOfQuestions))
        }
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