package org.licenta.statics

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import org.licenta.model.Led

object Database {
    private lateinit var ledList: MutableState<MutableList<Led>>
    private val database = Firebase.database("\n" +
            "https://teolicenta-5a6be-default-rtdb.europe-west1.firebasedatabase.app/")

    init {
        readLeds()
    }

    fun addLed(ledLabel: String, ledId: String, context: Context): Boolean {
        if(ledId.length != 10 || (!ledId.startsWith("I") && !ledId.startsWith("N"))) {
            Toast.makeText(context, "LED ID is incorrect!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(ledLabel.isEmpty()) {
            Toast.makeText(context, "The LED must have a label!", Toast.LENGTH_SHORT).show()
            return false
        }

        val usrAcc = database.getReference(Authentication.auth.currentUser!!.uid)
        usrAcc.child(ledId).setValue(Led(ledLabel, ledId, 0, ledId.startsWith("N")))

        return true
    }

    private fun readLeds() {
        database.getReference(Authentication.auth.currentUser!!.uid).addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TAG", "onChildAdded:" + snapshot.key!!)
                val led = snapshot.getValue<Led>()
                ledList.value.add(led!!)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val led = snapshot.getValue<Led>()
                ledList.value.removeIf {
                    it.id == led!!.id
                }
                ledList.value.add(led!!)
                Log.d("TAG", "onChildAdded:" + snapshot.key!!)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("TAG", "onChildAdded:" + snapshot.key!!)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TAG", "onChildAdded:" + snapshot.key!!)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onChildAdded:", error.toException())
            }

        })
    }

    fun setLedList(ledList: MutableState<MutableList<Led>>) {
        this.ledList = ledList
    }

    fun changeLedValue(led: Led, i: Int) {
        Log.i("Tag", "aici")
        database.getReference(Authentication.auth.currentUser!!.uid).child(led.id).child("value").setValue(i)
    }
}