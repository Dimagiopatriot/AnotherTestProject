package com.example.anothertestproject.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anothertestproject.data.Rate
import com.example.anothertestproject.domain.isOnline
import com.example.anothertestproject.domain.usecases.OnExchangeUseCase
import com.example.anothertestproject.domain.viewnodels.MainActivityViewModel
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val mainActivityViewModel: MainActivityViewModel by inject()
    private var freeFeeCounter = 5


    private val receiveCurrency = mutableStateOf("")
    private val receiveCurrencyVal = mutableStateOf(0.0)
    private val sellCurrency = mutableStateOf("")
    private val sellCurrencyVal = mutableStateOf(0.0)

    private val initialCurrency = mutableStateOf(Rate("", 0.0, ""))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isOnline()) {
            mainActivityViewModel.getCurrencies()
        } else {
            Toast.makeText(this, "No Internet connection!", Toast.LENGTH_SHORT).show()
        }

        setContent {
            RenderUI()
        }
    }

    @Composable
    @Preview
    fun RenderUI() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
            Text(
                text = "MY BALANCES",
                modifier = Modifier.padding(bottom = 10.dp),
                fontSize = 30.sp
            )
            mainActivityViewModel.user.observeAsState().value?.let {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    it.balances.forEach {
                        Row {
                            Text(
                                text = it.balance.toString(),
                                fontSize = 20.sp,
                                modifier = Modifier.padding(end = 10.dp),
                            )
                            Text(
                                text = it.currency,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(end = 30.dp)
                            )
                        }
                    }
                }
            }
            Text(
                text = "CURRENCY EXCHANGE",
                modifier = Modifier.padding(bottom = 10.dp),
                fontSize = 30.sp
            )
            mainActivityViewModel.rates.observeAsState().value?.let { currencies ->
                if (initialCurrency.value.currency.isEmpty()) {
                    initialCurrency.value = mainActivityViewModel.getInitialCurrency()!!

                }
                if (receiveCurrency.value.isEmpty()) {
                    receiveCurrency.value = initialCurrency.value.currency
                }

                if (sellCurrency.value.isEmpty()) {
                    sellCurrency.value = initialCurrency.value.currency
                }
                mainActivityViewModel.getUser(currencies)
                ExchangeRow(
                    titleText = "Sell",
                    isEditable = true,
                    initialCurrency = initialCurrency.value,
                    enabledCurrencies = mainActivityViewModel.enabledCurrenciesToSell(),
                    currencies = currencies,
                    onCurrencyValueChange = { currencyValue ->
                        sellCurrencyVal.value =
                            if (currencyValue.isNotEmpty()) currencyValue.toDouble() else 0.0
                        mainActivityViewModel.calculateExchange(
                            sellCurrencyVal.value,
                            receiveCurrency.value
                        )
                    },
                    onCurrencyChange = { newCurrency ->
                        sellCurrency.value = newCurrency
                        mainActivityViewModel.getCurrencies(newCurrency)
                    }
                )
                mainActivityViewModel.receiveCurrencyLiveData.observeAsState().value?.let {
                    receiveCurrencyVal.value = it.toDouble()
                }
                mainActivityViewModel.base.observeAsState().value?.let {
                    mainActivityViewModel.calculateExchange(
                        sellCurrencyVal.value,
                        receiveCurrency.value
                    )
                }
                ExchangeRow(
                    titleText = "Receive",
                    isEditable = false,
                    text = receiveCurrencyVal.value.toString(),
                    shouldRemember = false,
                    initialCurrency = initialCurrency.value,
                    currencies = currencies,
                    enabledCurrencies = mainActivityViewModel.enabledCurrenciesToReceive(),
                    onCurrencyChange = { newCurrency ->
                        receiveCurrency.value = newCurrency
                        mainActivityViewModel.calculateExchange(
                            sellCurrencyVal.value,
                            receiveCurrency.value
                        )
                    }
                )
            }

            Button(
                onClick = {
                    freeFeeCounter--
                    val updatedBalances = mainActivityViewModel.calculateCommission(
                        sellCurrency.value,
                        sellCurrencyVal.value,
                        receiveCurrency.value,
                        receiveCurrencyVal.value,
                        freeFeeCounter > 0
                    )
                    mainActivityViewModel.updateUser(updatedBalances.first, updatedBalances.second)
                },
                enabled = receiveCurrency.value.isNotEmpty(),
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(text = "SUBMIT")
            }
            ShowMessage(
                showMessage = mainActivityViewModel.showUpdateUserError.observeAsState() as MutableState<Boolean>,
                titleText = "Update Error",
                description = "Can't update user balances",
                confirmButtonText = "OK"
            )
            val fee = if (freeFeeCounter > 0) OnExchangeUseCase.FEE else 0.0
            ShowMessage(
                showMessage = mainActivityViewModel.showUpdateUserSuccess.observeAsState() as MutableState<Boolean>,
                titleText = "Currency converted",
                description = "You have converted ${sellCurrencyVal.value} ${sellCurrency.value} to ${receiveCurrencyVal.value} ${receiveCurrency.value}. Commission Fee - $fee ${sellCurrency.value}",
                confirmButtonText = "Done"
            )
        }
    }

}