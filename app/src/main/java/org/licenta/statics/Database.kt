package org.licenta.statics

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.licenta.model.Led

object Database {
    private val database = Firebase.firestore

    fun addLed(ledLabel: String, ledId: String, context: Context): Boolean {
        if(ledId.length != 10 || (!ledId.startsWith("I") && !ledId.startsWith("N"))) {
            Toast.makeText(context, "LED ID is incorrect!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(ledLabel.isEmpty()) {
            Toast.makeText(context, "The LED must have a label!", Toast.LENGTH_SHORT).show()
            return false
        }

        val led = hashMapOf(
            "ledId" to ledId,
            "lefLabel" to ledLabel,
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

    fun readLeds(leadList: MutableList<Led>) {
        database.collection(Authentication.auth.currentUser!!.email!!).whereNotEqualTo("lefLabel", "").addSnapshotListener { value, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            for (doc in value!!) {
                leadList.add(Led(doc.id, doc["lefLabel"].toString(), doc["ledId"].toString(), doc["value"].toString().toInt(), doc["normal"].toString().toBooleanStrict()))
            }
        }
    }

    fun changeLedValue(dbId: String, i: Int) {
        database.collection(Authentication.auth.currentUser!!.email!!) //
                .document(dbId).update("value", i) //
                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
    }
}