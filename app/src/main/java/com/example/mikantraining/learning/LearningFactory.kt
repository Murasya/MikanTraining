package com.example.mikantraining.learning

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mikantraining.database.ExerciseDao
import java.lang.IllegalArgumentException

class LearningFactory(private val database: ExerciseDao, private val application: Application): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearningViewModel::class.java)) {
            return LearningViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}