package com.example.coffeeshop.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.Adapter.CategoryAdapter
import com.example.coffeeshop.Adapter.PopularAdapter
import com.example.coffeeshop.Adapter.SpecialAdapter
import com.example.coffeeshop.ViewModel.MainViewModel
import com.example.coffeeshop.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val viewModel = MainViewModel()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        initCategory()
        initPopular()
        initSpecial()
        initBottomMenu()
    }

    private fun initBottomMenu() {
        binding.cartBtn.setOnClickListener { startActivity(Intent(this, CartActivity::class.java)) }
        binding.favoriteBtn.setOnClickListener { startActivity(Intent(this, FavoriteActivity::class.java)) }
        binding.profileBtn.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
    }

    private fun initSpecial() {
        binding.progressBarSpecial.visibility = View.VISIBLE
        viewModel.loadSpecial().observe(this) {
            binding.recyclerViewSpecial.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
            binding.progressBarSpecial.visibility = View.GONE
            binding.recyclerViewSpecial.adapter = SpecialAdapter(it)
        }
        viewModel.loadSpecial()
    }

    private fun initPopular() {
        binding.progressBarPopular.visibility = View.VISIBLE
        viewModel.loadPopular().observe(this) {
            binding.recyclerViewPopular.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false)
            binding.progressBarPopular.visibility = View.GONE
            binding.recyclerViewPopular.adapter = PopularAdapter(it)
        }
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE
        viewModel.loadCategory().observe(this) {
            binding.recyclerViewCategory.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.progressBarCategory.visibility = View.GONE
            binding.recyclerViewCategory.adapter = CategoryAdapter(it)
        }
        viewModel.loadCategory()
    }
}
