package com.example.semestralnapraca.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Database {
    private val database = FirebaseDatabase.getInstance()
    companion object {
        private var instance: Database? = null
        fun getInstance(): Database {
            if (instance == null) {
                instance = Database()
            }
            return instance!!
        }
    }

    fun addQuizToDatabase(quiz : QuizData): String {
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
            }
        return newQuizRef.key.toString()
    }
    interface QuizLoadListener {
        fun onQuizLoaded(quiz: QuizData)
    }

    fun loadQuizFromDatabase(quizID: String, listener: QuizLoadListener) {
        val quizRef = database.getReference("quizzes").child(quizID)

        quizRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.child("name").getValue(String::class.java) ?: ""
                val sharing = dataSnapshot.child("sharedToPublicQuizzes").getValue(String::class.java) ?: false.toString()
                val shareID = dataSnapshot.child("shareID").getValue(String::class.java) ?: ""
                val numberOfQuestions = dataSnapshot.child("numberOfQuestions").getValue(Int::class.java) ?: 0

                listener.onQuizLoaded(QuizData(name,quizID,sharing.toBoolean(),shareID,numberOfQuestions))
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e("LoadQuizFromDatabase", "Failed to read value.", error.toException())
            }
            })
    }
    interface QuizShareLoadListener {
        fun onQuizzesLoaded(quizShareID: String)
    }
    fun loadQuizFreeSharingKey(listener: QuizShareLoadListener) {
        val codeRef = database.getReference("freeSharingCode")

        codeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val codeSharingID = dataSnapshot.children.elementAt(0).child("code").getValue(String::class.java) ?: "0"
                Log.d("LoadingQuizSharingQuiz", codeSharingID)
                listener.onQuizzesLoaded(codeSharingID)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e("LoadingQuizSharingQuiz", "Failed to read value.", error.toException())
            }

        })
    }

    interface QuizzesLoadListener {
        fun onQuizzesLoaded(quizList: List<QuizData>)
    }
    fun loadQuizzesFromDatabase(listener: QuizzesLoadListener, sharedQuizzes: Boolean = false) {
        val quizzesRef = database.getReference("quizzes")

        val quizList = mutableListOf<QuizData>()

        quizzesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (quizSnapshot in dataSnapshot.children) {
                    val name = quizSnapshot.child("name").getValue(String::class.java) ?: ""
                    val sharing = quizSnapshot.child("sharedToPublicQuizzes").getValue(String::class.java) ?: false.toString()
                    val shareID = quizSnapshot.child("shareID").getValue(String::class.java) ?: ""
                    val questions =  quizSnapshot.child("numberOfQuestions").getValue(Int::class.java) ?: 0
                    if (!sharedQuizzes){
                        var currentUserID = ""
                        val userIDinQuiz = quizSnapshot.child("userID").getValue(String::class.java) ?: ""
                        val currentUser = FirebaseAuth.getInstance().currentUser

                        currentUser?.let {
                            currentUserID = currentUser.uid
                        }

                        if (currentUserID.equals(userIDinQuiz)) {
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

    fun updateContentInDatabase(table: String,contentID: String, updateInfo: HashMap<String, String>) {
        val quizzesRef = database.getReference(table)

        val quizRef = quizzesRef.child(contentID)

        updateInfo.forEach {
            quizRef.child(it.key).setValue(it.value)
                .addOnSuccessListener {
                    Log.d("Database", "${table} updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.w("Database", "Error updating ${table} ", e)
                }
        }

    }

    fun addQuestionToDatabase(quizID: String,qData: QuestionData) : String{
        val questionsRef = database.getReference().child("quizzes").child(quizID).child("questions")

        val newQuestionRef = questionsRef.push()

        val questionData = hashMapOf(
            "numberOfAnswers" to qData.numberOfAnswers,
            "content" to qData.content,
            //Add more data
        )

        newQuestionRef.setValue(questionData)
            .addOnSuccessListener {
                Log.d("addQuestionToDatabase","Question Succesfully Added To Database")
            }
            .addOnFailureListener { e ->
                Log.e("addQuestionToDatabase","Error",e)
            }
        return newQuestionRef.key.toString()
    }
    interface QuestionLoadListener {
        fun onQuestionLoaded(question: QuestionData)
    }
    fun loadQuestionFromDatabase(questionID: String, listener: QuestionLoadListener) {
        val questionRef = database.getReference("questions").child(questionID)

        questionRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val quizID = dataSnapshot.child("quizID").getValue(String::class.java) ?: ""
                val content = dataSnapshot.child("content").getValue(String::class.java) ?: ""
                val numberOfAnswers = dataSnapshot.child("numberOfAnswers").getValue(Int::class.java) ?: 0

                listener.onQuestionLoaded(QuestionData(
                    quizID = quizID,
                    questionID = questionRef.key.toString(),
                    numberOfAnswers = numberOfAnswers,
                    content = content
                ))
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e("LoadQuestionFromDatabase", "Failed to read value.", error.toException())
            }
        })
    }
}