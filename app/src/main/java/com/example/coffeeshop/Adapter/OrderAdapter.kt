package com.example.coffeeshop.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshop.Domain.Order
import com.example.coffeeshop.databinding.ViewholderOrderBinding
import com.google.firebase.database.FirebaseDatabase

class OrderAdapter(private val orders: MutableList<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ViewholderOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.binding.orderIdTextView.text = "Order ID: ${order.orderId}"
        holder.binding.orderDateTextView.text = "Date: ${order.date}"
        holder.binding.orderTotalTextView.text = "Total: $${order.total}"

        holder.binding.orderItemsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.binding.orderItemsRecyclerView.adapter = CartAdapter(ArrayList(order.items), holder.itemView.context, null, true)

        holder.binding.deleteOrderBtn.setOnClickListener {
            val orderId = order.orderId
            if (!orderId.isNullOrEmpty()) {
                val database = FirebaseDatabase.getInstance().reference.child("Orders").child(orderId)
                database.removeValue()
            }
        }
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(val binding: ViewholderOrderBinding) : RecyclerView.ViewHolder(binding.root)
}
