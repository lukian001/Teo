package org.licenta.statics

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.licenta.MainActivity
import org.licenta.MainMenuActivity
import org.licenta.model.Led

object Authentication {
    val auth = Firebase.auth

    fun login(
        email: String,
        password: String,
        context: Context,
        ledList: MutableState<MutableList<Led>>
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            task ->
                if (task.isSuccessful) {
                    Database.ledList = ledList
                    Database.startSnapshotForLeds()
                    context.startActivity(Intent(context, MainMenuActivity::class.java))
                } else {
                    Toast.makeText(context, "Authentication failed!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun register(
        email: String,
        password: String,
        confirmPassword: String,
        context: Context,
        ledList: MutableState<MutableList<Led>>
    ) {
        if (password == confirmPassword) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                task ->
                    if (task.isSuccessful) {
                        Database.ledList = ledList
                        Database.startSnapshotForLeds()
                        context.startActivity(Intent(context, MainMenuActivity::class.java))
                    } else {
                        Toast.makeText(context, "Registration failed!", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(context, " Passwords does not match. Try again!", Toast.LENGTH_SHORT).show()
        }
    }

    fun signOut(context: Context) {
        Database.ledList.value = mutableListOf()
        Database.removeListener()
        auth.signOut()
        context.startActivity(Intent(context, MainActivity::class.java))
    }
}