package uk.ac.tees.mad.dealzone.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    @SerializedName("discountPercentage")
    val discount: Double = 0.0,
    val rating: Double = 0.0,
    val category: String = "",
    @SerializedName("thumbnail")
    val image: String = "",
    val lastViewed: Long = 0
)