package com.androiddevs.assignment.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.assignment.R
import com.androiddevs.assignment.ui.adapter.ItemAdapter
import com.androiddevs.assignment.ui.viewmodel.ItemViewModel

class ItemListFragment : Fragment() {

    private val viewModel: ItemViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isTv = resources.getBoolean(R.bool.is_tv)
        val spanCount = if (isTv) 4 else 1
        Log.d("ItemListFragment", "Span count: $spanCount")

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, spanCount, RecyclerView.VERTICAL, false)

        viewModel.items.observe(viewLifecycleOwner, Observer { items ->
            recyclerView.adapter = ItemAdapter(items) { item ->
                val action = ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(item)
                findNavController().navigate(action)
            }
        })
    }
}
