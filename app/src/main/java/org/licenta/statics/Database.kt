package org.licenta.statics

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.licenta.model.Led
import org.licenta.model.LedLocations

object Database {
    private lateinit var listener: ListenerRegistration
    private val databaseRealtime = Firebase.database(
            "https://teolicenta-5a6be-default-rtdb.europe-west1.firebasedatabase.app/"
    )
    lateinit var ledList: MutableState<MutableList<Led>>

    fun addLed(
        ledLabel: String,
        ledId: String,
        context: Context,
        ledlbl: MutableState<LedLocations>
    ): Boolean {
        if(ledId.length != 10 || (!ledId.startsWith("I") && !ledId.startsWith("N"))) {
            Toast.makeText(context, "LED ID is incorrect!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(ledLabel.isEmpty()) {
            Toast.makeText(context, "The LED must have a label!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(ledlbl.value == LedLocations.EMPTY) {
            Toast.makeText(context, "You must select a label for your led!", Toast.LENGTH_SHORT).show()
            return false
        }

        val usrAcc = databaseRealtime.getReference(Authentication.auth.currentUser!!.uid)
        val led = Led(ledLabel, ledId, 0, ledId.startsWith("N"), ledlbl.value.displayName)

        Firebase.firestore.collection(Authentication.auth.currentUser!!.uid).document(ledId).set(
            mutableMapOf(
                "ledLabel" to led.ledLabel,
                "ledId" to led.id,
                "value" to led.value,
                "normal" to led.normal,
                "loc" to led.loc
            )
        )
        usrAcc.child(ledId).setValue(led)

        return true
    }

    fun changeLedValue(led: Led, i: Int) {
        databaseRealtime.getReference(Authentication.auth.currentUser!!.uid).child(led.id).child("value").setValue(i)
        Firebase.firestore.collection(Authentication.auth.currentUser!!.uid).document(led.id).update("value", i)
    }

    fun startSnapshotForLeds() {
        listener = Firebase.firestore.collection(Authentication.auth.uid!!).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                ledList.value = mutableListOf()

                for (document in snapshot) {
                    val led = Led(
                        ledLabel = document.data["ledLabel"].toString(),
                        id = document.data["ledId"].toString(),
                        value = document.data["value"].toString().toInt(),
                        normal = document.data["normal"].toString().toBooleanStrict(),
                        loc = document.data["loc"].toString()
                    )

                    ledList.value.add(led)
                }
            }
        }
    }

    fun removeListener() {
        listener.remove()
    }

    fun deleteLed(value: Led) {
        databaseRealtime.getReference(Authentication.auth.currentUser!!.uid).child(value.id).removeValue()
        Firebase.firestore.collection(Authentication.auth.currentUser!!.uid).document(value.id).delete()
    }
}