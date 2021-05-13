package com.example.thirdtask.Repositories

import androidx.lifecycle.MutableLiveData
import com.example.thirdtask.Crud.PracticeDao
import com.example.thirdtask.Crud.PracticeDatabase
import com.example.thirdtask.Crud.PracticeEntity
import com.example.thirdtask.Network.Habit
import com.example.thirdtask.Network.HabitUID
import com.example.thirdtask.Network.PracticeService
import kotlinx.coroutines.*
import retrofit2.awaitResponse
import kotlin.coroutines.CoroutineContext


class HabitRepository(val room: PracticeDao, val api: PracticeService): CoroutineScope {

    companion object {
        private var INSTANCE: HabitRepository? = null
        fun getInstance(room: PracticeDao, api: PracticeService): HabitRepository {
            if (INSTANCE == null) {
                synchronized(PracticeDatabase::class) {
                    if (INSTANCE == null) {
                        INSTANCE = HabitRepository(room, api)
                    }
                }
            }
            return INSTANCE!!
        }
    }
    private val job: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job + CoroutineExceptionHandler { _, e -> throw e }


    val practicesStorage: MutableLiveData<List<Habit>> = MutableLiveData()
    private val practices = mutableListOf<Habit>()

    suspend fun initPractices() {
        practices.addAll(api.getPractices())
        practicesStorage.postValue(practices)
    }

    suspend fun insert(habit: Habit) {
        habit.uid = null
        val habitUID: HabitUID = api.addHabit(habit).awaitResponse().body()!!
        habit.uid = habitUID.uid
        room.insert(PracticeEntity(habit))
        practices.add(habit)
        practicesStorage.postValue(practices)
    }

    suspend fun update(habit: Habit) {
        room.update(
            habit.title,
            habit.description,
            habit.priority,
            habit.type,
            habit.count,
            habit.frequency,
            habit.uid!!
        )
        api.addHabit(habit).awaitResponse()
        practices[practices.indexOfFirst { h -> h.uid == habit.uid }] = habit
        practicesStorage.postValue(practices)
    }
}