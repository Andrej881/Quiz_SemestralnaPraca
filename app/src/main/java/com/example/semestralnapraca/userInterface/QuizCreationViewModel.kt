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
/**
 * viewModel pre obrazovku quizCreation
 * */
class QuizCreationViewModel: ViewModel() {
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
    /**
     * Načíta lebo vytvorí nový kvíz
     *
     * @param quizID id kvízu
     * */
    fun loadQuiz(quizID:String) {
        if (quizID == ""){
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

        questions.forEach { it ->
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
            _creationState.value.questionsIDS.add(it.questionID)
            Log.d("Loading question $it","Loading question $it")
        }
        Log.d("LOADED",_creationState.value.questionsIDS.size.toString())
        loadDataFromQuestion(0)
    }
    /**
     * Logika presunu z jednej otázky kvízu na inú
     *
     * @param forward či sa presúva na následujúcu otázku(true) alebo predchádzajúcu(false)
     * */
    fun move(forward: Boolean) {
        val position = _creationState.value.currentPositionInList
        val size = _creationState.value.questionsIDS.size

        CoroutineScope(Dispatchers.Main).launch {
            if (size == 0) {
                createNewQuestionAndLoadData(0)
            } else if (position == 0 && !forward) {
                // Do nothing
            } else if (position == size - 1 && forward) {
                saveQuestionData(position)
                createNewQuestionAndLoadData(position + 1)
                _creationState.value = _creationState.value.copy(currentPositionInList = position + 1)
            } else {
                var newPosition = position
                if (!forward) {
                    newPosition -= 1
                } else {
                    newPosition += 1
                }
                saveQuestionData(position)
                loadDataFromQuestion(newPosition)
                _creationState.value = _creationState.value.copy(currentPositionInList = newPosition)
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

        val qustionsIDS = _creationState.value.questionsIDS
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
        _creationState.value = _creationState.value.copy(currentQuestion = database.loadQuestionFromDatabase(_creationState.value.quizID,_creationState.value.questionsIDS[i]))
        _creationState.value.currentQuestion?.let { changeContent(it.content) }
        loadAnswersFromDatabase()
    }

    private suspend fun createNewQuestion() {
        _creationState.value.questionsIDS.add(database.addQuestionToDatabase(_creationState.value.quizID,QuestionData(quizID = _creationState.value.quizID)))

        _creationState.value.quiz.value = _creationState.value.quiz.value.copy(numberOfQuestions = _creationState.value.quiz.value.numberOfQuestions+1)
        database.updateContentInDatabase("quizzes", listOf(_creationState.value.quizID), hashMapOf("numberOfQuestions" to _creationState.value.quiz.value.numberOfQuestions))

        Log.d("AAAAAAAAAAAAAAAAAA","${_creationState.value.quiz.value.numberOfQuestions}")
    }
    /**
     * Zmení obsah otázky
     * @param content nový obsah otázky
     * */
    fun changeContent(content: String) {
        _creationState.value = _creationState.value.copy(currentQuestion = creationState.value.currentQuestion?.copy(content = content))
    }
    /**
     * @param show hodnota na aku sa ma zmeniť showingAnswer v uiState
     * */
    fun changeShowAddingAnswer(show: Boolean) {
        _creationState.value = _creationState.value.copy(showingAnswer = show)
    }
    /**
     * @param content hodnota na aku sa ma zmeniť currentAnswerContent v uiState
     * */
    fun changeAnswerContent(content : String) {
        _creationState.value = _creationState.value.copy(currentAnswerContent = content)
    }
    /**
     * @param correct hodnota na aku sa ma zmeniť currentAnswerCorrectness v uiState
     * */
    fun changeAnswerCorrectness(correct : Boolean) {
        _creationState.value = _creationState.value.copy(currentAnswerCorrectness = correct)
    }
    /**
     * @param points hodnota na aku sa ma zmeniť currentAnswerPoints v uiState
     * */
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
    /**
     * Logika upravovanie odpovede
     * */
    fun editAnswer() {
        if (_creationState.value.currentAnswerID == "") {
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
        if(_creationState.value.currentAnswerPoints == "") {
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
                if(currentAnswerID == "" || currentAnswerID == "answers") {
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
    /**
     * @param id hodnota na aku sa ma zmeniť currentAnswerID v uiState
     * */
    fun setCurrentAnswerID(id: String) {
        _creationState.value = _creationState.value.copy(currentAnswerID = id)
    }
    /**
     * odstráni momentálne upravovanú odpoveď
     * */
    fun deleteAnswer() {
        if(_creationState.value.currentAnswerID == ""){
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
    /**
     * Odstráni kvíz
     * */
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
    /**
     * @param show hodnota na aku sa ma zmeniť saving v uiState
     * */
    fun changeShowSavingQuiz(show: Boolean) {
        _creationState.value = _creationState.value.copy(saving = show)
    }
    /**
     * uloži kvíz do databázi
     * @param nav navigácia na inú obrazovku
     * */
    fun saveQuiz(nav: () -> Unit) {
        if (_creationState.value.quizTime == "") _creationState.value = _creationState.value.copy(quizTime = "0")
        val updateInfo:HashMap<String, Any> = hashMapOf(
            "name" to _creationState.value.quizName,
            "time" to _creationState.value.quizTime
        )
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                saveQuestionData(_creationState.value.currentPositionInList)
                val path = listOf(
                    _creationState.value.quizID
                )
                database.updateContentInDatabase("quizzes",path,updateInfo)
            }
        }
        nav()
    }
    /**
     * @param it hodnota na aku sa ma zmeniť quizName v uiState
     * */
    fun changeNameContent(it: String) {
        _creationState.value = _creationState.value.copy(quizName = it)
    }
    /**
     * @param it hodnota na aku sa ma zmeniť quizTime v uiState
     * */
    fun changeTimeContent(it: String) {
        _creationState.value = _creationState.value.copy(quizTime = it)
    }

    /**
     * odstráni otázku
     * */
    fun deleteQuestion() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                val questionID =  _creationState.value.questionsIDS[_creationState.value.currentPositionInList]
                val size = _creationState.value.questionsIDS.size
                changeContent("")
                if (size < 2) {
                    _creationState.value.answers.forEach {
                        _creationState.value = _creationState.value.copy(currentAnswerID = it.answerID)
                        deleteAnswer()
                    }
                    _creationState.value = _creationState.value.copy(currentAnswerID = "")
                    return@withContext
                }
                var position = _creationState.value.currentPositionInList
                if (position > 0) {
                    position -= 1
                    _creationState.value = _creationState.value.copy(currentPositionInList =  position)
                } else {
                    _creationState.value = _creationState.value.copy(currentPositionInList =  position)
                    position += 1
                }
                loadDataFromQuestion( position)
                _creationState.value.quiz.value = _creationState.value.quiz.value.copy(numberOfQuestions = _creationState.value.quiz.value.numberOfQuestions-1)
                database.removeQuestionFromDatabase(_creationState.value.quizID,questionID)
                database.updateContentInDatabase("quizzes", listOf(_creationState.value.quizID),
                    hashMapOf("numberOfQuestions" to _creationState.value.quiz.value.numberOfQuestions))
                _creationState.value.questionsIDS.remove(questionID)
            }
        }
    }
}
/**
 * informácie potrebné pre viewModel
 *
 * @param quizID id kvízu
 * @param quiz informácie kvízu
 * @param questionsIDS idčka všetkých otázok kvízu
 * @param currentPositionInList na akéj otázke kvízu sa uživateľ nachádza
 * @param currentQuestion momentálna otázka
 * @param questionContent obsah otázky
 * @param showingAnswer či má ukázať úprava odpovede
 * @param currentAnswerContent obsah momentálne upravovanej odpovede
 * @param currentAnswerPoints body momentálne upravovanej odpovede
 * @param currentAnswerCorrectness či momentálne upravovana odpoveď správna
 * @param currentAnswerID id momentálne upravovanej odpovede
 * @param answers list odpovede momentálnej otázky
 * @param newQuiz či je tovrení kvíz nový
 * @param saving či sa má ukázať pole na uloženie kvízu
 * @param quizTime čas na dokončenie kvízu
 * @param quizName meno kvízu
 * @param quizBackUp záloha kvízu pre prípad keď sa zruší úprava kvízu
 * */
data class QuizCreationUiState(
    val quizID: String = "",
    val quiz: MutableStateFlow<QuizData> = MutableStateFlow(QuizData("",quizID)),
    val questionsIDS: ArrayList<String> = arrayListOf(),
    val currentPositionInList: Int = 0,
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
/**
 * záloha kvízu pre prípad keď sa zruší úprava kvízu
 *
 * @param quizID id kvízu
 * @param quiz data kvízu
 * @param questions list otázok kvízu
 * @param answers list odpovedí kvízu
 * @param statistics list štatistik kvízu
 * */
data class QuizBackUp(
    val quizID: String = "",
    val quiz: QuizData = QuizData(""),
    val questions: List<QuestionData> = listOf(),
    val answers: ArrayList<AnswerData> = arrayListOf(),
    val statistics: List<StatData> = listOf()
)