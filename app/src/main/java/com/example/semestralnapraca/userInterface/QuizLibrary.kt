package com.example.semestralnapraca.userInterface

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semestralnapraca.R
import com.example.semestralnapraca.ui.theme.Color2
import com.example.semestralnapraca.ui.theme.Color3
import com.example.semestralnapraca.ui.theme.Color4
import com.example.semestralnapraca.ui.theme.Color5
/**
 * Obrazovka knižnice kvízov uživateľa
 *
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * @param quizLibraryViewModel viewModel obrazovky
 * @param navigateToQuizGame funkcia, ktorá zabezpečí navigáciu do vybraného kvízu
 * @param navigateToQuizCreation funkcia, ktorá zabezpečí navigáciu do tvorby kvízov
 * @param navigateToMainMenu funkcia, ktorá zabezpečí navigáciu do Main Menu
 * */
@Composable
fun QuizLibrary(
    modifier: Modifier = Modifier,
    quizLibraryViewModel: QuizLibraryViewModel = viewModel(),
    navigateToQuizGame: (quizID: String) -> Unit = {},
    navigateToQuizCreation: (quizID: String) -> Unit = {},
    navigateToMainMenu:() -> Unit = {},
) {
    quizLibraryViewModel.loadQuizzesFromDatabase()
    val quizzesState by quizLibraryViewModel.quizzesState.collectAsState()

    QuizOptionsDialog(
        showOptions = quizzesState.quizOptions,
        quizLibraryViewModel = quizLibraryViewModel,
        quizID = quizzesState.quizID,
        navigateToQuizCreation = navigateToQuizCreation,
        navigateToQuizGame = navigateToQuizGame
    )

    SharingDialog(sharing = quizzesState.sharingState,
        shareID = quizzesState.sharingID,
        alreadyShared = quizzesState.alreadyShared,
        quizLibraryViewModel = quizLibraryViewModel)

    RenamingDialog(quizLibraryViewModel = quizLibraryViewModel,
        renaming = quizzesState.renaming,
        textForRenaming = quizzesState.textForRenaming)

    LazyColumn(
        modifier = Modifier.padding(32.dp)
    ) {
        item {
            Row (
                modifier = Modifier
                    .border(width = 5.dp, color = Color5)
                    .fillMaxWidth()
                    .background(color = Color4),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Button(
                    onClick = navigateToMainMenu,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color4
                    ),
                    shape = RectangleShape
                ) {
                    Icon(painter = painterResource(id = R.drawable.back), contentDescription = "", Modifier.size(25.dp), tint = Color5)
                }
                TextField(
                    value = stringResource(R.string.my_library),
                    textStyle = TextStyle(fontSize = 25.sp,
                        textAlign = TextAlign.Left),
                    onValueChange = {},
                    readOnly = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color4,
                        focusedContainerColor = Color4,
                    )
                )
            }
            Spacer(modifier = Modifier.padding(bottom = 50.dp))
        }

        items(quizzesState.quizzes) { quiz ->
            QuizInLibrary(quizName = quiz.quizName,quizID = quiz.quizId, shared = quiz.shared, shareID = quiz.shareID , libraryViewModel = quizLibraryViewModel)
        }

        item {
            QuizButton(
                onClick = {
                    navigateToQuizCreation("")
                    quizLibraryViewModel.loadQuizzesFromDatabase()
                },
                icon = R.drawable.add,
                color = Color4,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 5.dp, color = Color5, shape = CircleShape)
            )
        }
    }
}
/**
 * Zobrazí okno na úpravu informácií kvízu
 *
 * @param showOptions rozhoduje či sa má okno ukázať
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * @param navigateToQuizCreation navigácia do tvorby kvízu
 * @param navigateToQuizGame navigácia do hrania kvízu
 * @param quizID id kvízu
 * @param quizLibraryViewModel viewModel obrazovky
 * */
@Composable
fun QuizOptionsDialog(
    modifier: Modifier = Modifier,
    navigateToQuizGame:(quizID:String) -> Unit = {},
    navigateToQuizCreation:(quizID:String) -> Unit = {},
    quizID: String,
    showOptions: Boolean,
    quizLibraryViewModel: QuizLibraryViewModel
                      ) {
    if (showOptions) {
        AlertDialog(onDismissRequest = { quizLibraryViewModel.changeQuizOptionsState(false) },
            title = { Text(stringResource(R.string.quiz_options), color = Color5) },
            modifier = modifier,
            confirmButton = {
                TextButton(onClick = {
                    navigateToQuizGame(quizID)
                    quizLibraryViewModel.changeQuizOptionsState(false)
                }) {
                    Text(text = stringResource(R.string.play_quiz),
                        fontSize = 20.sp,
                        color = Color5)
                }
            },
            dismissButton = {TextButton(onClick = {
                navigateToQuizCreation(quizID)
                quizLibraryViewModel.changeQuizOptionsState(false)
            }) {
                Text(text = stringResource(R.string.edit_quiz),
                    fontSize = 20.sp,
                    color = Color5)
            }},
            containerColor = Color2)
    }
}
/**
 * Zobrazí okno na ukážku zdielaneho id kvízu
 *
 * @param sharing rozhoduje či sa má okno ukázať
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * @param alreadyShared či už je kvíz zdielaný
 * @param shareID id zdielania
 * @param quizLibraryViewModel viewModel obrazovky
 * */
