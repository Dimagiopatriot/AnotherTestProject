package com.example.anothertestproject.domain.usecases

import com.example.anothertestproject.data.Rate
import com.example.anothertestproject.repositories.cyrrency.CurrencyApiRepository

class GetCurrenciesUseCase(
    private val currencyApiRepository: CurrencyApiRepository
) {

    suspend fun getCurrencies(baseCurrency: String): List<Rate> = currencyApiRepository.getCurrencies(baseCurrency)
}