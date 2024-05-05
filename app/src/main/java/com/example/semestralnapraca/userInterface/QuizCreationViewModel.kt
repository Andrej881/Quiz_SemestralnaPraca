package com.example.semestralnapraca.userInterface

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.semestralnapraca.data.AnswerData
import com.example.semestralnapraca.data.Database
import com.example.semestralnapraca.data.QuestionData
import com.example.semestralnapraca.data.QuizData
import com.example.semestralnapraca.data.StatData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizCreationViewModel(): ViewModel() {
    private val _creationState = MutableStateFlow(QuizCreationUiState())
    val creationState: StateFlow<QuizCreationUiState> = _creationState
    private val database = Database.getInstance()
    private var quizBackUp = QuizBackUp()

    private fun createNewQuiz() {
        CoroutineScope(Dispatchers.Main).launch {
            val id = database.addQuizToDatabase(QuizData("Name"))
            _creationState.value = _creationState.value.copy(quizID = id)
            val quiz = database.loadQuizFromDatabase(id)
            _creationState.value = _creationState.value.copy(quiz = MutableStateFlow(quiz))
            createNewQuestionAndLoadData(0)
        }
    }
    fun loadQuiz(quizID:String) {
        if (quizID.equals("")){
            _creationState.value = _creationState.value.copy(newQuiz = true)
            createNewQuiz()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                _creationState.value = _creationState.value.copy(quizID = quizID)
                val quiz = database.loadQuizFromDatabase(quizID)
                _creationState.value = _creationState.value.copy(
                    quiz = MutableStateFlow(quiz),
                    quizName = quiz.quizName,
                    quizTime = quiz.time.toString()
                )
                loadAllQuestions()
                saveQuizBackUp()
            }
        }

    }

    private suspend fun saveQuizBackUp() {
        val questions: List<QuestionData> = database.loadQuestionsFromDatabase(_creationState.value.quizID)
        val answers: ArrayList<AnswerData> = arrayListOf()
        val statistics = database.loadStatisticsFromDatabase(_creationState.value.quizID)

        questions.forEach {
            database.loadAnswersFromDatabase(_creationState.value.quizID,it.questionID)
            .forEach{
                answers.add(it)
            }
        }


        quizBackUp = quizBackUp.copy(
            quizID = _creationState.value.quizID,
            quiz = _creationState.value.quiz.value,
            questions = questions,
            answers = answers,
            statistics = statistics
        )
    }

    private suspend fun loadAllQuestions() {
        val data: List<QuestionData> = database.loadQuestionsFromDatabase(_creationState.value.quizID)
        data.forEach {
            _creationState.value.qustionsIDS.add(it.questionID)
            Log.d("Loading question $it","Loading question $it")
        }
        Log.d("LOADED",_creationState.value.qustionsIDS.size.toString())
        loadDataFromQuestion(0)
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
                loadDataFromQuestion(newPosition)
                _creationState.value = _creationState.value.copy(curentPositionInList = newPosition)
            }
        }
    }

    private suspend fun createNewQuestionAndLoadData(position: Int) {
        createNewQuestion()
        loadDataFromQuestion(position)
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

    private suspend fun loadDataFromQuestion(i: Int) {
        _creationState.value = _creationState.value.copy(currentQuestion = database.loadQuestionFromDatabase(_creationState.value.quizID,_creationState.value.qustionsIDS[i]))
        _creationState.value.currentQuestion?.let { changeContent(it.content) }
        loadAnswersFromDatabase()
    }

    private suspend fun createNewQuestion() {
        _creationState.value.qustionsIDS.add(database.addQuestionToDatabase(_creationState.value.quizID,QuestionData(quizID = _creationState.value.quizID)))

        _creationState.value.quiz.value = _creationState.value.quiz.value.copy(numberOfQuestions = _creationState.value.quiz.value.numberOfQuestions+1)
        database.updateContentInDatabase("quizzes", listOf(_creationState.value.quizID), hashMapOf("numberOfQuestions" to _creationState.value.quiz.value.numberOfQuestions))

        Log.d("AAAAAAAAAAAAAAAAAA","${_creationState.value.quiz.value.numberOfQuestions}")
    }

    fun changeContent(it: String) {
        _creationState.value = _creationState.value.copy(currentQuestion = creationState.value.currentQuestion?.copy(content = it))
    }

    fun changeShowAddingAnswer(b: Boolean) {
        _creationState.value = _creationState.value.copy(showingAnswer = b)
    }

    fun changeAnswerContent(content : String) {
        _creationState.value = _creationState.value.copy(currentAnswerContent = content)
    }

    fun changeAnswerCorrectnes(correct : Boolean) {
        _creationState.value = _creationState.value.copy(currentAnswerCorrectness = correct)
    }
    fun changeAnswerPoints(points : String) {
        _creationState.value = _creationState.value.copy(currentAnswerPoints = points)
    }
    private fun createAnswer() {
        CoroutineScope(Dispatchers.Main).launch {
            val id: String = database.addAnswerToDatabase(
                _creationState.value.quizID,
                _creationState.value.currentQuestion?.questionID ?: "",
                AnswerData(questionID = _creationState.value.currentQuestion?.questionID ?: "",
                    points = _creationState.value.currentAnswerPoints.toInt(),
                    correct = _creationState.value.currentAnswerCorrectness,
                    content = _creationState.value.currentAnswerContent
                    )
            )
            loadAnswersFromDatabase()
            _creationState.value = _creationState.value.copy(currentAnswerID = id)
            resetShownEditAnswerContent()
        }
    }
    fun editAnswer() {
        if (_creationState.value.currentAnswerID.equals("")) {
            createAnswer()
        } else {
            editExistingAnswer(_creationState.value.currentAnswerID)
        }
    }

    private fun loadAnswersFromDatabase() {
        CoroutineScope(Dispatchers.Main).launch {
            val answers = withContext(Dispatchers.IO) {
                database.loadAnswersFromDatabase(quizID = _creationState.value.quizID, questionID = _creationState.value.currentQuestion?.questionID ?: "")
            }
            _creationState.value = _creationState.value.copy(answers = answers)
        }

    }

    private fun editExistingAnswer(currentAnswerID: String) {
        if(_creationState.value.currentAnswerPoints.equals("")) {
            changeAnswerPoints("0")
        }
        val updateInfo: HashMap<String, Any> = hashMapOf(
            "points" to _creationState.value.currentAnswerPoints.toInt(),
            "correct" to _creationState.value.currentAnswerCorrectness,
            "content" to _creationState.value.currentAnswerContent
        )
        resetShownEditAnswerContent()
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                if(currentAnswerID.equals("") || currentAnswerID.equals("answers")) {
                    Log.e("ERROR","id does not exist")
                }
                val path = listOf(
                    _creationState.value.quizID,
                    "questions",
                    _creationState.value.currentQuestion?.questionID ?:"",
                    "answers",
                    currentAnswerID

                )
                database.updateContentInDatabase(
                    table = "quizzes",
                    childPath = path,
                    updateInfo = updateInfo
                )
                loadAnswersFromDatabase()
            }
        }
    }

    private fun resetShownEditAnswerContent() {
        _creationState.value = _creationState.value.copy(currentAnswerPoints = "0", currentAnswerCorrectness = false, currentAnswerContent = "")
    }

    fun setCurrentAnswerID(it: String) {
        _creationState.value = _creationState.value.copy(currentAnswerID = it)
    }

    fun deleteAnswer() {
        if(_creationState.value.currentAnswerID.equals("")){
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                database.removeAnswerFromDatabase(
                    quizID =  _creationState.value.quizID,
                    questionID = _creationState.value.currentQuestion?.questionID
                    ?: "",
                    currentAnswerID =  _creationState.value.currentAnswerID,)
            }
            loadAnswersFromDatabase()
        }
    }

    fun deleteQuiz(nav: () -> Unit) {
        if (_creationState.value.newQuiz) {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    database.removeQuizFromDatabase(_creationState.value.quizID)
                }
            }
        } else {
            loadQuizBackUp()
        }
        nav()
    }

    private fun loadQuizBackUp() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                database.removeQuizFromDatabase(_creationState.value.quizID)

                database.addQuizToDatabase(quiz = quizBackUp.quiz)
                quizBackUp.questions.forEach {
                    database.addQuestionToDatabase(quizBackUp.quizID,it)
                }
                quizBackUp.answers.forEach {
                    database.addAnswerToDatabase(quizBackUp.quizID,it.questionID,it)
                }
                quizBackUp.statistics.forEach {
                    database.addStatToDatabase(quizBackUp.quizID, it)
                }
            }
        }
    }

    fun changeShowSavingQuiz(show: Boolean) {
        _creationState.value = _creationState.value.copy(saving = show)
    }

    fun saveQuiz(nav: () -> Unit) {
        if (_creationState.value.quizTime.equals("")) _creationState.value = _creationState.value.copy(quizTime = "0")
        val updateInfo:HashMap<String, Any> = hashMapOf(
            "name" to _creationState.value.quizName,
            "time" to _creationState.value.quizTime
        )
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                saveQuestionData(_creationState.value.curentPositionInList)
                val path = listOf(
                    _creationState.value.quizID
                )
                database.updateContentInDatabase("quizzes",path,updateInfo)
            }
        }
        nav()
    }

    fun changeNameContent(it: String) {
        _creationState.value = _creationState.value.copy(quizName = it)
    }

    fun changeTimeContent(it: String) {
        _creationState.value = _creationState.value.copy(quizTime = it)
    }

    fun deleteQuestion() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val questionID =  _creationState.value.qustionsIDS[_creationState.value.curentPositionInList]
                val size = _creationState.value.qustionsIDS.size
                changeContent("")
                if (size < 2) {
                    _creationState.value.answers.forEach {
                        _creationState.value = _creationState.value.copy(currentAnswerID = it.answerID)
                        deleteAnswer()
                    }
                    _creationState.value = _creationState.value.copy(currentAnswerID = "")
                    return@withContext
                }
                var position = _creationState.value.curentPositionInList
                if (position > 0) {
                    position -= 1
                    _creationState.value = _creationState.value.copy(curentPositionInList =  position)
                } else {
                    _creationState.value = _creationState.value.copy(curentPositionInList =  position)
                    position += 1
                }
                loadDataFromQuestion( position)
                _creationState.value.quiz.value = _creationState.value.quiz.value.copy(numberOfQuestions = _creationState.value.quiz.value.numberOfQuestions-1)
                database.removeQuestionFromDatabase(_creationState.value.quizID,questionID)
                database.updateContentInDatabase("quizzes", listOf(_creationState.value.quizID),
                    hashMapOf("numberOfQuestions" to _creationState.value.quiz.value.numberOfQuestions))
                _creationState.value.qustionsIDS.remove(questionID)
            }
        }
    }
}

data class QuizCreationUiState(
    val quizID: String = "",
    val quiz: MutableStateFlow<QuizData> = MutableStateFlow(QuizData("",quizID)),
    val qustionsIDS: ArrayList<String> = arrayListOf(),
    val curentPositionInList: Int = 0,
    val currentQuestion: QuestionData? = null,
    val questionContent:String = "",
    val showingAnswer:Boolean = false,
    val currentAnswerContent: String = "",
    val currentAnswerPoints: String = "0",
    val currentAnswerCorrectness: Boolean = false,
    val currentAnswerID: String = "",
    val answers: List<AnswerData> = listOf(),//answersOfCurrentShownQuestion
    val newQuiz:Boolean = false,
    val saving:Boolean = false,
    val quizTime:String = "",
    val quizName: String = "",
    val quizBackUp: QuizBackUp? = null
)

data class QuizBackUp(
    val quizID: String = "",
    val quiz: QuizData = QuizData(""),
    val questions: List<QuestionData> = listOf(),
    val answers: ArrayList<AnswerData> = arrayListOf(),
    val statistics: List<StatData> = listOf()
)