package com.urahimli.chatappgeeks.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.urahimli.chatappgeeks.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //ActionBar gizleme
        supportActionBar?.hide()

        //Firebase initialize
        mAuth = Firebase.auth

        //1 defe giribse her defe parol istemir
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }



    fun signUpClicked (view: View) {
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
    }

    fun loginClicked (view: View) {
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        login(email, password)    //ozumuz login methodunda yaziriq Firebase'i
    }

    //Login current user via Firebase
    private fun login(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    Toast.makeText(applicationContext,"Welcome: ${mAuth.currentUser?.email.toString()}",Toast.LENGTH_LONG).show()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                }

            }
                .addOnFailureListener { exception ->
                    Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }


    }

}