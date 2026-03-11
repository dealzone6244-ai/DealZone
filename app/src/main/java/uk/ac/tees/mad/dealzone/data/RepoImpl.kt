package uk.ac.tees.mad.dealzone.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uk.ac.tees.mad.dealzone.Model.Product
import uk.ac.tees.mad.dealzone.Model.ResultState
import uk.ac.tees.mad.dealzone.domain.Repo.ApiBuilder
import uk.ac.tees.mad.dealzone.domain.Repo.Repo

class RepoImpl() : Repo {
    override fun getproducts(): Flow<ResultState<List<Product>>> = flow {
        emit(ResultState.Loading)
        try {
            val response = ApiBuilder.provedApi.getProducts()
            emit(ResultState.Succes(response.body()!!.products))
        } catch (e: Exception) {
            emit(ResultState.error(e.message.toString()))
        }
    }
}
