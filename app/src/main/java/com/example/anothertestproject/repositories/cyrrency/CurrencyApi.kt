package com.example.anothertestproject.repositories.cyrrency

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface CurrencyApi {

    @GET("exchangerates_data/latest")
    suspend fun getCurrencies(
        @Query("base") baseCurrency: String = "EUR",
        @Header("apikey") apikey: String = "ApKi7PvxI8VTUrART0D17u6GXTkqqVvT"
    ): Response<Currency>
}