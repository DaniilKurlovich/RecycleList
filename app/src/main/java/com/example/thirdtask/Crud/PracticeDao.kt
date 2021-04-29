package com.example.thirdtask.Crud

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.thirdtask.Models.Practice


@Dao
interface PracticeDao {
    @Query("select * from practices")
    fun getAll(): LiveData<List<PracticeEntity>>

    @Query("select * from practices where name like :find_name || '%'")
    fun getPracticeByName(find_name: String): LiveData<List<PracticeEntity>?>

    @Insert
    fun insert(practice: PracticeEntity)

    @Query("DELETE FROM practices")
    fun nukeTable()
}