package com.example.semestralnapraca.userInterface

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semestralnapraca.R
import com.example.semestralnapraca.ui.theme.Color1
import com.example.semestralnapraca.ui.theme.Color2
import com.example.semestralnapraca.ui.theme.Color3
import com.example.semestralnapraca.ui.theme.Color4
import com.example.semestralnapraca.ui.theme.Color5

/**
 * Obrazovka Tvorenia kvízu
 *
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * @param quizCreationViewModel viewModel obrazovky
 * @param navigateOnCancel funkcia, ktorá zabezpečí navigáciu po zrušení tvorby kvízu
 * @param quizID id tvoreného kvízu
 * */
@Composable
fun QuizCreation(
    modifier: Modifier = Modifier,
    navigateOnCancel: () -> Unit = {},
    quizCreationViewModel: QuizCreationViewModel = viewModel(),
    quizID: String =""
)
{
    val quizCreationUiState by quizCreationViewModel.creationState.collectAsState()
    LaunchedEffect(quizID) {
        Log.d("CREATION",quizCreationUiState.quizID + " " + quizID)
        if ((quizID != quizCreationUiState.quizID && quizID != "")||(quizID == quizCreationUiState.quizID && quizID == "")) {
            Log.d("CREATION","called")
            Log.d("CREATION",quizID)
            quizCreationViewModel.loadQuiz(quizID)
        }
    }
    ShowSavingQuizAlerDialog(
        show = quizCreationUiState.saving,
        onDismiss = {quizCreationViewModel.changeShowSavingQuiz(false)},
        onConfirm = {
            quizCreationViewModel.saveQuiz(navigateOnCancel)
            quizCreationViewModel.changeShowSavingQuiz(false)
        },
        onNameUpdate = { quizCreationViewModel.changeNameContent(it) },
        onTimeUpdate = { quizCreationViewModel.changeTimeContent(it) },
        time = quizCreationUiState.quizTime,
        name = quizCreationUiState.quizName
    )
    ShowEditingAnswerAlertDialog(
        show = quizCreationUiState.showingAnswer,
        onDismiss = {quizCreationViewModel.changeShowAddingAnswer(false)},
        onDelete = {quizCreationViewModel.deleteAnswer()
            quizCreationViewModel.changeShowAddingAnswer(false)},
        points = quizCreationUiState.currentAnswerPoints,
        onContentUpdate = {quizCreationViewModel.changeAnswerContent(it)},
        onCorrectChanged = {quizCreationViewModel.changeAnswerCorrectness(it)},
        onPointsUpdate = {quizCreationViewModel.changeAnswerPoints(it)},
        content = quizCreationUiState.currentAnswerContent,
        correct =  quizCreationUiState.currentAnswerCorrectness,
        onConfirm = {
            quizCreationViewModel.editAnswer()
            quizCreationViewModel.changeShowAddingAnswer(false)
        }
    )
    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(bottom = 20.dp).fillMaxWidth()
            ) {
                BarButton(onClick = {quizCreationViewModel.changeShowSavingQuiz(true)}, icon = R.drawable.save)
            }

        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                AddAnswerButton( onClick = {
                    quizCreationViewModel.changeShowAddingAnswer(true)
                    quizCreationViewModel.setCurrentAnswerID("")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color1),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically)
                {
                    BarButton(onClick = {quizCreationViewModel.move(forward = false)}, icon = R.drawable.back)
                    BarButton(onClick = {quizCreationViewModel.deleteQuiz(navigateOnCancel)} , icon = R.drawable.cancel)
                    BarButton(onClick = {quizCreationViewModel.move(forward = true)}, icon = R.drawable.next)
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        innerPadding ->

        LazyColumn (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color1)
        ) {
            item {
                QuestionTextField(value = stringResource(id = R.string.question_number) + (quizCreationUiState.currentPositionInList+1),
                    remove = {quizCreationViewModel.deleteQuestion()})
                Spacer(modifier = Modifier.padding(bottom = 25.dp))
                TextField(
                    value = quizCreationUiState.currentQuestion?.content ?: "",
                    onValueChange = { quizCreationViewModel.changeContent(it)},
                    label = { Text(stringResource(R.string.enter_question_here))},
                    modifier = Modifier
                        .padding(bottom = 25.dp)
                        .fillMaxWidth()
                        .border(width = 5.dp, color = Color5),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color3,
                        unfocusedContainerColor = Color3
                    )
                )

                ReadOnlyTextField(value = stringResource(id = R.string.answers))
                Spacer(modifier = Modifier.padding(bottom = 25.dp))
            }
            items(quizCreationUiState.answers) {answer ->
                AnswerButton(
                    answerID = answer.answerID,
                    value = answer.content,
                    points = answer.points.toString(),
                    onClick = {
                        quizCreationViewModel.setCurrentAnswerID(it)
                        quizCreationViewModel.changeShowAddingAnswer(true)
                    }
                )
            }
        }

    }
}
/**
 * Ukáže okno pri ukladaní
 *
 * @param show rozhoduje či sa má upozornenie ukázať
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * @param onDismiss funkcia sa zavolá pri zrušení okna
 * @param onConfirm funcia sa zavolá pri potvrdení okna
 * @param name meno kvízu
 * @param time čas na dokončenie kvízu
 * @param onTimeUpdate čo sa stane pri zmene poľa nastavujúceho čas
 * @param onNameUpdate čo sa stane pri zmene poľa nastavujúceho meno
 * */
