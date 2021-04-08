 package com.example.thirdtask

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Base64.encodeToString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.os.persistableBundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.cards_practice.*
import kotlinx.android.synthetic.main.fragment_add_edit.*
import kotlinx.android.synthetic.main.fragment_add_edit.view.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import java.util.*

const val PRACTICE_KEY = "practice"
val PRACTICE_LEVELS = listOf("LOW", "MEDIUM", "HIGH", "ULTRA")

class AddEditFragment : DialogFragment() {
    private var practice: Practice? = null

    private var practiceLevel: String? = null
    private var type_practice_edit: String? = null

    lateinit var namePracticeView: TextInputLayout
    lateinit var descrPracticeView: TextInputLayout
    lateinit var countPracticeView: TextInputLayout
    lateinit var periodPracticeView: TextInputLayout
    lateinit var currentRadioButton: RadioButton
    lateinit var spinner: Spinner

    inner class SpinnerContext : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            practiceLevel = PRACTICE_LEVELS[position]
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            practice = it.getParcelable(PRACTICE_KEY)!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ad = AlertDialog.Builder(context)
        ad.setTitle("OK")
        ad.setPositiveButton("OK") { _, _ -> applyPractice() }

        spinner = requireView().findViewById(R.id.priority_practice)
        val spinnerAdapter = view.let {
            ArrayAdapter(
                it.context,
                android.R.layout.simple_spinner_dropdown_item,
                PRACTICE_LEVELS
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        }
        priority_practice.adapter = spinnerAdapter
        priority_practice.onItemSelectedListener = SpinnerContext()

        currentRadioButton = requireView().findViewById(radioPracticeType.checkedRadioButtonId)
        type_practice_edit = currentRadioButton.text as String?

        // EDIT TEXT
        namePracticeView = requireView().findViewById(R.id.editNamePractice)
        descrPracticeView = requireView().findViewById(R.id.editDescriptionPractice)
        countPracticeView = requireView().findViewById(R.id.count_practice)
        periodPracticeView = requireView().findViewById(R.id.period_practice)

        namePracticeView.editText?.setText(practice?.name)
        descrPracticeView.editText?.setText(practice?.description)
        if (practice != null) {
            countPracticeView.editText?.setText(practice?.count.toString())
            periodPracticeView.editText?.setText(practice?.period.toString())
        } else {
            countPracticeView.editText?.setText("0")
            periodPracticeView.editText?.setText("0")
        }

        spinner.setSelection(PRACTICE_LEVELS.indexOf(practice?.priority))

        when (practice?.typePractice) {
            "GOOD" -> requireView().findViewById<RadioButton>(R.id.good_habit).isChecked = true
            "BAD" -> requireView().findViewById<RadioButton>(R.id.bad_habit).isChecked = true
        }

        setupClickListeners(view)
    }

    private fun setupClickListeners(view: View) {
        view.btnPositive.setOnClickListener {
            applyPractice()
        }
        view.btnNegative.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_edit, container, false)

    companion object {
        @JvmStatic
        fun newInstance(practice: Practice?) =
            AddEditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PRACTICE_KEY, practice)
                }
            }
    }

    fun applyPractice() {
        val radioButton = requireView().findViewById<RadioButton>(radioPracticeType.checkedRadioButtonId)
        type_practice_edit = radioButton.text as String?
        val textName: String = namePracticeView.editText?.text.toString()
        val description: String = descrPracticeView.editText?.text.toString()
        val countPractice: Int? = countPracticeView.editText?.text.toString().toIntOrNull()
        val periodPractice: Int? = periodPracticeView.editText?.text.toString().toIntOrNull()

        if (textName == "") {
//            namePracticeView.backgroundTintList = ColorStateList.valueOf(Color.RED)
            return
        }
        if (description == "") {
//            descrPracticeView.backgroundTintList = ColorStateList.valueOf(Color.RED)
            return
        }
        if (countPractice == null) {
//            countPracticeView.backgroundTintList = ColorStateList.valueOf(Color.RED)
            return
        }
        if (periodPractice == null) {
//            periodPracticeView.backgroundTintList = ColorStateList.valueOf(Color.RED)
            return
        }
        if (type_practice_edit == null) {
            return
        }
        if (practiceLevel == null) {
//            spinner.background.setTint(Color.RED)
            return
        }
        practice = Practice(textName, description, practiceLevel!!, type_practice_edit!!, periodPractice, countPractice, practice?.uniq_id ?: UUID.randomUUID().toString())
        setFragmentResult("response", bundleOf(Pair("pair", practice)))
        dismiss()
    }
}
