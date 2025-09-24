package com.example.coffeeshop.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.Adapter.OrderAdapter
import com.example.coffeeshop.Domain.ItemsModel
import com.example.coffeeshop.Domain.Order
import com.example.coffeeshop.databinding.ActivityOrderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var orderAdapter: OrderAdapter
    private val ordersList = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("Orders")

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.clearOrdersBtn.setOnClickListener {
            clearOrderHistory()
        }

        setupRecyclerView()
        loadOrders()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(ordersList)
        binding.ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderActivity)
            adapter = orderAdapter
        }
    }

    private fun loadOrders() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val query = database.orderByChild("userId").equalTo(user.uid)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ordersList.clear()
                    for (orderSnapshot in snapshot.children) {
                        val orderId = orderSnapshot.child("orderId").getValue(String::class.java) ?: ""
                        val date = orderSnapshot.child("date").getValue(String::class.java) ?: ""
                        val total = orderSnapshot.child("total").getValue(Double::class.java) ?: 0.0
                        val userId = orderSnapshot.child("userId").getValue(String::class.java) ?: ""

                        val itemsList = mutableListOf<ItemsModel>()
                        val itemsSnapshot = orderSnapshot.child("items")
                        for (itemSnapshot in itemsSnapshot.children) {
                            val item = itemSnapshot.getValue(ItemsModel::class.java)
                            item?.let { itemsList.add(it) }
                        }

                        val order = Order(
                            orderId = orderId,
                            date = date,
                            total = total,
                            items = itemsList,
                            userId = userId
                        )
                        ordersList.add(order)
                    }
                    orderAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    private fun clearOrderHistory() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val query = database.orderByChild("userId").equalTo(user.uid)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (orderSnapshot in snapshot.children) {
                        orderSnapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }
}
