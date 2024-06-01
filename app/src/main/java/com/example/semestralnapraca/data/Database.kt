package com.example.semestralnapraca.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
/**
 * Trieda zabezpečuje ukladanie a čítanie dát z Firebase databázy
 *
 * Spravene pomocou firebase dokumentacie https://firebase.google.com/docs/database
 * a upravene na asynchronne aj pomocou ChatGPT
 * */
class Database {
    private val database = FirebaseDatabase.getInstance()
    /**
     * singleton inštancia triedy
     * */
    companion object {
        @Volatile
        private var instance: Database? = null
        /**
         * @return vráti singleton inšatnciu triedy
         * */
        fun getInstance(): Database {
            return instance ?: synchronized(this) {
                instance ?: Database().also { instance = it }
            }
        }
    }
    /**
     * uloží kvíz do databázy
     *
     * @param quiz data kvízu, ktorý sa ma uložiť
     * */
    suspend fun addQuizToDatabase(quiz : QuizData): String {
        return withContext(Dispatchers.IO) {
            val quizzesRef = database.getReference().child("quizzes")
            val newQuizRef: DatabaseReference = if (quiz.quizId == "") {
                quizzesRef.push()
            } else {
                quizzesRef.child(quiz.quizId)
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
    /**
     * Načíta kvíz z databázy
     * @param quizID id kvízu
     * @return vráti dáta kvízu
     * */
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
                val loadedTime = dataSnapshot.child("time").getValue(String::class.java) ?: "0"


                // Create and return QuizData object
                QuizData(name, quizID, sharing.toBoolean(), shareID, numberOfQuestions, loadedTime.toInt())
            } else {
                // If dataSnapshot is null or doesn't exist, return a default QuizData or handle it as needed
                QuizData("", "", false, "", 0, 0)
            }
        }
    }

