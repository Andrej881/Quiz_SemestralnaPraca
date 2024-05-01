package com.example.semestralnapraca.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Database {
    private val database = FirebaseDatabase.getInstance()
    companion object {
        /*private var instance: Database? = null
        fun getInstance(): Database {
            if (instance == null) {
                instance = Database()
            }
            return instance!!
        }*/
        @Volatile
        private var instance: Database? = null

        fun getInstance(): Database {
            return instance ?: synchronized(this) {
                instance ?: Database().also { instance = it }
            }
        }
    }

    suspend fun addQuizToDatabase(quiz : QuizData): String {
        return withContext(Dispatchers.IO) {
            val quizzesRef = database.getReference().child("quizzes")
            val newQuizRef = quizzesRef.push()

            var userID = ""

            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.let {
                userID = currentUser.uid
            }

            val quizData = hashMapOf(
                "userID" to userID,
                "name" to quiz.quizName,
                "sharedToPublicQuizzes" to quiz.shared.toString(),
                "shareID" to quiz.quizId,
                "numberOfQuestions" to quiz.numberOfQuestions,
                //Add more data
            )

            newQuizRef.setValue(quizData)
                .addOnSuccessListener {
                    Log.d("addQuizToDatabase", "Quiz Added Succesfully")
                }
                .addOnFailureListener { e ->
                    Log.e("addQuizToDatabase","ERROR adding quiz",e)
                }.await()

            newQuizRef.key.toString()
        }
    }

    suspend fun loadQuizFromDatabase(quizID: String): QuizData {
        return withContext(Dispatchers.IO) {
            val quizRef = database.getReference("quizzes").child(quizID)

            // Use addListenerForSingleValueEvent to retrieve data once
            val dataSnapshot = quizRef.get().await()

            // Check if dataSnapshot is null or if it doesn't exist
            if (dataSnapshot.exists()) {
                // Retrieve values using dataSnapshot
                val name = dataSnapshot.child("name").getValue(String::class.java) ?: ""
                val sharing = dataSnapshot.child("sharedToPublicQuizzes").getValue(String::class.java) ?: false.toString()
                val shareID = dataSnapshot.child("shareID").getValue(String::class.java) ?: ""
                val numberOfQuestions = dataSnapshot.child("numberOfQuestions").getValue(Int::class.java) ?: 0

                // Create and return QuizData object
                QuizData(name, quizID, sharing.toBoolean(), shareID, numberOfQuestions)
            } else {
                // If dataSnapshot is null or doesn't exist, return a default QuizData or handle it as needed
                QuizData("", "", false, "", 0)
            }
        }
    }
    suspend fun loadQuizFreeSharingKey(): String {
        return withContext(Dispatchers.IO) {
            val codeRef = database.getReference("freeSharingCode")
            val dataSnapshot = codeRef.get().await()
            val codeSharingID = dataSnapshot.children.firstOrNull()?.child("code")?.getValue(String::class.java) ?: "0"
            Log.d("LoadingQuizSharingQuiz", codeSharingID)
            codeSharingID
        }
    }
    suspend fun loadQuizzesFromDatabase(sharedQuizzes: Boolean = false): List<QuizData> {
        return withContext(Dispatchers.IO) {
            val quizzesRef = database.getReference("quizzes")
            val quizList = mutableListOf<QuizData>()

            val dataSnapshot = quizzesRef.get().await()
            dataSnapshot.children.forEach { quizSnapshot ->
                val name = quizSnapshot.child("name").getValue(String::class.java) ?: ""
                val sharing = quizSnapshot.child("sharedToPublicQuizzes").getValue(String::class.java) ?: false.toString()
                val shareID = quizSnapshot.child("shareID").getValue(String::class.java) ?: ""
                val questions = quizSnapshot.child("numberOfQuestions").getValue(Int::class.java) ?: 0

                if (!sharedQuizzes) {
                    val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val userIDinQuiz = quizSnapshot.child("userID").getValue(String::class.java) ?: ""

                    if (currentUserID == userIDinQuiz) {
                        val quiz = QuizData(name, quizSnapshot.key.toString(), shared = sharing.toBoolean(), shareID = shareID, numberOfQuestions = questions)
                        quizList.add(quiz)
                    }
                } else {
                    if (sharing.toBoolean()) {
                        val quiz = QuizData(name, quizSnapshot.key.toString(), shared = sharing.toBoolean(), shareID = shareID, numberOfQuestions = questions)
                        quizList.add(quiz)
                    }
                }
            }
            quizList
        }
    }

    suspend fun removeQuizFromDatabase(quizID: String) {
        withContext(Dispatchers.IO) {
            val quizzRef = database.getReference("quizzes").child(quizID)
            quizzRef.removeValue().await()
        }
    }

    suspend fun updateContentInDatabase(table: String, contentID: List<String>, updateInfo: HashMap<String, Any>) {
        val tablesRef = database.getReference(table)
        var contentRef = tablesRef.ref

        if (!contentID.isEmpty()) {
            contentID.forEach {
                contentRef = contentRef.child(it)
            }
        }
        try {
            updateInfo.forEach { (key, value) ->
                withContext(Dispatchers.IO) {
                    contentRef.child(key).setValue(value).await()
                }
                Log.d("Database", "${contentRef.key.toString()} updated successfully $value ")
            }
        } catch (e: Exception) {
            Log.e("Database", "Error updating $table", e)
        }
    }

    suspend fun addQuestionToDatabase(quizID: String, qData: QuestionData): String {
        return withContext(Dispatchers.IO) {
            val questionsRef = database.getReference().child("quizzes").child(quizID).child("questions")
            val newQuestionRef = questionsRef.push()
            val questionData = hashMapOf(
                "numberOfAnswers" to qData.numberOfAnswers,
                "content" to qData.content
                //Add more data
            )
            newQuestionRef.setValue(questionData).await()
            newQuestionRef.key.toString()
        }
    }
    suspend fun loadQuestionFromDatabase(quizID: String, questionID: String): QuestionData {
        return withContext(Dispatchers.IO) {
            val questionRef = database.getReference("quizzes").child(quizID).child("questions").child(questionID)
            val dataSnapshot = questionRef.get().await()
            val content = dataSnapshot.child("content").getValue(String::class.java) ?: "isEmpty"
            val numberOfAnswers = dataSnapshot.child("numberOfAnswers").getValue(Int::class.java) ?: 0

            Log.d("LoadQuestionFromDatabase", content)
            return@withContext QuestionData(
                quizID = quizID,
                questionID = questionRef.key.toString(),
                numberOfAnswers = numberOfAnswers,
                content = content
            )
        }
    }
}