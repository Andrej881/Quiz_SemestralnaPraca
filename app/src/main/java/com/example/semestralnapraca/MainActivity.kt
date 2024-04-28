package com.example.semestralnapraca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.semestralnapraca.ui.theme.SemestralnaPracaTheme
import com.example.semestralnapraca.userInterface.Authorization
import com.example.semestralnapraca.userInterface.QuizzLibrary
import com.example.semestralnapraca.userInterface.MainMenu
import com.example.semestralnapraca.userInterface.OnlineQuizzes
import com.example.semestralnapraca.userInterface.QuizCreation
import com.example.semestralnapraca.userInterface.QuizzGame

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SemestralnaPracaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    QuizzGame()
                }
            }
        }
    }
}