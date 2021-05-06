package com.example.thirdtask.ViewModels

import androidx.lifecycle.*
import com.example.thirdtask.Crud.PracticeDao
import com.example.thirdtask.Crud.PracticeEntity
import com.example.thirdtask.Models.Practice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import java.lang.Exception
import kotlin.coroutines.CoroutineContext


class ViewModelFactory(private val practiceDao: PracticeDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PracticesListViewModel::class.java)) {
            val key = "PracticesListViewModel"
            if (hashMapViewModel.containsKey(key)) {
                return getViewModel(key) as T
            } else {
                addViewModel(key, PracticesListViewModel(practiceDao))
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

class PracticesListViewModel(private val practiceDao: PracticeDao) : ViewModel() {
    companion object {
        const val NO_FILTER =  "No filter"
        const val ALPHABET_FILTER = "Alphabetical name"

        val filters = mutableListOf(NO_FILTER, ALPHABET_FILTER)
    }

    val practiceList: MutableLiveData<ArrayList<Practice>> = MutableLiveData()
    val observer: Observer<List<PracticeEntity>> = Observer { newPractices(it) }

    init {
        practiceDao.getAll().observeForever(observer)
    }

    fun setFilter(name: String) {
        when (name) {
            NO_FILTER-> return
            ALPHABET_FILTER-> getSortedNameListByName()
            else -> throw Exception("Filter with name $name does not implemented.")
        }
    }

    fun newPractices(it: List<PracticeEntity>) {
        val practices = castToArrayList(it)
        practices.sortBy { practice ->  practice.name }
        practiceList.postValue(practices)

    }

    fun getSortedNameListByName() {
        practiceDao.getAll().observeOnce {
            val practices = castToArrayList(it)
            practices.sortBy { practice ->  practice.name }
            practiceList.postValue(practices)
        }
    }

    fun findPracticeByName(name: String) {
        if (name == "") {
            practiceDao.getAll().observeOnce { practiceList.postValue(castToArrayList(it)) }
            return
        }

        practiceDao.getPracticeByName(name).observeOnce { practiceList.postValue(castToArrayList(it)) }

    }

    fun castToArrayList(listPractices: List<PracticeEntity>?): ArrayList<Practice> {
        val practices = mutableListOf<Practice>() as ArrayList<Practice>
        listPractices?.map { practiceEntity -> practices.add(practiceEntity.practice) }
        return practices
    }

    override fun onCleared() {
        super.onCleared()
        practiceDao.getAll().removeObserver { observer }
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
