package com.androiddevs.assignment.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.assignment.R
import com.androiddevs.assignment.Item

class ItemAdapter(private val items: List<Item>, private val onItemClicked: (Item) -> Unit) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.itemTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.itemDescription)
        private val imageView: ImageView = itemView.findViewById(R.id.itemImage)

        fun bind(item: Item) {
            titleTextView.text = item.title
            descriptionTextView.text = item.description
            imageView.setImageURI(item.imageUri ?: Uri.parse("android.resource://com.androiddevs.assignment/drawable/android"))

            itemView.setOnClickListener {
                onItemClicked(item)
            }
        }
    }
}
