package com.example.thirdtask.Crud

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.thirdtask.Models.Practice


@Entity(tableName = "practices")
data class PracticeEntity(
    @Embedded val practice: Practice
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
