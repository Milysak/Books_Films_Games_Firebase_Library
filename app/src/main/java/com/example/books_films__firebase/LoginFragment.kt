package com.example.books_films__firebase

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.books_films__firebase.databinding.FragmentLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentLoginBinding? = null
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            binding.passwordArea.error = ""
            binding.emailArea.error = ""

            val dialog = Dialog(binding.root.context)
            dialog.setContentView(R.layout.custom_login_loading_dialog)
            dialog.setCancelable(false)

            val email = binding.emailField.text.toString()
            val password = binding.passwordField.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()) {
                dialog.show()

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful) {
                        binding.emailField.setText("")
                        binding.passwordField.setText("")

                        Toast.makeText(binding.root.context, "Zalogowano!", Toast.LENGTH_SHORT).show()

                        dialog.dismiss()

                        val intent = Intent(view.context, MainMenuActivity::class.java)
                        startActivity(intent)
                    } else {
                        try {
                            throw it.exception!!
                        } catch (e: FirebaseAuthException) {
                            if(e.errorCode == "ERROR_INVALID_EMAIL") {
                                binding.emailArea.error = "Nieprawidłowy format...!"
                            } else if(e.errorCode == "ERROR_USER_NOT_FOUND") {
                                binding.emailArea.error = "Nie ma takiego użytkownika...!"
                            } else if(e.errorCode == "ERROR_USER_DISABLED") {
                                binding.emailArea.error = "To konto jest wyłączone...!"
                            } else if(e.errorCode == "ERROR_WRONG_PASSWORD") {
                                binding.passwordArea.error = "Nieprawidłowe hasło...!"
                            }
                        } catch (e: FirebaseTooManyRequestsException) {
                            binding.emailArea.error = "Zbyt dużo nieudanych prób... Czasowa blokada...!"
                        } catch (e: FirebaseNetworkException) {
                            Toast.makeText(binding.root.context, "Brak połączenia...!", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(binding.root.context, "Błąd logowanie...!", Toast.LENGTH_SHORT).show()
                        }

                        dialog.dismiss()
                    }
                }
            } else {
                if(email.isEmpty()) binding.emailArea.error = "Brakujące pole...!"
                if(password.isEmpty()) binding.passwordArea.error = "Brakujące pole...!"
            }
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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}