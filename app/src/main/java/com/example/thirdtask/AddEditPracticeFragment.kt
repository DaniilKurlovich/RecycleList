package com.example.thirdtask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thirdtask.Crud.PracticeDatabase
import com.example.thirdtask.Network.*
import com.example.thirdtask.Repositories.HabitRepository
import com.example.thirdtask.ViewModels.PreparePracticeViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_add_edit.*
import kotlinx.android.synthetic.main.fragment_add_edit.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddEditPracticeFragment : DialogFragment() {
    companion object {
        const val PRACTICE_KEY_PUBSUB = "practice"
        val PRACTICE_LEVELS = listOf("LOW", "MEDIUM", "HIGH", "ULTRA")
        val TYPE_PRACTICE = listOf("Good", "Bad")

        @JvmStatic
        fun newInstance(practice: Habit?) =
            AddEditPracticeFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PRACTICE_KEY_PUBSUB, practice)
                }
            }
    }

    // ViewModels
    private lateinit var preparePracticeVM: PreparePracticeViewModel

    // UI
    private lateinit var namePracticeView: TextInputLayout
    private lateinit var descrPracticeView: TextInputLayout
    private lateinit var countPracticeView: TextInputLayout
    private lateinit var periodPracticeView: TextInputLayout
    private lateinit var currentRadioButton: RadioButton
    private lateinit var spinner: Spinner

    inner class SpinnerContext : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            preparePracticeVM.setLevel(PRACTICE_LEVELS[position])
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_edit, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = PracticeDatabase.getInstance(requireContext())
        val gson = GsonBuilder().registerTypeAdapter(Habit::class.java, HabitJsonDeserializer())
            .registerTypeAdapter(Habit::class.java, HabitJsonSerializer())
            .registerTypeAdapter(HabitUID::class.java, HabitUidDeserializer())
            .registerTypeAdapter(HabitUID::class.java, HabitJsonSerializer())
            .create()

        val logginh = HttpLoggingInterceptor()
        logginh.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(AuthorizationInterceptor())
            .addInterceptor(logginh).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://droid-test-server.doubletapp.ru/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val service = retrofit.create(PracticeService::class.java)

        val repo = HabitRepository.getInstance(db!!.practiceDao(), service)
        preparePracticeVM = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return PreparePracticeViewModel(repo) as T
            }
        }).get(PreparePracticeViewModel::class.java)

        preparePracticeVM.practiceReady.observe(requireActivity(), {
            dismiss()
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        namePracticeView = requireView().findViewById(R.id.editNamePractice)
        descrPracticeView = requireView().findViewById(R.id.editDescriptionPractice)
        countPracticeView = requireView().findViewById(R.id.count_practice)
        periodPracticeView = requireView().findViewById(R.id.period_practice)
        currentRadioButton = requireView().findViewById(radioPracticeType.checkedRadioButtonId)

        arguments?.let {
            val practice: Habit = it.getParcelable(PRACTICE_KEY_PUBSUB)!!
            namePracticeView.editText?.setText(practice.title)
            descrPracticeView.editText?.setText(practice.description)
            countPracticeView.editText?.setText(practice.count.toString())
            periodPracticeView.editText?.setText(practice.frequency.toString())

            when (practice.type) {
                0 -> requireView().findViewById<RadioButton>(R.id.good_habit).isChecked = true
                1 -> requireView().findViewById<RadioButton>(R.id.bad_habit).isChecked = true
            }
            practice.uid?.let { it1 -> preparePracticeVM.setUniqId(it1) }
        } ?: run {
            countPracticeView.editText?.setText("0")
            periodPracticeView.editText?.setText("0")
        }

        // SPINNER
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

        // CONFIRM/CANCEL BUTTONS
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

    fun applyPractice() {
        preparePracticeVM.setName(namePracticeView.editText?.text.toString())
        preparePracticeVM.setDescription(descrPracticeView.editText?.text.toString())
        preparePracticeVM.setCount(countPracticeView.editText?.text.toString().toIntOrNull())
        preparePracticeVM.setPeriod(periodPracticeView.editText?.text.toString().toIntOrNull())
        preparePracticeVM.setType(requireView().findViewById<RadioButton>(radioPracticeType.checkedRadioButtonId).text as String)
        GlobalScope.launch {
            preparePracticeVM.applyPractice()
        }
    }
}