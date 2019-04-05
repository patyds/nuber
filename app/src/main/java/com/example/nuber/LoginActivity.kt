package com.example.nuber

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            doLogin()
        }

        back_to_register.setOnClickListener {
            finish()
        }

    }

    private  fun doLogin(){
        val email = email_edittext.text.toString()
        val password = password_edittext.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(this.currentFocus, "Please fill out email/pw.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            //Toast.makeText(this, "Please fill out email/pw.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener
                Toast.makeText(applicationContext,
                    " id: ${it.result!!.user.uid}", Toast.LENGTH_LONG).show()

                val intent = Intent(this, Main2Activity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext,
                    " id: ${it.message}", Toast.LENGTH_LONG).show()
            }



    }
}








