package com.example.thirdtask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thirdtask.Crud.PracticeDatabase
import com.example.thirdtask.Models.Practice
import com.example.thirdtask.ViewModels.PracticesListViewModel
import com.example.thirdtask.ViewModels.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.collections.ArrayList

class PracticesListFragment: Fragment(), PracticeClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var practicesListViewModel: PracticesListViewModel
    private lateinit var listAdapter: PracticeAdapter
    private lateinit var typePractice: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = PracticeDatabase.getInstance(requireContext())
        practicesListViewModel = ViewModelProvider(this, ViewModelFactory(db!!.practiceDao())).get(PracticesListViewModel::class.java)
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
            val newPracticesList = it.filter { p -> p.typePractice == typePractice } as ArrayList<Practice>
            listAdapter.practices = newPracticesList
            listAdapter.notifyDataSetChanged()
        })
    }

    override fun onPracticeListener(data: Practice) {
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