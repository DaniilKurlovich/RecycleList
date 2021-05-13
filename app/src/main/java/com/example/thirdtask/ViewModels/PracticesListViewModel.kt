package com.example.thirdtask.ViewModels

import androidx.lifecycle.*
import com.example.thirdtask.Crud.PracticeDao
import com.example.thirdtask.Crud.PracticeEntity
import com.example.thirdtask.Network.Habit
import com.example.thirdtask.Repositories.HabitRepository
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext


class ViewModelFactory(private val repo: HabitRepository?) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PracticesListViewModel::class.java)) {
            val key = "PracticesListViewModel"
            if (hashMapViewModel.containsKey(key)) {
                return getViewModel(key) as T
            } else {
                addViewModel(key, PracticesListViewModel(repo!!))
                return getViewModel(key) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        val hashMapViewModel = HashMap<String, ViewModel>()
        fun addViewModel(key: String, viewModel: ViewModel) {
            hashMapViewModel.put(key, viewModel)
        }

        fun getViewModel(key: String): ViewModel? {
            return hashMapViewModel[key]
        }
    }
}

class PracticesListViewModel(private val repo: HabitRepository) : ViewModel(), CoroutineScope {
    companion object {
        const val NO_FILTER =  "No filter"
        const val ALPHABET_FILTER = "Alphabetical name"

        val filters = mutableListOf(NO_FILTER, ALPHABET_FILTER)
    }

    private val job: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job + CoroutineExceptionHandler { _, e -> throw e }

    val practiceList: MutableLiveData<List<Habit>> = MutableLiveData()
    val observer: Observer<List<Habit>> = Observer { practiceList.postValue(it) }

    init {
        repo.practicesStorage.observeForever(observer)
        launch {  repo.initPractices() }
    }

    fun setFilter(name: String) {
        when (name) {
            NO_FILTER-> return
            ALPHABET_FILTER-> getSortedNameListByName()
            else -> throw Exception("Filter with name $name does not implemented.")
        }
    }

    fun getSortedNameListByName() {
        repo.room.getAll().observeOnce {
            val practices = castToArrayList(it)
            practices.sortedBy { practice ->  practice.title }
            practiceList.postValue(practices)
        }
    }

    fun castToArrayList(listPractices: List<PracticeEntity>?): List<Habit> {
        val practices = mutableListOf<Habit>()
        listPractices?.map { practiceEntity -> practices.add(practiceEntity.practice) }
        return practices
    }

    fun findPracticeByName(name: String) {
        if (name == "") {
            repo.room.getAll().observeOnce{ practiceList.postValue(castToArrayList(it)) }
            return
        }

        repo.room.getPracticeByName(name).observeOnce { practiceList.postValue(castToArrayList(it)) }
    }


    override fun onCleared() {
        super.onCleared()
        repo.practicesStorage.removeObserver { observer }
    }

    fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
        observeForever(object : Observer<T> {

            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }

        })
    }
}
