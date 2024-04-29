package com.example.semestralnapraca.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Database {
    fun addQuizToDatabase(quiz : QuizData) {
        val database = FirebaseDatabase.getInstance().reference
        val quizzesRef = database.child("quizzes")

        val newQuizRef = quizzesRef.push()

        var userID = ""

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
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
    interface QuizLoadListener {
        fun onQuizzesLoaded(quizList: List<QuizData>)
    }
    fun loadQuizFromDatabase(listener: QuizLoadListener, onlyUsersQuizzes: Boolean = true) {

        val database = FirebaseDatabase.getInstance()
        val quizRef = database.getReference("quizzes")

        val quizList = mutableListOf<QuizData>()

        quizRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (quizSnapshot in dataSnapshot.children) {
                    val name = quizSnapshot.child("name").getValue(String::class.java) ?: ""

                    val quiz = QuizData(name)
                    quizList.add(quiz)
                }
                listener.onQuizzesLoaded(quizList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                val errorMessage = databaseError.message

                Log.e("Firebase Database", "Error: $errorMessage")
            }

        }
        )
    }
}