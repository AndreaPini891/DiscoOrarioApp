package com.example.discoorario

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.activity_timer.*
import java.util.*
import kotlin.math.roundToInt

var timeMin: Long = 0
var countDownTimer: CountDownTimer? = null

class TimerActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            "sharedpreference",
            Context.MODE_PRIVATE
        )

        val calendar: Calendar = Calendar.getInstance()
        val currentHour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinutes: Int = calendar.get(Calendar.MINUTE)

        val currentTime = currentHour * 60 + currentMinutes

        if (sharedPreferences.getInt(
                "startMinutes",
                0
            ) != 0 && sharedPreferences.getInt("startMinutes", 0) < currentTime
        ) {
            //.......
            //far partire


        }

        imageViewDelete.setOnClickListener {
            stopCounting()
        }

        imageViewSet.setOnClickListener {

            val calendar: Calendar = Calendar.getInstance()
            val currentHour: Int = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinutes: Int = calendar.get(Calendar.MINUTE)

            val materialTimePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText(R.string.text_fine_sosta)
                .setHour(currentHour)
                .setMinute(currentMinutes)
                .build()

            materialTimePicker.show(supportFragmentManager, "timePicker_fragment")

            materialTimePicker.addOnPositiveButtonClickListener {

                if(countDownTimer != null)
                    countDownTimer!!.cancel()


                val editor: SharedPreferences.Editor = sharedPreferences.edit()

                val diff = sharedPreferences.getInt(
                    "stopMinutes",
                    0
                ) - sharedPreferences.getInt("startMinutes", 0)

                val calendar: Calendar = Calendar.getInstance()
                val currentHour: Int = calendar.get(Calendar.HOUR_OF_DAY)
                val currentMinutes: Int = calendar.get(Calendar.MINUTE)

                val hours: Int = materialTimePicker.hour
                val minutes: Int = materialTimePicker.minute

                val stopMinutes = hours * 60 + minutes
                val startMinutes = currentHour * 60 + currentMinutes



                editor.putInt("startMinutes", startMinutes)
                editor.putInt("stopMinutes", stopMinutes)
                editor.putFloat("latitude", intent.getFloatExtra("latitude", 0f))
                editor.putFloat("longitude", intent.getFloatExtra("longitude", 0f))
                editor.apply()
                editor.commit()

                startCounting(diff)


            }

            /*stopCounting()
            isRunning = false
            imageViewSwitch.setImageResource(R.drawable.start_foreground)
            textViewCount!!.text="" + timeMil / 1000
            progressBar.progress = timeMil.toInt() / 1000
            progressBar.max = timeMil.toInt() / 1000*/
        }


    }

    private fun stopCounting() {

        if(countDownTimer != null)
            countDownTimer!!.cancel()

        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            "sharedpreference",
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt("startMinutes", -1)
        editor.putInt("stopMinutes", -1)
        editor.apply()
        editor.commit()

        textViewCount.text = "0"
        progressBar.max = 100
        progressBar.progress = 100

        Toast.makeText(this, "La sosta è stata annullata", Toast.LENGTH_LONG).show()

    }

    private fun startCounting(diff: Int) {
        timeMin = diff.toLong()
        val mills = timeMin.toInt() * 60000
        progressBar.max = mills

        countDownTimer = object : CountDownTimer(mills.toLong(), 1000) {
            override fun onFinish() {

                //parcheggio scaduto

            }

            override fun onTick(millisUntilFinished: Long) {

                // val diff min rimasti = mills - millisUntilFinished ....quanti mls ti rimangono

                //if(mls riman < 5 min) textview.text = " Il parcheggo sta per scadere"
                textViewCount.text = (millisUntilFinished * 0.001f).roundToInt().toString()
                progressBar.progress = (millisUntilFinished * 0.001f).roundToInt()
            }
        }.start()

        countDownTimer!!.start()
    }

}

