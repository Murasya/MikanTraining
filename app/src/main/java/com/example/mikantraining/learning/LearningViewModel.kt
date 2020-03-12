package com.example.mikantraining.learning

import android.app.Application
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mikantraining.database.Exercise
import com.example.mikantraining.database.ExerciseDao
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt

class LearningViewModel(private val database: ExerciseDao, application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private val _question = MutableLiveData<String>()
    val question: LiveData<String>
        get() = _question
    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish
    private val _answers = MutableLiveData<MutableList<String>>()
    val answers: LiveData<MutableList<String>>
        get() = _answers
    private var i = 0
    private var answerIndex = -1
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var problems: List<Exercise> = emptyList()
    private lateinit var nowProblem: Exercise
    private var currentAnswer: Int = -1
    private var _eventCorrect = MutableLiveData<Boolean>()
    val eventCorrect: LiveData<Boolean>
        get() = _eventCorrect
    private var _eventIncorrect = MutableLiveData<Boolean>()
    val eventIncorrect: LiveData<Boolean>
        get() = _eventIncorrect
    private var textToSpeech: TextToSpeech? = null
    val handler = Handler()
    var runnable = Runnable { onMistake() }

    init {
        Log.d("LearningViewModel", "Start LearningViewModel")
        textToSpeech = TextToSpeech(application, this)
        initializeProblem()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let {
                val locale = Locale.ENGLISH
                if (it.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                    it.language = Locale.ENGLISH
                }
            }
        }
    }

    private suspend fun setDatabase() {
        withContext(Dispatchers.IO) {
            val exercises = database.readCsv("単語.csv")
            exercises.forEach {
                database.insertOnce(it.first, it.second)
            }
        }
    }

    private fun initializeProblem() {
        uiScope.launch {
            setDatabase()
            problems = getProblemFromDatabase().shuffled()
            answerIndex = problems.lastIndex
            setProblem()
        }
    }

    private suspend fun getProblemFromDatabase(): List<Exercise> {
        return withContext(Dispatchers.IO) {
            database.getAllData()
        }
    }

    private fun setProblem() {
        nowProblem = problems[i]
        textToSpeech?.speak(nowProblem.question, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
        _question.value = nowProblem.question
        currentAnswer = Random.nextInt(0..3)
        _answers.value = mutableListOf("", "", "", "")
        for (index in 0..3) {
            if (index == currentAnswer) {
                _answers.value?.add(index, nowProblem.answer)
            } else {
                _answers.value?.add(index, problems[answerIndex--].answer)
            }
        }

        handler.postDelayed(runnable, 10000)
    }

    private fun gameFinish() {
        _eventGameFinish.value = true
    }

    fun onButton(buttonNum: Int) {
        handler.removeCallbacks(runnable)
        if (buttonNum == currentAnswer) {
            onCorrect()
        } else {
            onMistake()
        }
    }

    private fun next() {
        i++
        setProblem()
    }

    private fun onCorrect() {
        uiScope.launch {
            _eventCorrect.value = true
            update(1)
            delay(500)
            if (i == 9) {
                gameFinish()
                return@launch
            }
            _eventCorrect.value = false
            next()
        }
    }

    private fun onMistake() {
        uiScope.launch {
            _eventIncorrect.value = true
            update(2)
            delay(1000)
            if (i == 9) {
                gameFinish()
                return@launch
            }
            _eventIncorrect.value = false
            next()
        }
    }

    private suspend fun update(statusNum: Int) {
        withContext(Dispatchers.IO) {
            nowProblem.status = statusNum
            database.update(nowProblem)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}