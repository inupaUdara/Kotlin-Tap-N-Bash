package com.example.whack_a_mole

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val textHighestScore:TextView = findViewById(R.id.highScore)
        val score:TextView = findViewById(R.id.score)
        val winOrLoss:TextView = findViewById(R.id.winOrLose)
        val btnBack: Button = findViewById(R.id.retry)
        val btnHome: Button = findViewById(R.id.home)

        val intent = intent
        val result = intent.getIntExtra("Score", 0)

        //display win or lose status
        if (result >= 10){
            winOrLoss.text = "You Won"
            winOrLoss.setTextColor(ContextCompat.getColor(this, R.color.green))
        }else{
            winOrLoss.text = "You lose"
            winOrLoss.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        val highestScore = intent.getIntExtra("HighestScore", 0)
        score.text = "$result"
        textHighestScore.text = "$highestScore"

        //retry the game
        btnBack.setOnClickListener {
            var intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //back to home(startActivity)
        btnHome.setOnClickListener {
            var intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //display alert when click on back button of device
    private fun displayExitAlert() {
        AlertDialog.Builder(this)
            .setTitle("Exit Confirmation")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onBackPressed() {
        displayExitAlert()
    }
}