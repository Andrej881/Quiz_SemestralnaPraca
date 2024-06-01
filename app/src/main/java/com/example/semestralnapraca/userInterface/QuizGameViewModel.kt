package com.example.semestralnapraca.userInterface

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.semestralnapraca.data.AnswerData
import com.example.semestralnapraca.data.Database
import com.example.semestralnapraca.data.QuestionData
import com.example.semestralnapraca.data.StatData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
/**
 * viewModel pre QuizGame
 * */
class QuizGameViewModel: ViewModel() {
    private val _gameUiState =  MutableStateFlow(QuizGameUIState())
    val gameUiState: StateFlow<QuizGameUIState> = _gameUiState

    private val database = Database.getInstance()
    private var countDownTimer: CountDownTimer? = null
    /**
     * Pokúsi sa načítať kvíz
     * @param quizID id kvízu
     * */
    fun loadQuiz(quizID: String) {
        if (quizID == "") {
            Log.e("ERROR QuizGameViewModel","error loading quiz: quizID = ''")
        } else {
            Log.d("loadingQuizGame","loading quiz game $quizID")
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
            _gameUiState.value.allQuizAnswers[id.questionID] = answers
        }
        var points = 0
        for (answers:List<AnswerData> in _gameUiState.value.allQuizAnswers.values) {
            for (answer:AnswerData in answers) {
                if (answer.correct) points += answer.points
            }
        }
        _gameUiState.value = _gameUiState.value.copy(maxPoints = points)
    }
    /**
     * Logika presunu z jednej otázky kvízu na inú
     *
     * @param forward či sa presúva na následujúcu otázku(true) alebo predchádzajúcu(false)
     * */
    fun move(forward: Boolean) {
        val position = _gameUiState.value.currentQuestionNumber-1
        val size = _gameUiState.value.questions.size

        if (position == 0 && !forward) {
            // Do nothing
        } else if (position == size - 1 && forward) {
            if (_gameUiState.value.showingAnswersOnEnd) {
                changeShowStats(true)
            } else {
                changeShowEndQuizAlertField(true)
            }
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
    /**
     * @param show nastaví showEndQuiz v gameUiState
     * */
    fun changeShowEndQuizAlertField(show: Boolean) {
        _gameUiState.value = _gameUiState.value.copy(showEndQuiz = show)
    }

    private fun startCountdown(minutes: Long) {
        val milliseconds = minutes * 60 * 1000
        countDownTimer = object : CountDownTimer(milliseconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val min = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                val timeString = String.format("%02d:%02d", min, seconds)
                Log.d("TIMER", timeString)
                _gameUiState.value = _gameUiState.value.copy(quizTime = timeString)
            }
            override fun onFinish() {
                changeShowStats(true)
            }
        }.start()
    }
    /**
     * Ukončí odpočet
     * */
    fun stopCountDown() {
        countDownTimer?.cancel()
        countDownTimer = null
    }
    /**
     * Bud pridá do zoznamu kliknutých otazok otázku alebo ju odoberie
     * @param answer otázka, ktorá sa pridáva alebo odoberá
     * */
    fun changeAnswerClickedState(answer : AnswerData) {
        if (_gameUiState.value.clickedAnswers.contains(answer)) {
            _gameUiState.value.clickedAnswers.remove(answer)
        } else {
            _gameUiState.value.clickedAnswers.add(answer)
        }
    }
    /**
     * @param show nastaví showStats v gameUiState
     * */
    fun changeShowStats(show: Boolean) {
        _gameUiState.value = _gameUiState.value.copy(showStats = show)
    }
    /**
     * Načíta statistiky z databázi a vyhodnotí pozíciu hráča
     * */
    fun showStats() {
        CoroutineScope(Dispatchers.Main).launch {
            var points = 0
            _gameUiState.value.clickedAnswers.forEach {
                if(it.correct) points += it.points else points -= it.points
            }
            _gameUiState.value = _gameUiState.value.copy(quizTime = _gameUiState.value.quizTime, points = points)
            calculatePlace()
            database.addStatToDatabase(
                _gameUiState.value.quizID,
                StatData(
                    quizID = _gameUiState.value.quizID,
                    timeLeft = _gameUiState.value.quizTime,
                    points = _gameUiState.value.points
                    )
            )
            changeShowStats(true)
        }
    }

    private suspend fun calculatePlace() {
        val stats: List<StatData> = database.loadStatisticsFromDatabase(_gameUiState.value.quizID)
        var place = stats.size+1
        stats.forEach {
            if (_gameUiState.value.points > it.points) {
                place -= 1
            } else if (_gameUiState.value.points == it.points && compareTimeAbiggerB(_gameUiState.value.quizTime,it.timeLeft)) {
                place -= 1
            }
        }
        _gameUiState.value = _gameUiState.value.copy(place = "$place th")
    }
    /**
     * pomocou chatGPT
     * */
    private fun compareTimeAbiggerB(a: String, b: String): Boolean {
        val result:Boolean

        val componentsA = a.split(":")
        val hoursA = componentsA[0].toIntOrNull() ?: 0
        val minutesA = componentsA.getOrNull(1)?.toIntOrNull() ?: 0

        val componentsB = b.split(":")
        val hoursB = componentsB[0].toIntOrNull() ?: 0
        val minutesB = componentsB.getOrNull(1)?.toIntOrNull() ?: 0

        result = if (hoursA > hoursB) {
            true
        } else if (hoursA == hoursB) {
            (minutesA > minutesB)
        } else {
            false
        }

        return result
    }
    /**
     * Nastavi showingAnswersOnEnd na state a currentQuestionNumber na 1 a načíta odpovede
     * */
    fun changeShowingAnswers(state: Boolean) {
        _gameUiState.value = _gameUiState.value.copy(showingAnswersOnEnd = state, currentQuestionNumber = 1)
        loadDataFromQuestion(0)
    }

}
/**
 * data potrebné ore viewModel
 *
 * @param quizID id kvízu
 * @param questions zoznam otázok
 * @param numberOfQuestions počet otázok
 * @param currentQuestionNumber čislo momentálnej otázky
 * @param quizTime momentalny čas na dokončenie kvízu
 * @param answers zoznam odpovedí momentálnej otázky
 * @param currentQuestionID id momentálnej otázky
 * @param currentQuestionContent obsah momentálnej otázky
 * @param points počet získanych bodov
 * @param clickedAnswers zoznam stlačených odpovedí
 * @param allQuizAnswers hashmapa všetkých odpovedí
 * @param showEndQuiz rozhoduje či sa ma ukázať okno na potvrdenie ukončenia kvízu
 * @param showStats rozhoduje či sa má ukázať okno zo štatistikami
 * @param maxPoints maximálny počet bodov
 * @param place umiestnenie v tabuľke
 * @param showingAnswersOnEnd či sa majú ukazovať odpovede na konci na ukážku výsledkov
 * */
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
    val clickedAnswers: ArrayList<AnswerData> = arrayListOf(),
    val allQuizAnswers: HashMap<String, List<AnswerData>> = hashMapOf(),
    val showEndQuiz:Boolean = false,
    val showStats:Boolean = false,
    val maxPoints: Int = 0,
    val place: String = "1th",
    val showingAnswersOnEnd: Boolean = false
)