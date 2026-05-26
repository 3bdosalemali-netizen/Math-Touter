package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.model.Converters
import com.example.model.Lesson
import com.example.model.Quiz
import com.example.model.Topic
import kotlinx.coroutines.flow.Flow

@Dao
interface MathDao {
    @Query("SELECT * FROM topics")
    fun getAllTopics(): Flow<List<Topic>>

    @Query("SELECT * FROM lessons WHERE topicId = :topicId")
    fun getLessonsForTopic(topicId: String): Flow<List<Lesson>>

    @Query("SELECT * FROM quizzes WHERE topicId = :topicId")
    fun getQuizzesForTopic(topicId: String): Flow<List<Quiz>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<Topic>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<Lesson>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizzes(quizzes: List<Quiz>)

    @Query("UPDATE lessons SET isCompleted = 1 WHERE id = :lessonId")
    suspend fun markLessonCompleted(lessonId: String)

    @Query("UPDATE quizzes SET isCompleted = 1, isPassed = :isPassed WHERE id = :quizId")
    suspend fun markQuizCompleted(quizId: String, isPassed: Boolean)
    
    @Query("SELECT COUNT(*) FROM topics")
    suspend fun getTopicCount(): Int
}

@Database(entities = [Topic::class, Lesson::class, Quiz::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mathDao(): MathDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "math_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
