package com.example.mikantraining.database

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mikantraining.MyContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Dao
interface ExerciseDao {
    @Insert
    fun insert(exercise: Exercise)

    @Update
    fun update(exercise: Exercise)

    @Query("select count(exerciseId) from exercise where status = :key")
    fun getStatusNum(key: Int): Int

    @Query("select answer from exercise")
    fun getAllAnswers(): List<String>

    @Query("select * from exercise")
    fun getAllData(): List<Exercise>

    @Query("insert into exercise (question, answer, status) select * from (select :q, :a, 0) as tmp where not exists (select * from exercise where question = :q) limit 1")
    fun insertOnce(q: String, a: String)

    fun readCsv(filename: String): List<Pair<String,String>> {
        var exercises: List<Pair<String, String>> = emptyList()
        try {
            val file = MyContext.getContext().resources.assets.open(filename)
            val fileReader = BufferedReader(InputStreamReader(file))
            fileReader.forEachLine {
                if (it.isNotBlank()) {
                    val line = it.split(",").toTypedArray()
                    exercises += fetchCsv(line)
                }
            }
        } catch (e: IOException) {
            Log.e("MainActivity", e.toString())
        }
        return exercises
    }

    private fun fetchCsv(line: Array<String>): Pair<String, String> {
        return Pair(line[0], line[1])
    }
}