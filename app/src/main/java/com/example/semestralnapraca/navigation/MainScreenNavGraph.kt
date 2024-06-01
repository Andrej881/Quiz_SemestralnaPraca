package com.example.semestralnapraca.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.semestralnapraca.R
import com.example.semestralnapraca.userInterface.Authorization
import com.example.semestralnapraca.userInterface.MainMenu
import com.example.semestralnapraca.userInterface.OnlineQuizzes
import com.example.semestralnapraca.userInterface.QuizCreation
import com.example.semestralnapraca.userInterface.QuizGame
import com.example.semestralnapraca.userInterface.QuizLibrary
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


var quizID:String = ""
/**
 * enum Obrazoviek aplikácie
 * */
enum class Screens(@StringRes val title: Int) {
    Authorization(title = R.string.auth),
    MainMenu(title = R.string.menu),
    Online(title = R.string.online),
    Creation(title = R.string.create),
    Game(title = R.string.game),
    Library(title = R.string.libr)
}
/**
 * Rieší riadenie toho na akej sa momentalne nachadzate obrazovke
 * Pomocou https://developer.android.com/codelabs/basic-android-kotlin-compose-navigation?hl=en#0
 *
 * @param modifier modifier upravujúci vlastnosti obrazovky
 * @param navController riadi navigaciu medzi obrazovkami
 * */
@Composable
fun MainScreenNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val startDestination: String
    val currentUser = Firebase.auth.currentUser
    if (currentUser != null) {
        startDestination = Screens.MainMenu.name
    } else {
        startDestination = Screens.Authorization.name
    }
    System.out.println(currentUser)
    NavHost(navController = navController,
        startDestination = startDestination ) {
        composable(route = Screens.Authorization.name) {
            Authorization(onNavigateUp = { navController.navigate(Screens.MainMenu.name) })
        }
        composable(route = Screens.MainMenu.name) {
            MainMenu(onNavigateBack = { navController.navigate(Screens.Authorization.name) },
                navigateLibrary = { navController.navigate(Screens.Library.name) },
                navigateOnline = { navController.navigate(Screens.Online.name) })
        }
        composable(route = Screens.Online.name) {
            OnlineQuizzes( navigateToQuizGame = {navController.navigate(Screens.Game.name)
                quizID = it
            }
            ) { navController.navigate(Screens.MainMenu.name) }
        }
        composable(route = Screens.Creation.name) {
            QuizCreation (navigateOnCancel =  {navController.navigate(Screens.Library.name)},
                quizID = quizID)
        }
        composable(route = Screens.Game.name) {
            QuizGame(
                quizID = quizID,
                navigateBack = {navController.navigateUp()}
            )
        }
        composable(route = Screens.Library.name) {
            QuizLibrary( navigateToQuizGame = {navController.navigate(Screens.Game.name)
                quizID = it
                },
                navigateToQuizCreation = {navController.navigate(Screens.Creation.name)
                    quizID = it
                },
                navigateToMainMenu = {navController.navigate(Screens.MainMenu.name)}
                )
        }
    }
}
