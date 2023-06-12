package org.licenta.statics

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.licenta.MainActivity
import org.licenta.MainMenuActivity
import org.licenta.model.Led

object Authentication {
    val auth = Firebase.auth

    fun login(email: String, password: String, context: Context) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            task ->
                if (task.isSuccessful) {
                    Database.startSnapshotForLeds()
                    context.startActivity(Intent(context, MainMenuActivity::class.java))
                } else {
                    Toast.makeText(context, "Authentication failed!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun register(email: String, password: String, confirmPassword: String, context: Context) {
        if (password == confirmPassword) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                task ->
                    if (task.isSuccessful) {
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
        Database.ledList = mutableListOf()
        Database.removeListener()
        auth.signOut()
        context.startActivity(Intent(context, MainActivity::class.java))
    }
}