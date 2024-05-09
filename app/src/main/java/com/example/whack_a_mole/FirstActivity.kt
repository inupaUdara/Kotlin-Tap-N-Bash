package com.example.whack_a_mole

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler


class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        supportActionBar?.hide()

        // Create a Handler object
        val handler = Handler()

        // Post a delayed action on the handler
        handler.postDelayed({
            //// Create an intent to start the StartActivity
            val intent = Intent(this@FirstActivity, StartActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}