@Composable
fun ShowSavingQuizAlerDialog(
    show: Boolean,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    name: String,
    time: String,
    onTimeUpdate: (String) -> Unit,
    onNameUpdate: (String) -> Unit
) {
    if (show) {
        AlertDialog(onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.saveQuiz), color = Color5) },
            text = {
                Column {
                    TextField(
                        value = name,
                        modifier = Modifier
                            .padding(bottom = 25.dp)
                            .border(width = 5.dp, Color5),
                        label = {Text(stringResource(R.string.renaming))},
                        onValueChange = {newContent -> onNameUpdate(newContent)},
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color3,
                            focusedContainerColor = Color3,
                            unfocusedLabelColor = Color5,
                            focusedLabelColor = Color5,
                            cursorColor = Color5)
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            value = time,
                            modifier = Modifier
                                .widthIn(15.dp)
                                .border(width = 5.dp, Color5),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            label = { Text(stringResource(R.string.time))},
                            onValueChange = {newTime -> onTimeUpdate(newTime)},
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color3,
                                focusedContainerColor = Color3,
                                unfocusedLabelColor = Color5,
                                focusedLabelColor = Color5,
                                cursorColor = Color5
                            )
                        )
                    }
                }
            },
            modifier = modifier,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = stringResource(R.string.ok),
                        fontSize = 20.sp,
                        color = Color5)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.cancel),
                        fontSize = 20.sp,
                        color = Color5)
                }
            },
            containerColor = Color2)
    }
}
/**
 * Ukáže okno pri úprave parametrov odpovede
 *
 * @param show rozhoduje či sa má upozornenie ukázať
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * @param onDismiss funkcia sa zavolá pri zrušení okna
 * @param onConfirm funcia sa zavolá pri potvrdení okna
 * @param onDelete funcia sa zavolá po stlačení tlačidľa delete
 * @param onContentUpdate čo sa stane pri zmene poľa nastavujúceho obsah odpovede
 * @param onPointsUpdate čo sa stane pri zmene poľa nastavujúceho počet získanych bodov
 * @param content obsah odpovede
 * @param correct či je odpoveď správna
 * @param onCorrectChanged čo sa stane pri zmene správnosti
 * */
@Composable
fun ShowEditingAnswerAlertDialog(
    show:Boolean,
    points: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onDelete: () -> Unit,
    onContentUpdate: (String) -> Unit,
    onPointsUpdate: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: String,
    correct: Boolean,
    onCorrectChanged: (Boolean) -> Unit
) {
    if (show) {
        AlertDialog(onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.answer), color = Color5) },
            text = {
                Column {
                    TextField(
                        value = content,
                        modifier = Modifier
                            .padding(bottom = 25.dp)
                            .border(width = 5.dp, Color5),
                        label = {Text(stringResource(R.string.answer))},
                        onValueChange = {newContent -> onContentUpdate(newContent)},
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color3,
                            focusedContainerColor = Color3,
                            unfocusedLabelColor = Color5,
                            focusedLabelColor = Color5,
                            cursorColor = Color5)
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            value = points,
                            modifier = Modifier
                                .widthIn(15.dp)
                                .border(width = 5.dp, Color5),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            label = { Text(stringResource(R.string.points))},
                            onValueChange = {newPoints -> onPointsUpdate(newPoints)},
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color3,
                                focusedContainerColor = Color3,
                                unfocusedLabelColor = Color5,
                                focusedLabelColor = Color5,
                                cursorColor = Color5
                            )
                        )
                        Switch(
                            colors = SwitchDefaults.colors(
                                checkedBorderColor = Color5,
                                uncheckedBorderColor = Color5,
                                uncheckedIconColor = Color5,
                                uncheckedThumbColor = Color5,
                                checkedThumbColor = Color5,
                                uncheckedTrackColor = Color3,
                                checkedTrackColor = Color4


                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp)
                                .wrapContentWidth(Alignment.End),
                            checked = correct,
                            onCheckedChange = {newCorrectness -> onCorrectChanged(newCorrectness)}
                        )
                    }
                }
                   },
            modifier = modifier,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = stringResource(R.string.ok),
                        fontSize = 20.sp,
                        color = Color5)
                }
            },
            dismissButton = {

                TextButton(onClick = onDelete) {
                    Text(text = stringResource(R.string.delete),
                        fontSize = 20.sp,
                        color = Color5)
                }
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.cancel),
                        fontSize = 20.sp,
                        color = Color5)
                }
            },
            containerColor = Color2)
    }
}
/**
 * Tlačidlo na okraji obrazovky
 *
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * @param icon Ikona tlačidľa
 * @param onClick funckia, ktorá sa vykoná po stlačení tlačidla
 * */
