package org.hyperskill.stopwatch

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    var running = false
    var seconds = 0
    lateinit var timer :TextView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timer = findViewById(R.id.textView)

        findViewById<Button>(R.id.startButton).setOnClickListener { start() }
        findViewById<Button>(R.id.resetButton).setOnClickListener { reset() }
    }
    private fun reset() {
        handler.removeCallbacks(updateTime)
        timer.text = "00:00"
        running = false
        seconds = 0
    }
    private fun start() {
        if (running) return
        running = true
        handler.post(updateTime)
    }
    private val updateTime: Runnable = object : Runnable {
        override fun run() {
            timer.text = "%02d:%02d".format(seconds / 60, seconds % 60)
            seconds++
            handler.postDelayed(this, 1000)
        }
    }
    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateTime)
    }
}