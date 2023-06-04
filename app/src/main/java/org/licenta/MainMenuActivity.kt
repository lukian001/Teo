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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.licenta.model.Led
import org.licenta.statics.Database
import org.licenta.ui.theme.TeoTheme

class MainMenuActivity: ComponentActivity() {
    private lateinit var cardShown: MutableState<Boolean>
    private lateinit var ledList: MutableState<MutableList<Led>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeoTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    cardShown = remember {
                        mutableStateOf(false)
                    }
                    ledList = remember {
                        mutableStateOf(mutableListOf())
                    }
                    Database.readLeds(ledList)
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
                    }
                }
            }
        }
    }

    @Composable
    fun LEDList(ledList: MutableState<MutableList<Led>>) {
        LazyColumn{
            items(ledList.value) {
                led -> Card (
                    modifier = Modifier.fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Text(led.id)
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
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                TextField(
                    value = ledId,
                    onValueChange = { ledId = it },
                    label = { Text("Led ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 5.dp)
                ) {
                    Button(onClick = {
                        cardShown.value = !Database.addLed(ledId, context)
                    },
                        modifier = Modifier.padding(end = 5.dp)) {
                        Text("Add")
                    }
                }
            }
        }
    }
}