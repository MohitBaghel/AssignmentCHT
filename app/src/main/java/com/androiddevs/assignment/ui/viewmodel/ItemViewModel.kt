package com.androiddevs.assignment.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevs.assignment.Item

class ItemViewModel : ViewModel() {
    private val _items = MutableLiveData<List<Item>>().apply {
        value = listOf(
            Item("Android Development", "Learn about Android development.", Uri.parse("android.resource://com.androiddevs.assignment/drawable/android")),
            Item("Kotlin Programming", "Learn about Kotlin programming.", Uri.parse("android.resource://com.androiddevs.assignment/drawable/kotlin")),
            Item("Html/Css Development", "Learn about Html/Css development.", Uri.parse("android.resource://com.androiddevs.assignment/drawable/html")),
            Item("Javascript Programming", "Learn about Javascript programming.", Uri.parse("android.resource://com.androiddevs.assignment/drawable/js")),
            Item("Web Development", "Learn about Web development.", Uri.parse("android.resource://com.androiddevs.assignment/drawable/html")),
            Item("Java Programming", "Learn about Java programming.", Uri.parse("android.resource://com.androiddevs.assignment/drawable/java"))
        )
    }
    val items: LiveData<List<Item>> = _items

    private val _selectedItem = MutableLiveData<Item>()
    val selectedItem: LiveData<Item> = _selectedItem

    fun selectItem(item: Item) {
        _selectedItem.value = item
    }

    fun updateSelectedItem(title: String, description: String, imageUri: Uri?) {
        _selectedItem.value?.let {
            it.title = title
            it.description = description
            it.imageUri = imageUri
            _selectedItem.value = it
        }
    }
}
