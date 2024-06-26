package com.example.semestralnapraca.userInterface

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semestralnapraca.R
import com.example.semestralnapraca.ui.theme.Color2
import com.example.semestralnapraca.ui.theme.Color4
import com.example.semestralnapraca.ui.theme.Color5

/**
* Prihlasovanie do firebase UI
* Spravená podľa tutorialu https://firebase.google.com/docs/auth/android/start
*
* @param modifier modifier upravujúci vlastnosti obrazovky
* @param authorizationViewModel viewModel obrazovky
* @param onNavigateUp funkcia, ktorá zabezpečí navigáciu na daľšiu obrazovku po úspešnom prihlásení
* */
@Composable
fun Authorization(
    modifier: Modifier = Modifier,
    authorizationViewModel: AuthorizationViewModel = viewModel(),
    onNavigateUp: () -> Unit = {}
) {
    if (authorizationViewModel.errorMessage) {
        AlertDialog(onDismissRequest = { /* Do nothing */ },
            title = { Text(stringResource(R.string.failed),
                fontSize = 30.sp,
                color = Color5) },
            text = { Text(stringResource(R.string.wrongInfo),
                fontSize = 25.sp,
                color = Color5) },
            modifier = modifier,
            confirmButton = {
                TextButton(onClick = {authorizationViewModel.errorMessage = false},
                    colors = ButtonDefaults.buttonColors(contentColor = Color5,
                        containerColor = Color.Transparent)) {
                    Text(text = stringResource(R.string.ok))
                }
            },
            containerColor = Color2)
    }

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
            value = authorizationViewModel.email,
            leadingIcon = R.drawable.email,
            onValueChanged = {authorizationViewModel.updateEmail(it)},
            modifier = Modifier
                .padding(bottom = 32.dp,top = 32.dp)
                .fillMaxWidth()
        )
        AuthorizationTextField(
            password = true,
            label = R.string.password,
            value = authorizationViewModel.password,
            leadingIcon = R.drawable.password,
            onValueChanged = {authorizationViewModel.updatePassword(it)},
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
        )
        AuthorizationButton(onClick = {authorizationViewModel.logInClick( onNavigateUp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            text = stringResource(R.string.log_in)
        )
        AuthorizationButton(onClick = { authorizationViewModel.signUpClick(onNavigateUp)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            text = stringResource(R.string.sign_up)
        )
    }
}

/**
 * Tlačidlo na obrazovke
 *
 * @param onClick Funkcia rozhodujúca čo sa má stať po stlačení
 * @param text text napísani na tlačidlu
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * */
@Composable
fun AuthorizationButton(
    onClick: () -> Unit,
    text : String,
    modifier : Modifier
) {
    Button(onClick = onClick,
        modifier = modifier.border(width = 5.dp, color = Color5, shape = CutCornerShape(5.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color4,
            contentColor = Color5

        ),
        shape = CutCornerShape(5.dp)
    )
         {
             Text(text = text,
                 color = Color5)
    }
}

/**
 * Poľe, do ktorého sa zapisujú informácie pre prihlásenie
 *
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * @param label označenie textoveho poľa
 * @param leadingIcon ikona poľa
 * @param onValueChanged funkcia, ktorá sa ma zavolať pri zmene hodnoty poľa
 * @param password či má poľe namiesto písaneho textu ukazovať iba ***
 * */
@Composable
fun AuthorizationTextField(
    label: Int,
    value: String = "",
    @DrawableRes leadingIcon: Int,
    onValueChanged: (String) -> Unit,
    modifier: Modifier,
    password: Boolean = false
) {
    TextField(
        leadingIcon = { Icon(painter = painterResource(id = leadingIcon), null, tint = Color5) },
        value = value,
        singleLine = true,
        modifier = modifier.border(width = 5.dp, Color5),
        label = { Text(stringResource(label)) },
        onValueChange = onValueChanged,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color2,
            focusedContainerColor = Color2,
            unfocusedLabelColor = Color5,
            focusedLabelColor = Color5,
            focusedTextColor = Color5,
            unfocusedTextColor = Color5
        ),
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None
    )
}
/**
 * Preview obrazovky
 * */
@Preview(showBackground = true)
@Composable
fun AuthorizationPreview() {
    Authorization()
}