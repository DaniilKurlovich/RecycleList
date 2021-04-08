package com.example.thirdtask

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


const val BROADCAST_CHANGE_TYPE_PRACTICE = "BROADCAST_CHANGE_TYPE_PRACTICE"

class ListPracticeFragment : Fragment(), PracticeClickListener {
    private var recyclerView: RecyclerView? = null
    private lateinit var practices: ArrayList<Practice>
    private var listAdapter: PracticeAdapter? = null
    private var typePractice: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            arguments.let {
                typePractice = requireArguments().getString(keyTypePractice)
                practices = mutableListOf<Practice>() as ArrayList<Practice>
            }

        } else {
            practices = mutableListOf<Practice>() as ArrayList<Practice>
        }

        val broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent) {
                val new_practice = intent.getParcelableExtra<Practice>(BROADCAST_CHANGE_TYPE_PRACTICE)
                if (new_practice != null) {
                    practices.add(new_practice)
                }
            }
        }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadCastReceiver, IntentFilter(BROADCAST_CHANGE_TYPE_PRACTICE))

        childFragmentManager.setFragmentResultListener("response", this){ requestKey, bundle ->
            val practice = bundle.get("pair") as Practice
            if (practice.typePractice != typePractice) {
                val intent = Intent()
                intent.putExtra(BROADCAST_CHANGE_TYPE_PRACTICE, practice)
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
                return@setFragmentResultListener
            }
            var flagAdd = false
            practices.forEachIndexed{ i, p ->
                if (p.uniq_id == practice.uniq_id) {
                    practices[i] = practice
                    flagAdd = true
                }
            }

            if (!flagAdd)
                practices.add(bundle.get("pair") as Practice)
            listAdapter?.notifyDataSetChanged()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_list_practice, container, false)
        recyclerView = view.findViewById(R.id.list_fragment)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager (view.context);

        listAdapter = PracticeAdapter(practices, this)
        recyclerView?.adapter = listAdapter

        val addButton: FloatingActionButton = view.findViewById(R.id.floatingActionButton)
        addButton.setOnClickListener {
            onClick(view)
        }
        return view
    }

    companion object {
        const val keyTypePractice: String = "type_practice"
        @JvmStatic
        fun newInstance(typePractice: String) =
            ListPracticeFragment().apply {
                arguments = Bundle().apply {
                    putString(keyTypePractice, typePractice)
                }
            }
    }

    override fun onPracticeListener(data: Practice) {
        val dialog = AddEditFragment.newInstance(data)
        dialog.show(childFragmentManager, "dialog")
    }

    fun onClick(v: View?) {
        val dialog = AddEditFragment()
        dialog.show(childFragmentManager, "dialog")
    }
}
