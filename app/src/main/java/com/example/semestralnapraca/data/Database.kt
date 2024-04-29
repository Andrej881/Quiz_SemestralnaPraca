package com.example.semestralnapraca.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Database constructor(){
    fun addQuizToDatabase(quiz : Quiz) {
        val database = FirebaseDatabase.getInstance().reference
        val quizzesRef = database.child("quizzes")

        val newQuizRef = quizzesRef.push()
        val quizID = newQuizRef.key

        var userID: String = ""

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            userID = currentUser.uid
        }

        val quizData = hashMapOf(
            "userID" to userID,
            "name" to quiz.quizName
            //Add more data
        )

        newQuizRef.setValue(quizData)
            .addOnSuccessListener {
                // Data successfully written to the database
            }
            .addOnFailureListener { e ->
                // Handle any errors
            }
    }


}