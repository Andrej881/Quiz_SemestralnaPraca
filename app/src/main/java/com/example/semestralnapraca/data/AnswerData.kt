package com.example.semestralnapraca.data

class AnswerData (
    val questionID: String,
    val answerID: String = "",
    val points: Int = 0,
    val correct: Boolean = false,
    val content: String = ""
)