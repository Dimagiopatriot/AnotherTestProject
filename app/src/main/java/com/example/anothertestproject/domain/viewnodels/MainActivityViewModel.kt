package com.example.anothertestproject.domain.viewnodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anothertestproject.data.Rate
import com.example.anothertestproject.domain.INITIAL_USER_BALANCE
import com.example.anothertestproject.domain.replace
import com.example.anothertestproject.domain.usecases.GetCurrenciesUseCase
import com.example.anothertestproject.domain.usecases.ManageUserUseCase
import com.example.anothertestproject.domain.usecases.OnExchangeUseCase
import com.example.anothertestproject.repositories.user.BalanceDto
import com.example.anothertestproject.repositories.user.UserDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivityViewModel(
    private val getCurrenciesUseCase: GetCurrenciesUseCase,
    private val manageUserUseCase: ManageUserUseCase,
    private val onExchangeUseCase: OnExchangeUseCase
) : ViewModel() {

    val receiveCurrencyLiveData = MutableLiveData<String>()
    val rates = MutableLiveData<List<Rate>>()
    val user = MutableLiveData<UserDto>()
    val base = MutableLiveData<String>()

    val showUpdateUserError = MutableLiveData(false)
    val showUpdateUserSuccess = MutableLiveData(false)

    fun getUser(rates: List<Rate>) {
        CoroutineScope(Dispatchers.IO).launch {
            manageUserUseCase.getUser()?.let {
                user.postValue(it)
            } ?: run {
                manageUserUseCase.initiateUserSave(
                    rates,
                    BalanceDto(balance = INITIAL_USER_BALANCE, currency = rates[0].base)
                )
                manageUserUseCase.getUser()?.let {
                    user.postValue(it)
                }
            }
        }
    }

    fun getCurrencies(baseCurrency: String = "EUR") {
        CoroutineScope(Dispatchers.IO).launch {
            val currencies = getCurrenciesUseCase.getCurrencies(baseCurrency = baseCurrency)
            base.postValue(currencies[0].base)
            rates.postValue(currencies)
        }
    }

    fun getInitialCurrency() = rates.value?.find { it.currency == it.base }

    fun calculateCommission(
        sellCurrency: String,
        sellValue: Double,
        receiveCurrency: String,
        receiveValue: Double,
        shouldCommissionCharge: Boolean
    ): Pair<BalanceDto, BalanceDto> {
        val sellBalance = user.value?.balances?.find { it.currency == sellCurrency }
        val receiveBalance = user.value?.balances?.find { it.currency == receiveCurrency }

        val updatedSellBalanceValue = onExchangeUseCase.getWithFeeCommission(
            sellBalance?.balance!! - sellValue,
            shouldCommissionCharge
        )
        val updatedSellBalance =
            BalanceDto(sellBalance.balanceId, updatedSellBalanceValue, sellCurrency)
        val updatedReceiveBalance = BalanceDto(
            receiveBalance?.balanceId,
            receiveBalance?.balance!! + receiveValue,
            receiveCurrency
        )
        return Pair(updatedSellBalance, updatedReceiveBalance)
    }

    fun updateUser(newSellUserBalance: BalanceDto, newReceiveUserBalance: BalanceDto) {
        if (newSellUserBalance.balance > 0) {
            user.value?.apply {
                balances =
                    this.balances.replace(newSellUserBalance) { it.currency == newSellUserBalance.currency }
            }
            user.value?.apply {
                balances =
                    this.balances.replace(newReceiveUserBalance) { it.currency == newReceiveUserBalance.currency }
            }
            CoroutineScope(Dispatchers.IO).launch {
                manageUserUseCase.updateUserBalances(
                    newReceiveUserBalance,
                    newSellUserBalance,
                    userId = user.value?.uid!!
                )
                showUpdateUserSuccess.postValue(true)
            }
            user.postValue(user.value)
        } else {
            showUpdateUserError.postValue(true)
        }
    }

    fun calculateExchange(sellCurrency: Double, receiveCurrency: String) {
        val receiveRate = rates.value?.find { it.currency == receiveCurrency }
        receiveCurrencyLiveData.postValue(receiveRate?.let {
            onExchangeUseCase.getExchange(sellCurrency, it)
        })
    }

    fun enabledCurrenciesToSell(): List<String> {
        return user.value?.let { it.balances.filter { it.balance > 0 }.map { it.currency } }
            ?: emptyList()
    }

    fun enabledCurrenciesToReceive(): List<String> {
        return rates.value?.map { it.currency } ?: emptyList()
    }
}