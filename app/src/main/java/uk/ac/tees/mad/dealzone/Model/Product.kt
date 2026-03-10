package uk.ac.tees.mad.dealzone.Model

data class Product(
    val category: String,
    val discount: Double,
    val id: Int,
    val image: String,
    val price: Double,
    val rating: Double,
    val title: String
)