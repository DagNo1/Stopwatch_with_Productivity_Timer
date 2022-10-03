package org.hyperskill.stopwatch

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var watch: TextView
    lateinit var bar: ProgressBar
    private var running = false
    private var seconds = 0
    private val handler = Handler(Looper.getMainLooper())
    private val colors = listOf(Color.RED, Color.GREEN, Color.BLUE)
    private var color = colors[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        watch = findViewById(R.id.textView)
        bar = findViewById(R.id.progressBar)

        findViewById<Button>(R.id.startButton).setOnClickListener { start() }
        findViewById<Button>(R.id.resetButton).setOnClickListener { reset() }
    }
    private fun reset() {
        handler.removeCallbacks(updateWatch)
        bar.visibility = android.view.View.INVISIBLE
        handler.removeCallbacks(updateColor)
        watch.text = "00:00"
        running = false
        seconds = 0
    }
    private fun start() {
        if (running) return
        running = true
        handler.post(updateWatch)
        bar.visibility = android.view.View.VISIBLE
        handler.post(updateColor)
    }
    private val updateWatch: Runnable = object : Runnable {
        override fun run() {
            watch.text = "%02d:%02d".format(seconds / 60, seconds % 60)
            seconds++
            handler.postDelayed(this, 1000)
        }
    }
    private val updateColor: Runnable = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun run() {
            bar.indeterminateTintList =
                ColorStateList.valueOf(Color.rgb(randomInt(), randomInt(), randomInt()))
            handler.postDelayed(this, 1000)
        }
    }
    private fun randomInt() = (0..255).random()
    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateWatch)
        handler.removeCallbacks(updateColor)
    }
}