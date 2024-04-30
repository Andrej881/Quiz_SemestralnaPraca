package com.example.semestralnapraca.data

class AnswerData (
    val QuestionID: String,
    val AnswerID: String = "",
    val points: Int = 0,
    val correct: Boolean = false,
    val content: String = ""
)