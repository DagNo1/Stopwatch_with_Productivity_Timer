package org.hyperskill.stopwatch

import android.app.*
import android.content.Context
import android.content.Intent
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
import androidx.core.app.NotificationCompat


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
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            if (upperLimit != -1 && upperLimit == seconds) {
                watch.setTextColor(Color.RED)
                if (upperLimit > 0) notifier()
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
    @RequiresApi(Build.VERSION_CODES.O)
    private fun notifier() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channelId = "org.hyperskill"
        val name = "Notification"
        val descriptionText = "Time exceeded"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, LauncherActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this,channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Notification")
            .setContentText("Time exceeded")
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(pendingIntent, true)
            .build()
        notificationBuilder.flags = Notification.FLAG_INSISTENT or Notification.FLAG_ONLY_ALERT_ONCE
        notificationManager.notify(393939, notificationBuilder)
    }
    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateWatch)
        handler.removeCallbacks(updateColor)
    }
}