package com.example.semestralnapraca.data
/**
 * trieda s informáciami jedntilivych štatistik
 *
 * @param points počet bodov hráča
 * @param timeLeft koľko hráčovy ostalo času
 * @param userID id hráča
 * @param quizID id kvízu, ktorému patirí zapis štatistik
 * @param userEmail email hráča
 * */
data class StatData(
    val points:Int = 0,
    val timeLeft:String = "0:00",
    val userID:String = "",
    val quizID:String = "",
    val userEmail:String = ""
)
