package com.example.books_films__firebase.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.books_films__firebase.CustomDialogFragment
import com.example.books_films__firebase.data_classes.Book
import com.example.books_films__firebase.databinding.BookRecordBinding

class Adapter(var books: List<Book>) : RecyclerView.Adapter<Adapter.MyViewHolder>() {

    inner class MyViewHolder(binding: BookRecordBinding) : ViewHolder(binding.root) {
        val title = binding.titleField
        val author = binding.authorField
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val bookRecordBinding = BookRecordBinding.inflate(inflater, parent, false)
        return MyViewHolder(bookRecordBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = books[position].title
        holder.author.text = books[position].author

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