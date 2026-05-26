package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.MathRepository
import com.example.model.Lesson
import com.example.model.Quiz
import com.example.model.Topic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class MathViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MathRepository
    
    init {
        val dao = AppDatabase.getDatabase(application).mathDao()
        repository = MathRepository(dao)
        
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty(application)
        }
    }

    val allTopics: StateFlow<List<Topic>> = repository.allTopics
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    private val _selectedTopicId = MutableStateFlow<String?>(null)
    
    val currentLessons: StateFlow<List<Lesson>> = _selectedTopicId
        .flatMapLatest { topicId ->
            if (topicId != null) repository.getLessonsForTopic(topicId) else MutableStateFlow(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentQuizzes: StateFlow<List<Quiz>> = _selectedTopicId
        .flatMapLatest { topicId ->
            if (topicId != null) repository.getQuizzesForTopic(topicId) else MutableStateFlow(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    fun selectTopic(topicId: String) {
        _selectedTopicId.value = topicId
    }

    fun markLessonCompleted(lessonId: String) {
        viewModelScope.launch {
            repository.markLessonCompleted(lessonId)
        }
    }
    
    fun submitQuizAnswer(quizId: String, isCorrect: Boolean) {
        viewModelScope.launch {
            repository.markQuizCompleted(quizId, isCorrect)
        }
    }
}
