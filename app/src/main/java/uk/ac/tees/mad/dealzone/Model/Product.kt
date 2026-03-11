package uk.ac.tees.mad.dealzone.Model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    @SerializedName("discountPercentage")
    val discount: Double,
    val rating: Double,
    val category: String,
    @SerializedName("thumbnail")
    val image: String
)