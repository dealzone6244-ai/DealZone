package uk.ac.tees.mad.dealzone.Model

data class Coupon(
    val advertiserId: String?,
    val advertiserName: String?,
    val couponCode: String?,
    val description: String?,
    val startDate: String?,
    val endDate: String?
)