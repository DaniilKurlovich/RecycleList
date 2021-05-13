package com.example.thirdtask.Crud

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.thirdtask.Network.Habit


@Entity(tableName = "practices")
data class PracticeEntity(
    @Embedded var practice: Habit
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
