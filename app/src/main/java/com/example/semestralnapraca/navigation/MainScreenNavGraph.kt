package com.example.semestralnapraca.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.semestralnapraca.R
import com.example.semestralnapraca.userInterface.Authorization
import com.example.semestralnapraca.userInterface.MainMenu
import com.example.semestralnapraca.userInterface.OnlineQuizzes
import com.example.semestralnapraca.userInterface.QuizCreation
import com.example.semestralnapraca.userInterface.QuizGame
import com.example.semestralnapraca.userInterface.QuizLibrary
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

enum class Screens(@StringRes val title: Int) {
    Authorization(title = R.string.auth),
    MainMenu(title = R.string.menu),
    Online(title = R.string.online),
    Creation(title = R.string.create),
    Game(title = R.string.game),
    Library(title = R.string.libr)
}
@Composable
fun MainScreenNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
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
            OnlineQuizzes()
        }
        composable(route = Screens.Creation.name) {
            QuizCreation { navController.navigate(Screens.Library.name) }
        }
        composable(route = Screens.Game.name) {
            QuizGame()
        }
        composable(route = Screens.Library.name) {
            QuizLibrary( navigateToQuizGame = {navController.navigate(Screens.Game.name)},
                navigateToQuizCreation = {navController.navigate(Screens.Creation.name)})
        }
    }
}
