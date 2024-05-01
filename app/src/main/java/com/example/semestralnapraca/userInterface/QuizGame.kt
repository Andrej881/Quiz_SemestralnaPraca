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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.semestralnapraca.R
import com.example.semestralnapraca.ui.theme.Color1
import com.example.semestralnapraca.ui.theme.Color2
import com.example.semestralnapraca.ui.theme.Color3
import com.example.semestralnapraca.ui.theme.Color5

@Composable
fun QuizGame() {
    Scaffold(
        bottomBar = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(color = Color1),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom)
            {
                BarButton(onClick = {}, icon = R.drawable.back)
                BarButton(onClick = {}, icon = R.drawable.cancel)
                BarButton(onClick = {}, icon = R.drawable.next)
            }
        },
        modifier = Modifier.fillMaxSize().padding(32.dp)
    ) {
        innerPadding ->

        Column (
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(color = Color1),
        ) {
            var number = 1
            val max = 4
            ReadOnlyTField(value = stringResource(id = R.string.question_number_playing) + number + "/" + max)
            Spacer(modifier = Modifier.padding(bottom = 25.dp))
            ExtraInformation()
            Spacer(modifier = Modifier.padding(bottom = 25.dp))
            TextField(
                value = "Question",
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

            ReadOnlyTField(value = stringResource(id = R.string.answers))
            Spacer(modifier = Modifier.padding(bottom = 25.dp))
            AnswerButtonGame("Answer", onClick = {}, modifier = Modifier.fillMaxWidth())
            AnswerButtonGame("Answer", onClick = {}, modifier = Modifier.fillMaxWidth())
            AnswerButtonGame("Answer", onClick = {}, modifier = Modifier.fillMaxWidth())
            AnswerButtonGame("Answer", onClick = {}, modifier = Modifier.fillMaxWidth())
            AnswerButtonGame("Answer", onClick = {}, modifier = Modifier.fillMaxWidth())
            AnswerButtonGame("Answer", onClick = {}, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun ExtraInformation() {
    Row (modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center){
        Box(modifier = Modifier
            .border(width = 5.dp, color = Color5, shape = CircleShape)
            .padding(15.dp)) {
            Text(
                text = "3:36",
                textAlign = TextAlign.Center,
                fontSize = 25.sp

            )
        }
        Spacer(modifier = Modifier.padding(start = 100.dp))
        Box(modifier = Modifier
            .border(width = 5.dp, color = Color5, shape = CircleShape)
            .padding(15.dp)) {
            Text(
                text = "3/20",
                textAlign = TextAlign.Center,
                fontSize = 25.sp

            )
        }
    }
}

@Composable
fun AnswerButtonGame(
    answerValue: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
    ){
        Button(
            modifier = modifier
                .padding(bottom = 25.dp)
                .border(width = 5.dp, color = Color5, shape = RectangleShape),
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color2
            ),
            shape = RectangleShape
        ) {
            Text(answerValue)
        }
}

@Preview(showBackground = true)
@Composable
fun QuizGamePreview() {
    QuizGame()
}