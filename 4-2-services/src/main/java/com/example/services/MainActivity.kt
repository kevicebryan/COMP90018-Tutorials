package com.example.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.services.databinding.ActivityMainBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /// Notification Channel ///
    private lateinit var notificationManager: NotificationManager
    private lateinit var rpl: ActivityResultLauncher<Array<String>>
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.POST_NOTIFICATIONS)

    private var downloadBinder: MyService.DownloadBinder? = null
    private var bound = false

    // To ServiceConnection for monitoring the change of communication between Service and Activity
    private val connection: ServiceConnection = object : ServiceConnection {

        // called when disconnected to Service
        override fun onServiceDisconnected(name: ComponentName) {
            bound = false
        }

        // called when connecting to Service
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            downloadBinder = service as MyService.DownloadBinder
            downloadBinder?.startDownload()
            downloadBinder?.getProgress()
            bound = true
        }
    }

    /**
     * Create a notification channel to submit notifications from the application
     */
    private fun createNotificationChannel() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val name: CharSequence = getString(R.string.channel_name)
        val description = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(id, name, importance)
        channel.description = description
        channel.enableLights(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel.lightColor = Color.BLUE
        channel.enableVibration(true)
        channel.setShowBadge(true)
        channel.vibrationPattern = longArrayOf(100, 100, 100)

        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Check to confirm that we have the necessary permissions to show notifications (in > Tiramisu)
     * @return if all permissions are granted
     */
    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /// region NotificationManager
        // for notifications permission now required in api 33
        rpl = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
            var granted = true
            for ((key, value) in isGranted) {
                Log.d("MainActivity", "$key is $value")
                if (!value) granted = false
            }
            if (granted) Log.d("MainActivity", "Permissions granted for api 33+")
        }

        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!allPermissionsGranted()) {
                rpl.launch(REQUIRED_PERMISSIONS)
            }
        }
        /// endregion

        /// region onClickListeners
        binding.startService.setOnClickListener {
            val startIntent = Intent(this@MainActivity, MyService::class.java)
            startService(startIntent) // Start Service
            Toast.makeText(binding.root.context, "Service starting", Toast.LENGTH_SHORT).show()
        }

        binding.stopService.setOnClickListener {
            val stopIntent = Intent(this@MainActivity, MyService::class.java)
            stopService(stopIntent) // Stop Service
            makeNotification("Stopped Service", 2)
            Toast.makeText(binding.root.context, "Service stopping", Toast.LENGTH_SHORT).show()
        }

        binding.bindService.setOnClickListener {
            val bindIntent = Intent(this@MainActivity, MyService::class.java)
            bindService(bindIntent, connection, BIND_AUTO_CREATE) // Bind Service
            Toast.makeText(binding.root.context, "Binding Service", Toast.LENGTH_SHORT).show()
        }

        binding.unbindService.setOnClickListener {
            if (bound) {
                unbindService(connection) // Unbind Service
                Toast.makeText(binding.root.context, "Unbinding Service", Toast.LENGTH_SHORT).show()
            }
        }
        /// endregion

        // Register for EventBus Library
        EventBus.getDefault().register(this)
    }

    /**
     * A reusable method to construct necessary notifications
     */
    fun makeNotification(message: String, msgCount: Int) {
        val notification = Notification.Builder(applicationContext, id)
            .setSmallIcon(R.mipmap.ic_launcher)     // set the small icon <REQUIRED>
            .setContentTitle("Service")             // title of the notification <REQUIRED>
            .setContentText(message)                // message of the notification <REQUIRED>
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setWhen(System.currentTimeMillis())    // when did the notification occur
            .setChannelId(id)                       // the notification channel to use
            .setAutoCancel(true)                    // allow the message to be cancelled
            .build()

        //Show the notification
        notificationManager.notify(msgCount, notification)
    }

    override fun onStop() {
        // Unregister to avoid Android OOM (out-of-memory)
        EventBus.getDefault().unregister(this)
        super.onStop()
        unbindService(connection)
        bound = false
    }

    //  Method to process when receiving MessageEvent
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEventActivity(event: MessageEvent) {
        if (event.type == MessageEvent.SERVICE) {
            Log.d("MyService", "Service Message Content: " + event.message)
            EventBus.getDefault().post(MessageEvent(MessageEvent.ACTIVITY, "Hello from Activity!"))
        }
    }

    companion object {
        /// Notification Channel ///
        const val id = "channel_01"
    }
}
