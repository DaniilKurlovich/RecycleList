package com.example.thirdtask.Crud

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface PracticeDao {
    @Query("select * from practices")
    fun getAll(): LiveData<List<PracticeEntity>>

    @Query("select * from practices where name like :find_name || '%'")
    fun getPracticeByName(find_name: String): LiveData<List<PracticeEntity>?>

    @Query("select * from practices where uniqId = :other_id")
    fun getPracticeById(other_id: String): LiveData<PracticeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(practice: PracticeEntity)

    @Query("UPDATE practices SET name = :name, description = :description, priority = :priority, typePractice = :type, count = :count, period = :period WHERE uniqId = :uniqId")
    fun update(name: String, description: String, priority: String, type: String, count: Int, period: Int,
               uniqId: String)

    @Query("DELETE FROM practices")
    fun nukeTable()
}