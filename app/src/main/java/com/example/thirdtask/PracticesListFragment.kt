package com.example.thirdtask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thirdtask.AddEditPracticeFragment.Companion.TYPE_PRACTICE
import com.example.thirdtask.Crud.PracticeDatabase
import com.example.thirdtask.Network.*
import com.example.thirdtask.Repositories.HabitRepository
import com.example.thirdtask.ViewModels.PracticesListViewModel
import com.example.thirdtask.ViewModels.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PracticesListFragment: Fragment(), PracticeClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var practicesListViewModel: PracticesListViewModel
    private lateinit var listAdapter: PracticeAdapter
    private lateinit var typePractice: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = PracticeDatabase.getInstance(requireContext())
        val gson = GsonBuilder().registerTypeAdapter(Habit::class.java, HabitJsonDeserializer())
            .registerTypeAdapter(Habit::class.java, HabitJsonSerializer())
            .registerTypeAdapter(HabitUID::class.java, HabitUidDeserializer())
            .registerTypeAdapter(HabitUID::class.java, HabitJsonSerializer())
            .create()

        val client = OkHttpClient.Builder().addInterceptor(AuthorizationInterceptor()).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://droid-test-server.doubletapp.ru/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val service = retrofit.create(PracticeService::class.java)
        val repo = HabitRepository.getInstance(db!!.practiceDao(), service)
        practicesListViewModel = ViewModelProvider(this, ViewModelFactory(repo)).get(PracticesListViewModel::class.java)
        typePractice = arguments?.getString(keyTypePractice)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.practices_list_fragment, container, false)
        recyclerView = view.findViewById(R.id.recycle_view_list)
        listAdapter = PracticeAdapter(practiceClickListener = this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = listAdapter

        val showFilters: FloatingActionButton = view.findViewById(R.id.leftButton)
        showFilters.setOnClickListener {
            showFiltersFragment(view)
        }

        val addButton: FloatingActionButton = view.findViewById(R.id.addPracticeButton)
        addButton.setOnClickListener {
            onClick(view)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        practicesListViewModel.practiceList.observe(requireActivity(), {
            val newPracticesList = it.filter { p -> TYPE_PRACTICE.get(p.type) == typePractice }
            listAdapter.practices = newPracticesList
            listAdapter.notifyDataSetChanged()
        })
    }

    override fun onPracticeListener(data: Habit) {
        val dialog = AddEditPracticeFragment.newInstance(data)
        dialog.show(childFragmentManager, "dialog")
    }

    fun onClick(v: View?) {
        val dialog = AddEditPracticeFragment()
        dialog.show(childFragmentManager, "dialog")
    }

    fun showFiltersFragment(v: View?) {
        val dialog = BottomSheetFragment.newInstance()
        dialog.show(childFragmentManager, BottomSheetFragment.TAG)
    }

    companion object {
        const val keyTypePractice: String = "type_practice"

        @JvmStatic
        fun newInstance(typePractice: String) =
            PracticesListFragment().apply {
                arguments = Bundle().apply {
                    putString(keyTypePractice, typePractice)
                }
            }
    }
}