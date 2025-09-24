package com.example.coffeeshop.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.Adapter.CartAdapter
import com.example.coffeeshop.Domain.Order
import com.example.coffeeshop.Helper.ChangeNumberItemsListener
import com.example.coffeeshop.Helper.ManagmentCart
import com.example.coffeeshop.databinding.ActivityCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var managmentCart: ManagmentCart
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var tax: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        calculateCart()
        setVariable()
        initCartList()
        setupCheckoutButton()
    }

    private fun initCartList() {
        binding.apply {
            listView.layoutManager = LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL, false)
            listView.adapter = CartAdapter(
                managmentCart.getListCart(),
                this@CartActivity,
                object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        calculateCart()
                    }
                }
            )
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
    }

    private fun calculateCart() {
        val percentTax = 0.02
        val delivery = if (managmentCart.getTotalFee() > 0) 15.0 else 0.0
        tax = Math.round((managmentCart.getTotalFee() * percentTax) * 100) / 100.0

        val total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100
        val itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100
        binding.apply {
            totalFeeTxt.text = "$$itemTotal"
            taxTxt.text = "$$tax"
            deliveryTxt.text = "$$delivery"
            totalTxt.text = "$$total"
        }
    }

    private fun setupCheckoutButton() {
        binding.checkOutBtn.setOnClickListener {
            if (managmentCart.getListCart().isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user != null) {
                val orderRef = database.getReference("Orders").push()
                val orderId = orderRef.key!!
                val total = (managmentCart.getTotalFee() + tax + if (managmentCart.getTotalFee() > 0) 15.0 else 0.0)
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val currentDate = sdf.format(Date())

                val order = Order(
                    orderId = orderId,
                    date = currentDate,
                    total = total,
                    items = managmentCart.getListCart(),
                    userId = user.uid
                )

                orderRef.setValue(order).addOnSuccessListener {
                    managmentCart.clearCart()
                    finish()
                }
            }
        }
    }
}
