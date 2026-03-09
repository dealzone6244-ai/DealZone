package uk.ac.tees.mad.dealzone.domain.Repo

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface EndPoint_Builder {

    @GET("coupon/1.0")
    suspend fun getCoupons(
        @Header("Authorization") token: String
    ): Response<List<Coupon>>
}