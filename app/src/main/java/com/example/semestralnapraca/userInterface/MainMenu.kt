package com.example.semestralnapraca.userInterface

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.OutlinedButtonDefaults
import com.example.semestralnapraca.R
import com.example.semestralnapraca.ui.theme.Color3
import com.example.semestralnapraca.ui.theme.Color4
import com.example.semestralnapraca.ui.theme.Color5

@Composable
fun MainMenu() {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(75.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        OptionButton(
            onClick = { /*TODO*/ },
            text = stringResource(R.string.my_library),
            icon = R.drawable.library,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)

            )
        OptionButton(
            onClick = { /*TODO*/ },
            text = stringResource(R.string.play_quizzes),
            icon = R.drawable.quizz,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
            )
        OptionButton(
            onClick = { /*TODO*/ },
            text = stringResource(R.string.log_out),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
            )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun OptionButton(
    onClick: () -> Unit,
    @DrawableRes icon: Int? = null,
    text : String,
    modifier : Modifier
) {
    OutlinedButton(onClick =  onClick,
        modifier = modifier,
        colors = OutlinedButtonDefaults.colors(
            containerColor = Color4
        ),
        border = OutlinedButtonDefaults.border(
            border = Border(
                border = BorderStroke(
                    width = 5.dp,
                    color = Color5
                ),
            )
        ),
        shape = OutlinedButtonDefaults.shape(
            shape = AbsoluteCutCornerShape(5.dp)
        ),
    )
    {
        if (icon != null) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.padding(end = 48.dp)
            )
        } else {
            Spacer(modifier = Modifier.padding(end = 72.dp))
        }

        Text(
            text = text,
            color = Color5,
            textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
fun MainMenuPreview()
{
    MainMenu()
}