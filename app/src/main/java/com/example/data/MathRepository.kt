package com.example.data

import android.content.Context
import com.example.model.Lesson
import com.example.model.Quiz
import com.example.model.QuizOption
import com.example.model.Topic
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MathRepository(private val dao: MathDao) {

    val allTopics: Flow<List<Topic>> = dao.getAllTopics()

    fun getLessonsForTopic(topicId: String): Flow<List<Lesson>> = dao.getLessonsForTopic(topicId)
    fun getQuizzesForTopic(topicId: String): Flow<List<Quiz>> = dao.getQuizzesForTopic(topicId)

    suspend fun markLessonCompleted(lessonId: String) {
        dao.markLessonCompleted(lessonId)
    }

    suspend fun markQuizCompleted(quizId: String, isPassed: Boolean) {
        dao.markQuizCompleted(quizId, isPassed)
    }
    
    suspend fun initializeDatabaseIfEmpty(context: Context) {
        if (dao.getTopicCount() > 0) return
        
        withContext(Dispatchers.IO) {
            val jsonString = context.assets.open("math_data.json").bufferedReader().use { it.readText() }
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(MathDataResponse::class.java)
            val data = adapter.fromJson(jsonString)
            
            if (data != null) {
                val dbTopics = data.topics.map { Topic(it.id, it.title, it.description) }
                val dbLessons = data.topics.flatMap { topic ->
                    topic.lessons.map { lesson ->
                        Lesson(lesson.id, topic.id, lesson.title, lesson.content, lesson.interactive)
                    }
                }
                
                dao.insertTopics(dbTopics)
                dao.insertLessons(dbLessons)
                
                if (data.quizzes != null) {
                    val dbQuizzes = data.quizzes.map { quiz ->
                        Quiz(quiz.id, quiz.topic_id, quiz.question, quiz.options)
                    }
                    dao.insertQuizzes(dbQuizzes)
                }
            }
        }
    }
}

// DTOs for Moshi matching the Python script JSON format
@JsonClass(generateAdapter = true)
data class MathDataResponse(
    val topics: List<TopicDto>,
    val quizzes: List<QuizDto>? = null
)

@JsonClass(generateAdapter = true)
data class TopicDto(
    val id: String,
    val title: String,
    val description: String,
    val lessons: List<LessonDto>
)

@JsonClass(generateAdapter = true)
data class LessonDto(
    val id: String,
    val title: String,
    val content: String,
    val interactive: Boolean
)

@JsonClass(generateAdapter = true)
data class QuizDto(
    val id: String,
    val topic_id: String,
    val question: String,
    val options: List<QuizOption>
)
