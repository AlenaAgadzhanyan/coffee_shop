package com.example.coffeeshop.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coffeeshop.Domain.CategoryModel
import com.example.coffeeshop.Domain.ItemsModel
import com.example.coffeeshop.Repository.MainRepository

class MainViewModel: ViewModel() {
    private val repository = MainRepository()

    private val _allItems: LiveData<MutableList<ItemsModel>> = repository.loadAllItems()
    val allItems: LiveData<MutableList<ItemsModel>> = _allItems

    val searchResults = MutableLiveData<MutableList<ItemsModel>>()

    fun search(query: String) {
        val all = _allItems.value
        if (all != null) {
            val filtered = all.filter {
                it.title.contains(query, ignoreCase = true)
            }.toMutableList()
            searchResults.value = filtered
        }
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>>{
        return repository.loadCategory()
    }

    fun loadPopular(): LiveData<MutableList<ItemsModel>>{
        return repository.loadPopular()
    }

    fun loadSpecial(): LiveData<MutableList<ItemsModel>>{
        return repository.loadSpecial()
    }

    fun loadItems(categoryId: String) : LiveData<MutableList<ItemsModel>> {
        return repository.loadCategoryItems(categoryId)
    }
}