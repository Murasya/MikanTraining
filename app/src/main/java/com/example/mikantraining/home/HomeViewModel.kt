package com.example.mikantraining.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mikantraining.database.ExerciseDao
import kotlinx.coroutines.*

class HomeViewModel(private var database: ExerciseDao) : ViewModel() {
    var unlearned: Int = 100
    var incorrect:Int = 0
    var correct: Int = 0
    var job = Job()

    suspend fun setValues() =
        withContext(Dispatchers.IO) {
            unlearned = database.getStatusNum(0)
            correct = database.getStatusNum(1)
            incorrect = database.getStatusNum(2)
            Log.d("HomeViewModel", correct.toString())
        }

    fun setDatabase() {
        unlearned = database.getStatusNum(0)
        correct = database.getStatusNum(1)
        incorrect = database.getStatusNum(2)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}