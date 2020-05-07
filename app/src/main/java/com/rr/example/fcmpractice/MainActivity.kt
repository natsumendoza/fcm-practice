package com.rr.example.fcmpractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*
import java.io.FileInputStream

private const val TAG = "MainActivity";

class MainActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
//        toolbar.alpha = 0F

        val viewModelJob = Job()

        val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener {
                Log.d(TAG, "onCreate: Subscribe complete")
            }


        val serviceAccount = resources.openRawResource(R.raw.serviceaccount)

        val scoped = GoogleCredential
            .fromStream(serviceAccount)
            .createScoped(
                listOf("https://www.googleapis.com/auth/firebase", // Add this scope
                    "https://www.googleapis.com/auth/firebase.database",
                    "https://www.googleapis.com/auth/firebase.messaging",
                    "https://www.googleapis.com/auth/identitytoolkit",
                    "https://www.googleapis.com/auth/userinfo.email")
            )


        runBlocking {
            withContext(Dispatchers.IO) {
                scoped.refreshToken()
                coroutineScope.launch {
                    val token = scoped.accessToken
                    Log.d(TAG, "onCreate: $token")
                }
            }
        }
    }
}
