package com.example.services

class MessageEvent(val type: Int, val message: String) {
    companion object {
        const val ACTIVITY = 1
        const val SERVICE = 2
    }
}
