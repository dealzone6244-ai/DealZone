package uk.ac.tees.mad.dealzone.data

import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uk.ac.tees.mad.dealzone.Model.Product
import uk.ac.tees.mad.dealzone.Model.ResultState
import uk.ac.tees.mad.dealzone.domain.Repo.ApiBuilder
import uk.ac.tees.mad.dealzone.domain.Repo.Repo

class RepoImpl() : Repo {
    private val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

    override fun getproducts(): Flow<ResultState<List<Product>>> = flow {
        emit(ResultState.Loading)
        try {
            val response = ApiBuilder.provedApi.getProducts()
            emit(ResultState.Succes(response.body()!!.products))
        } catch (e: Exception) {
            emit(ResultState.error(e.message.toString()))
        }
    }

    override fun getSavedCoupons(uid: String): Flow<ResultState<List<Product>>> = flow {
        emit(ResultState.Loading)
        try {
            val snapshot = firestore.collection("users").document(uid).collection("savedCoupons").get().await()
            val products = snapshot.toObjects(Product::class.java)
            emit(ResultState.Succes(products))
        } catch (e: Exception) {
            emit(ResultState.error(e.message.toString()))
        }
    }

    override suspend fun saveCoupon(uid: String, product: Product): ResultState<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            firestore.collection("users").document(uid).collection("savedCoupons").document(product.id.toString()).set(product).await()
            ResultState.Succes(Unit)
        } catch (e: Exception) {
            ResultState.error(e.message.toString())
        }
    }

    override suspend fun removeCoupon(uid: String, productId: Int): ResultState<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
          firestore.collection("users").document(uid).collection("savedCoupons").document(productId.toString()).delete().await()
            ResultState.Succes(Unit)
        } catch (e: Exception) {
            ResultState.error(e.message.toString())
        }
    }
}
