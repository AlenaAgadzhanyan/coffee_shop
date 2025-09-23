package com.example.coffeeshop.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.Adapter.FavoriteAdapter
import com.example.coffeeshop.Helper.ManagmentCart
import com.example.coffeeshop.databinding.ActivityFavoriteBinding

class FavoriteActivity : AppCompatActivity() {
    lateinit var binding: ActivityFavoriteBinding
    lateinit var managmentCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

        setVariable()
        initFavoriteList()
    }

    private fun initFavoriteList() {
        binding.apply {
            listView.layoutManager = LinearLayoutManager(this@FavoriteActivity, LinearLayoutManager.VERTICAL, false)
            listView.adapter = FavoriteAdapter(managmentCart.getListFavorite())
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
    }
}
