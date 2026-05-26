@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package com.example.multithreads

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.multithreads.databinding.ActivityMainBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask
import java.util.concurrent.ScheduledThreadPoolExecutor

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var text: TextView
    private lateinit var buttonPressed: TextView
    private var future: FutureTask<String>? = null
    private var exampleHandlerThread: HandlerThread? = null
    private var broadcastReceiver: BroadcastReceiver? = null

    private var actionedTime: Long = 0
    private var elapsedTime: Long = 0

    private var localBroadcastManager: LocalBroadcastManager? = null

    @SuppressLint("DefaultLocale")
    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            UPDATE_TEXT_RUNNABLE -> {
                elapsedTime = (System.currentTimeMillis() - actionedTime) / 1000
                text.text = String.format("Message Received from Runnable Interface after %d seconds", elapsedTime)
            }
            UPDATE_TEXT_THREAD -> {
                elapsedTime = (System.currentTimeMillis() - actionedTime) / 1000
                text.text = String.format("Message Received from Thread Class after %d seconds", elapsedTime)
            }
            UPDATE_TEXT_FUTURETASK -> {
                text.text = String.format("%s Message received after %d seconds", msg.obj, elapsedTime)
            }
            UPDATE_TEXT_HANDLER_RECEIVE -> {
                text.text = String.format("Message Received from HandlerThread Class after %d seconds", elapsedTime)
                exampleHandlerThread?.quit()
            }
        }
        true
    }

    private fun updateAfterClick(v: String) {
        buttonPressed.text = resources.getString(R.string.button_press) + v
        actionedTime = System.currentTimeMillis()
        text.text = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        text = binding.text
        buttonPressed = binding.buttonPress

        binding.crashButton.setOnClickListener {
            updateAfterClick(" A")
            Thread {
                Log.d("Future Tasks", "Thread(Done method): " + Thread.currentThread().id)
                Log.d("Future Tasks", "TextView Object Reference(Done method): $text")
                /// Let's access UI thread components from the thread....
                elapsedTime = (System.currentTimeMillis() - actionedTime) / 1000
                text.text = "Crash Happens after $elapsedTime seconds"
            }.start()
        }

        binding.changeTextRunnable.setOnClickListener {
            updateAfterClick(" B")
            Thread {
                try {
                    Thread.sleep(2000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                // Send the Message object towards UI threads
                val message = Message()
                message.what = UPDATE_TEXT_RUNNABLE
                handler.sendMessage(message)
            }.start()
        }

        binding.changeTextThreads.setOnClickListener {
            updateAfterClick(" C")
            object : Thread() {
                override fun run() {
                    try {
                        sleep(2000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    // Send the Message object towards UI threads
                    val message = Message()
                    message.what = UPDATE_TEXT_THREAD
                    handler.sendMessage(message)
                }
            }.start()
        }

        binding.changeTextFutureTask.setOnClickListener {
            updateAfterClick(" D")
            val executor = ScheduledThreadPoolExecutor(2)
            // Implement Callable for futureTask, detail explained in Tutorial slides
            future = object : FutureTask<String>(Callable {
                Thread.sleep(2000)
                "This message was created in the FutureTask thread!"
            }) {
                //  Called when the worker thread finish their work (from isDone() of Future Class)
                override fun done() {
                    var textContent: String? = null
                    try {
                        textContent = get()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                    }
                    val message = Message()
                    message.what = UPDATE_TEXT_FUTURETASK
                    message.obj = textContent
                    handler.sendMessage(message)
                }
            }
            executor.execute(future)
        }

        /**
         * Async Task was deprecated in API 30 : https://developer.android.com/reference/android/os/AsyncTask
         *
         * This implementation uses no reference point, this stops the task from being Garbage Collected if the activity is destroyed, resulting in a memory leak.
         * This example is provided for historical purposes, but you should use one of the other threading examples that are still supported.
         */
        binding.changeTextAsynctask.setOnClickListener {
            updateAfterClick(" E")
            object : AsyncTask<Int, Int, String>() {
                override fun doInBackground(vararg integers: Int?): String {
                    val count = integers[0] ?: 0
                    for (i in 0 until count) {
                        try {
                            Thread.sleep(200)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        publishProgress(i)
                    }
                    elapsedTime = (System.currentTimeMillis() - actionedTime) / 1000
                    return "Message Received from AsyncTask after $elapsedTime seconds"
                }

                override fun onProgressUpdate(vararg progress: Int?) {
                    text.text = "Progress: ${(progress[0] ?: 0) * 10}%"
                    super.onProgressUpdate(*progress)
                }

                override fun onPostExecute(result: String) {
                    text.text = result
                }
            }.execute(10)
        }

        binding.changeTextHandlerThread.setOnClickListener {
            updateAfterClick(" F")
            val handlerThread = HandlerThread("example")
            exampleHandlerThread = handlerThread
            handlerThread.start()

            val msg = Message()
            msg.what = UPDATE_TEXT_HANDLER_SEND

            Handler(handlerThread.looper) { message ->
                when (message.what) {
                    UPDATE_TEXT_HANDLER_SEND -> {
                        try {
                            Thread.sleep(2000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        val reply = Message()
                        reply.what = UPDATE_TEXT_HANDLER_RECEIVE
                        handler.sendMessage(reply)
                    }
                }
                false
            }.sendMessage(msg)
        }

        binding.changeTextIntentService.setOnClickListener {
            updateAfterClick(" G")
            val intentFilter = IntentFilter()
            intentFilter.addAction(ExampleIntentService.ACTION_EXAMPLE_END)
            val manager = LocalBroadcastManager.getInstance(binding.root.context)
            localBroadcastManager = manager
            val receiver = object : BroadcastReceiver() {
                @SuppressLint("DefaultLocale")
                override fun onReceive(context: Context, intent: Intent) {
                    val message = intent.getStringExtra(ExampleIntentService.RESULT_PARAM)
                    elapsedTime = (intent.getLongExtra(ExampleIntentService.RESULT_DURATION, System.currentTimeMillis()) - actionedTime) / 1000
                    text.text = String.format("%s Received in %d seconds.", message, elapsedTime)
                    broadcastReceiver?.let { manager.unregisterReceiver(it) }
                }
            }
            broadcastReceiver = receiver
            manager.registerReceiver(receiver, intentFilter)
            ExampleIntentService.startExample(binding.root.context)
        }

        binding.changeTextEventBus.setOnClickListener {
            updateAfterClick(" H")
            object : Thread() {
                override fun run() {
                    try {
                        sleep(2000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    EventBus.getDefault().post(MessageEvent(
                        "Message Received from Thread through EventBus after $elapsedTime seconds"))
                }
            }.start()
        }

        // To show the thread id
        Log.d("Future Tasks", "Thread Number: " + Thread.currentThread().id)
        // To show the reference of TextView
        Log.d("Future Tasks", "TextView Object Reference: $text")
    }

    override fun onStart() {
        super.onStart()
        // register for receiving EventBus
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        // Unregister EventBus to avoid Android OOM (out-of-memory)
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    // The method to process when receiving MessageEvent
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        text.text = event.message
    }

    companion object {
        const val UPDATE_TEXT_RUNNABLE = 1
        const val UPDATE_TEXT_THREAD = 2
        const val UPDATE_TEXT_FUTURETASK = 3
        const val UPDATE_TEXT_HANDLER_SEND = 4
        const val UPDATE_TEXT_HANDLER_RECEIVE = 5
    }
}
