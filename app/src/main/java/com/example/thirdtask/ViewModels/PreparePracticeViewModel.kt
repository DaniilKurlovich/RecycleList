package com.example.thirdtask.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.thirdtask.Crud.PracticeDao
import com.example.thirdtask.Crud.PracticeEntity
import com.example.thirdtask.Models.Practice
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext


class PreparePracticeViewModel(val practiceDao: PracticeDao) : ViewModel(), CoroutineScope {

    private val job: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job + CoroutineExceptionHandler { _, e -> throw e }

    val practiceReady: MutableLiveData<Boolean> = MutableLiveData()

    private var statusReady: Boolean = false
    private var uniqId: String? = null
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
        if (this.statusReady) {

            val practice = Practice(
                this.name,
                this.description,
                this.level,
                this.type,
                this.count,
                this.period,
                uniqId
            )

            launch {
                if (practice.uniqId == null) {
                    practice.uniqId = UUID.randomUUID().toString()
                    practiceDao.insert(PracticeEntity(practice))
                } else {
                    practiceDao.update(
                        practice.name!!,
                        practice.description!!,
                        practice.priority!!,
                        practice.typePractice!!,
                        practice.count,
                        practice.period,
                        practice.uniqId!!
                    )
                }
            }

            practiceReady.postValue(true)
        }
    }

}