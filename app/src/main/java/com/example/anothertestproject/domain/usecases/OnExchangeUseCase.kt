package com.example.anothertestproject.domain.usecases

import com.example.anothertestproject.data.Rate

class OnExchangeUseCase {

    companion object {
        val FEE = 0.7
    }

    fun getWithFeeCommission(fullBalance: Double, shouldCommissionCharge: Boolean = false): Double =
        if (shouldCommissionCharge) {
            fullBalance - FEE
        } else {
            fullBalance
        }

    fun getExchange(sellCurrency: Double, receiveCurrency: Rate): String {
        return (sellCurrency * receiveCurrency.valueToBase).toString()
    }
}