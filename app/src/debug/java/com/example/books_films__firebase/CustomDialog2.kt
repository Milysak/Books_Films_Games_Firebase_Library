package com.example.books_films__firebase

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.books_films__firebase.adapters.Adapter
import com.example.books_films__firebase.data_classes.Book
import com.example.books_films__firebase.databinding.FragmentCustomDialog1Binding
import com.example.books_films__firebase.databinding.FragmentCustomDialog2Binding
import com.example.books_films__firebase.databinding.FragmentCustomDialogBinding
import com.example.books_films__firebase.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CustomDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CustomDialog2(private val title: String, private val author: String, private val read: Boolean) : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentCustomDialog2Binding? = null

    private val binding get() = _binding!!

    private lateinit var databaseReferenceToUsersBooks: DatabaseReference

    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //val view = inflater.inflate(R.layout.fragment_custom_dialog, container, false)

        _binding = FragmentCustomDialog2Binding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        binding.titleField.text = title
        binding.authorField.text = author

        if(read) {
            binding.readField.text = "Widzany"
            binding.readField.setTextColor(Color.parseColor("#14D61C"))
        } else {
            binding.readField.text = "Nie widzany"
            binding.readField.setTextColor(Color.parseColor("#FF9800"))
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        user = Firebase.auth.currentUser!!

        databaseReferenceToUsersBooks = FirebaseDatabase
            .getInstance("https://books-films-firebase-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("films")
            .child(user.uid)

        binding.deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(binding.root.context)
                .setTitle("Czy na pewno usunąć...?")
                .setNeutralButton("WRÓĆ") { dialog, which ->
                    // Respond to neutral button press
                }
                .setPositiveButton("POTWIERDŹ") { dialog, which ->
                    // Respond to positive button press

                    databaseReferenceToUsersBooks.child(title).removeValue()
                    dismiss()
                }
                .show()
        }

        binding.saveButton.setOnClickListener {
            MaterialAlertDialogBuilder(binding.root.context)
                .setTitle("Czy na pewno edytować...?")
                .setNeutralButton("WRÓĆ") { dialog, which ->
                    // Respond to neutral button press
                }
                .setPositiveButton("POTWIERDŹ") { dialog, which ->
                    // Respond to positive button press
                    if (binding.authorInput.text.toString().isNotEmpty()) {
                        databaseReferenceToUsersBooks.child(title).child(author).setValue(binding.authorInput.text)
                    }

                    if (binding.readChecked.isChecked) {
                        databaseReferenceToUsersBooks.child(title).child("read").setValue(true)
                    } else {
                        databaseReferenceToUsersBooks.child(title).child("read").setValue(false)
                    }

                    dismiss()
                }
                .show()
        }

        return binding.root
    }

    fun onDismiss(function: () -> Unit) {

    }
}