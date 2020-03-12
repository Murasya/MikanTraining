package com.example.mikantraining.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    var exerciseId: Long = 0L,

    @ColumnInfo(name = "question")
    var question: String = "",

    @ColumnInfo(name = "answer")
    var answer: String = "",

    // unlearned = 0, correct = 1, mistake = 2
    @ColumnInfo(name = "status")
    var status: Int = 0
)