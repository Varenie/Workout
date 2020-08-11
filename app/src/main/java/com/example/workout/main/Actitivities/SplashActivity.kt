package com.example.workout.main.Actitivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.example.workout.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    private var flag: Boolean = false
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()

        auth = Firebase.auth

        if(!flag){
            Thread(Runnable {
                Thread.sleep(2000)
                val user = auth.currentUser

                if(user != null) {
                    startActivity(Intent(this, BasicActivity::class.java))
                } else
                    startActivity(Intent(this, MainActivity::class.java))
                finish()
            }).start()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("flag", true)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        flag = savedInstanceState.getBoolean("flag")
    }
}