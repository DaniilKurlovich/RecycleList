package com.example.thirdtask.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thirdtask.Crud.PracticeDao
import com.example.thirdtask.Crud.PracticeEntity
import com.example.thirdtask.Models.Practice
import java.lang.Exception


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
        const val ALPHBTCL_FILTER = "Alphabetical name"

        val filters = mutableListOf(NO_FILTER, ALPHBTCL_FILTER)
    }

    val practiceList: MutableLiveData<ArrayList<Practice>> = MutableLiveData()

    fun setFilter(name: String) {
        when (name) {
            NO_FILTER-> return
            ALPHBTCL_FILTER-> getSortedNameListByName()
            else -> throw Exception("Filter with name $name does not implemented.")
        }
    }

    fun getSortedNameListByName() {
        practiceDao.getAll().observeForever {
            val practices = castToArrayList(it)
            practices.sortBy { practice ->  practice.name }
            practiceList.postValue(practices)
        }
    }

    fun postPractices(){
        practiceDao.getAll().observeForever {
            val practices = mutableListOf<Practice>() as ArrayList<Practice>
            it.map { practiceEntity -> practices.add(practiceEntity.practice) }
            practiceList.postValue(practices)
        }
    }

    fun findPracticeByName(name: String) {
        if (name == "") {
            practiceDao.getAll().observeForever { practiceList.postValue(castToArrayList(it)) }
            return
        }

        practiceDao.getPracticeByName(name).observeForever { practiceList.postValue(castToArrayList(it)) }
    }

    fun addPractice(practice: Practice) {
        practiceDao.insert(practice as PracticeEntity)
        practiceDao.getAll().observeForever{ practiceList.postValue(castToArrayList(it)) }
    }

    fun castToArrayList(listPractices: List<PracticeEntity>?): ArrayList<Practice> {
        val practices = mutableListOf<Practice>() as ArrayList<Practice>
        listPractices?.map { practiceEntity -> practices.add(practiceEntity.practice) }
        return practices
    }
}
