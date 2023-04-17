package com.example.books_films__firebase

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.books_films__firebase.databinding.FragmentRegisterBinding
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener {
            binding.nameArea.error = ""
            binding.passwordArea.error = ""
            binding.emailArea.error = ""

            val dialog = Dialog(binding.root.context)
            dialog.setContentView(R.layout.custom_login_loading_dialog)
            dialog.setCancelable(false)

            val nickname = binding.nameField.text.toString()
            val email = binding.emailField.text.toString()
            val password = binding.passwordField.text.toString()

            if(nickname.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                dialog.show()

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful) {
                        val user = Firebase.auth.currentUser

                        val profileUpdates = userProfileChangeRequest {
                            displayName = nickname
                        }

                        user!!.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "User profile updated.")
                                }
                            }

                        binding.nameField.setText("")
                        binding.emailField.setText("")
                        binding.passwordField.setText("")

                        dialog.dismiss()

                        Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment)

                        Toast.makeText(binding.root.context, "Utworzono konto!", Toast.LENGTH_SHORT).show()
                    } else {
                        try {
                            throw it.exception!!
                        } catch (e: FirebaseAuthException) {
                            if(e.errorCode == "ERROR_INVALID_EMAIL") {
                                binding.emailArea.error = "Nieprawidłowy format...!"
                            } else if(e.errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                                binding.emailArea.error = "Takie konto już istnieje...!"
                            } else if(e.errorCode == "ERROR_WEAK_PASSWORD") {
                                binding.passwordArea.error = "Hasło jest zbyt słabe...!"
                            }
                        } catch (e: FirebaseTooManyRequestsException) {
                            binding.emailArea.error = "Zbyt dużo nieudanych prób... Czasowa blokada...!"
                        }

                        dialog.dismiss()
                    }
                }
            } else {
                if(nickname.isEmpty()) binding.nameArea.error = "Brakujące pole...!"
                if(email.isEmpty()) binding.emailArea.error = "Brakujące pole...!"
                if(password.isEmpty()) binding.passwordArea.error = "Brakujące pole...!"
            }
        }

        binding.nameField.addTextChangedListener {
            binding.nameArea.error = ""
        }

        binding.passwordField.addTextChangedListener {
            binding.passwordArea.error = ""
        }

        binding.emailField.addTextChangedListener {
            binding.emailArea.error = ""
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}