@Composable
fun SharingDialog(
    modifier: Modifier = Modifier,
    sharing: Boolean,
    alreadyShared: Boolean,
    shareID: String,
    quizLibraryViewModel: QuizLibraryViewModel
) {
    if (sharing) {
        AlertDialog(onDismissRequest = { },
            title = { Text(stringResource(R.string.share), color = Color5) },
            text = {
                val text = if (alreadyShared) stringResource(
                    R.string.quizz_is_already_shared,
                    shareID
                ) else stringResource(R.string.code, shareID)
                TextField(
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color3,
                        focusedContainerColor = Color3
                    ),
                    value = text,
                    readOnly = true,
                    onValueChange = {})
            },
            modifier = modifier,
            confirmButton = {
                if (!alreadyShared)
                {
                    TextButton(onClick = {
                        quizLibraryViewModel.share()
                        quizLibraryViewModel.changeSharingState(false)
                    }) {
                        Text(text = stringResource(R.string.ok),
                            fontSize = 20.sp,
                            color = Color5)
                    }
                }
                },
            dismissButton = {TextButton(onClick = {
                quizLibraryViewModel.changeSharingState(false)
                quizLibraryViewModel.changeAlreadyChanged(false)
                quizLibraryViewModel.loadFreeSharingIDFromDatabase()
            }) {
                Text(text = stringResource(R.string.cancel),
                    fontSize = 20.sp,
                    color = Color5)
            }},
            containerColor = Color2)
    }
}
/**
 * Zobrazí okno na úpravu mena kvízu
 *
 * @param renaming rozhoduje či sa má okno ukázať
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * @param textForRenaming meno
 * @param quizLibraryViewModel viewModel obrazovky
 * */
@Composable
fun RenamingDialog(
    quizLibraryViewModel: QuizLibraryViewModel,
    modifier: Modifier = Modifier,
    renaming: Boolean,
    textForRenaming: String
) {
    if (renaming) {
        AlertDialog(onDismissRequest = { quizLibraryViewModel.changeRenamingState(false) },
            title = { Text(stringResource(R.string.renaming), color = Color5) },
            text = {
                TextField(
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color3,
                        focusedContainerColor = Color3
                    ),
                    value = textForRenaming,
                    onValueChange = {quizLibraryViewModel.updateRenaming(it)})
            },
            modifier = modifier,
            confirmButton = {
                TextButton(onClick = {
                    quizLibraryViewModel.rename()
                    quizLibraryViewModel.changeRenamingState(false)
                    quizLibraryViewModel.updateRenaming("")
                }) {
                    Text(text = stringResource(R.string.ok),
                        fontSize = 20.sp,
                        color = Color5)
                }},
            containerColor = Color2)
    }
}
/**
 * Jednotlivý kvíz v knižnici kvízov
 *
 * @param quizName meno kvízu
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * @param shared či je kvíz zdielaný
 * @param shareID id zdielania kvízu
 * @param quizID id kvízu
 * @param libraryViewModel viewModel obrazovky
 * */
@Composable
fun QuizInLibrary(
    modifier: Modifier = Modifier,
    libraryViewModel: QuizLibraryViewModel = viewModel(),
    quizName: String,
    quizID: String,
    shared: Boolean,
    shareID: String
) {
    Row (
        modifier = modifier
            .padding(bottom = 25.dp)
            .fillMaxWidth()
            .border(width = 5.dp, color = Color5)

    ){
        Surface(color = Color3, modifier = modifier.fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Column (modifier = Modifier.weight(.2f)){
                    QuizButton(
                        onClick = { libraryViewModel.showRenamingDialog(quizID) },
                        icon = R.drawable.rename,
                        color = Color2)
                    QuizButton(
                        onClick = {libraryViewModel.showSharingDialog(quizID, shareID, shared) },
                        icon = R.drawable.share,
                        color = Color2)
                }
                Button(
                    modifier = Modifier.weight(.6f),
                    onClick = { libraryViewModel.showPlayOrEditOptionsDialog(quizID) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color3),
                ) {
                    Text(text = quizName,
                        fontSize = 25.sp,
                        color = Color5)
                }
                QuizButton(
                    onClick = { libraryViewModel.removeQuiz(quizID) },
                    icon = R.drawable.remove,
                    color = Color4,
                    modifier = Modifier.weight(0.2f)
                )
            }
        }
    }
}

/**
 * Tlačidlo obrazovky
 *
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * @param onClick čo sa stane po kliknutí
 * @param icon ikona tlačidla
 * @param color farba tlačidla
 * */
@Composable
fun QuizButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    color : Color
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        )
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color5)
    }
}

/**
 * Preview Obrazovky
 * */
@Preview(showBackground = true)
@Composable
fun GameLibraryPreview() {
    QuizLibrary()
}
/**
 * Preview jednotlivých kvízov
 * */
@Preview(showBackground = true)
@Composable
fun QuizPreview() {
    QuizInLibrary(
        quizName ="Quiz name",
        quizID = "1",
        modifier = Modifier.fillMaxWidth(),
        shared = false,
        shareID = "0"
    )
}