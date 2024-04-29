package com.example.semestralnapraca.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Database {
    val database = FirebaseDatabase.getInstance()
    fun addQuizToDatabase(quiz : QuizData) {
        val quizzesRef = database.getReference().child("quizzes")

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
        val quizzesRef = database.getReference("quizzes")

        val quizList = mutableListOf<QuizData>()

        quizzesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (quizSnapshot in dataSnapshot.children) {
                    val name = quizSnapshot.child("name").getValue(String::class.java) ?: ""
                    if (onlyUsersQuizzes){
                        var currentUserID = ""
                        val userIDinQuiz = quizSnapshot.child("userID").getValue(String::class.java) ?: ""
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        currentUser?.let {
                            currentUserID = currentUser.uid
                        }

                        if (currentUserID.equals(userIDinQuiz)) {
                            val quiz = QuizData(name, quizSnapshot.key.toString())
                            quizList.add(quiz)
                        }
                    } else {
                        val quiz = QuizData(name, quizSnapshot.key.toString())
                        quizList.add(quiz)
                    }

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

    fun removeQuizFromDatabase(quizID: String) {
        val quizzRef = database.getReference("quizzes").child(quizID)

        quizzRef.removeValue()
            .addOnSuccessListener {
                Log.d("Database", "Quiz deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.w("Database", "Error deleting quiz", e)
            }
    }

    fun updateQuizInDatabase(quizID: String, updateInfo: HashMap<String, String>) {
        val quizzesRef = database.getReference("quizzes")

        val quizRef = quizzesRef.child(quizID)

        updateInfo.forEach {
            quizRef.child(it.key).setValue(it.value)
                .addOnSuccessListener {
                    Log.d("Database", "Quiz updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.w("Database", "Error updating quiz ", e)
                }
        }

    }
}