package com.example.whack_a_mole

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.TextView
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var moleImageView: ImageView
    private lateinit var scoreTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var hammerImageView: ImageView
    private lateinit var mediaPlayer: MediaPlayer

    private var score = 0
    private var highestScore = 0
    private var timer: CountDownTimer? = null
    private var timeRemainingInMillis: Long = 0
    private var timeLeftInMillis: Long = 10000

    private var gamePaused = false
    private var isAlertShown = false
    private var alertDialog: AlertDialog? = null

    private val caveImages: List<ImageView> by lazy {
        // Initialize a list to hold all cave ImageViews
        listOf(
            findViewById(R.id.cave1),
            findViewById(R.id.cave2),
            findViewById(R.id.cave3),
            findViewById(R.id.cave4),
            findViewById(R.id.cave5),
            findViewById(R.id.cave6),
            findViewById(R.id.cave7),
            findViewById(R.id.cave8),
            findViewById(R.id.cave9)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        moleImageView = findViewById(R.id.moleImageView)
        moleImageView.bringToFront()
        scoreTextView = findViewById(R.id.scoreTextView)
        timerTextView = findViewById(R.id.time)
        hammerImageView = findViewById(R.id.hammer)

        //load highest score
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        highestScore = sharedPreferences.getInt("HighestScore", 0)


        moleImageView.setOnClickListener { view ->
            if (timer != null) {
                score++
                scoreTextView.text = "Score: $score"
                showHammer(view.x + view.width / 2, view.y + view.height / 2)
                playMoleHitSound()
            }
        }

        if(!gamePaused) {
            startGame(timeLeftInMillis)
        }
    }

    //add sound effect to when clicking on mole
    private fun playMoleHitSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.bing)
        mediaPlayer.start()
    }

    private fun showHammer(x: Float, y: Float) {
        hammerImageView.bringToFront()
        val offsetX = 80
        val offsetY = 80
        // Set hammer position when clicking on mole image
        hammerImageView.x = x + offsetX  - hammerImageView.width / 2
        hammerImageView.y = y - hammerImageView.height / 2 - offsetY
        // Show hammer
        hammerImageView.alpha = 1f
        // Hide hammer after a short delay
        hammerImageView.postDelayed({ hammerImageView.alpha = 0f }, 200)
    }

    private fun startGame(timeRemaining: Long) {
        timer = object : CountDownTimer(timeRemaining, 600) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000 //convert millis to seconds
                timerTextView.text = "Timer: $secondsRemaining" //display timer
                timeRemainingInMillis = millisUntilFinished //get the time remaining

                //choose random cave
                val randomCave = caveImages.random()

                val moleWidth = moleImageView.width
                val moleHeight = moleImageView.height

                // Calculate valid random positions within the chosen cave
                val caveX = randomCave.x.toInt()
                val caveY = randomCave.y.toInt()
                val maxX = caveX + randomCave.width - moleWidth
                val maxY = caveY + randomCave.height - moleHeight
                val randomX = Random.nextInt(caveX, maxX + 1)
                val randomY = Random.nextInt(caveY, maxY + 1)

                moleImageView.x = randomX.toFloat()
                moleImageView.y = randomY.toFloat()

            }
            override fun onFinish() {
                //check highest score
                if(score > highestScore){
                    highestScore = score
                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt("HighestScore", highestScore)
                    editor.apply()
                }

                // Start ResultActivity and pass scores and highest score
                val intent = Intent(this@MainActivity, ResultActivity::class.java)
                intent.putExtra("Score", score)
                intent.putExtra("HighestScore", highestScore)
                startActivity(intent)
                finish()

                // Reset timer and release reference
                timer?.cancel()
                timer = null
            }


        }.start()

    }

    //display the alert to handle the activity life cycle and back button click
    private fun displayResumeExitAlert() {
        if (!isAlertShown) {
            isAlertShown = true
            alertDialog = AlertDialog.Builder(this)
                .setTitle("Game Paused!")
                .setMessage("Are you sure you want to exit or continue?")
                .setPositiveButton("Exit") { dialog, _ ->
                    dialog.dismiss()
                    timer?.cancel()
                    val intent = Intent(this@MainActivity, StartActivity::class.java)
                    startActivity(intent)
                    finish()
                    isAlertShown = false // Reset the flag
                }
                .setCancelable(false)
                .setNegativeButton("Continue") { dialog, _ ->
                    dialog.dismiss()
                    startGame(timeRemainingInMillis)
                    isAlertShown = false
                }
                .setOnDismissListener {
                    isAlertShown = false
                    alertDialog = null
                }
                .create()
                .apply {
                    setOnKeyListener { _, keyCode, _ ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            true // Consume the back button press event
                        } else {
                            false
                        }
                    }
                }
            alertDialog?.show()
        }
    }

    //to pause the time when click back button of device
    private fun pauseTimer(){
        gamePaused = true
        timer?.cancel()

    }

    override fun onBackPressed() {
        pauseTimer()
        displayResumeExitAlert()
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        displayResumeExitAlert()
    }
}