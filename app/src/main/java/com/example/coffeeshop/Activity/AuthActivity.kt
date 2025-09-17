package com.example.coffeeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.example.coffeeshop.R

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        val userLogin: EditText = findViewById(R.id.user_login_auth)
        val userPassword: EditText = findViewById(R.id.user_password_auth)
        val button: Button = findViewById(R.id.sign_in)
        val auth = FirebaseAuth.getInstance()

        val linkToReg: TextView = findViewById(R.id.link_to_reg)

        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val pass = userPassword.text.toString().trim()

            if (login == "" || pass == "")
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            else {
                auth.signInWithEmailAndPassword(login, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Пользователь $login авторизован", Toast.LENGTH_LONG)
                                .show()
                            userLogin.text.clear()
                            userPassword.text.clear()
                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            Toast.makeText(this, "Пользователь $login НЕ авторизован", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
            }
        }

        linkToReg.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}