@Composable
fun BarButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    onClick: () -> Unit
) {
    Button(onClick = onClick,
        modifier = modifier
            .border(width = 5.dp, color = Color5, shape = CircleShape),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color4

        ),
        ) {
        Icon(painter = painterResource(id = icon), contentDescription = null, tint = Color5,
            modifier = modifier.size(24.dp))
    }
}

/**
 * Tlačidlo na pridávanie odpovedí
 *
 * @param onClick funkcia, ktorá sa vykoná po stlačení
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * */
@Composable
fun AddAnswerButton(
    onClick: () -> Unit,
    modifier : Modifier = Modifier
) {
    Button(onClick =  onClick,
        modifier = modifier.border(width = 5.dp, color = Color5),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color2
        ),
        shape = RectangleShape
    ) {
        Icon(painter = painterResource(id = R.drawable.add2), contentDescription = null, tint = Color5,
            modifier = Modifier.size(25.dp))
    }
}

/**
 * Jednotlivé odpovede ukázane na obrazovke
 *
 * @param answerID id odpovede
 * @param value obsah odpvoede
 * @param points po4et bodov za odpoved
 * @param onClick funkcia, ktorá sa stane po stlačení odpovede
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * */
@Composable
fun AnswerButton(
    modifier: Modifier = Modifier,
    answerID: String = "",
    value: String = "",
    points: String = "",
    onClick: (String) -> Unit = {}
) {
    Button(
        onClick = { onClick(answerID)},
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color2,
        ),
        modifier = Modifier
            .padding(bottom = 25.dp)
            .fillMaxWidth()
            .border(width = 5.dp, color = Color5),
        ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        )
        {
            Text(
                text = value,
                color = Color5,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = points,
                textAlign = TextAlign.Center,
                color = Color(0xFFFFFFFF),
                fontSize = 20.sp,
                modifier = Modifier.weight(.25f)
            )
        }
    }
}

/**
 * Textove pole, do ktorého sa zapisuje odpoved
 *
 * @param value obsah odpovede
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * @param remove funkcia, ktorá sa vykoná po odstránení odpovede
 * */
@Composable
fun QuestionTextField(
    modifier: Modifier = Modifier,
    value : String = "",
    remove: () -> Unit = {}
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color4)
            .border(width = 5.dp, color = Color5),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier.weight(.7f),
            value = value,
            textStyle = TextStyle(fontSize = 25.sp,
                textAlign = TextAlign.Center),
            onValueChange = {},
            readOnly = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color4,
                focusedContainerColor = Color4,
            )
        )
        Button(onClick = remove,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color4
            ),
            modifier = Modifier.weight(.3f)
        ) {
            Icon(painter = painterResource(id =  R.drawable.remove), contentDescription = null, tint = Color5,
                modifier = Modifier.fillMaxWidth())
        }
    }
}

/**
 * Preview pre obrazovku
 * */
@Preview(showBackground = true)
@Composable
fun QuizCreationPreview() {
    QuizCreation()
}

/**
 * Preview pre okna na upravu odpovede
 * */
@Preview(showBackground = true)
@Composable
fun ShowAddingAnswerAlertDialogPreview() {
    ShowEditingAnswerAlertDialog(
        show = true,
        onDismiss = {},
        points = "",
        onContentUpdate = {},
        onCorrectChanged = {},
        onPointsUpdate = {},
        content = "",
        correct = true,
        onConfirm = {},
        onDelete = {}
    )
}

/**
 * Preview na ukážku jednotlivej odpovede
 * */
@Preview(showBackground = true)
@Composable
fun ShowAnswerFieldPreview() {
    AnswerButton(value = "Answer", points = "1000")
}
