package com.example.mikantraining

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mikantraining.database.Exercise
import com.example.mikantraining.database.ExerciseDao
import com.example.mikantraining.database.ExerciseDatabase
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException
import java.lang.Exception

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var exerciseDao: ExerciseDao
    private lateinit var db: ExerciseDatabase
    private lateinit var problems: List<Pair<String, String>>

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, ExerciseDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        exerciseDao = db.exerciseDao
        problems = exerciseDao.readCsv("単語.csv")
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetData() {
        for (problem in problems) {
            exerciseDao.insertOnce(problem.first, problem.second)
        }
        assertEquals(exerciseDao.getStatusNum(0), 100)
        assertEquals(exerciseDao.getStatusNum(1), 0)
        assertEquals(exerciseDao.getStatusNum(2), 0)

        val exercises = exerciseDao.getAllData()
        val exercise = exercises[0]
        exercise.status = 1
        exerciseDao.update(exercise)
        assertEquals(exerciseDao.getStatusNum(0), 99)
    }
}
