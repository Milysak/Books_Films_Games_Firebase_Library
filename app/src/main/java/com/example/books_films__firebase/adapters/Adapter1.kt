package com.example.books_films__firebase.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.books_films__firebase.CustomDialogFragment
import com.example.books_films__firebase.data_classes.Book
import com.example.books_films__firebase.data_classes.Film
import com.example.books_films__firebase.databinding.BookRecordBinding
import com.example.books_films__firebase.databinding.FilmRecordBinding

class Adapter1(var books: List<Film>) : RecyclerView.Adapter<Adapter1.MyViewHolder>() {

    inner class MyViewHolder(binding: FilmRecordBinding) : ViewHolder(binding.root) {
        val title = binding.titleField
        val author = binding.authorField
        val read = binding.readField
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val bookRecordBinding = FilmRecordBinding.inflate(inflater, parent, false)
        return MyViewHolder(bookRecordBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = books[position].title
        holder.author.text = books[position].author

        val read = books[position].read
        if(read) {
            holder.read.text = "Widzany"
            holder.read.setTextColor(Color.parseColor("#14D61C"))
        } else {
            holder.read.text = "Nie widzany"
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