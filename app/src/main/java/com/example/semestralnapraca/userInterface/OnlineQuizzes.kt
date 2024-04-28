package com.example.semestralnapraca.userInterface

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.semestralnapraca.R
import com.example.semestralnapraca.ui.theme.Color3
import com.example.semestralnapraca.ui.theme.Color4
import com.example.semestralnapraca.ui.theme.Color5

@Composable
fun OnlineQuizzes() {
    Column (
        modifier = Modifier
            .padding(32.dp)
            .verticalScroll(rememberScrollState())
        ) {
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
        Spacer(modifier = Modifier.padding(bottom = 50.dp))
        Quizz("Quizz Name", modifier = Modifier.fillMaxWidth(), onClick = {})
        Quizz("Quizz Name", modifier = Modifier.fillMaxWidth(), onClick = {})
        Quizz("Quizz Name", modifier = Modifier.fillMaxWidth(), onClick = {})
        Quizz("Quizz Name", modifier = Modifier.fillMaxWidth(), onClick = {})
        Quizz("Quizz Name", modifier = Modifier.fillMaxWidth(), onClick = {})
        Quizz("Quizz Name", modifier = Modifier.fillMaxWidth(), onClick = {})
    }
}

@Composable
fun Quizz(
    quizzName: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    Button(
        modifier = modifier.padding(bottom = 25.dp)
            .border(width = 5.dp, color = Color5, shape = CircleShape),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color3
        )
    ) {
        Text(quizzName)
    }
}

@Preview(showBackground = true)
@Composable
fun OnlineQuizzesPreview() {
    OnlineQuizzes()
}