package uk.ac.tees.mad.dealzone.domain.Repo

import retrofit2.Response
import retrofit2.http.GET
import uk.ac.tees.mad.dealzone.Model.Coupon

interface EndPoint_Builder {

    @GET("products")
    suspend fun getProducts(): Response<Coupon>

}