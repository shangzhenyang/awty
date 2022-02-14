package edu.uw.awty

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import java.util.*

class MsgService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var context: Context
    private lateinit var timer: Timer
    private lateinit var msg: String
    private lateinit var phoneNum: String
    private var interval = 0

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel(): String{
        val channelId = "are_we_there_yet_msg_service"
        val channelName = "Are We There Yet Message Service"
        val channel = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.lightColor = Color.BLUE
        channel.importance = NotificationManager.IMPORTANCE_NONE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    override fun onCreate() {
        super.onCreate()
        val notificationBuilder = NotificationCompat.Builder(
            this,
            createNotificationChannel()
        )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        context = this
        msg = intent?.getStringExtra("msg").toString()
        phoneNum = intent?.getStringExtra("phone_num").toString()
        interval = intent?.getIntExtra("interval", 0)!!
        val toastText = "(" + phoneNum.substring(0, 3) + ") " + phoneNum.substring(3, 6) + "-" +
                phoneNum.substring(6) + ": " + msg
        timer = Timer()
        timer.scheduleAtFixedRate(
            DisplayToastTimerTask(this, handler, toastText),
            0,
            (interval * 60000).toLong()
        )
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        Toast.makeText(this, "Service Ended", Toast.LENGTH_SHORT).show()
    }

    private class DisplayToastTimerTask(
        val context: Context,
        val handler: Handler,
        val text: String
    ) : TimerTask() {
        override fun run() {
            handler.post(Runnable {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            })
        }
    }
}