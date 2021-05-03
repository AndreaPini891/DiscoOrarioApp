package com.example.discoorario

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_timer.*
import kotlin.math.roundToInt

var isRunning = false
var timeMil : Long = 0
var countDownTimer : CountDownTimer ?= null

class TimerActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)



        imageViewSwitch.setOnClickListener {
            if (!isRunning){
                if (editTextCount!!.text.toString().isEmpty()){
                    Toast.makeText(this,"Inserisci tempo",Toast.LENGTH_SHORT).show()
                }else{
                    startCounting()
                }
            }else{
                stopCounting()
            }
        }

        imageViewReset.setOnClickListener {
            stopCounting()
            isRunning = false
            imageViewSwitch.setImageResource(R.drawable.play_foreground)
            textViewCount!!.text="" + timeMil / 1000
            progressBar.progress = timeMil.toInt() / 1000
            progressBar.max = timeMil.toInt() / 1000
        }


    }

    private fun stopCounting() {
        imageViewSwitch.setImageResource(R.drawable.play_foreground)
        isRunning = false
        countDownTimer!!.cancel()

    }

    private fun startCounting() {
        val txtInput = editTextCount!!.text.toString()
        val timeInput = txtInput.toLong() * 1000
        timeMil = timeInput
        progressBar.max = timeMil.toInt() / 1000
        imageViewSwitch.setImageResource(R.drawable.stop_foreground)
        isRunning = true
        countDownTimer = object  : CountDownTimer(timeMil,1000){
            override fun onFinish() {

            }

            override fun onTick(millisUntilFinished: Long) {



                textViewCount.text = (millisUntilFinished * 0.001f).roundToInt().toString()
                progressBar.progress = Math.round(millisUntilFinished * 0.001f)
            }
        }.start()

        countDownTimer!!.start()
    }
}