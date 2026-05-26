package com.example.layoutdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.layoutdemo.databinding.FragmentLayoutDemoListBinding
import com.example.layoutdemo.databinding.FragmentLayoutDemoRecyclerBinding

class LayoutDemoFragment : Fragment() {

    private var layout = R.layout.fragment_layout_demo_linear

    private var listBinding: FragmentLayoutDemoListBinding? = null
    private var recyclerBinding: FragmentLayoutDemoRecyclerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let { layout = it.getInt(LAYOUT_TYPE) }

        return when (layout) {
            R.layout.fragment_layout_demo_list -> {
                val binding = FragmentLayoutDemoListBinding.inflate(inflater, container, false)
                listBinding = binding

                val adapter = ListDemoAdapter(requireActivity(), R.layout.list_example, getFruits())
                binding.demoListView.adapter = adapter

                // To set onItemClickListener - method 1
                binding.demoListView.setOnItemClickListener { adapterView, _, i, _ ->
                    val fruit = adapterView.getItemAtPosition(i) as Fruit
                    Toast.makeText(context, fruit.fruitName, Toast.LENGTH_SHORT).show()
                }

                binding.root
            }
            R.layout.fragment_layout_demo_recycler -> {
                val binding = FragmentLayoutDemoRecyclerBinding.inflate(inflater, container, false)
                recyclerBinding = binding

                val adapter = RecyclerDemoAdapter(getFruits(), R.layout.recycler_example)
                // To lay out children in a staggered grid formation
                binding.demoRecycler.layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                binding.demoRecycler.adapter = adapter

                binding.root
            }
            else -> inflater.inflate(layout, container, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listBinding = null
        recyclerBinding = null
    }

    // To generate an array of fruit example for ListView and RecyclerView Demonstration
    private fun getFruits(): ArrayList<Fruit> {
        val fruits = ArrayList<Fruit>()
        fruits.add(Fruit(R.drawable.apple, "Apple"))
        fruits.add(Fruit(R.drawable.bananas, "Bananas"))
        fruits.add(Fruit(R.drawable.cherry, "Cherry"))
        fruits.add(Fruit(R.drawable.grapes, "Grapes"))
        fruits.add(Fruit(R.drawable.lemon, "Lemon"))
        fruits.add(Fruit(R.drawable.orange, "Orange"))
        fruits.add(Fruit(R.drawable.melon, "Melon"))
        fruits.add(Fruit(R.drawable.peach, "Peach"))
        fruits.add(Fruit(R.drawable.pear, "Pear"))
        fruits.add(Fruit(R.drawable.pomegranate, "Pomegranate"))
        fruits.add(Fruit(R.drawable.strawberry, "Strawberry"))
        fruits.add(Fruit(R.drawable.watermelon, "Watermelon"))
        return fruits
    }

    companion object {
        val LINEAR_DEMO = R.layout.fragment_layout_demo_linear
        val RELATIVE_DEMO = R.layout.fragment_layout_demo_relative
        val LIST_DEMO = R.layout.fragment_layout_demo_list
        val RECYCLER_DEMO = R.layout.fragment_layout_demo_recycler
        const val LAYOUT_TYPE = "type"

        // Recommended method to generate new LayoutDemoFragment
        // Instead of calling LayoutDemoFragment() directly
        fun newInstance(layout: Int): Fragment {
            val fragment: Fragment = LayoutDemoFragment()
            val bundle = Bundle()
            bundle.putInt(LAYOUT_TYPE, layout)
            fragment.arguments = bundle
            return fragment
        }
    }
}
