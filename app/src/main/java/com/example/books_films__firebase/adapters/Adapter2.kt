package com.example.books_films__firebase.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.books_films__firebase.CustomDialogFragment
import com.example.books_films__firebase.data_classes.Book
import com.example.books_films__firebase.data_classes.Game
import com.example.books_films__firebase.databinding.BookRecordBinding
import com.example.books_films__firebase.databinding.GameRecordBinding

class Adapter2(var books: List<Game>) : RecyclerView.Adapter<Adapter2.MyViewHolder>() {

    inner class MyViewHolder(binding: GameRecordBinding) : ViewHolder(binding.root) {
        val title = binding.titleField
        val author = binding.authorField
        val read = binding.readField
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val bookRecordBinding = GameRecordBinding.inflate(inflater, parent, false)
        return MyViewHolder(bookRecordBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = books[position].title
        holder.author.text = books[position].author

        val read = books[position].read
        if(read) {
            holder.read.text = "Ograna"
            holder.read.setTextColor(Color.parseColor("#14D61C"))
        } else {
            holder.read.text = "Nie ograna"
            holder.read.setTextColor(Color.parseColor("#FF9800"))
        }

        holder.itemView.setOnClickListener {
            mListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }
}