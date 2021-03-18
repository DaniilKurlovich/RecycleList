package com.example.thirdtask

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.add_edit_activity.*
import java.lang.Exception

class AddEditActivity : AppCompatActivity() {
    val levels_practice = listOf("CHOOSE TYPE", "LOW", "MEDIUM", "HIGH", "ULTRA")
    var practice_level: String? = null
    var type_practice: String? = TypePractice.GOOD.kind

    lateinit var namePracticeView: TextInputLayout
    lateinit var descrPracticeView: TextInputLayout
    lateinit var countPracticeView: TextInputLayout
    lateinit var periodPracticeView: TextInputLayout
    lateinit var currentRadioButton: RadioButton
    lateinit var spinner: Spinner


    inner class SpinnerContext : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position > 0) {
                practice_level = levels_practice[position]
                spinner.setBackgroundColor(Color.WHITE)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_edit_activity)

        // SPINNER
        spinner = findViewById<Spinner>(R.id.priority_practice)
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            levels_practice
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        priority_practice.adapter = spinnerAdapter
        priority_practice.onItemSelectedListener = SpinnerContext()

        currentRadioButton = findViewById(radioPracticeType.checkedRadioButtonId)
        type_practice = currentRadioButton.text as String?

        // EDIT TEXT
        namePracticeView = findViewById(R.id.editNamePractice)
        descrPracticeView = findViewById(R.id.editDescriptionPractice)
        countPracticeView = findViewById(R.id.count_practice)
        periodPracticeView = findViewById(R.id.period_practice)
    }

    fun switchRadioButton(typePractice: String) {
        when (typePractice) {
            TypePractice.GOOD.kind -> findViewById<RadioButton>(R.id.good_habit).isChecked = true
            TypePractice.BAD.kind -> findViewById<RadioButton>(R.id.bad_habit).isChecked = true
        }
    }

    override fun onStart() {
        super.onStart()
        if (intent.hasExtra("edit_mode")) {

            val typePractice: String = intent.getStringExtra(TransportParams.TYPE_PRACTICE.name) ?: throw Exception("Missing type")
            switchRadioButton(typePractice)

            val levelPractice: String = intent.getStringExtra(TransportParams.LEVEL_PRACTICE.name) ?: throw Exception("Missing level")
            spinner.setSelection(levels_practice.indexOf(levelPractice))

            namePracticeView.editText?.setText(
                    intent.getStringExtra(TransportParams.NAME_PRACTICE.name)
                        ?: throw Exception("Missing name")
            )

            descrPracticeView.editText?.setText(
                intent.getStringExtra(TransportParams.DESCRIPTION_PRACTICE.name)
                    ?: throw Exception("Missing description")
            )

            countPracticeView.editText?.setText(
                intent.getIntExtra(TransportParams.COUNT_PRACTICE.name, 0).toString()
            )

            periodPracticeView.editText?.setText(
                intent.getIntExtra(TransportParams.PERIOD_PRACTICE.name, 0).toString()
            )
        }
    }

    fun setTypePractice(view: View) {
        val radio: RadioButton = findViewById(radioPracticeType.checkedRadioButtonId)
        type_practice = radio.text as String
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun applyPractice(view: View) {
        val textName: String = namePracticeView.editText?.text.toString()
        val description: String = descrPracticeView.editText?.text.toString()
        val countPractice: Int? = countPracticeView.editText?.text.toString().toIntOrNull()
        val periodPractice: Int? = periodPracticeView.editText?.text.toString().toIntOrNull()

        if (textName == "") {
            namePracticeView.backgroundTintList = ColorStateList.valueOf(Color.RED)
            return
        }
        if (description == "") {
            descrPracticeView.backgroundTintList = ColorStateList.valueOf(Color.RED)
            return
        }
        if (countPractice == null) {
            countPracticeView.backgroundTintList = ColorStateList.valueOf(Color.RED)
            return
        }
        if (periodPractice == null) {
            periodPracticeView.backgroundTintList = ColorStateList.valueOf(Color.RED)
            return
        }

        if (practice_level == null) {
            spinner.background.setTint(Color.RED)
            return
        }

        val squareIntent = Intent()
        squareIntent.putExtra(TransportParams.NAME_PRACTICE.name, textName)
        squareIntent.putExtra(TransportParams.DESCRIPTION_PRACTICE.name, description)
        squareIntent.putExtra(TransportParams.COUNT_PRACTICE.name, countPractice)
        squareIntent.putExtra(TransportParams.PERIOD_PRACTICE.name, periodPractice)
        squareIntent.putExtra(TransportParams.LEVEL_PRACTICE.name, practice_level)
        squareIntent.putExtra(TransportParams.TYPE_PRACTICE.name, type_practice)
        squareIntent.putExtra(TransportParams.ID_PRACTICE.name, intent.getStringExtra(TransportParams.ID_PRACTICE.name))

        if (intent.hasExtra("edit_mode")) {
            squareIntent.putExtra("edit_mode", true)
        }
        setResult(RESULT_OK, squareIntent)
        finish()
    }
}
