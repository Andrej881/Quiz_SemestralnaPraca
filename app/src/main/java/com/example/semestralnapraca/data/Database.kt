package com.example.semestralnapraca.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
            var newQuizRef: DatabaseReference
            if (quiz.quizId.equals("")) {
                newQuizRef = quizzesRef.push()
            } else {
                newQuizRef = quizzesRef.child(quiz.quizId)
            }

            var userID = ""

            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.let {
                userID = currentUser.uid
            }

            val quizData = hashMapOf(
                "userID" to userID,
                "name" to quiz.quizName,
                "sharedToPublicQuizzes" to quiz.shared.toString(),
                "shareID" to quiz.shareID,
                "numberOfQuestions" to quiz.numberOfQuestions,
                "time" to quiz.time.toString()
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

    suspend fun updateContentInDatabase(table: String, childPath: List<String>, updateInfo: HashMap<String, Any>) {
        val tablesRef = database.getReference(table)
        var contentRef = tablesRef.ref

        if (!childPath.isEmpty()) {
            childPath.forEach {
                Log.d("LD", it)
                contentRef = contentRef.child(it)
            }
        }
        Log.d("LD", contentRef.key.toString())
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
            val newQuestionRef:DatabaseReference
            if (qData.questionID.equals("")) {
                newQuestionRef = questionsRef.push()
            } else {
                newQuestionRef = questionsRef.child(qData.questionID)
            }
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

    suspend fun loadQuestionsFromDatabase(quizID: String): List<QuestionData> {
        return withContext(Dispatchers.IO) {
            val questionsRef = database.getReference("quizzes").child(quizID).child("questions")
            val questionList = mutableListOf<QuestionData>()

            val dataSnapshot = questionsRef.get().await()
            dataSnapshot.children.forEach { questionSnapshot ->
                val content = questionSnapshot.child("content").getValue(String::class.java) ?: ""
                val answers = questionSnapshot.child("numberOfAnswers").getValue(Int::class.java) ?: 0

                questionList.add(QuestionData(quizID=quizID, questionID = questionSnapshot.key.toString(), numberOfAnswers = answers, content = content))
            }
            questionList
        }
    }

    suspend fun addAnswerToDatabase(quizID: String, questionID: String, answerData: AnswerData): String {
        return withContext(Dispatchers.IO) {

            val answerRef = database.getReference("quizzes").child(quizID).child("questions").child(questionID).child("answers")
            val newAnswerRef = answerRef.push()

            val id = newAnswerRef.key.toString()
            val content = answerData.content
            val points = answerData.points
            val correct = answerData.correct

            val answerDataHash = hashMapOf(
                "questionID" to questionID,
                "content" to content,
                "points" to points,
                "correct" to correct
            )
            newAnswerRef.setValue(answerDataHash).await()
            return@withContext id
        }
    }
    suspend fun loadAnswersFromDatabase(quizID: String, questionID: String): List<AnswerData> {
        return withContext(Dispatchers.IO) {
            val quizRef = database.getReference("quizzes")
            val answerList = mutableListOf<AnswerData>()
            var answersRef = quizRef.child(quizID).child("questions").child(questionID).child("answers")

            val dataSnapshot = answersRef.get().await()
            dataSnapshot.children.forEach { answerSnapshot ->
                val content = answerSnapshot.child("content").getValue(String::class.java) ?: ""
                val correct = answerSnapshot.child("correct").getValue(Boolean::class.java) ?: false
                val points = answerSnapshot.child("points").getValue(Int::class.java) ?: 0

                answerList.add(AnswerData(
                    questionID =  questionID,
                    answerID = answerSnapshot.key.toString(),
                    points = points,
                    correct = correct,
                    content = content
                ))
            }
            answerList
        }
    }

    suspend fun removeAnswerFromDatabase(currentAnswerID: String, quizID: String, questionID: String) {
        Log.d("removeAnswerFromDatabase", quizID + questionID + currentAnswerID)
        withContext(Dispatchers.IO) {
            val quizzRef = database.getReference("quizzes").child(quizID).child("questions").child(questionID).child("answers").child(currentAnswerID)
            quizzRef.removeValue().await()
        }
    }

    suspend fun removeQuestionFromDatabase(quizID: String, questionID: String) {
        Log.d("removeQuestionFromDatabase", quizID + questionID)
        withContext(Dispatchers.IO) {
            val quizzRef = database.getReference("quizzes").child(quizID).child("questions").child(questionID)
            quizzRef.removeValue().await()
        }
    }
}