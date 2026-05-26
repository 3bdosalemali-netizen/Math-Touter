package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "topics")
data class Topic(
    @PrimaryKey val id: String,
    val title: String,
    val description: String
)

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey val id: String,
    val topicId: String,
    val title: String,
    val content: String,
    val interactive: Boolean,
    val isCompleted: Boolean = false
)

@Entity(tableName = "quizzes")
data class Quiz(
    @PrimaryKey val id: String,
    val topicId: String,
    val question: String,
    val options: List<QuizOption>,
    val isCompleted: Boolean = false,
    val isPassed: Boolean = false
)

data class QuizOption(
    val id: String,
    val content: String,
    val is_correct: Boolean
)

class Converters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, QuizOption::class.java)
    private val adapter: JsonAdapter<List<QuizOption>> = moshi.adapter(listType)

    @TypeConverter
    fun fromOptionsList(value: List<QuizOption>?): String {
        return adapter.toJson(value ?: emptyList())
    }

    @TypeConverter
    fun toOptionsList(value: String): List<QuizOption> {
        return adapter.fromJson(value) ?: emptyList()
    }
}
