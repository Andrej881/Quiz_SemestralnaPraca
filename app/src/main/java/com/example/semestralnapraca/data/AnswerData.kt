package com.example.semestralnapraca.data

/**
 * trieda s informáciami o odpovedi
 *
 * @param questionID id otázky, ku ktorej patrí
 * @param answerID id odpovede
 * @param points počet bodov za odpoveď
 * @param content obsah odpovede
 * @param correct správnosť odpovede
 * */
data class AnswerData (
    val questionID: String,
    val answerID: String = "",
    val points: Int = 0,
    val correct: Boolean = false,
    val content: String = ""
)