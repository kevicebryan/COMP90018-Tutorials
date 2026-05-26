package com.example.services

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import android.util.Log
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MyService : Service() {

    private lateinit var notificationManager: NotificationManager

    // Example for the Binder
    private val mBinder = DownloadBinder()

    inner class DownloadBinder : Binder() {
        fun startDownload() {
            Log.d("MyService", "startDownload executed")
        }

        fun getProgress(): Int {
            Log.d("MyService", "getProgress executed")
            return 0
        }
    }

    // Called when Activity perform bindService() method
    override fun onBind(intent: Intent): IBinder = mBinder

    override fun onCreate() {
        super.onCreate()

        // Example for implement foreground service
        Log.d("MyService", "onCreate executed")

        // Example for Eventbus
        EventBus.getDefault().register(this)
        EventBus.getDefault().post(MessageEvent(MessageEvent.SERVICE, "Hello from Service using EventBus"))

        ///region NotificationManager
        // Lets notify the user from within the service
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = Notification.Builder(applicationContext, MainActivity.id)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setChannelId(MainActivity.id)
            .setContentTitle("MyService")
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setContentText("Notification from inside MyService")
            .setNumber(5)
            .build()

        notificationManager.notify(1, notification)
        ///endregion
    }

    override fun onDestroy() {
        //Unregister EventBus after stop service to avoid OOM (out-of-memory)
        EventBus.getDefault().unregister(this)
        super.onDestroy()
        Log.d("MyService", "onDestroy executed")
    }

    //called when startService() method is executed
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService", "onStartCommand executed")
        return super.onStartCommand(intent, flags, startId)
    }

    //method to process when receiving MessageEvent
    @Subscribe
    fun onMessageEventService(event: MessageEvent) {
        if (event.type == MessageEvent.ACTIVITY)
            Log.d("MyService", "Activity Message Content: " + event.message)
    }
}
