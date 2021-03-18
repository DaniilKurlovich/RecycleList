package com.example.thirdtask

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.list_activity.*

interface PracticeClickListener {
    fun onPracticeListener(data: Practice)
}


class ListActivity : AppCompatActivity(), PracticeClickListener {
    private val listPractice = ArrayList<Practice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_activity)
    }

    override fun onStart() {
        super.onStart()
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = PracticeAdapter(listPractice, this)
        val fab: View = findViewById(R.id.floatingActionButton)
        fab.setOnClickListener {
            startActivityForResult(Intent(this, AddEditActivity::class.java), 1)
        }

        if (intent.hasExtra(TransportParams.TYPE_PRACTICE.name)){
            recycler_view.adapter?.notifyDataSetChanged()
        }

        if (intent.hasExtra("practices_array")) {
            val practicesList: ArrayList<Practice> = intent.getParcelableArrayListExtra("practices_array")!!
            listPractice.addAll(practicesList)
            recycler_view.adapter?.notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // ADD NEW ITEM
        val name_practice = data?.getStringExtra(TransportParams.NAME_PRACTICE.name)
        val descr_practice = data?.getStringExtra(TransportParams.DESCRIPTION_PRACTICE.name)
        val level = data?.getStringExtra(TransportParams.LEVEL_PRACTICE.name)
        val type = data?.getStringExtra(TransportParams.TYPE_PRACTICE.name)
        val count = data?.getIntExtra(TransportParams.COUNT_PRACTICE.name, 0) ?: 1
        val period = data?.getIntExtra(TransportParams.PERIOD_PRACTICE.name, 0) ?: 1
        val id = data?.getStringExtra(TransportParams.ID_PRACTICE.name)

        val newPractice = Practice(
            name = name_practice, description = descr_practice, priority = level,
            typePractice = type, period = period, count = count
        )

        if (data?.hasExtra("edit_mode") == true) {
            listPractice.forEachIndexed { index: Int, practice: Practice ->
                if (practice.uniq_id == id) {
                    practice.setParcel(newPractice)
                }
            }
        } else {
            listPractice.add(newPractice)
        }
        recycler_view.adapter?.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("practices_array", listPractice as ArrayList<out Parcelable>?)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        listPractice.clear()
        
        val practicesList: ArrayList<Practice> =
            savedInstanceState.getParcelableArrayList("practices_array")!!
        listPractice.addAll(practicesList)
        recycler_view.adapter?.notifyDataSetChanged()
    }

    override fun onPracticeListener(data: Practice) {
        val intent: Intent = Intent(this, AddEditActivity::class.java)

        intent.putExtra("edit_mode", true)
        intent.putExtra(TransportParams.NAME_PRACTICE.name, data.name)
        intent.putExtra(TransportParams.DESCRIPTION_PRACTICE.name, data.description)
        intent.putExtra(TransportParams.TYPE_PRACTICE.name, data.typePractice)
        intent.putExtra(TransportParams.LEVEL_PRACTICE.name, data.priority)
        intent.putExtra(TransportParams.COUNT_PRACTICE.name, data.count)
        intent.putExtra(TransportParams.ID_PRACTICE.name, data.uniq_id)

        startActivityForResult(intent, 1)
    }

}