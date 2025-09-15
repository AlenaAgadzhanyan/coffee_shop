package com.example.coffeeshop

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import android.widget.TextView
class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)

        val userLogin: EditText = findViewById(R.id.user_login)
        val userPassword: EditText = findViewById(R.id.user_password)
        val userRepeatPassword: EditText = findViewById(R.id.user_repeat_password)
        val button: Button = findViewById(R.id.sign_up)

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
                val user = User(login, pass)

                val db = DbHelper(this, null)
                db.addUser(user)

                Toast.makeText(this, "Пользователь $login добавлен", Toast.LENGTH_LONG).show()

                userLogin.text.clear()
                userPassword.text.clear()
                userRepeatPassword.text.clear()
            }
        }
    }
}