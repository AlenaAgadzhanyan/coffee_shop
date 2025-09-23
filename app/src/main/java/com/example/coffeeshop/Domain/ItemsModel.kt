package com.example.coffeeshop.Domain

import java.io.Serializable

data class ItemsModel(var title: String = "",
                      var description: String = "",
                      val picUrl: ArrayList<String> = ArrayList(),
                      val price: Double = 0.0,
                      var rating: Double = 0.0,
                      var numberInCart: Int = 0,
                      var extra: String = "",
    var isFavorite: Boolean = false
) : Serializable
