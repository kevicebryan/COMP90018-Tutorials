package com.example.layoutdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.layoutdemo.databinding.RecyclerExampleBinding

//RecyclerView Adapter for Fruits
class RecyclerDemoAdapter(
    // an array of fruits need to display at recyclerView
    private val fruits: List<Fruit>,
    // the resource id of item layout
    private val resourceId: Int
) : RecyclerView.Adapter<RecyclerDemoAdapter.ViewHolder>() {

    private lateinit var binding: RecyclerExampleBinding

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    //  Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RecyclerExampleBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding.root)
    }

    // to bind the resources to viewHolder, including fruit image resource id and fruit name
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binding.recyclerExampleImage.setImageResource(fruits[position].fruitImage)
        binding.recyclerExampleText.text = fruits[position].fruitName

        // To show how to add click listener to a item in recyclerView
        // Set onClickListener for the fruit array;
        // When clicking a item from the list, a new Toast shows the name of the clicked fruit;
        binding.recyclerExampleLayout.setOnClickListener { view ->
            Toast.makeText(view.context, fruits[holder.adapterPosition].fruitName, Toast.LENGTH_SHORT).show()
        }
    }

    // to get the size of the fruits array
    override fun getItemCount(): Int = fruits.size
}
