package com.example.semestralnapraca.data

data class QuizData(
    val quizName: String,
    val quizId: String = "",
    val shared: Boolean = false,
    val shareID: String = "",
    val numberOfQuestions: Int = 0
)
