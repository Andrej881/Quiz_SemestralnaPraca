package com.example.semestralnapraca.data
/**
 * trieda s informáciami o otázke
 *
 * @param questionID id otazky
 * @param quizID id kvízu ku, ktoremu patrí
 * @param numberOfAnswers počet odpovedí k danej otázke
 * @param content obsah otázky
 * */
data class QuestionData(
    val quizID: String,
    val questionID: String = "",
    val numberOfAnswers: Int = 0,
    val content: String = ""
)
