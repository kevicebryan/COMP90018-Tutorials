package com.example.multithreads

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.MutableLiveData

class ExampleService : Service() {

    protected val mToken: MutableLiveData<String> = MutableLiveData("current token")

    override fun onBind(intent: Intent): IBinder? = null
}
