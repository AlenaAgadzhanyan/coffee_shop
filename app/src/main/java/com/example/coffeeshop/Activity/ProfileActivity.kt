package com.example.coffeeshop.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.coffeeshop.Helper.ManagmentCart
import com.example.coffeeshop.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var isEditing = false

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { uploadAvatar(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        loadProfile()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener { finish() }

        binding.changeAvatarBtn.setOnClickListener { galleryLauncher.launch("image/*") }

        binding.editBtn.setOnClickListener {
            isEditing = !isEditing
            if (isEditing) {
                binding.editBtn.text = "Сохранить"
                enableEditing(true)
            } else {
                binding.editBtn.text = "Редактировать"
                enableEditing(false)
                saveProfile()
            }
        }

        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Вы вышли из аккаунта.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }

        binding.deleteAccountBtn.setOnClickListener { showDeleteConfirmationDialog() }

        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Уведомления включены." else "Уведомления выключены."
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Удаление аккаунта")
            .setMessage("Вы уверены, что хотите удалить свой аккаунт? Все ваши данные (профиль, корзина, избранное, история заказов) будут стерты безвозвратно.")
            .setPositiveButton("Удалить") { _, _ -> deleteUserAccount() }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun deleteUserAccount() {
        val user = auth.currentUser ?: return

        ManagmentCart(this).clearCartAndFavorites()

        val uid = user.uid
        val dbRef = database.reference
        val storageRef = storage.reference.child("avatars/$uid.jpg")

        dbRef.child("users").child(uid).removeValue()
        dbRef.child("favorites").child(uid).removeValue()
        dbRef.child("cart").child(uid).removeValue()
        dbRef.child("orders").child(uid).removeValue().addOnCompleteListener {
            storageRef.delete().addOnCompleteListener {
                user.delete().addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        Toast.makeText(this, "Аккаунт успешно удален.", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this, AuthActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Не удалось удалить аккаунт. Пожалуйста, войдите снова и попробуйте еще раз.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun enableEditing(isEnable: Boolean) {
        binding.nameEditText.isEnabled = isEnable
        binding.phoneEditText.isEnabled = isEnable
        binding.birthdayEditText.isEnabled = isEnable
    }

    private fun loadProfile() {
        val user = auth.currentUser ?: return
        binding.emailEditText.setText(user.email)
        binding.nameEditText.setText(user.displayName)
        user.photoUrl?.let {
            Glide.with(this).load(it).into(binding.avatarImageView)
        }

        database.reference.child("users").child(user.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.phoneEditText.setText(snapshot.child("phone").value as? String)
                        binding.birthdayEditText.setText(snapshot.child("birthday").value as? String)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Не удалось загрузить дополнительные данные профиля.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun saveProfile() {
        val user = auth.currentUser ?: return
        val name = binding.nameEditText.text.toString()
        val phone = binding.phoneEditText.text.toString()
        val birthday = binding.birthdayEditText.text.toString()

        // Update Firebase Auth display name
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()
        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Имя профиля обновлено.", Toast.LENGTH_SHORT).show()
            }
        }

        // Update custom fields in Realtime Database
        val userMap = mapOf("phone" to phone, "birthday" to birthday)
        database.reference.child("users").child(user.uid).updateChildren(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Профиль обновлен.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Не удалось обновить профиль.", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun uploadAvatar(uri: Uri) {
        val user = auth.currentUser ?: return
        val storageRef = storage.reference.child("avatars/${user.uid}.jpg")
        storageRef.putFile(uri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(downloadUri)
                    .build()
                user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Glide.with(this).load(downloadUri).into(binding.avatarImageView)
                        Toast.makeText(this, "Аватар обновлен.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Не удалось обновить аватар.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Не удалось загрузить аватар.", Toast.LENGTH_SHORT).show()
        }
    }
}