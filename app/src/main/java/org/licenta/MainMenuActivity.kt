package org.licenta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.licenta.model.Led
import org.licenta.model.LedLocations
import org.licenta.statics.Authentication
import org.licenta.statics.Database
import org.licenta.ui.theme.TeoTheme

class MainMenuActivity: ComponentActivity() {
    private lateinit var cardShown: MutableState<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeoTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val context = LocalContext.current

                    val ledOpen = remember {
                        mutableStateOf(false)
                    }

                    val selectedLed = remember {
                        mutableStateOf(Led())
                    }

                    val filterLed = remember {
                        mutableStateOf(LedLocations.EMPTY)
                    }

                    if(!ledOpen.value)  {
                        cardShown = remember {
                            mutableStateOf(false)
                        }

                        Column {
                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(end = 20.dp, bottom = 20.dp),
                                onClick = {
                                    cardShown.value = !cardShown.value
                                }
                            ) {
                                if(cardShown.value) {
                                    Text("Close")
                                } else {
                                    Text("Add new LED")
                                }
                            }

                            if(cardShown.value) {
                                LEDCard(cardShown)
                            }

                            OneChip(
                                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                                selectedItem = filterLed,
                                showAll = false
                            )

                            LEDList(ledOpen, selectedLed, filterLed)

                            Spacer(modifier = Modifier.weight(1f))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, end = 5.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        for(led in Database.ledList.value) {
                                            Database.changeLedValue(led, 0)
                                        }
                                    }
                                ) {
                                    Text("All Off")
                                }
                                Button(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 5.dp, end = 10.dp),
                                    onClick = {
                                        for(led in Database.ledList.value) {
                                            Database.changeLedValue(led, returnOnValue(led))
                                        }
                                    }
                                ) {
                                    Text("All On")
                                }
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, end = 10.dp),
                                onClick = {
                                    Authentication.signOut(context)
                                }
                            ) {
                                Text("Sign Out")
                            }
                        }
                    } else {
                        LedChanges(ledOpen, selectedLed)
                    }
                }
            }
        }
    }

    private fun returnOnValue(led: Led): Int {
        if(led.normal) return 1
        return 100
    }

    @Composable
    private fun LedChanges(ledOpen: MutableState<Boolean>, selectedLed: MutableState<Led>) {
        val ledValue = remember {
            mutableStateOf(selectedLed.value.value)
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(selectedLed.value.id)
                Text(selectedLed.value.ledLabel)
                Text(ledValue.value.toString())
                Icon(
                    painter = painterResource(id = R.drawable.baseline_lightbulb_24),
                    contentDescription = "",
                    tint = getColorForLed(ledValue.value)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (selectedLed.value.normal) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        onClick = {
                            if(ledValue.value == 1) {
                                Database.changeLedValue(selectedLed.value, 0)
                                ledValue.value = 0
                            } else {
                                Database.changeLedValue(selectedLed.value, 1)
                                ledValue.value = 1
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            getNormalColor(ledValue.value)
                        )
                    ) {
                        if(ledValue.value == 1) {
                            Text("Turn Off")
                        } else {
                            Text("Turn On")
                        }
                    }
                } else {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        onClick = {
                            Database.changeLedValue(selectedLed.value, ledValue.value - 10)
                            ledValue.value = ledValue.value - 10
                        },
                        enabled = ledValue.value >= 10,
                        colors = ButtonDefaults.buttonColors(Color.Red)
                    ) {
                        Text("Decrease")
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        onClick = {
                            Database.changeLedValue(selectedLed.value, ledValue.value + 10)
                            ledValue.value = ledValue.value + 10
                        },
                        enabled = ledValue.value <= 90,
                        colors = ButtonDefaults.buttonColors(Color.Green)
                    ) {
                        Text("Increase")
                    }
                }
            }

            Spacer(
                Modifier.weight(1f)
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                onClick = {
                    Database.deleteLed(selectedLed.value)
                    selectedLed.value = Led()
                    ledOpen.value = false
                }
            ) {
                Text("Delete LED")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                onClick = {
                    selectedLed.value = Led()
                    ledOpen.value = false
                }
            ) {
                Text("Back")
            }
        }
    }

    private fun getColorForLed(value: Int): Color {
        if(value >= 1) return Color.Yellow
        return Color.Gray
    }

    private fun getNormalColor(value: Int): Color {
        if(value == 1) return Color.Red
        return Color.Green
    }

    @Composable
    fun LEDList(
        ledOpen: MutableState<Boolean>,
        selectedLed: MutableState<Led>,
        filterLed: MutableState<LedLocations>
    ) {
        Column{
            for(led in Database.ledList.value) {
                if(filterLed.value != LedLocations.EMPTY) {
                    if(filterLed.value.displayName != led.loc) continue
                }

                Card (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 15.dp, start = 15.dp, top = 5.dp)
                ) {
                    Row{
                        Column(
                            modifier = Modifier.padding(5.dp)
                        ) {
                            Text("Name: " + led.ledLabel)
                            Text("Id: " + led.id)
                            Text("Tag: " + led.loc)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            modifier = Modifier.padding(5.dp),
                            onClick = {
                            selectedLed.value = led
                            ledOpen.value = true
                        }) {
                            Text("->")
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LEDCard(cardShown: MutableState<Boolean>) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clickable { }
        ) {
            val context = LocalContext.current
            var ledId by remember { mutableStateOf("") }
            var ledLabel by remember { mutableStateOf("") }
            val ledlbl = remember { mutableStateOf(LedLocations.EMPTY) }

            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                TextField(
                    value = ledId,
                    onValueChange = { ledId = it },
                    label = { Text("Led ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = ledLabel,
                    onValueChange = { ledLabel = it },
                    label = { Text("Led Label") },
                    modifier = Modifier.fillMaxWidth()
                )
                OneChip(selectedItem = ledlbl, showAll = true)
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 5.dp)
                ) {
                    Button(onClick = {
                        cardShown.value = !Database.addLed(ledLabel, ledId, context, ledlbl)
                    },
                        modifier = Modifier.padding(end = 5.dp)) {
                        Text("Add")
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun OneChip(
        modifier: Modifier = Modifier,
        selectedItem: MutableState<LedLocations>,
        showAll: Boolean
    ) {
        Row(
            modifier = modifier
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                var selected by remember {
                    mutableStateOf(-1)
                }

                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    LedLocations.values().forEachIndexed { index, item ->
                        if(showAll && (item == LedLocations.EMPTY)) return@forEachIndexed

                        FilterChip(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            selected = selectedItem.value == item,
                            onClick = {
                                selectedItem.value = item
                                selected = index
                            },
                            label = { Text(item.displayName) }
                        )
                    }
                }
            }
        }
    }
}