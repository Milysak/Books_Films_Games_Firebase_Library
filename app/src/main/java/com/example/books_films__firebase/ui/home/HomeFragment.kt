package com.example.books_films__firebase.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.books_films__firebase.CustomDialogAddBookFragment
import com.example.books_films__firebase.CustomDialogFragment
import com.example.books_films__firebase.R
import com.example.books_films__firebase.adapters.Adapter
import com.example.books_films__firebase.data_classes.Book
import com.example.books_films__firebase.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var databaseReferenceToUsersBooks: DatabaseReference

    private lateinit var adapter: Adapter
    private lateinit var lists: kotlin.collections.ArrayList<Book>

    private lateinit var user: FirebaseUser

    private lateinit var eventListener: ValueEventListener

    private var filterChecked: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = Firebase.auth.currentUser!!

        database = FirebaseDatabase.getInstance("https://books-films-firebase-default-rtdb.europe-west1.firebasedatabase.app").getReference("books")
        databaseReferenceToUsersBooks = FirebaseDatabase
            .getInstance("https://books-films-firebase-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("books")
            .child(user.uid)

        lists = ArrayList<Book>()

        val loadingDialog = Dialog(view.context)
        loadingDialog.setContentView(R.layout.custom_login_loading_dialog)
        loadingDialog.setCancelable(false)

        adapter = Adapter(lists)
        binding.listOfBooks.layoutManager = LinearLayoutManager(binding.root.context)
        binding.listOfBooks.adapter = adapter

        eventListener = databaseReferenceToUsersBooks.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                //loadingDialog.show()

                lists.clear()
                for (itemSnapshot in snapshot.children) {
                    val title = itemSnapshot.child("title").value.toString()
                    val author = itemSnapshot.child("author").value.toString()
                    val read = itemSnapshot.child("read").value as Boolean
                    val dataClass = Book(title, author, read)

                    lists.add(dataClass)

                    //Log.v(TAG, itemSnapshot.value.toString())
                }
                adapter.notifyDataSetChanged()

                //loadingDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filteredList()
                }

                return true
            }
        })

        adapter.setOnItemClickListener(object : Adapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val dialog = CustomDialogFragment(adapter.books[position].title, adapter.books[position].author, adapter.books[position].read)

                dialog.isCancelable = false

                //val id = user.uid

                //val bookID = database.push().key!!
                /*val book = Book(lists[position].title, lists[position].author, true)
                Toast.makeText(context, id, Toast.LENGTH_SHORT).show()
                database.child(id).child(book.title).setValue(book)*/

                //dialog.setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog_Custom)

                dialog.show(parentFragmentManager, "dialog")

                dialog.onDismiss() {
                    filteredList()
                }
            }
        })

        binding.addRecord.setOnClickListener {
            /*val user = Firebase.auth.currentUser

            if (user != null) {
                val id = user.uid

                //val bookID = database.push().key!!
                val book = Book("Ferdydurke", "Witold Gombrowicz", true)
                Toast.makeText(context, id, Toast.LENGTH_SHORT).show()
                database.child(id).child(book.title).setValue(book)
            }*/

            val dialog = CustomDialogAddBookFragment()

            dialog.isCancelable = false

            dialog.show(parentFragmentManager, "dialog")
        }

        binding.settingsButton.setOnClickListener {
            val singleItems = arrayOf("Wszystkie", "Przeczytane", "Nieprzeczytane")
            val checkedItem = filterChecked

            var item = filterChecked

            MaterialAlertDialogBuilder(binding.root.context)
                .setTitle("Wybierz filtr...")
                .setNeutralButton("WRÓĆ") { dialog, which ->
                    // Respond to neutral button press
                }
                .setPositiveButton("POTWIERDŹ") { dialog, which ->
                    // Respond to positive button press
                    filterChecked = item

                    filteredList()
                }
                // Single-choice items (initialized with checked item)
                .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                    // Respond to item chosen
                    item = which
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()

        adapter.books = lists

        val query = binding.searchView.query

        if (query.toString().isNotEmpty()) {
            filteredList()
        }

        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filteredList() {
        val newText = binding.searchView.query.toString()

        val filteredList = kotlin.collections.ArrayList<Book>()

        for (book in lists) {
            if (book.title.lowercase().contains(newText.lowercase())) {
                if (filterChecked == 0) {
                    filteredList.add(book)
                } else if (filterChecked == 1) {
                    if (book.read) {
                        filteredList.add(book)
                    }
                } else if (filterChecked == 2) {
                    if (!book.read) {
                        filteredList.add(book)
                    }
                }
            } else if(book.author.lowercase().contains(newText.lowercase())) {
                if (filterChecked == 0) {
                    filteredList.add(book)
                } else if (filterChecked == 1) {
                    if (book.read) {
                        filteredList.add(book)
                    }
                } else if (filterChecked == 2) {
                    if (!book.read) {
                        filteredList.add(book)
                    }
                }
            }
        }

        adapter.books = filteredList

        adapter.notifyDataSetChanged()
    }
}