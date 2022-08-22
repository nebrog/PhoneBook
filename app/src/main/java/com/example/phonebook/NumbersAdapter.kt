package com.example.phonebook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NumbersAdapter(private val inflater: LayoutInflater) :
    RecyclerView.Adapter<NumbersAdapter.ViewHolder>() {

    private val contacts: ArrayList<NumbersModel> = ArrayList()

    fun setChanges(contact: List<NumbersModel>) {
        contacts.clear()
        contacts.addAll(contact)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts.get(position)
        holder.name.text = contact.name
        holder.number.text = contact.numbers
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val photo: ImageView
        val name: TextView
        val number: TextView

        init {
            photo = view.findViewById(R.id.photo)
            name = view.findViewById(R.id.name)
            number = view.findViewById(R.id.number)
        }
    }
}