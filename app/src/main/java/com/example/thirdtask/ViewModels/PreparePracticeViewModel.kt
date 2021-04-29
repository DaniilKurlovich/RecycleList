package com.example.thirdtask.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thirdtask.Crud.PracticeDao
import com.example.thirdtask.Crud.PracticeEntity
import com.example.thirdtask.Models.Practice
import java.util.*

class PreparePracticeViewModel(val practiceDao: PracticeDao) : ViewModel() {
    val practiceReady: MutableLiveData<Boolean> = MutableLiveData()

    private var statusReady: Boolean = false
    private var uniqId: String = UUID.randomUUID().toString()
    private lateinit var name: String
    private lateinit var description: String
    private lateinit var type: String
    private lateinit var level: String

    private var count: Int = -1
    private var period: Int = -1

    fun setName(name: String) {
        this.name = name
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun setType(type: String) {
        this.type = type
    }

    fun setLevel(level: String) {
        this.level = level
    }

    fun setCount(count: Int?) {
        if (count != null) {
            this.count = count
        }
    }

    fun setPeriod(period: Int?) {
        if (period != null) {
            this.period = period
        }
    }

    fun setUniqId(uniqId: String) {
        this.uniqId = uniqId
    }

    fun applyPractice() {
        this.statusReady =
            (this.period != -1) and (this.count != -1) and (this.name != "") and (this.description != "") and (this.type != "") and (this.level != "")
        if (this.statusReady){
            practiceDao.insert(PracticeEntity(Practice(this.name, this.description, this.level, this.type, this.count, this.period, uniqId)))
            practiceReady.postValue(true)
        }
    }

}