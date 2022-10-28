package com.example.anothertestproject.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anothertestproject.data.Rate

@Composable
@Preview
fun ExchangeRow(
    titleText: String = "example",
    isEditable: Boolean = false,
    currencies: List<Rate> = listOf(),
    text: String = "",
    shouldRemember: Boolean = true,
    enabledCurrencies: List<String> = listOf(),
    initialCurrency: Rate = Rate("base", 0.0, "base"),
    onCurrencyValueChange: (String) -> Unit = { },
    onCurrencyChange: (String) -> Unit = {}
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = titleText,
            modifier = Modifier.padding(end = 5.dp),
            fontSize = 15.sp
        )
        val mutableText = rememberSaveable { mutableStateOf(text) }
        TextField(
            value = if (shouldRemember) mutableText.value else text,
            onValueChange = {
                mutableText.value = it
                onCurrencyValueChange(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            enabled = isEditable
        )
        SampleSpinner(
            list = currencies,
            preselected = initialCurrency,
            enabledCurrencies = enabledCurrencies
        ) { onCurrencyChange(it.currency) }
    }
}

@Composable
fun SampleSpinner(
    list: List<Rate>,
    preselected: Rate,
    enabledCurrencies: List<String> = listOf(),
    onSelectionChanged: (selection: Rate) -> Unit = {}
) {
    val expanded = remember { mutableStateOf(false) }
    val currentValue = remember { mutableStateOf(preselected) }

    Surface {

        Box {

            Row(modifier = Modifier
                .clickable {
                    expanded.value = !expanded.value
                }
                .align(Alignment.Center)) {
                Text(text = currentValue.value.currency, fontSize = 15.sp)
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)

                DropdownMenu(expanded = expanded.value, onDismissRequest = {
                    expanded.value = false
                }) {

                    list.forEach { rate ->

                        DropdownMenuItem(
                            onClick = {
                                currentValue.value = rate
                                expanded.value = false
                                onSelectionChanged(rate)
                            },
                            enabled = !enabledCurrencies.find { it == rate.currency }.isNullOrEmpty()
                        ) {

                            Text(text = rate.currency)
                        }

                    }
                }

            }
        }
    }
}

@Composable
fun ShowMessage(
    titleText: String = "Dialog Title",
    description: String = "Here is a text",
    confirmButtonText: String = "This is the Confirm Button",
    showMessage: MutableState<Boolean> = mutableStateOf(false)
) {
    val openDialog = remember { showMessage }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = titleText)
            },
            text = {
                Text(description)
            },
            confirmButton = {
                Button(

                    onClick = {
                        openDialog.value = false
                    }) {
                    Text(confirmButtonText)
                }
            }
        )
    }
}