package com.example.semestralnapraca.userInterface

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
        quizGameViewModel.loadQuiz(quizID)
    }
    Scaffold(
        bottomBar = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(color = Color1),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom)
            {
                BarButton(onClick = {quizGameViewModel.move(forward = false)}, icon = R.drawable.back)
                BarButton(onClick = navigateBack, icon = R.drawable.cancel)
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
                AnswerButtonGame(
                    answerValue = answer.content,
                    answerID = answer.answerID,
                    clicked = gameUiState.clickedAnswers.contains(answer.answerID),
                    onClick = {quizGameViewModel.changeAnswerClickedState(it)},
                )
            }
        }
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
fun AnswerButtonGame(
    answerValue: String,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    clicked: Boolean,
    answerID:String
){
    Button(
        modifier = modifier
            .padding(bottom = 25.dp)
            .fillMaxWidth()
            .border(width = 5.dp, color = Color5, shape = RectangleShape),
        onClick = { onClick(answerID) },
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
            // Display the answer value
            Text(answerValue)
            // Add a checked button based on the clicked state
            if (clicked) {
                Icon(
                    painter = painterResource(id = R.drawable.cancel),
                    modifier = Modifier.size(16.dp),
                    contentDescription = ""
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