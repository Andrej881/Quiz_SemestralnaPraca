package com.example.semestralnapraca.userInterface

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import  androidx.tv.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.OutlinedButtonDefaults
import com.example.semestralnapraca.R
import com.example.semestralnapraca.ui.theme.Color2
import com.example.semestralnapraca.ui.theme.Color3
import com.example.semestralnapraca.ui.theme.Color4
import com.example.semestralnapraca.ui.theme.Color5


@Composable
fun Authorization(
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
        AuthorizationTextField(
            label = R.string.email,
            leadingIcon = R.drawable.email,
            onValueChanged = {},
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
                .padding(top = 32.dp)
                .fillMaxWidth()
        )
        AuthorizationTextField(
            label = R.string.password,
            leadingIcon = R.drawable.password,
            onValueChanged = {},
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
        )
        AuthorizationButton(onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            text = stringResource(R.string.log_in)
        )
        AuthorizationButton(onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            text = stringResource(R.string.sign_in)
        )
    }
}
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AuthorizationButton(
    onClick: () -> Unit,
    text : String,
    modifier : Modifier
) {
    OutlinedButton(onClick = onClick,
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
        )

    )
         {
             Text(text = text,
                 color = Color5)
    }
}

@Composable
fun AuthorizationTextField(
    label: Int,
    @DrawableRes leadingIcon: Int,
    onValueChanged: (String) -> Unit,
    modifier: Modifier
) {
    TextField(
        leadingIcon = { Icon(painter = painterResource(id = leadingIcon), null) },
        value = "",
        singleLine = true,
        modifier = modifier.border(width = 5.dp, Color5),
        label = { Text(stringResource(label)) },
        onValueChange = onValueChanged,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color2,
            focusedContainerColor = Color2

        )

    )
}
@Preview(showBackground = true)
@Composable
fun AuthorizationPreview() {
    Authorization()
}