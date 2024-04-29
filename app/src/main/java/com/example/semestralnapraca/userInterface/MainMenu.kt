package com.example.semestralnapraca.userInterface

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semestralnapraca.R
import com.example.semestralnapraca.ui.theme.Color4
import com.example.semestralnapraca.ui.theme.Color5

@Composable
fun MainMenu(onNavigateBack: () -> Unit = {},
             navigateLibrary: () -> Unit = {},
             navigateOnline: () -> Unit = {},
             modifier: Modifier = Modifier,
             mainMenuViewModel: MainMenuViewModel = viewModel()) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(75.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        OptionButton(
            onClick = navigateLibrary ,
            text = stringResource(R.string.my_library),
            icon = R.drawable.library,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)

            )
        OptionButton(
            onClick = navigateOnline,
            text = stringResource(R.string.play_quizzes),
            icon = R.drawable.quizz,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
            )
        OptionButton(
            onClick = { mainMenuViewModel.signOutButton()
                      onNavigateBack()},
            text = stringResource(R.string.sign_out),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
            )
    }
}


@Composable
fun OptionButton(
    onClick: () -> Unit,
    @DrawableRes icon: Int? = null,
    text : String,
    modifier : Modifier
) {
    Button(onClick = onClick,
        modifier = modifier.border(width = 5.dp, color = Color5, shape = CutCornerShape(5.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color4
        ),
        shape = CutCornerShape(5.dp)
    )
    {
        Row (
            modifier = Modifier.fillMaxWidth()
        ){
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
}

@Preview(showBackground = true)
@Composable
fun MainMenuPreview()
{
    MainMenu()
}