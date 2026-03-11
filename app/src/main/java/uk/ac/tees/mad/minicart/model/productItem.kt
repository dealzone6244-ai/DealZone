package uk.ac.tees.mad.minicart.model

data class productItem(
    val category: String,
    val discount: Double,
    val id: Int,
    val image: String,
    val price: Double,
    val rating: Double,
    val title: String,
    val description: String = ""
)
