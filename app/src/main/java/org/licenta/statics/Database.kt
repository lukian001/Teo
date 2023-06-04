package org.licenta.statics

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.licenta.model.Led

object Database {
    private val database = Firebase.firestore

    fun addLed(ledId: String, context: Context): Boolean {
        if(ledId.length != 10 || (!ledId.startsWith("I") && !ledId.startsWith("N"))) {
            Toast.makeText(context, "LED ID is incorrect!", Toast.LENGTH_SHORT).show()
            return false
        }


        val led = hashMapOf(
            "ledId" to ledId,
            "value" to 0,
            "normal" to ledId.startsWith("N")
        )
        database.collection(Authentication.auth.currentUser!!.email!!).
        add(led).addOnSuccessListener { documentReference ->
            Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
        }.addOnFailureListener { e ->
            Log.w("TAG", "Error adding document", e)
        }

        return true
    }

    fun readLeds(leadList: MutableState<MutableList<Led>>) {
        database.collection(Authentication.auth.currentUser!!.email!!).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    val led = Led(document.data["normal"].toString(), document.data["value"].toString().toInt(), document.data["normal"].toString().toBooleanStrict())
                    leadList.value.add(led)
                }
            }.addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }
}