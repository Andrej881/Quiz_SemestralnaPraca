package com.example.semestralnapraca.userInterface

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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

@Composable
fun QuizGame(quizID: String = "",
             quizGameViewModel: QuizGameViewModel = viewModel(),
             navigateBack: () -> Unit = {},
) {
    val gameUiState by quizGameViewModel.gameUiState.collectAsState()
    LaunchedEffect(quizID) {
        if (!gameUiState.quizID.equals(quizID)) {
            quizGameViewModel.loadQuiz(quizID)
        }
    }
    StatisticsAlertDialog(
        show = gameUiState.showStats,
        onDismiss = navigateBack,
        onConfirm = {
            quizGameViewModel.changeShowStats(false)
            quizGameViewModel.changeShowingAnswers(true)
        },
        points = "${gameUiState.points}/${gameUiState.maxPoints}",
        timeLeft = gameUiState.quizTime,
        place = gameUiState.place
    )
    EndGameAlertDialog(
        show = gameUiState.showEndQuiz,
        onDismiss = {quizGameViewModel.changeShowEndQuizAlertField(false)},
        onConfirm = {
            quizGameViewModel.stopCountDown()
            quizGameViewModel.changeShowEndQuizAlertField(false)
            quizGameViewModel.showStats()
        }
        )
    Scaffold(
        bottomBar = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(color = Color1),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom)
            {
                BarButton(onClick = {quizGameViewModel.move(forward = false)}, icon = R.drawable.back)
                BarButton(onClick = {
                    quizGameViewModel.stopCountDown()
                    navigateBack()
                                    },
                    icon = R.drawable.cancel)
                BarButton(onClick = {quizGameViewModel.move(true)}, icon = R.drawable.next)
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        innerPadding ->

        LazyColumn (
            modifier = Modifier
                .padding(innerPadding)
                .background(color = Color1),
        ) {
            item {
                ReadOnlyTextField(value = stringResource(id = R.string.question_number_playing) + "${gameUiState.currentQuestionNumber}/${gameUiState.numberOfQuestions}")
                Spacer(modifier = Modifier.padding(bottom = 25.dp))
                ExtraInformation(time = gameUiState.quizTime, points = gameUiState.points.toString())
                Spacer(modifier = Modifier.padding(bottom = 25.dp))
                TextField(
                    value = gameUiState.currentQuestionContent,
                    onValueChange = {},
                    readOnly = true,
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
            items(gameUiState.answers) {answer ->
                if  (gameUiState.showingAnswersOnEnd) {
                    EndAnswerField(
                        answerValue = answer.content,
                        clicked = gameUiState.clickedAnswers.contains(answer),
                        correct = answer.correct,
                        points = answer.points
                    )
                } else {
                    AnswerButtonGame(
                        answerValue = answer.content,
                        clicked = gameUiState.clickedAnswers.contains(answer),
                        onClick = {quizGameViewModel.changeAnswerClickedState(answer)},
                    )
                }
            }
        }
    }
}
@Composable
fun StatisticsAlertDialog(
    show: Boolean,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    points: String = "0/0",
    timeLeft: String = "0:00",
    place: String = "1th"
) {
    if (show) {
        AlertDialog(onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.stats),
                fontSize = 30.sp,
                color = Color5) },
            text = {
                Column {
                    Text(stringResource(R.string.points) + " $points",
                        fontSize = 25.sp,
                        textAlign = TextAlign.Left,
                        color = Color5)
                    Text(stringResource(R.string.time_left ) + " $timeLeft",
                        fontSize = 25.sp,
                        textAlign = TextAlign.Left,
                        color = Color5)
                    Text(stringResource(R.string.place) + " $place",
                        fontSize = 25.sp,
                        textAlign = TextAlign.Left,
                        color = Color5)
                }
            },
            modifier = modifier,
            confirmButton = {
                TextButton(onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(contentColor = Color5, containerColor = Color.Transparent)) {
                    Text(text = stringResource(R.string.show_answers))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(contentColor = Color5, containerColor = Color.Transparent)) {
                    Text(text = stringResource(R.string.end_quiz))
                }
            },
            containerColor = Color2)
    }
}
@Composable
fun EndGameAlertDialog(
    show: Boolean,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (show) {
        AlertDialog(onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.end_quiz),
                fontSize = 30.sp,
                color = Color5) },
            text = { Text(stringResource(R.string.end_quiz2),
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                color = Color5) },
            modifier = modifier,
            confirmButton = {
                TextButton(onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(contentColor = Color5, containerColor = Color.Transparent)) {
                    Text(text = stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(contentColor = Color5, containerColor = Color.Transparent)) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            containerColor = Color2)
    }
}

@Composable
fun ExtraInformation(
    time: String = "",
    points: String = ""
) {
    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
        ){
        Box(modifier = Modifier
            .widthIn(min = 50.dp)
            .border(width = 5.dp, color = Color5, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(10.dp),
                text =  time,
                textAlign = TextAlign.Center,
                fontSize = 25.sp

            )
        }
        Spacer(modifier = Modifier.padding(start = 100.dp))
        Box(modifier = Modifier
            .widthIn(min = 50.dp)
            .border(width = 5.dp, color = Color5, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(10.dp),
                text = points,
                textAlign = TextAlign.Center,
                fontSize = 25.sp

            )
        }
    }
}

@Composable
fun ReadOnlyTextField(
    value : String = "",
    modifier: Modifier = Modifier
){
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 5.dp, color = Color5),
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
}
@Composable
fun EndAnswerField(
    answerValue: String = "Answer",
    clicked: Boolean,
    correct: Boolean,
    modifier: Modifier = Modifier,
    points: Int = 10
) {
    val color = if (clicked) Color3 else Color2
    val id = if (correct) R.drawable.good else R.drawable.cancel
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 25.dp)
            .border(width = 5.dp, color = Color5),
        value = answerValue + " [Points: $points]",
        textStyle = TextStyle(textAlign = TextAlign.Center),
        onValueChange = {},
        readOnly = true,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = color,
            focusedContainerColor = color,
        ),
        leadingIcon = {
            if (clicked) {
                Icon(
                    painter = painterResource(id = id),
                    contentDescription = "",
                    tint = Color5,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    )
}
@Composable
fun AnswerButtonGame(
    answerValue: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    clicked: Boolean,
){
    Button(
        modifier = modifier
            .padding(bottom = 25.dp)
            .fillMaxWidth()
            .border(width = 5.dp, color = Color5, shape = RectangleShape),
        onClick = {onClick()},
        colors = ButtonDefaults.buttonColors(
            containerColor = Color2
        ),
        shape = RectangleShape
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(answerValue, color = Color5)
            if (clicked) {
                Icon(
                    painter = painterResource(id = R.drawable.cancel),
                    modifier = Modifier.size(16.dp),
                    contentDescription = "",
                    tint = Color5
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizGamePreview() {
    QuizGame()
}
@Preview(showBackground = true)
@Composable
fun StatisticsAlertDialogPrevie() {
    StatisticsAlertDialog(true,Modifier,{},{})
}

@Preview(showBackground = true)
@Composable
fun EndAnswerFieldPreview() {
    Column {
        EndAnswerField(clicked = false, correct = true)
        EndAnswerField(clicked = false, correct = false)
        EndAnswerField(clicked = true, correct = true)
        EndAnswerField(clicked = true, correct = false)
    }
}