    /**
     * @return vráti id zdielania z databázi pre kvíz, ktorému chcete nastaviť zdielanie
     * */
    suspend fun loadQuizFreeSharingKey(): String {
        return withContext(Dispatchers.IO) {
            val codeRef = database.getReference("freeSharingCode")
            val dataSnapshot = codeRef.get().await()
            val codeSharingID = dataSnapshot.children.firstOrNull()?.child("code")?.getValue(String::class.java) ?: "0"
            Log.d("LoadingQuizSharingQuiz", codeSharingID)
            codeSharingID
        }
    }
    /**
     * @param sharedQuizzes rozhoduje o tom či ma načítať všetky zdielane kvízy (= true) alebo kvízy patriace danému prihlásenemu uživateľovi(= false)
     * @return podľa parametrov vráti kvízi z databázi
     * */
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
    /**
     * Odstáni kvíz z databázy
     * @param quizID id kvízu
     * */
    suspend fun removeQuizFromDatabase(quizID: String) {
        withContext(Dispatchers.IO) {
            val quizzRef = database.getReference("quizzes").child(quizID)
            quizzRef.removeValue().await()
        }
    }
    /**
     * Podľa parametrov updatne obsah databázy
     * @param table tabuľka odkial začínaju referencie
     * @param childPath cesta na miesto kde ma nastať úprava
     * @param updateInfo hashmapa <meno dát v databaze, nove dáta> informácii na zmenu dát
     * */
    suspend fun updateContentInDatabase(table: String, childPath: List<String>, updateInfo: HashMap<String, Any>) {
        val tablesRef = database.getReference(table)
        var contentRef = tablesRef.ref

        if (childPath.isNotEmpty()) {
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

    /**
     * Pridá otázku do databázy
     *
     * @param quizID id quizu, ktorému má patriť
     * @param qData data otázky
     * @return Id otázky
     * */
    suspend fun addQuestionToDatabase(quizID: String, qData: QuestionData): String {
        return withContext(Dispatchers.IO) {
            val questionsRef = database.getReference().child("quizzes").child(quizID).child("questions")
            val newQuestionRef:DatabaseReference = if (qData.questionID == "") {
                questionsRef.push()
            } else {
                questionsRef.child(qData.questionID)
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
    /**
     * Načíta otázku z databázy
     *
     * @param quizID id quizu, ktorému patri
     * @param questionID id otázky
     * @return Dáta Otázky
     * */
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

    /**
     * @param quizID id quizu, ktorému patria
     * @return Vráti všetky otázky patriace vybranému kvízu z databázy
     * */
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
    /**
     * Pridá odpoveď do databázy
     *
     * @param quizID id quizu, ktorému má patriť
     * @param questionID id otázkym ktorej ma patriť
     * @param answerData data odpovede
     * @return Vráti id odpovede
     * */
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
    /**
     * @param quizID id quizu, ktoréj patrí otázka
     * @param questionID id otázky ktorej patira odpovede
     * @return Vráti všetky odpovede patriace vybranej otázky z databázy
     * */
    suspend fun loadAnswersFromDatabase(quizID: String, questionID: String): List<AnswerData> {
        return withContext(Dispatchers.IO) {
            val quizRef = database.getReference("quizzes")
            val answerList = mutableListOf<AnswerData>()
            val answersRef = quizRef.child(quizID).child("questions").child(questionID).child("answers")

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
    /**
     * Odstráni odpoveď z databázy
     *
     * @param currentAnswerID id odpovede
     * @param quizID id kvízu, ktoremu patri otázka
     * @param questionID id otázky ktorej patrí odpoveď
     * */
    suspend fun removeAnswerFromDatabase(currentAnswerID: String, quizID: String, questionID: String) {
        Log.d("removeAnswerFromDatabase", quizID + questionID + currentAnswerID)
        withContext(Dispatchers.IO) {
            val quizzRef = database.getReference("quizzes").child(quizID).child("questions").child(questionID).child("answers").child(currentAnswerID)
            quizzRef.removeValue().await()
        }
    }
    /**
     * Odstráni otázku z databázy
     *
     * @param quizID id kvízu, ktoremu patri otázka
     * @param questionID id otázky
     * */
    suspend fun removeQuestionFromDatabase(quizID: String, questionID: String) {
        Log.d("removeQuestionFromDatabase", quizID + questionID)
        withContext(Dispatchers.IO) {
            val quizzRef = database.getReference("quizzes").child(quizID).child("questions").child(questionID)
            quizzRef.removeValue().await()
        }
    }
    /**
     * Pridá zápis do štatistik kvízu
     *
     * @param quizID id kvízu
     * @param data dáta, čo sa majú zapísať
     * */
    suspend fun addStatToDatabase(quizID: String, data: StatData): String {
        return withContext(Dispatchers.IO) {
            val statisticsRef = database.getReference().child("quizzes").child(quizID).child("statistics")
            val newStatisticsRef = statisticsRef.push()
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userID = currentUser?.uid
            val userEmail = currentUser?.email

            val statisticsData = hashMapOf(
                "points" to data.points,
                "timeLeft" to data.timeLeft,
                "quizID" to quizID,
                "userID" to userID,
                "userEmail" to userEmail
            )
            newStatisticsRef.setValue(statisticsData).await()
            newStatisticsRef.key.toString()
        }
    }
    /**
     * @param quizID id kvízu, ktorého štatistiky sa získavajú
     * @return Vráti Zoznam všetkých zápisov štatistik daného kvízu
     * */
    suspend fun loadStatisticsFromDatabase(quizID: String): List<StatData> {
        return withContext(Dispatchers.IO) {
            val statRef = database.getReference("quizzes").child(quizID).child("statistics")
            val statList = mutableListOf<StatData>()

            val dataSnapshot = statRef.get().await()
            dataSnapshot.children.forEach { statSnapshot ->
                val points = statSnapshot.child("points").getValue(Int::class.java) ?: 0
                val timeLeft = statSnapshot.child("timeLeft").getValue(String::class.java) ?: "0:00"
                val userID = statSnapshot.child("userID").getValue(String::class.java) ?: ""
                val userEmail = statSnapshot.child("userEmail").getValue(String::class.java) ?: ""

                statList.add(StatData(
                    points = points,
                    timeLeft = timeLeft,
                    userID = userID,
                    userEmail = userEmail,
                    quizID = quizID
                ))
            }
            statList
        }
    }

}