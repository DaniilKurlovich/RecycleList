package com.example.thirdtask.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thirdtask.Models.Practice
import com.example.thirdtask.Models.PracticeStorageModel
import java.lang.Exception


class ViewModelFactory(private val model: PracticeStorageModel) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PracticesListViewModel::class.java)) {
            val key = "PracticesListViewModel"
            if (hashMapViewModel.containsKey(key)) {
                return getViewModel(key) as T
            } else {
                addViewModel(key, PracticesListViewModel(model))
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

class PracticesListViewModel(private val model: PracticeStorageModel) : ViewModel() {
    companion object {
        val filters = mutableListOf("No filter", "Alphabetical name")
    }

    val practiceList: MutableLiveData<ArrayList<Practice>> = MutableLiveData()

    fun setFilter(name: String) {
        when (name) {
            "No filter" -> return
            "Alphabetical name" -> practiceList.postValue(getSortedNameListByName())
            else -> throw Exception("Filter with name $name does not implemented.")
        }
    }

    fun getSortedNameListByName(): ArrayList<Practice> {
        val practices = model.getPractices()
        practices.sortBy { it.name }
        return practices
    }

    fun findPracticeByName(name: String) {
        if (name == "") {
            practiceList.postValue(model.getPractices())
            return
        }

        practiceList.postValue(
            model.getPractices()
                .filter { practice -> practice.name!!.startsWith(name) } as ArrayList<Practice>)
    }

    fun addPractice(practice: Practice) {
        practiceList.postValue(model.addEditPractice(practice))
    }
}
