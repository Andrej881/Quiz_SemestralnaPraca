package com.example.semestralnapraca.userInterface

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.semestralnapraca.R
import com.example.semestralnapraca.ui.theme.Color2
import com.example.semestralnapraca.ui.theme.Color3
import com.example.semestralnapraca.ui.theme.Color4
import com.example.semestralnapraca.ui.theme.Color5

@Composable
fun QuizzLibrary() {
    Column (
        modifier = Modifier
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),

    ){
        TextField(
            value = stringResource(R.string.my_library),
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
        Quizz(quizzName = "Quizz Name", modifier = Modifier.fillMaxWidth())
        Quizz(quizzName = "Quizz Name", modifier = Modifier.fillMaxWidth())
        Quizz(quizzName = "Quizz Name", modifier = Modifier.fillMaxWidth())
        Quizz(quizzName = "Quizz Name", modifier = Modifier.fillMaxWidth())
        QuizzButton(
            onClick = { /*TODO*/ },
            icon = R.drawable.add,
            color = Color4,
            modifier = Modifier.fillMaxWidth().border(width = 5.dp, color = Color5, shape = CircleShape)
        )
    }
}

@Composable
fun Quizz(
    quizzName: String,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier
            .padding(bottom = 25.dp)
            .border(width = 5.dp, color = Color5)

    ){
        Surface(color = Color3) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Column {
                    QuizzButton(
                        onClick = { /*TODO*/ },
                        icon = R.drawable.rename,
                        color = Color2)
                    QuizzButton(
                        onClick = { /*TODO*/ },
                        icon = R.drawable.share,
                        color = Color2)
                }
                Text(quizzName,
                    fontSize = 25.sp)
                QuizzButton(
                    onClick = { /*TODO*/ },
                    icon = R.drawable.remove,
                    color = Color4,
                    modifier = Modifier.padding(bottom = 50.dp)
                )
            }
        }
    }
}

@Composable
fun QuizzButton(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
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
            contentDescription = null,)
    }
}

@Preview(showBackground = true)
@Composable
fun GameLibraryPreview() {
    QuizzLibrary()
}

@Preview(showBackground = true)
@Composable
fun QuizzPreview() {
    Quizz("Quizz name",
        Modifier.fillMaxWidth())
}