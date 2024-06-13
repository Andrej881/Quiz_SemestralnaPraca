package com.example.semestralnapraca.data
/**
 * trieda s informáciami o kvízu
 *
 * @param quizId id kvízu
 * @param quizName meno kvízu
 * @param shared či je kvíz zdielany
 * @param shareID id zdielania kvízu
 * @param numberOfQuestions počet otázok kvízu
 * @param time čas na dokončenie kvízu v minútach
 * */
data class QuizData(
    val quizName: String,
    val quizId: String = "",
    val shared: Boolean = false,
    val shareID: String = "",
    val numberOfQuestions: Int = 0,
    val time: Int = 0
)
