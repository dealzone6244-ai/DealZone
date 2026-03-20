package uk.ac.tees.mad.dealzone.Model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    @SerializedName("discountPercentage")
    val discount: Double = 0.0,
    val rating: Double = 0.0,
    val category: String = "",
    @SerializedName("thumbnail")
    val image: String = ""
)