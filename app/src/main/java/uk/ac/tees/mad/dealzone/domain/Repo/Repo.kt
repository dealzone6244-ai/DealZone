package uk.ac.tees.mad.dealzone.domain.Repo

import uk.ac.tees.mad.dealzone.Model.Product
import uk.ac.tees.mad.dealzone.Model.ResultState
import kotlinx.coroutines.flow.Flow

interface Repo {
fun getproducts(): Flow<ResultState<List<Product>>>
}