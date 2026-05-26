package com.example.layoutdemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.layoutdemo.databinding.ListExampleBinding

class ListDemoAdapter(
    context: Context,
    private val resourceId: Int,
    objects: List<Fruit>
) : ArrayAdapter<Fruit>(context, resourceId, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ListExampleBinding.inflate(LayoutInflater.from(parent.context))
        val fruit = getItem(position)

        if (fruit != null) {
            binding.listExampleImage.setImageResource(fruit.fruitImage)
            binding.listExampleText.text = fruit.fruitName
        }

        return binding.root
    }
}
