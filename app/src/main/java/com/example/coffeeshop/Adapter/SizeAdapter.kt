package com.example.coffeeshop.Adapter

import com.example.coffeeshop.R
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshop.databinding.ViewholderSizeBinding

class SizeAdapter(val items: MutableList<String>): RecyclerView.Adapter<SizeAdapter.Viewholder>() {
    inner class Viewholder(val binding: ViewholderSizeBinding):
            RecyclerView.ViewHolder(binding.root)

    private var selectedPosition = -1
    private var lastSelectionPosition = -1
    private lateinit var context: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Viewholder {
        context = parent.context
        val binding = ViewholderSizeBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(
        holder: Viewholder,
        position: Int
    ) {
        val imageSize = when(position){
            0->45.dpToPx(context)
            1->50.dpToPx(context)
            2->55.dpToPx(context)
            3->60.dpToPx(context)
            else->65.dpToPx(context)
        }

        val layoutParams = holder.binding.img.layoutParams
        layoutParams.width = imageSize
        layoutParams.height = imageSize

        holder.binding.img.layoutParams = layoutParams

        holder.binding.root.setOnClickListener {
            lastSelectionPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(lastSelectionPosition)
            notifyItemChanged(selectedPosition)
        }
        if (selectedPosition == position){
            holder.binding.img.setBackgroundResource(R.drawable.orange_bg)
        }else {
            holder.binding.img.setBackgroundResource(R.drawable.stroke_bg)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun Int.dpToPx(context: Context):Int{
        return (this * context.resources.displayMetrics.density).toInt()
    }


}