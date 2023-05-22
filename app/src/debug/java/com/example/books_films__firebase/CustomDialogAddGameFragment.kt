package com.example.books_films__firebase

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.books_films__firebase.data_classes.Book
import com.example.books_films__firebase.data_classes.Film
import com.example.books_films__firebase.databinding.FragmentCustomDialogAddBookBinding
import com.example.books_films__firebase.databinding.FragmentCustomDialogAddFilmBinding
import com.example.books_films__firebase.databinding.FragmentCustomDialogAddGameBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CustomDialogAddFilmFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CustomDialogAddGameFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentCustomDialogAddGameBinding? = null

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCustomDialogAddGameBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        user = Firebase.auth.currentUser!!

        databaseReferenceToUsersBooks = FirebaseDatabase
            .getInstance("https://books-films-firebase-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("games")
            .child(user.uid)

        binding.addBookButton.setOnClickListener {
            if(binding.titleInput.text.toString().isNotEmpty() && binding.authorInput.text.toString().isNotEmpty()) {
                val id = user.uid

                val book = Film(binding.titleInput.text.toString(),
                    binding.authorInput.text.toString(),
                    binding.readField.isChecked)
                Toast.makeText(context, "Dodano!", Toast.LENGTH_SHORT).show()

                databaseReferenceToUsersBooks.child(book.title).setValue(book)

                dismiss()
            }
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CustomDialogAddFilmFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CustomDialogAddFilmFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}