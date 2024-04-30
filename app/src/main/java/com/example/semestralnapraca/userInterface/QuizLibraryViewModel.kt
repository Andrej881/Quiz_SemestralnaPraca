package com.example.semestralnapraca.userInterface

import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import com.example.semestralnapraca.R
import com.example.semestralnapraca.data.Database
import com.example.semestralnapraca.data.QuizData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QuizLibraryViewModel(): ViewModel() {
    private val _quizzesState = MutableStateFlow(QuizLibraryUiState())
    val quizzesState: StateFlow<QuizLibraryUiState> = _quizzesState

    private val database = Database()
    init {
        loadQuizzesFromDatabase()
        loadFreeSharingIDFromDatabase()
    }

    fun loadFreeSharingIDFromDatabase(){
        database.loadQuizFreeSharingKey(object : Database.QuizShareLoadListener {
            override fun onQuizzesLoaded(quizShareID: String) {
                _quizzesState.value = _quizzesState.value.copy(sharingID = quizShareID)
            }
        })
    }
    fun loadQuizzesFromDatabase(){
        database.loadQuizFromDatabase(object : Database.QuizLoadListener {
            override fun onQuizzesLoaded(quizList: List<QuizData>) {
                _quizzesState.value = _quizzesState.value.copy(quizzes = quizList)
            }
        })
    }

    fun removeQuiz(quizID: String) {
        database.removeQuizFromDatabase(quizID)
        loadQuizzesFromDatabase()
    }

    fun rename() {
        val updateInfo = hashMapOf(
            "name" to _quizzesState.value.textForRenaming,)
        database.updateContentInDatabase(table = "quizzes" ,contentID = _quizzesState.value.quizID, updateInfo)
        _quizzesState.value = _quizzesState.value.copy(quizID = "")
        loadQuizzesFromDatabase()
    }

    fun changeRenamingState(state: Boolean) {
        _quizzesState.value = _quizzesState.value.copy(renaming = state)
    }

    fun showRenamingDialog(quizID: String) {



        changeRenamingState(true)
        _quizzesState.value = _quizzesState.value.copy(quizID = quizID)
    }

    fun updateRenaming(it: String) {
        _quizzesState.value = _quizzesState.value.copy(textForRenaming = it)
    }

    fun changeSharingState(state: Boolean) {
        _quizzesState.value = _quizzesState.value.copy(sharingState = state)
    }
    fun share() {
        loadFreeSharingIDFromDatabase()
        val id = _quizzesState.value.sharingID
        val updateInfo = hashMapOf(
            "sharedToPublicQuizzes" to true.toString(),
            "shareID" to id
        )
        database.updateContentInDatabase(table = "quizzes" ,contentID = _quizzesState.value.quizID, updateInfo)

        database.updateContentInDatabase(table = "freeSharingCode" ,contentID = "code", hashMapOf(
            "code" to (id.toInt() + 1).toString()
        ))
        loadQuizzesFromDatabase()
    }

    fun showSharingDialog(quizID: String, sharingID: String, shared: Boolean) {
        changeSharingState(true)
        if (shared)
        {
            _quizzesState.value = _quizzesState.value.copy(quizID = quizID, sharingID = sharingID, alreadyShared = shared)
        } else {
            _quizzesState.value = _quizzesState.value.copy(quizID = quizID)
        }


    }

    fun changeAlreadyChanged(b: Boolean) {
        _quizzesState.value = _quizzesState.value.copy(alreadyShared = b)
    }

    fun showPlatOrEditOptionsDialog(quizID: String) {
        changeQuizOptionsState(true)
        _quizzesState.value = _quizzesState.value.copy(quizID = quizID)
        //TO DO refactor fun changeQuizID
    }

    fun changeQuizOptionsState(b: Boolean) {
        _quizzesState.value = _quizzesState.value.copy(quizOptions = b)
    }

}

data class QuizLibraryUiState(val quizzes: List<QuizData> = listOf(),
    val renaming: Boolean = false,
    val quizID: String = "",
    val textForRenaming: String = "",
    val sharingID: String = "0",
    val sharingState: Boolean = false,
    val alreadyShared: Boolean = false,
    val quizOptions: Boolean = false)
