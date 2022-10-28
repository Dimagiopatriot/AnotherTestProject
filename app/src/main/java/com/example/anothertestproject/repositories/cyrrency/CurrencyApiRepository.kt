package com.example.anothertestproject.repositories.cyrrency

import com.example.anothertestproject.data.Rate

interface CurrencyApiRepository {

    suspend fun getCurrencies(baseCurrency: String): List<Rate>
}