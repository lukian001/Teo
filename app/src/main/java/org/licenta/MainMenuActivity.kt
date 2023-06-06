package org.licenta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import org.licenta.model.Led
import org.licenta.statics.Authentication
import org.licenta.statics.Database
import org.licenta.ui.theme.TeoTheme

class MainMenuActivity: ComponentActivity() {
    private lateinit var cardShown: MutableState<Boolean>
    private lateinit var ledList: MutableState<MutableList<Led>>

    private val dummyLedList = listOf(
        Led("Led cu intensitate", "I000000001", 0, false),
        Led("Led normal", "N000000001", 1, true),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeoTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val context = LocalContext.current

                    cardShown = remember {
                        mutableStateOf(false)
                    }
                    ledList = remember {
                        mutableStateOf(mutableListOf())
                    }
                    Database.setLedList(ledList)
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

                        LEDList(ledList)

                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp),
                            onClick = {
                                Authentication.signOut(context)
                            }
                        ) {
                            Text("Sign Out")
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun LEDList(ledList: MutableState<MutableList<Led>>) {
        Column{
            for(led in ledList.value) {
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
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Row{
                            if(led.normal) {
                                Button(
                                    modifier = Modifier.padding(5.dp),
                                    onClick = {
                                        if(led.value == 1) {
                                            Database.changeLedValue(led, 0)
                                        } else {
                                            Database.changeLedValue(led, 1)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        getBttnColor(led.value)
                                    )
                                ) {
                                    if(led.value == 1) {
                                        Text("Turn off")
                                    } else {
                                        Text("Turn on")
                                    }
                                }
                            } else {
                                Button(
                                    modifier = Modifier.padding(5.dp),
                                    onClick = {
                                        Database.changeLedValue(led, led.value - 10)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        Color.Red
                                    ),
                                    enabled = led.value != 0
                                ) {
                                    Text("-")
                                }
                                Button(
                                    modifier = Modifier.padding(5.dp),
                                    onClick = {
                                        Database.changeLedValue(led, led.value + 10)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        Color.Green
                                    ),
                                    enabled = led.value != 100
                                ) {
                                    Text("+")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getBttnColor(value: Int): Color {
        if (value == 1) return Color.Red
        return Color.Green
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
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 5.dp)
                ) {
                    Button(onClick = {
                        cardShown.value = !Database.addLed(ledLabel, ledId, context)
                    },
                        modifier = Modifier.padding(end = 5.dp)) {
                        Text("Add")
                    }
                }
            }
        }
    }
}