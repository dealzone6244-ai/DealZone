package uk.ac.tees.mad.minicart.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.dealzone.domain.Repo.ApiBuilder

import uk.ac.tees.mad.minicart.model.CartItem
import uk.ac.tees.mad.minicart.model.ResultState
import uk.ac.tees.mad.minicart.model.UserData
import uk.ac.tees.mad.minicart.model.productItem

class RepoImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : Repo {

    override fun loginuserwithemailandpassword(userData: UserData): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            auth.signInWithEmailAndPassword(userData.email, userData.password).await()
            emit(ResultState.Succes("Login Successful"))
        } catch (e: Exception) {
            emit(ResultState.error(e.message ?: "Login Failed"))
        }
    }

    override fun registeruserwithemailandpassword(userData: UserData): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            auth.createUserWithEmailAndPassword(userData.email, userData.password).await()
            emit(ResultState.Succes("Registration Successful"))
        } catch (e: Exception) {
            emit(ResultState.error(e.message ?: "Registration Failed"))
        }
    }

    override fun getproducts(): Flow<ResultState<List<productItem>>> = flow {
        emit(ResultState.Loading)
        try {
            // Reusing existing ApiBuilder/Retrofit logic but mapping to new model if necessary
            // For now, mapping dummy API response to productItem
            val response = ApiBuilder.provedApi.getProducts()
            if (response.isSuccessful) {
                val products = response.body()?.products?.map {
                    productItem(
                        id = it.id,
                        title = it.title,
                        price = it.price,
                        image = it.image,
                        category = it.category,
                        discount = it.discount,
                        rating = it.rating
                    )
                } ?: emptyList()
                emit(ResultState.Succes(products))
            } else {
                emit(ResultState.error("Failed to fetch products"))
            }
        } catch (e: Exception) {
            emit(ResultState.error(e.message ?: "An error occurred"))
        }
    }

    override fun getproductItem(id: Int): Flow<ResultState<productItem>> = flow {
        emit(ResultState.Loading)
        try {
            // Mocking for now as existing EndPoint_Builder only has getProducts
            val response = ApiBuilder.provedApi.getProducts()
            val product = response.body()?.products?.find { it.id == id }
            if (product != null) {
                emit(ResultState.Succes(productItem(
                    id = product.id,
                    title = product.title,
                    price = product.price,
                    image = product.image,
                    category = product.category,
                    discount = product.discount,
                    rating = product.rating
                )))
            } else {
                emit(ResultState.error("Product not found"))
            }
        } catch (e: Exception) {
            emit(ResultState.error(e.message ?: "An error occurred"))
        }
    }

    override fun placeOrder(cartItems: List<CartItem>): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            // Simulate order placement
            kotlinx.coroutines.delay(1000)
            emit(ResultState.Succes("Order Placed Successfully"))
        } catch (e: Exception) {
            emit(ResultState.error(e.message ?: "Failed to place order"))
        }
    }

    override fun clearCache(): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        emit(ResultState.Succes("Cache Cleared"))
    }
}
