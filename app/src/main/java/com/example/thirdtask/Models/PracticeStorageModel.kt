package com.example.thirdtask.Models

class PracticeStorageModel private constructor() {

    companion object {
        private var instance: PracticeStorageModel? = null
        operator fun invoke() = synchronized(this ) {
            if (instance == null)
                instance = PracticeStorageModel()
            instance
        }
    }

    private val practices: ArrayList<Practice> = ArrayList()

    fun addEditPractice(practice: Practice): ArrayList<Practice> {
        val index: Int? = practices.indices.firstOrNull{ it -> practices[it].uniqId == practice.uniqId}
        if (index != null) {
            practices[index] = practice
        } else {
            practices.add(practice)
        }
        return practices
    }

    fun getPractices(): ArrayList<Practice>{
        return practices
    }

}