package org.hyperskill.stopwatch

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    lateinit var watch: TextView
    lateinit var bar: ProgressBar
    lateinit var setting: Button
    private var running = false
    private var seconds = 0
    private val handler = Handler(Looper.getMainLooper())
    private var upperLimit = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        watch = findViewById(R.id.textView)
        bar = findViewById(R.id.progressBar)
        setting = findViewById(R.id.settingsButton)

        findViewById<Button>(R.id.startButton).setOnClickListener { start() }
        findViewById<Button>(R.id.resetButton).setOnClickListener { reset() }
        setting.setOnClickListener { settings() }
    }
    private fun reset() {
        handler.removeCallbacks(updateWatch)
        bar.visibility = android.view.View.INVISIBLE
        handler.removeCallbacks(updateColor)
        watch.text = "00:00"
        watch.setTextColor(Color.GRAY)
        running = false
        seconds = 0
        setting.isEnabled = true
        upperLimit = -1
    }
    private fun start() {
        if (running) return
        running = true
        setting.isEnabled = false
        bar.visibility = android.view.View.VISIBLE
        handler.post(updateWatch)
        handler.post(updateColor)
    }
    private val updateWatch: Runnable = object : Runnable {
        override fun run() {
            if (upperLimit != -1 && upperLimit <= seconds) {
                watch.setTextColor(Color.RED)
            }
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
    private fun settings() {
        val editText = EditText(this)
        editText.id = R.id.upperLimitEditText
        editText.inputType = InputType.TYPE_CLASS_NUMBER;
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle("Set the upper limits in seconds")
            .setPositiveButton("Ok") { _, _ ->
                upperLimit = editText.text.toString().toInt()
            }
            .setNegativeButton("Cancel", null)
            .setView(editText).create()
        dialog.show()
    }
    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateWatch)
        handler.removeCallbacks(updateColor)
    }
}