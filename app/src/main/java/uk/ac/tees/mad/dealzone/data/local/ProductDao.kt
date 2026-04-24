package uk.ac.tees.mad.dealzone.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.dealzone.Model.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE lastViewed > 0 ORDER BY lastViewed DESC")
    fun getRecentlyViewed(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("DELETE FROM products")
    suspend fun clearCache()
}
