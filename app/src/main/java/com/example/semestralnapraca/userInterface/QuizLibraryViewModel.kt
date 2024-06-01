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
 * viewModel pre obrazovku QuizLibrary
 * */
class QuizLibraryViewModel: ViewModel() {
    private val _quizzesState = MutableStateFlow(QuizLibraryUiState())
    val quizzesState: StateFlow<QuizLibraryUiState> = _quizzesState

    private val database = Database.getInstance()
    /**
     * načíta id, ktoré ma prideliť zdielanemu kvízu
     * */
    fun loadFreeSharingIDFromDatabase(){
        CoroutineScope(Dispatchers.Main).launch {
            val sharingID = withContext(Dispatchers.IO) {
                database.loadQuizFreeSharingKey()
            }
            _quizzesState.value = _quizzesState.value.copy(sharingID = sharingID)
        }
    }
    /**
     * načíta kvízi z databázi
     * */
    fun loadQuizzesFromDatabase(){
        CoroutineScope(Dispatchers.Main).launch {
            val quizzes = withContext(Dispatchers.IO) {
                database.loadQuizzesFromDatabase()
            }
            _quizzesState.value = _quizzesState.value.copy(quizzes = quizzes)
        }
    }
    /**
     * Odstraní kvíz z databázi
     * @param quizID id kvízu
     * */
    fun removeQuiz(quizID: String) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                database.removeQuizFromDatabase(quizID)
            }
            loadQuizzesFromDatabase()
        }
    }
    /**
     * Zmeni meno kvízu v databáze
     * */
    fun rename() {
        val updateInfo: HashMap<String, Any> = hashMapOf(
            "name" to _quizzesState.value.textForRenaming
        )
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                database.updateContentInDatabase(
                    table = "quizzes",
                    childPath = listOf(_quizzesState.value.quizID),
                    updateInfo
                )
            }
            _quizzesState.value = _quizzesState.value.copy(quizID = "")
            loadQuizzesFromDatabase()
        }
    }
    /**
     * @param state zmeni renaming v quizzesState
     * */
    fun changeRenamingState(state: Boolean) {
        _quizzesState.value = _quizzesState.value.copy(renaming = state)
    }
    /**
     * @param quizID zmeni quizID v quizzesState
     * */
    fun showRenamingDialog(quizID: String) {
        changeRenamingState(true)
        _quizzesState.value = _quizzesState.value.copy(quizID = quizID)
    }
    /**
     * @param it zmeni textForRenaming v quizzesState
     * */
    fun updateRenaming(it: String) {
        _quizzesState.value = _quizzesState.value.copy(textForRenaming = it)
    }
    /**
     * @param state zmeni sharingState v quizzesState
     * */
    fun changeSharingState(state: Boolean) {
        _quizzesState.value = _quizzesState.value.copy(sharingState = state)
    }
    /**
     * nastaví zdielanie kvízu v databaze
     * */
    fun share() {
        val id = _quizzesState.value.sharingID
        val updateInfo: HashMap<String, Any> = hashMapOf(
            "sharedToPublicQuizzes" to true.toString(),
            "shareID" to id
        )
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                database.updateContentInDatabase(
                    table = "quizzes",
                    childPath = listOf(_quizzesState.value.quizID),
                    updateInfo
                )

                database.updateContentInDatabase(
                    table = "freeSharingCode",
                    childPath = listOf("code"),
                    hashMapOf("code" to (id.toInt() + 1).toString())
                )
            }
            loadQuizzesFromDatabase()
        }
    }
    /**
     * Zobrazi okno z informaciami zdielania
     *
     * @param quizID id kvízu
     * @param sharingID id zdielania kvízu
     * @param shared či je kvíz zdielany
     * */
    fun showSharingDialog(quizID: String, sharingID: String, shared: Boolean) {
        loadFreeSharingIDFromDatabase()
        changeSharingState(true)
        if (shared)
        {
            _quizzesState.value = _quizzesState.value.copy(quizID = quizID, sharingID = sharingID, alreadyShared = true)
        } else {
            _quizzesState.value = _quizzesState.value.copy(quizID = quizID)
        }


    }
    /**
     * @param bool zmení alreadyShared v quizzesState
     * */
    fun changeAlreadyChanged(bool: Boolean) {
        _quizzesState.value = _quizzesState.value.copy(alreadyShared = bool)
    }
    /**
     * Zobrazí okno na výber hrania alebo úpravi kvízu
     * @param quizID id kvizu
     * */
    fun showPlayOrEditOptionsDialog(quizID: String) {
        changeQuizOptionsState(true)
        _quizzesState.value = _quizzesState.value.copy(quizID = quizID)
    }
    /**
     * @param b zmení quizOptions v quizzesState
     * */
    fun changeQuizOptionsState(b: Boolean) {
        _quizzesState.value = _quizzesState.value.copy(quizOptions = b)
    }

}
/**
 * data potrebné pre viewModel
 * @param quizzes zoznam kvízov
 * @param renaming či sa má zobraziť okno na zmenu mena
 * @param quizID id aktualneho kvízu
 * @param textForRenaming meno kvízu počas premenovanie
 * @param sharingID id zdielania aktualneho kvízu
 * @param sharingState či sa ma ukázať okno zdielania
 * @param alreadyShared či je aktualny kvíz zdielany
 * @param quizOptions či sa má ukázať okno na hranie alebo úpravu kvízu
 * */
data class QuizLibraryUiState(
    val quizzes: List<QuizData> = listOf(),
    val renaming: Boolean = false,
    val quizID: String = "",
    val textForRenaming: String = "",
    val sharingID: String = "0",
    val sharingState: Boolean = false,
    val alreadyShared: Boolean = false,
    val quizOptions: Boolean = false)
