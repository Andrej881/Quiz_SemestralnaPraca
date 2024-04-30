package com.example.semestralnapraca.userInterface

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun QuizCreation(navigateOnCancel: () -> Unit = {},
                 quizCreationViewModel: QuizCreationViewModel = viewModel(),
                 quizID: String ="")  {
    val quizCreationUiState = quizCreationViewModel.creationState
    LaunchedEffect(quizID) {
        Log.d("CREATION",quizID)
        quizCreationViewModel.loadQuiz(quizID)
    }
    Scaffold(
        bottomBar = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(color = Color1),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom)
            {
                BottomBarButton(onClick = {quizCreationViewModel.move(forward = false)}, icon = R.drawable.back)
                BottomBarButton(onClick = navigateOnCancel, icon = R.drawable.cancel)
                BottomBarButton(onClick = {quizCreationViewModel.move(forward = true)}, icon = R.drawable.next)
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
                .fillMaxSize()
                .background(color = Color1)
        ) {
            item {
                var number = 1
                ReadOnlyTField(value = stringResource(id = R.string.question_number) + number)
                Spacer(modifier = Modifier.padding(bottom = 25.dp))

                var amountInput by remember { mutableStateOf("") }

                TextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
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

                ReadOnlyTField(value = stringResource(id = R.string.answers))
                Spacer(modifier = Modifier.padding(bottom = 25.dp))
            }
            //items{}
            item{
                AddAnswerButton( onClick = {}, modifier = Modifier.fillMaxWidth())
            }
        }

    }
}

@Composable
fun BottomBarButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    onClick: () -> Unit
) {
    Button(onClick = onClick,
        modifier = modifier
            .padding(start = 10.dp, end = 30.dp)
            .border(width = 5.dp, color = Color5, shape = CircleShape),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color4

        ),
        ) {
        Icon(painter = painterResource(id = icon), contentDescription = null, tint = Color5,
            modifier = modifier.size(24.dp))
    }
}

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

@Composable
fun AnswerField(
    value: String = "",
    modifier: Modifier = Modifier
) {
    TextField(
        value = "",
        onValueChange = {},
        label = { Text(stringResource(R.string.enter_answer_option_here))},
        modifier = Modifier
            .padding(bottom = 25.dp)
            .fillMaxWidth()
            .border(width = 5.dp, color = Color5),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color2,
            unfocusedContainerColor = Color2
        )
    )
}

@Composable
fun ReadOnlyTField(
    value : String = "",
    modifier: Modifier = Modifier
){
    TextField(
        value = value,
        textStyle = TextStyle(fontSize = 25.sp,
            textAlign = TextAlign.Center),
        onValueChange = {},
        readOnly = true,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 5.dp, color = Color5),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color4,
            focusedContainerColor = Color4,
        )
    )
}

@Preview(showBackground = true)
@Composable
fun QuizCreationPreview() {
    QuizCreation()
}