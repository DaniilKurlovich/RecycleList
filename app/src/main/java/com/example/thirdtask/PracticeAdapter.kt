package com.example.thirdtask

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text


class PracticeAdapter(private val practices: ArrayList<Practice>,
                      private val practiceClickListener: PracticeClickListener
): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.cards_practice, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            practiceClickListener.onPracticeListener(practices[position])
        }
        holder.bind(practices[position])
    }

    override fun getItemCount(): Int {
        return practices.size
    }

}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val namePractice: TextView = itemView.findViewById(R.id.name)
    private val descriptionPractice: TextView = itemView.findViewById(R.id.description)
    private val count_repeat: TextView = itemView.findViewById(R.id.count_repeat)
    private val type: TextView = itemView.findViewById(R.id.type_practice)
    private val level: TextView = itemView.findViewById(R.id.level_practice)

    fun bind(user: Practice) {
        namePractice.text = user.name
        descriptionPractice.text = user.description
        count_repeat.text = itemView.context.getString(R.string.placeholder_cr, user.count, user.period)
        type.text = itemView.context.getString(R.string.type_text, user.typePractice)
        level.text = itemView.context.getString(R.string.level_text, user.priority)
    }

}