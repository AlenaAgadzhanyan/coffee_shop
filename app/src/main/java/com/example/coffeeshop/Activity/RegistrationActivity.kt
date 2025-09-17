package com.example.coffeeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.example.coffeeshop.R

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)

        val userLogin: EditText = findViewById(R.id.user_login)
        val userPassword: EditText = findViewById(R.id.user_password)
        val userRepeatPassword: EditText = findViewById(R.id.user_repeat_password)
        val button: Button = findViewById(R.id.sign_up)
        val auth = FirebaseAuth.getInstance()

        val linkToAuth: TextView = findViewById(R.id.link_to_auth)
        linkToAuth.setOnClickListener{
            startActivity(Intent(this, AuthActivity::class.java))
        }

        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val pass = userPassword.text.toString().trim()
            val repeatPass = userRepeatPassword.text.toString().trim()

            if (login == "" || pass == "" || repeatPass == "")
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            else if (pass != repeatPass)
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_LONG).show()
            else {
                auth.createUserWithEmailAndPassword(login, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Пользователь $login добавлен", Toast.LENGTH_LONG).show()
                            userLogin.text.clear()
                            userPassword.text.clear()
                            userRepeatPassword.text.clear()
                            startActivity(Intent(this, AuthActivity::class.java))
                        } else {
                            Log.e("RegistrationActivity", "Ошибка регистрации", task.exception)
                            Toast.makeText(this, "Ошибка регистрации: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}