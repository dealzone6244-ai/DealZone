package uk.ac.tees.mad.dealzone.domain.Repo

import uk.ac.tees.mad.dealzone.Model.Product
import uk.ac.tees.mad.dealzone.Model.ResultState
import kotlinx.coroutines.flow.Flow

interface Repo {
    fun getproducts(): Flow<ResultState<List<Product>>>
    fun getRecentlyViewedProducts(): Flow<List<Product>>
    suspend fun markProductAsViewed(product: Product)
    fun getSavedCoupons(uid: String): Flow<ResultState<List<Product>>>
    suspend fun saveCoupon(uid: String, product: Product): ResultState<Unit>
    suspend fun removeCoupon(uid: String, productId: Int): ResultState<Unit>
}