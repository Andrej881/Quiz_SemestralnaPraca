package com.example.semestralnapraca.userInterface

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.semestralnapraca.ui.theme.Color2
import com.example.semestralnapraca.ui.theme.Color3
import com.example.semestralnapraca.ui.theme.Color4
import com.example.semestralnapraca.ui.theme.Color5

@Composable
fun OnlineQuizzes(
    onlineQuizzesViewModel: OnlineQuizzesViewModel = viewModel(),
    modifier: Modifier = Modifier,
    navigateToQuizGame:(quizID:String) -> Unit = {}
) {
    val quizzesState by onlineQuizzesViewModel.quizzesState.collectAsState()
    ShowBadShareID(show = quizzesState.showQuizDoesNotExist,
        changeShowToFalse = {onlineQuizzesViewModel.changeShowQuizDoesNotExist(false)})
    LazyColumn(
        modifier = Modifier.padding(32.dp)
    ) {
        item {
            TextField(
                value = stringResource(R.string.quizzes),
                textStyle = TextStyle(fontSize = 25.sp,
                    textAlign = TextAlign.Center),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .border(width = 5.dp, color = Color5)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color4,
                    focusedContainerColor = Color4,
                )
            )
            SearchID(label = R.string.enter_id_of_quiz,
                icon = R.drawable.search,
                searchedQuizID = quizzesState.searchedShareID,
                onValueChanged = {onlineQuizzesViewModel.changeSearchedQuizID(it)},
                onButtonClick = {onlineQuizzesViewModel.searchQuizWihtID(quizzesState.searchedShareID)
                    if (!quizzesState.quizID.equals("")) {navigateToQuizGame(quizzesState.quizID)}
                }
            )
            Spacer(modifier = Modifier.padding(bottom = 50.dp))
        }
        items(quizzesState.quizzes) { quiz ->
            QuizInOnlineQuizzes(quizName = quiz.quizName, quizShareID = quiz.shareID ,onClick = {
                navigateToQuizGame(quiz.quizId)
                onlineQuizzesViewModel.changeQuizID("")})
        }
    }
}

@Composable
fun ShowBadShareID(
    show: Boolean,
    modifier: Modifier = Modifier,
    changeShowToFalse: () -> Unit,
    ) {
    if (show) {
        AlertDialog(onDismissRequest = changeShowToFalse,
            title = { Text(stringResource(R.string.id_does_not_exist), color = Color5) },
            modifier = modifier,
            confirmButton = {
                TextButton(onClick = changeShowToFalse) {
                    Text(text = stringResource(R.string.ok),
                        fontSize = 20.sp,
                        color = Color5)
                }
            })
    }
}

@Composable
fun SearchID(
    modifier: Modifier = Modifier,
    label: Int,
    @DrawableRes icon: Int,
    searchedQuizID: String,
    onValueChanged: (String) -> Unit,
    onButtonClick: () -> Unit
) {
    Row (
        modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .background(Color2)
            .border(width = 5.dp, Color5),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        TextField(
            value = searchedQuizID,
            singleLine = true,
            modifier = modifier.weight(0.8f),
            label = { Text(stringResource(label)) },
            onValueChange = onValueChanged,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color2,
                focusedContainerColor = Color2
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
        )
        Button(onClick = onButtonClick,
            shape = RectangleShape,
            modifier = modifier.weight(0.2f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color2
            )) {
            Icon(painter = painterResource(id = icon), contentDescription = null, modifier = modifier.size(24.dp))
        }
    }
}
@Composable
fun QuizInOnlineQuizzes(
    quizName: String,
    quizShareID: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    Row (
        modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
            .background(Color3)
            .border(width = 5.dp, Color5),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically){
        Text(text = "ID: [$quizShareID]",
            color = Color5,
            fontSize = 15.sp,
            modifier = modifier.padding(10.dp))
        Button(
            modifier = modifier
                .padding(10.dp)
                .widthIn(min = 1000.dp),
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color3
            ),
            shape = RectangleShape
        ){
            Text(quizName,
                color = Color5,
                fontSize = 15.sp,
                textAlign = TextAlign.Start,
                modifier = modifier
                    .padding(start = 25.dp)
                    .fillMaxWidth())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnlineQuizzesPreview() {
    OnlineQuizzes()
}