package com.example.semestralnapraca.data

data class QuestionData(
    val quizID: String,
    val questionID: String = "",
    val numberOfAnswers: Int = 0,
    val content: String = ""
)
