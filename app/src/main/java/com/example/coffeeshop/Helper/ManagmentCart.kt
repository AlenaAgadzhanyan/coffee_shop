package com.example.coffeeshop.Helper

import android.content.Context
import android.widget.Toast
import com.example.coffeeshop.Domain.ItemsModel;


class ManagmentCart(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertItems(item: ItemsModel) {
        var listItem = getListCart()
        val existAlready = listItem.any { it.title == item.title }
        val index = listItem.indexOfFirst { it.title == item.title }

        if (existAlready) {
            listItem[index].numberInCart = item.numberInCart
        } else {
            listItem.add(item)
        }
        tinyDB.putListObject("CartList", listItem)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<ItemsModel> {
        return tinyDB.getListObject("CartList") ?: arrayListOf()
    }

    fun minusItem(listItems: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (listItems[position].numberInCart == 1) {
            listItems.removeAt(position)
        } else {
            listItems[position].numberInCart--
        }
        tinyDB.putListObject("CartList", listItems)
        listener.onChanged()
    }
    fun removeItem(listItems: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {

        listItems.removeAt(position)

        tinyDB.putListObject("CartList", listItems)
        listener.onChanged()
    }

    fun plusItem(listItems: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        listItems[position].numberInCart++
        tinyDB.putListObject("CartList", listItems)
        listener.onChanged()
    }

    fun getTotalFee(): Double {
        val listItem = getListCart()
        var fee = 0.0
        for (item in listItem) {
            fee += item.price * item.numberInCart
        }
        return fee
    }

    fun getListFavorite(): ArrayList<ItemsModel> {
        return tinyDB.getListObject("FavoriteList") ?: arrayListOf()
    }

    fun insertFavorite(item: ItemsModel) {
        val listFavorites = getListFavorite()
        val existAlready = listFavorites.any { it.title == item.title }
        if (!existAlready) {
            listFavorites.add(item)
            tinyDB.putListObject("FavoriteList", listFavorites)
            Toast.makeText(context, "Added to your Favorites", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeFavorite(item: ItemsModel) {
        val listFavorites = getListFavorite()
        val index = listFavorites.indexOfFirst { it.title == item.title }
        if (index != -1) {
            listFavorites.removeAt(index)
            tinyDB.putListObject("FavoriteList", listFavorites)
            Toast.makeText(context, "Removed from your Favorites", Toast.LENGTH_SHORT).show()
        }
    }

    fun isFavorite(item: ItemsModel): Boolean {
        val listFavorites = getListFavorite()
        return listFavorites.any { it.title == item.title }
    }

    fun clearCart() {
        tinyDB.remove("CartList")
    }

    fun clearCartAndFavorites() {
        tinyDB.remove("CartList")
        tinyDB.remove("FavoriteList")
    }
}