package com.example.anothertestproject.repositories.cyrrency

import com.example.anothertestproject.data.Rate

class CurrencyApiRepositoryImpl(
    private val currencyApi: CurrencyApi
) : CurrencyApiRepository {

    override suspend fun getCurrencies(baseCurrency: String): List<Rate> {
        val response = currencyApi.getCurrencies(baseCurrency)
        return if (response.isSuccessful && response.body() != null) {
            response.body()?.let { currency ->
                currency.rates.map {
                    Rate(it.key, it.value, currency.base)
                }
            } ?: listOf()
        } else {
            listOf()
        }
    }
}