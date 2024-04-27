package com.example.semestralnapraca.userInterface

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.semestralnapraca.R
import com.example.semestralnapraca.ui.theme.MyTheme
import com.example.semestralnapraca.ui.theme.SemestralnaPracaTheme


@Composable
fun Login(
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        UserNameField(
            label = R.string.username,
            onValueChanged = {},
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
                .padding(top = 32.dp)
                .fillMaxWidth()
        )
        UserNameField(
            label = R.string.password,
            onValueChanged = {},
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
        )
        Button(onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(text = "Log in")
        }
        Button(onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        ) {
            Text(text = "Sign in")
        }
    }
}

@Composable
fun UserNameField(
    @StringRes label: Int,
    onValueChanged: (String) -> Unit,
    modifier: Modifier
) {
    TextField(
        value = "",
        singleLine = true,
        modifier = modifier,
        label = { Text(stringResource(label)) },
        onValueChange = onValueChanged
    )
}


@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MyTheme() {
        Login()
    }
}